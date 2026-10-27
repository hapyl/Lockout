package me.hapyl.lockout.board;

import com.google.common.collect.Maps;
import io.papermc.paper.registry.keys.SoundEventKeys;
import me.hapyl.lockout.*;
import me.hapyl.lockout.board.task.Task;
import me.hapyl.lockout.board.task.TaskProgress;
import me.hapyl.lockout.mode.LockoutMode;
import me.hapyl.lockout.util.ComponentList;
import me.hapyl.lockout.util.ItemBuilder;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBarViewer;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class Board implements Runnable, TablistHandler {
    
    public static final int BOARD_SIZE = 25;
    
    public static final int WORLD_BORDER_SIZE_PREGAME = 20;
    public static final int WORLD_BORDER_SIZE = 5000;
    public static final int WORLD_BORDER_TIME = 1200;
    
    public static final Sound SOUND_TASK_PROGRESSED = Sound.sound(SoundEventKeys.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.Source.UI, 3, 1.0f);
    public static final Sound SOUND_TASK_COMPLETED_OWN_TEAM = Sound.sound(SoundEventKeys.ENTITY_PLAYER_LEVELUP, Sound.Source.UI, 3, 1.25f);
    public static final Sound SOUND_TASK_COMPLETED_OTHER_TEAM = Sound.sound(SoundEventKeys.BLOCK_NOTE_BLOCK_BASS, Sound.Source.UI, 3, 0f);
    
    private static final Component COMPONENT_INFINITY_CHAR = Component.text("∞");
    private static final Component COMPONENT_HEADER_FOOTER = Component.text("▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀", Colors.GOLD, TextDecoration.BOLD);
    private static final Component COMPONENT_GAME_OVER = Component.text(" ɢᴀᴍᴇ ᴏᴠᴇʀ", Colors.GOLD, TextDecoration.BOLD);
    
    private final LockoutMode lockoutMode;
    private final Map<Task, BoardTask> tasks;
    private final BossBar bossBar;
    private final Map<Player, PinnedTasks> pinnedTasks;
    
    private int tick;
    private @NotNull Board.State state;
    
    public Board(@NotNull LockoutMode lockoutMode, @NotNull List<? extends Task> tasks) {
        if (tasks.size() != BOARD_SIZE) {
            throw new IllegalArgumentException("There must be exactly %s tasks, not %s!".formatted(BOARD_SIZE, tasks.size()));
        }
        
        this.lockoutMode = lockoutMode;
        this.tasks = generateTaskProgress(tasks);
        this.bossBar = createBossBar();
        this.state = State.STARTING;
        this.pinnedTasks = Maps.newHashMap();
    }
    
    public @NotNull PinnedTasks getPinnedTasks(@NotNull Player player) {
        return pinnedTasks.computeIfAbsent(player, _ -> new PinnedTasks(player, this));
    }
    
    public void onPlayerJoin(@NotNull Player player) {
        player.showBossBar(bossBar);
    }
    
    public void onPlayerQuit(@NotNull Player player) {
        player.hideBossBar(bossBar);
    }
    
    public @NotNull List<? extends PlayerTeamInfo> playerTeamInfo() {
        return PlayerTeam.nonEmptyTeams().map(playerTeam -> PlayerTeamInfo.create(this, playerTeam)).sorted().toList();
    }
    
    public @NotNull PlayerTeamInfo playerTeamInfo(@NotNull PlayerTeam playerTeam) {
        return PlayerTeamInfo.create(this, playerTeam);
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
        final List<? extends PlayerTeamInfo> playerTeamInfos = this.playerTeamInfo();
        
        if (playerTeamInfos.isEmpty()) {
            everyone.sendMessage(Component.text("No one completed any tasks, yuk :/", Colors.DARK_GRAY));
        }
        else {
            playerTeamInfos.forEach(playerTeamInfo -> everyone.sendMessage(Component.space().append(playerTeamInfo)));
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
        final int gracePeriod = lockoutMode.gracePeriod().ticks();
        
        if (state == State.STARTING) {
            if (tick > gracePeriod) {
                this.setState(State.IN_PROGRESS);
                this.tick = 0;
                
                handleStateChangeToInProgress();
                return;
            }
            
            bossBar.progress(clamp01f(1 - (float) tick / gracePeriod));
            bossBar.name(state.bossBarName(gracePeriod - tick));
        }
        else if (state == State.IN_PROGRESS) {
            final int timeLimit = lockoutMode.timeLimit().ticks();
            
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
    
    @Override
    public void tablistFooter(@NotNull Player player, @NotNull ComponentList footer) {
        // Show pinned tasks
        footer.append(Component.text("ᴘɪɴɴᴇᴅ ᴛᴀꜱᴋꜱ", Colors.GOLD, TextDecoration.BOLD));
        footer.append();
        
        final PinnedTasks pinnedTasks = getPinnedTasks(player);
        final PlayerTeam playerTeam = PlayerTeam.getPlayerTeam(player).orElse(PlayerTeam.RED);
        
        if (pinnedTasks.getTasks().isEmpty()) {
            footer.append(Component.text("None!", Colors.DARK_GRAY));
        }
        else {
            int i = 0;
            
            for (BoardTask boardTask : pinnedTasks.getTasks()) {
                if (i++ != 0) {
                    footer.append();
                }
                
                final Task task = boardTask.getTask();
                final TaskProgress progress = boardTask.getProgress(playerTeam);
                
                footer.append(task.getName().color(Colors.YELLOW).appendSpace().append(progress));
                footer.append(task.getDescription().color(Colors.GRAY));
            }
        }
    }
    
    public @NotNull @Unmodifiable List<? extends BoardTask> getTasks() {
        return List.copyOf(tasks.values());
    }
    
    public <P extends TaskProgress> void progressTask(@NotNull Player player, @NotNull Task task, @NotNull ProgressConstructor<P> constructor, @NotNull ProgressAccumulator<P> accumulator) {
        if (isPlayerInvalid(player)) {
            return;
        }
        
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
    
    private static boolean isPlayerInvalid(@NotNull Player player) {
        if (player.getGameMode() == GameMode.SPECTATOR) {
            return true;
        }
        else if (PlayerTeam.getPlayerTeam(player).isEmpty()) {
            return true;
        }
        
        return false;
    }
    
    private static @NotNull BossBar createBossBar() {
        final BossBar bossBar = BossBar.bossBar(Component.empty(), 1.0f, BossBar.Color.YELLOW, BossBar.Overlay.NOTCHED_10);
        
        // Show the boss bar to all online players
        Bukkit.getOnlinePlayers().forEach(bossBar::addViewer);
        
        return bossBar;
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
    
    private static @NotNull @Unmodifiable Map<Task, BoardTask> generateTaskProgress(@NotNull Iterable<? extends Task> tasks) {
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
    
    public static class PlayerTeamInfo implements ComponentLike, Comparable<PlayerTeamInfo> {
        
        // We sort player team info by points
        private final int points;
        private final Component component;
        
        PlayerTeamInfo(@NotNull PlayerTeam playerTeam, int points, @NotNull List<? extends TaskProgress> completedTasks) {
            this.points = points;
            this.component = createComponent(playerTeam, points).hoverEvent(createHoverEvent(completedTasks));
        }
        
        @Override
        public @NotNull Component asComponent() {
            return component;
        }
        
        @Override
        public int compareTo(@NotNull Board.PlayerTeamInfo that) {
            return Double.compare(that.points, this.points);
        }
        
        static @NotNull PlayerTeamInfo create(@NotNull Board board, @NotNull PlayerTeam playerTeam) {
            final List<? extends BoardTask> tasksCompletedByTeam = board.tasks.values().stream().filter(task -> task.getCompletionTeam() == playerTeam).toList();
            
            if (tasksCompletedByTeam.isEmpty()) {
                return new PlayerTeamInfo(playerTeam, 0, List.of());
            }
            
            return new PlayerTeamInfo(
                    playerTeam,
                    tasksCompletedByTeam.stream().mapToInt(task -> task.getTask().getDifficulty().getPoints()).sum(),
                    tasksCompletedByTeam.stream().map(task -> task.getProgress(playerTeam)).toList()
            );
        }
        
        private static @NotNull Component createComponent(@NotNull PlayerTeam playerTeam, int points) {
            return Component.empty()
                            .append(playerTeam.asComponentWithPlayers())
                            .append(Component.text("  "))
                            .append(Component.text(points, Colors.WHITE, TextDecoration.BOLD));
        }
        
        private static @NotNull HoverEvent<Component> createHoverEvent(@NotNull List<? extends TaskProgress> completedTasks) {
            final TextComponent.Builder builder = Component.text();
            
            for (int i = 0; i < completedTasks.size(); i++) {
                if (i != 0) {
                    builder.appendNewline().appendNewline();
                }
                
                final TaskProgress taskProgress = completedTasks.get(i);
                final ComponentList components = taskProgress.getComponents();
                
                if (components != null) {
                    builder.append(taskProgress.getTask().getName().color(Colors.WHITE).decorate(TextDecoration.BOLD)).appendNewline();
                    
                    int j = 0;
                    for (Component component : components) {
                        if (j++ != 0) {
                            builder.appendNewline();
                        }
                        
                        builder.append(Component.space().append(component.color(Colors.GRAY)));
                    }
                }
            }
            
            return HoverEvent.showText(builder);
        }
    }
    
}