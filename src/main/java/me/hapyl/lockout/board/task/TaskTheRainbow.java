package me.hapyl.lockout.board.task;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
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

public final class TaskTheRainbow extends TaskImpl implements Listener {
    
    public static final ImmutableArray<Material> DYES = ImmutableArray.create(
            Material.RED_DYE,
            Material.ORANGE_DYE,
            Material.YELLOW_DYE,
            Material.GREEN_DYE,
            Material.LIGHT_BLUE_DYE,
            Material.BLUE_DYE,
            Material.PURPLE_DYE
    );
    
    TaskTheRainbow(@NotNull Key key) {
        super(key, Icon.ofMultipleMaterials(DYES), Component.text("The Rainbow"), Component.text("Collect a dye in each color of the rainbow."));
    }
    
    @EventHandler
    public void handlePlayerInventorySlotChangeEvent(PlayerInventorySlotChangeEvent ev) {
        final Player player = ev.getPlayer();
        final Material material = ev.getNewItemStack().getType();
        
        if (!DYES.contains(material)) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> progress.track(material, player));
    }
    
    @Override
    public @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.HARD;
    }
    
    @Override
    public @NotNull TaskTheRainbowProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskTheRainbowProgress(task, playerTeam);
    }
 
    public static class TaskTheRainbowProgress extends TaskProgressTrackerChecklist<Material> {
        
        TaskTheRainbowProgress(@NotNull Task task, @NotNull PlayerTeam team) {
            super(task, team, DYES);
        }
        
        @Override
        public @NotNull Component supplyComponent(@NotNull Material material) {
            return Component.translatable(material);
        }
        
    }
    
}