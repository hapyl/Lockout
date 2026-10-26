package me.hapyl.lockout.board.task;

import me.hapyl.lockout.*;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.ComponentList;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TaskTouchTheSky extends TaskImpl implements Listener {
    
    TaskTouchTheSky(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.WIND_CHARGE), Component.text("Touch the Sky"), Component.text("Reach the world height limit."));
    }
    
    @Override
    public @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.NORMAL;
    }
    
    @EventHandler
    public void handlePlayerMoveEvent(PlayerMoveEvent ev) {
        final Player player = ev.getPlayer();
        final Location from = ev.getFrom();
        final Location to = ev.getTo();
        
        if (to.getY() < from.getY() || to.getY() < to.getWorld().getMaxHeight()) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> {
            progress.sky = new Sky(player, to.getBlockX(), to.getBlockY(), to.getBlockZ());
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskTouchTheSkyProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskTouchTheSkyProgress(task, playerTeam);
    }
    
    public static class TaskTouchTheSkyProgress extends TaskProgress {
        
        private @Nullable Sky sky;
        
        TaskTouchTheSkyProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
            super(task, playerTeam);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return sky != null
                   ? ComponentList.builder(Component.text("Reached At")).append(sky)
                   : null;
        }
        
    }
    
    public static class Sky implements ComponentLike {
        
        private final Component component;
        
        Sky(@NotNull Player player, int x, int y, int z) {
            this.component = Component.space()
                                      .append(Component.text(x, Colors.RED))
                                      .append(Component.text(", "))
                                      .append(Component.text(y, Colors.GREEN))
                                      .append(Component.text(", "))
                                      .append(Component.text(z, Colors.AQUA))
                                      .appendSpace()
                                      .append(Locust.formatPlayerNameInBrackets(player));
        }
        
        @Override
        public @NotNull Component asComponent() {
            return component;
        }
        
    }
    
}