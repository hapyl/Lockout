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
import org.jetbrains.annotations.NotNull;

public final class TaskLockedAndLoaded extends TaskImpl implements Listener {
    
    public static final ImmutableArray<Material> IRON_TOOLS = ImmutableArray.create(
            Material.IRON_SHOVEL,
            Material.IRON_PICKAXE,
            Material.IRON_AXE,
            Material.IRON_HOE,
            Material.IRON_SWORD,
            Material.IRON_SPEAR
    );
    
    TaskLockedAndLoaded(@NotNull Key key) {
        super(key, Icon.ofMultipleMaterials(IRON_TOOLS), Component.text("Locked & Loaded"), Component.text("Craft every kind of Iron tool."));
    }
    
    @EventHandler
    public void handleItemCraftedEvent(ItemCraftedEvent ev) {
        final Player player = ev.getPlayer();
        final Material material = ev.getCraftedItem().getType();
        
        if (!IRON_TOOLS.contains(material)) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> progress.track(material, player));
    }
    
    @Override
    public @NotNull TaskLockedAndLoadedProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskLockedAndLoadedProgress(task, playerTeam);
    }
    
    public static class TaskLockedAndLoadedProgress extends TaskProgressTrackerChecklist<Material> {
        
        TaskLockedAndLoadedProgress(@NotNull Task task, @NotNull PlayerTeam team) {
            super(task, team, IRON_TOOLS);
        }
        
        @Override
        public @NotNull Component getProgressComponent() {
            return Component.text("Crafted Tools");
        }
        
        @Override
        public @NotNull Component supplyComponent(@NotNull Material material) {
            return Component.translatable(material);
        }
        
    }
    
}
