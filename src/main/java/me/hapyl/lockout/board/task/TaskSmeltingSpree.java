package me.hapyl.lockout.board.task;

import io.papermc.paper.event.inventory.ItemCraftedEvent;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.ImmutableArray;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class TaskSmeltingSpree extends TaskImpl implements Listener {
    
    public static final ImmutableArray<Material> FURNACES = ImmutableArray.create(
            Material.FURNACE,
            Material.SMOKER,
            Material.BLAST_FURNACE
    );
    
    TaskSmeltingSpree(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.FURNACE), Component.text("Smelting Spree"), Component.text("Craft all three kinds of furnaces."));
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handleItemCraftedEvent(ItemCraftedEvent ev) {
        final ItemStack craftedItem = ev.getCraftedItem();
        final Material craftedItemType = craftedItem.getType();
        
        if (!FURNACES.contains(craftedItemType)) {
            return;
        }
        
        final Player player = ev.getPlayer();
        
        this.progress(player, TaskSmeltingSpreeProgress::new, progress -> progress.track(craftedItemType, player));
    }
    
    @Override
    public @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.NORMAL;
    }
    
    @Override
    public @NotNull TaskSmeltingSpreeProgress computeProgress(@NotNull PlayerTeam playerTeam) {
        return new TaskSmeltingSpreeProgress(playerTeam);
    }
    
    public static class TaskSmeltingSpreeProgress extends TaskProgressTrackerChecklist<Material> {
        
        TaskSmeltingSpreeProgress(@NotNull PlayerTeam team) {
            super(team, FURNACES);
        }
        
        @Override
        public @NotNull Component getProgressComponent() {
            return Component.text("Furnaces Crafted");
        }
        
        @Override
        public @NotNull Component supplyComponent(@NotNull Material material) {
            return Component.translatable(material);
        }
        
    }
    
}