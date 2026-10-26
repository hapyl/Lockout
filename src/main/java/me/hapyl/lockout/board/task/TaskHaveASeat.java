package me.hapyl.lockout.board.task;

import me.hapyl.lockout.Locust;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.ComponentList;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Cushion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TaskHaveASeat extends TaskImpl implements Listener {
    
    TaskHaveASeat(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.WHITE_CUSHION), Component.text("Have a Seat"), Component.text("Sit on a Cushion of any color."));
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handleEntityMountEvent(EntityMountEvent ev) {
        if (!(ev.getEntity() instanceof Player player)) {
            return;
        }
        
        if (!(ev.getMount() instanceof Cushion)) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> {
            progress.player = player;
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskHaveASeatProgress computeProgress(@NotNull PlayerTeam playerTeam) {
        return new TaskHaveASeatProgress(playerTeam);
    }
    
    public static class TaskHaveASeatProgress extends TaskProgress {
        
        private @Nullable Player player;
        
        TaskHaveASeatProgress(@NotNull PlayerTeam playerTeam) {
            super(playerTeam);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return player != null
                   ? ComponentList.builder(Component.text("Chill Guy"))
                                  .append(Component.space().append(Locust.formatPlayerName(player)))
                   : null;
        }
        
    }
    
}