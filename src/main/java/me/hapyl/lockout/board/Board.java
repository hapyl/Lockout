package me.hapyl.lockout.board;

import com.google.common.collect.Maps;
import io.papermc.paper.registry.keys.SoundEventKeys;
import me.hapyl.lockout.Colors;
import me.hapyl.lockout.Lockout;
import me.hapyl.lockout.Locust;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.task.Task;
import me.hapyl.lockout.board.task.TaskProgress;
import me.hapyl.lockout.mode.LockoutMode;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBarViewer;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Board implements Runnable {
    
    public static final int BOARD_SIZE = 25;
    public static final int GRACE_PERIOD = 600;
    
    public static final int WORLD_BORDER_SIZE_PREGAME = 20;
    public static final int WORLD_BORDER_SIZE = 5000;
    public static final int WORLD_BORDER_TIME = 1200;
    
    public static final int DEFAULT_TIME_LIMIT_IN_MINUTES = 30;
    
    public static final Sound SOUND_TASK_PROGRESSED = Sound.sound(SoundEventKeys.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.Source.UI, 3, 1.0f);
    public static final Sound SOUND_TASK_COMPLETED_OWN_TEAM = Sound.sound(SoundEventKeys.ENTITY_PLAYER_LEVELUP, Sound.Source.UI, 3, 1.25f);
    public static final Sound SOUND_TASK_COMPLETED_OTHER_TEAM = Sound.sound(SoundEventKeys.BLOCK_NOTE_BLOCK_BASS, Sound.Source.UI, 3, 0f);
    
    private static final Component COMPONENT_INFINITY_CHAR = Component.text("∞");
    private static final Component COMPONENT_HEADER_FOOTER = Component.text("▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀", Colors.GOLD, TextDecoration.BOLD);
    private static final Component COMPONENT_GAME_OVER = Component.text(" ɢᴀᴍᴇ ᴏᴠᴇʀ", Colors.GOLD, TextDecoration.BOLD);
    
    private final LockoutMode lockoutMode;
    private final Map<Task, BoardTask> tasks;
    private final BossBar bossBar;
    
    private int tick;
    private @NotNull Board.State state;
    
    public Board(@NotNull LockoutMode lockoutMode, @NotNull Collection<? extends Task> tasks) {
        if (tasks.size() != BOARD_SIZE) {
            throw new IllegalArgumentException("There must be exactly %s tasks, not %s!".formatted(BOARD_SIZE, tasks.size()));
        }
        
        this.lockoutMode = lockoutMode;
        this.tasks = generateTaskProgress(tasks);
        this.bossBar = createBossBar();
        this.state = State.STARTING;
    }
    
    public void onPlayerJoin(@NotNull Player player) {
        player.showBossBar(bossBar);
    }
    
    public void onPlayerQuit(@NotNull Player player) {
        player.hideBossBar(bossBar);
    }
    
    public @NotNull Map<? extends PlayerTeam, ? extends Integer> calculateTeamPoints() {
        final Map<PlayerTeam, Integer> teamPoints = PlayerTeam.nonEmptyTeams()
                                                              .collect(Collectors.toMap(Function.identity(), _ -> 0, (a, b) -> a, () -> Maps.newEnumMap(PlayerTeam.class)));
        
        for (BoardTask task : tasks.values()) {
            final PlayerTeam completionTeam = task.getCompletionTeam();
            
            if (completionTeam != null) {
                teamPoints.merge(completionTeam, 1, Integer::sum);
            }
        }
        
        // Sort the map from highest to lowest points
        return teamPoints.entrySet()
                         .stream()
                         .sorted(Map.Entry.<PlayerTeam, Integer>comparingByValue().reversed())
                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }
    
    public int calculateTeamPoints(@NotNull PlayerTeam playerTeam) {
        return tasks.values().stream().filter(task -> task.getCompletionTeam() == playerTeam).mapToInt(task -> task.getTask().getDifficulty().getPoints()).sum();
    }
    
    public void onFinished() {
        for (BossBarViewer viewer : bossBar.viewers()) {
            if (viewer instanceof Player player) {
                player.hideBossBar(bossBar);
            }
        }
        
        final Audience everyone = Locust.everyone();
        
        everyone.sendMessage(COMPONENT_HEADER_FOOTER);
        everyone.sendMessage(Component.empty());
        everyone.sendMessage(COMPONENT_GAME_OVER);
        everyone.sendMessage(Component.empty());
        
        // Calculate points and award winners
        final Map<? extends PlayerTeam, ? extends Integer> teamPoints = this.calculateTeamPoints();
        
        if (teamPoints.isEmpty()) {
            everyone.sendMessage(Component.text("No one completed any tasks, yuk.", Colors.DARK_GRAY));
        }
        else {
            teamPoints.forEach((playerTeam, points) -> {
                everyone.sendMessage(
                        Component.empty()
                                 .append(Component.text("  "))
                                 .append(playerTeam.asComponentWithPlayers())
                                 .append(Component.text("   %s".formatted(points), Colors.WHITE, TextDecoration.BOLD))
                );
            });
        }
        
        everyone.sendMessage(Component.empty());
        everyone.sendMessage(COMPONENT_HEADER_FOOTER);
        
        // Reset world border and teleport players to the centre
        final World world = Lockout.world();
        final double worldBorderSizeAlmostHalf = WORLD_BORDER_SIZE_PREGAME * 0.4;
        
        Bukkit.getOnlinePlayers().forEach(player -> {
            // Offset the centre a little bit so we don't teleport players to the same block
            final Location location = world.getSpawnLocation();
            final Random random = Lockout.random();
            
            final double x = random.nextDouble(-worldBorderSizeAlmostHalf, worldBorderSizeAlmostHalf);
            final double z = random.nextDouble(-worldBorderSizeAlmostHalf, worldBorderSizeAlmostHalf);
            
            location.add(x, 0, z);
            location.setY(world.getHighestBlockYAt(location) + 1);
            
            // Keep player's yaw & pitch
            location.setYaw(player.getYaw());
            location.setPitch(player.getPitch());
            
            player.teleport(location);
        });
        
        world.getWorldBorder().changeSize(WORLD_BORDER_SIZE_PREGAME, 100);
    }
    
    @Override
    public void run() {
        if (state == State.STARTING) {
            if (tick > GRACE_PERIOD) {
                this.setState(State.IN_PROGRESS);
                this.tick = 0;
                
                handleStateChangeToInProgress();
                return;
            }
            
            bossBar.progress(clamp01f(1 - (float) tick / GRACE_PERIOD));
            bossBar.name(state.bossBarName(GRACE_PERIOD - tick));
        }
        else if (state == State.IN_PROGRESS) {
            final int timeLimit = lockoutMode.timeLimit();
            
            if (tick > timeLimit) {
                this.setState(State.FINISHED);
                return;
            }
            
            bossBar.progress(clamp01f(1 - (float) tick / timeLimit));
            bossBar.name(state.bossBarName(timeLimit - tick));
        }
        
        // Increment tick
        tick++;
    }
    
    public @NotNull @Unmodifiable List<? extends BoardTask> getTasks() {
        return List.copyOf(tasks.values());
    }
    
    public <P extends TaskProgress> void progressTask(@NotNull Player player, @NotNull Task task, @NotNull ProgressConstructor<P> constructor, @NotNull ProgressAccumulator<P> accumulator) {
        final BoardTask boardTask = this.tasks.get(task);
        
        if (boardTask == null) {
            return;
        }
        
        final PlayerTeam playerTeam = PlayerTeam.getPlayerTeam(player).orElse(PlayerTeam.RED);
        final BoardTask.ProgressResponse response = boardTask.progress(playerTeam, constructor, accumulator);
        final TaskProgress taskProgress = boardTask.getProgress(playerTeam);
        
        // Feedback the response
        switch (response) {
            case SUCCESSFULLY_PROGRESSED -> {
                playerTeam.sendActionBar(
                        Component.empty()
                                 .append(task.getName().color(Colors.DEFAULT))
                                 .appendSpace()
                                 .append(taskProgress.asComponent())
                );
                
                playerTeam.playSound(SOUND_TASK_PROGRESSED);
            }
            
            case SUCCESSFULLY_COMPLETED -> {
                final Component taskNameInGold = task.getName().color(Colors.GOLD);
                final Component otherTeamMessage = Component.empty()
                                                            .append(playerTeam)
                                                            .append(Component.text(" team has locked out ", Colors.RED))
                                                            .append(taskNameInGold)
                                                            .append(Component.text(" task!", Colors.RED));
                
                PlayerTeam.nonEmptyTeams().forEach(otherTeam -> {
                    // Notify completion for own team
                    if (otherTeam == playerTeam) {
                        playerTeam.sendMessage(
                                Component.empty()
                                         .append(Component.text("Your team has completed ", Colors.GREEN))
                                         .append(taskNameInGold)
                                         .append(Component.text(" task!", Colors.GREEN))
                                         .append(Component.text(" (+%s pts)".formatted(task.getDifficulty().getPoints()), Colors.DARK_GRAY))
                        
                        );
                        
                        playerTeam.playSound(SOUND_TASK_COMPLETED_OWN_TEAM);
                    }
                    else {
                        otherTeam.sendMessage(otherTeamMessage);
                        otherTeam.playSound(SOUND_TASK_COMPLETED_OTHER_TEAM);
                    }
                    
                });
            }
        }
    }
    
    public boolean hasStarted() {
        return state == State.IN_PROGRESS;
    }
    
    public @NotNull Board.State getState() {
        return state;
    }
    
    private void setState(@NotNull Board.State state) {
        if (this.state == state) {
            return;
        }
        
        this.state = state;
        this.bossBar.color(state.bossBarColor());
    }
    
    private @NotNull BossBar createBossBar() {
        final BossBar bossBar = BossBar.bossBar(Component.empty(), 1.0f, BossBar.Color.YELLOW, BossBar.Overlay.NOTCHED_10);
        
        // Show the boss bar to all online players
        Bukkit.getOnlinePlayers().forEach(bossBar::addViewer);
        
        return bossBar;
    }
    
    public static @NotNull Component formatTick(int tick) {
        if (tick <= 0) {
            return COMPONENT_INFINITY_CHAR;
        }
        
        final long totalSeconds = tick / 20;
        final long hours = totalSeconds / 3600;
        final long minutes = totalSeconds % 3600 / 60;
        final long seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return Component.text("%02dh %02dm %02ds".formatted(hours, minutes, seconds));
        }
        else if (minutes > 0) {
            return Component.text("%02dm %02ds".formatted(minutes, seconds));
        }
        else if (seconds > 10) {
            return Component.text("%02ds".formatted(seconds));
        }
        
        return Component.text("%d.%02ds".formatted(seconds, tick * 50 % 1000 / 10));
    }
    
    private static void handleStateChangeToInProgress() {
        final World world = Lockout.world();
        
        world.getWorldBorder().changeSize(WORLD_BORDER_SIZE, WORLD_BORDER_TIME);
        world.setTime(1);
        
        // Set every player's game mode to survival unless they are in spectator
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.getGameMode() != GameMode.SPECTATOR) {
                player.setGameMode(GameMode.SURVIVAL);
            }
            
            player.getInventory().clear();
            player.setTotalExperience(0);
        });
    }
    
    private static @NotNull @Unmodifiable Map<Task, BoardTask> generateTaskProgress(@NotNull Collection<? extends Task> tasks) {
        final Map<Task, BoardTask> outTask = Maps.newLinkedHashMapWithExpectedSize(BOARD_SIZE);
        
        // Get non-empty teams
        final List<? extends PlayerTeam> nonEmptyTeams = PlayerTeam.nonEmptyTeams().toList();
        
        // Fill the map with empty progress
        for (Task task : tasks) {
            outTask.put(task, new BoardTask(task, nonEmptyTeams));
        }
        
        return outTask;
    }
    
    private static float clamp01f(float f) {
        return f <= 0 ? 0 : f >= 1 ? 1 : f;
    }
    
    public enum State {
        
        STARTING {
            @Override
            public @NotNull BossBar.Color bossBarColor() {
                return BossBar.Color.YELLOW;
            }
            
            @Override
            public @NotNull Component bossBarName(int tick) {
                return Component.text("Starting In ", Colors.YELLOW).append(formatTick(tick).color(Colors.GOLD));
            }
        },
        
        IN_PROGRESS {
            @Override
            public @NotNull BossBar.Color bossBarColor() {
                return BossBar.Color.GREEN;
            }
            
            @Override
            public @NotNull Component bossBarName(int tick) {
                return Component.text("Time Left ", Colors.GREEN).append(formatTick(tick).color(Colors.DARK_GREEN));
            }
        },
        
        FINISHED {
            @Override
            public @NotNull BossBar.Color bossBarColor() {
                return BossBar.Color.RED;
            }
            
            @Override
            public @NotNull Component bossBarName(int tick) {
                return super.bossBarName(tick);
            }
        };
        
        public @NotNull BossBar.Color bossBarColor() {
            return BossBar.Color.WHITE;
        }
        
        public @NotNull Component bossBarName(int tick) {
            return Component.empty();
        }
        
    }
}