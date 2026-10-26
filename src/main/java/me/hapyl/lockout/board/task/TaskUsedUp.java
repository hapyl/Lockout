package me.hapyl.lockout.board.task;

import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.ComponentList;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import me.hapyl.lockout.util.PlayerTuple;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TaskUsedUp extends TaskImpl implements Listener {
    
    TaskUsedUp(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.DAMAGED_ANVIL), Component.text("Used Up"), Component.text("Break any tool."));
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handlePlayerItemBreakEvent(PlayerItemBreakEvent ev) {
        final Player player = ev.getPlayer();
        
        this.progress(player, this::computeProgress, progress -> {
            progress.broken = PlayerTuple.create(player, ev.getBrokenItem().getType());
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskUsedUpProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskUsedUpProgress(task, playerTeam);
    }
    
    public static class TaskUsedUpProgress extends TaskProgress {
        
        private @Nullable PlayerTuple<Material> broken;
        
        TaskUsedUpProgress(@NotNull Task task, @NotNull PlayerTeam team) {
            super(task, team);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return broken != null
                   ? ComponentList.builder(Component.text("Broken Item")).append(broken)
                   : null;
        }
        
    }
    
}