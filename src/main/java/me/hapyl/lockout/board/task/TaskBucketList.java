package me.hapyl.lockout.board.task;

import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.ImmutableArray;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class TaskBucketList extends TaskImpl implements Listener {
    
    public static final ImmutableArray<Material> BUCKETS = ImmutableArray.create(
            Material.WATER_BUCKET,
            Material.LAVA_BUCKET,
            Material.MILK_BUCKET
    );
    
    TaskBucketList(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.BUCKET), Component.text("Bucket List"), Component.text("Fill three buckets with water, lava, and milk."));
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handlePlayerBucketFillEvent(PlayerBucketFillEvent ev) {
        final ItemStack bucket = ev.getItemStack();
        
        if (bucket == null) {
            return;
        }
        
        final Player player = ev.getPlayer();
        final Material bucketType = bucket.getType();
        
        if (!BUCKETS.contains(bucketType)) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> progress.track(bucketType, player));
    }
    
    @Override
    public @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.NORMAL;
    }
    
    @Override
    public @NotNull TaskBucketListProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskBucketListProgress(task, playerTeam);
    }
    
    public static class TaskBucketListProgress extends TaskProgressTrackerChecklist<Material> {
        
        TaskBucketListProgress(@NotNull Task task, @NotNull PlayerTeam team) {
            super(task, team, BUCKETS);
        }
        
        @Override
        public @NotNull Component supplyComponent(@NotNull Material material) {
            return Component.translatable(material);
        }
        
    }
    
}