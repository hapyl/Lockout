package me.hapyl.lockout.board.task;

import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.util.EntityTags;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

public final class TaskMonsterSlayer extends TaskImpl implements Listener {
    
    public static final int NUMBER_OF_MONSTERS_TO_KILL = 5;
    
    TaskMonsterSlayer(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.ZOMBIE_HEAD), Component.text("Monster Slayer"), Component.text("Kill %s unique monsters.".formatted(NUMBER_OF_MONSTERS_TO_KILL)));
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handleEntityDeathEvent(EntityDeathEvent ev) {
        if (!(ev.getDamageSource().getCausingEntity() instanceof Player player)) {
            return;
        }
        
        final EntityType entityType = ev.getEntityType();
        
        if (!EntityTags.HOSTILE.isTagged(entityType)) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> progress.track(entityType, player));
    }
    
    @Override
    public @NotNull TaskMonsterSlayerProgress computeProgress(@NotNull PlayerTeam playerTeam) {
        return new TaskMonsterSlayerProgress(playerTeam);
    }
    
    public static class TaskMonsterSlayerProgress extends TaskProgressTrackerUnique<EntityType> {
        
        TaskMonsterSlayerProgress(@NotNull PlayerTeam team) {
            super(team);
        }
        
        @Override
        public @NotNull Component getProgressComponent() {
            return Component.text("Monsters Slain");
        }
        
        @Override
        public int getGoal() {
            return NUMBER_OF_MONSTERS_TO_KILL;
        }
        
        @Override
        public @NotNull Component supplyComponent(@NotNull EntityType entityType) {
            return Component.translatable(entityType);
        }
        
    }
    
}