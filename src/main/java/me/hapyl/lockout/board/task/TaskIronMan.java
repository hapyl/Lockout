package me.hapyl.lockout.board.task;

import me.hapyl.lockout.Locust;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.ComponentList;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public final class TaskIronMan extends TaskImpl implements Listener {
    
    private static final int MAX_SEARCH_RADIUS = 10;
    
    TaskIronMan(@NotNull Key key) {
        super(key, Icon.ofTexture("b69d0d4711153a089c5567a749b27879c769d3bdcea6fda9d6f66e93dd8c4512"), Component.text("Iron Man"), Component.text("Build an Iron Golem using Iron blocks and a Carved Pumpkin."));
    }
    
    @EventHandler
    public void handleCreatureSpawnEvent(CreatureSpawnEvent ev) {
        // FIXME (xanyjl @ Saturday, October 24) -> There is an achievement for this!
        if (ev.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM) {
            return;
        }
        
        if (!(ev.getEntity() instanceof IronGolem ironGolem) || !ironGolem.isPlayerCreated()) {
            return;
        }
        
        // Unfortunately, there doesn't seem to be a way to find the player who built the iron golem,
        // so just pick the closest player I guess?
        final Location location = ironGolem.getLocation();
        
        final Player closetPlayer = location.getNearbyEntities(MAX_SEARCH_RADIUS, MAX_SEARCH_RADIUS, MAX_SEARCH_RADIUS)
                                            .stream()
                                            .filter(Player.class::isInstance)
                                            .map(Player.class::cast)
                                            .min(Comparator.comparingDouble(player -> player.getLocation().distanceSquared(location)))
                                            .orElse(null);
        
        if (closetPlayer == null) {
            return;
        }
        
        this.progress(closetPlayer, this::computeProgress, progress -> {
            progress.builder = closetPlayer;
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskIronManProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskIronManProgress(task, playerTeam);
    }
    
    @Override
    public @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.HARD;
    }
    
    public static class TaskIronManProgress extends TaskProgress {
        
        private @Nullable Player builder;
        
        TaskIronManProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
            super(task, playerTeam);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return builder != null
                   ? ComponentList.builder(Component.text("Builder"))
                                  .append(Component.space().append(Locust.formatPlayerName(builder)))
                   : null;
        }
        
    }
    
}