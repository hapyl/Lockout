package me.hapyl.lockout.board.task;

import me.hapyl.lockout.Colors;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TaskDeepPockets extends TaskImpl implements Listener {
    
    public static final ImmutableArray<? extends Material> DEEPSLATE_ORES = ImmutableArray.create(
            Material.DEEPSLATE_COAL_ORE,
            Material.DEEPSLATE_COPPER_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.DEEPSLATE_EMERALD_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.DEEPSLATE_LAPIS_ORE,
            Material.DEEPSLATE_REDSTONE_ORE
    );
    
    TaskDeepPockets(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.DEEPSLATE), Component.text("Deep Pockets"), Component.text("Mine any Deepslate ore."));
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handleBlockBreakEvent(BlockBreakEvent ev) {
        final Player player = ev.getPlayer();
        final Material material = ev.getBlock().getType();
        
        if (!DEEPSLATE_ORES.contains(material)) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> {
            progress.mined = PlayerTuple.create(player, material);
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskDeepPocketProgress computeProgress(@NotNull PlayerTeam playerTeam) {
        return new TaskDeepPocketProgress(playerTeam);
    }
    
    public static class TaskDeepPocketProgress extends TaskProgress {
        
        private @Nullable PlayerTuple<Material> mined;
        
        TaskDeepPocketProgress(@NotNull PlayerTeam team) {
            super(team);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return mined != null
                   ? ComponentList.builder(Component.text("Mined Ore", Colors.GRAY)).append(mined)
                   : null;
        }
    }
    
}