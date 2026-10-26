package me.hapyl.lockout.board.task;

import me.hapyl.lockout.Locust;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

public final class TaskTryNotToDieChallenge extends TaskImpl implements Listener {
    
    public static final int UNIQUE_WAYS_TO_DIE = 5;
    
    TaskTryNotToDieChallenge(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.SKELETON_SKULL), Component.text("Try Not to Die Challenge"), Component.text("Die in %s different ways.".formatted(UNIQUE_WAYS_TO_DIE)));
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handleEntityDeathEvent(EntityDeathEvent ev) {
        if (!(ev.getEntity() instanceof Player player)) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> progress.track(ev.getDamageSource().getDamageType(), player));
    }
    
    @Override
    public @NotNull TaskTryNotToDieChallengeProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskTryNotToDieChallengeProgress(task, playerTeam);
    }
    
    @Override
    public @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.HARD;
    }
    
    public static class TaskTryNotToDieChallengeProgress extends TaskProgressTrackerUnique<DamageType> {
        
        TaskTryNotToDieChallengeProgress(@NotNull Task task, @NotNull PlayerTeam team) {
            super(task, team);
        }
        
        @Override
        public @NotNull Component getProgressComponent() {
            return Component.text("Deaths");
        }
        
        @Override
        public int getGoal() {
            return UNIQUE_WAYS_TO_DIE;
        }
        
        @Override
        public @NotNull Component supplyComponent(@NotNull DamageType damageType) {
            return Locust.capitalizeStringAsComponent(damageType.getKey().getKey());
        }
        
    }
    
}