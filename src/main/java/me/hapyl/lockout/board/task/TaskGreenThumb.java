package me.hapyl.lockout.board.task;

import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TaskGreenThumb extends TaskImpl implements Listener {
    
    public static final ImmutableArray<Material> SAPLINGS = ImmutableArray.create(
            Material.OAK_SAPLING,
            Material.SPRUCE_SAPLING,
            Material.BIRCH_SAPLING,
            Material.JUNGLE_SAPLING,
            Material.ACACIA_SAPLING,
            Material.DARK_OAK_SAPLING,
            Material.MANGROVE_PROPAGULE,
            Material.CHERRY_SAPLING,
            Material.PALE_OAK_SAPLING,
            Material.POPLAR_SAPLING
    );
    
    TaskGreenThumb(@NotNull Key key) {
        super(key, Icon.ofMultipleMaterials(SAPLINGS), Component.text("Green Thumb"), Component.text("Place a tree sapling of any kind."));
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handleBlockPlaceEvent(BlockPlaceEvent ev) {
        final Player player = ev.getPlayer();
        final Material material = ev.getBlock().getType();
        
        if (!SAPLINGS.contains(material)) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> {
            progress.planter = PlayerTuple.create(player, material);
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskGreenThumbProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskGreenThumbProgress(task, playerTeam);
    }
    
    public static class TaskGreenThumbProgress extends TaskProgress {
        
        private @Nullable PlayerTuple<Material> planter;
        
        TaskGreenThumbProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
            super(task, playerTeam);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return planter != null
                   ? ComponentList.builder(Component.text("Planter"))
                                  .append(planter)
                   : null;
        }
        
    }
    
}