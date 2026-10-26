package me.hapyl.lockout.board.task;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TaskFullyStacked extends TaskImpl implements Listener {
    
    TaskFullyStacked(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.BUNDLE), Component.text("Fully Stacked"), Component.text("Get a full stack of any single item (excluding non-stackable items)."));
    }
    
    @EventHandler
    public void handlePlayerInventorySlotChangeEvent(PlayerInventorySlotChangeEvent ev) {
        final Player player = ev.getPlayer();
        final ItemStack newItemStack = ev.getNewItemStack();
        
        final int maxStackSize = newItemStack.getMaxStackSize();
        final int amount = newItemStack.getAmount();
        
        if (maxStackSize == 1 || amount < maxStackSize) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> {
            progress.material = PlayerTuple.create(player, newItemStack.getType());
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskFullyStackedProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskFullyStackedProgress(task, playerTeam);
    }
    
    public static class TaskFullyStackedProgress extends TaskProgress {
        
        private @Nullable PlayerTuple<Material> material;
        
        TaskFullyStackedProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
            super(task, playerTeam);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            if (material == null) {
                return null;
            }
            
            return ComponentList.builder().append(Component.text("Stacked Item")).append(material);
        }
        
    }
    
}