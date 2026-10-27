package me.hapyl.lockout.board.task;

import me.hapyl.lockout.Locust;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.ComponentList;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TaskOneMansTrash extends TaskImpl implements Listener {
    
    TaskOneMansTrash(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.COMPOSTER), Component.text("One Man's Trash"), Component.text("Completely fill a Composter with green items to get a Bone Meal."));
    }
    
    @EventHandler
    public void handleEntityInteractEvent(PlayerInteractEvent ev) {
        if (!(ev.getClickedBlock() instanceof Block block)) {
            return;
        }
        
        if (block.getType() != Material.COMPOSTER) {
            return;
        }
        
        if (!(block.getBlockData() instanceof Levelled levelled)) {
            return;
        }
        
        if (levelled.getLevel() < levelled.getMaximumLevel()) {
            return;
        }
        
        final Player player = ev.getPlayer();
        
        this.progress(player, this::computeProgress, progress -> {
            progress.player = player;
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskOneMansTrashProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskOneMansTrashProgress(task, playerTeam);
    }
    
    public static class TaskOneMansTrashProgress extends TaskProgress {
        
        private @Nullable Player player;
        
        TaskOneMansTrashProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
            super(task, playerTeam);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return player != null
                   ? ComponentList.builder(Component.text("Composted By"))
                                  .append(Component.space().append(Locust.formatPlayerName(player)))
                   : null;
        }
        
    }
    
}