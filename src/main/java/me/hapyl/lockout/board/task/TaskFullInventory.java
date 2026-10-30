package me.hapyl.lockout.board.task;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import me.hapyl.lockout.Locust;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.ComponentList;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TaskFullInventory extends TaskImpl implements Listener {
    
    TaskFullInventory(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.CHEST_MINECART), Component.text("Full Inventory"), Component.text("Fully fill your inventory with items, leaving no empty slots (excluding equipment slots)."));
    }
    
    @EventHandler
    public void handlePlayerInventorySlotChangeEvent(PlayerInventorySlotChangeEvent ev) {
        final Player player = ev.getPlayer();
        
        // Check if player's inventory has an empty slot
        if (player.getInventory().firstEmpty() != -1) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> {
            progress.player = player;
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskFullInventoryProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskFullInventoryProgress(task, playerTeam);
    }
    
    public static class TaskFullInventoryProgress extends TaskProgress {
        
        private @Nullable Player player;
        
        TaskFullInventoryProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
            super(task, playerTeam);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return player != null
                   ? ComponentList.builder(Component.text("Filled By")).append(Component.space().append(Locust.formatPlayerName(player)))
                   : null;
        }
    }
    
}