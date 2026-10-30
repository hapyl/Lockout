package me.hapyl.lockout.board.task;

import me.hapyl.lockout.Locust;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.event.PlayerSpyglassEntityEvent;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class TaskFieldSurvey extends TaskImpl implements Listener {
    
    public static final int NUMBER_OF_SURVEYS = 10;
    
    TaskFieldSurvey(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.SPYGLASS), Component.text("Field Survey"), Component.text("Look at %s unique living entities using a Spyglass.".formatted(Locust.numberToString(NUMBER_OF_SURVEYS))));
    }
    
    @EventHandler
    public void handlePlayerSpyglassEntityEvent(PlayerSpyglassEntityEvent ev) {
        final Player player = ev.getPlayer();
        
        // Do not allow non-living entities
        if (!(ev.getEntity() instanceof LivingEntity entity)) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> progress.track(entity.getType(), player));
    }
    
    @Override
    public @NotNull TaskFieldSurveyProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskFieldSurveyProgress(task, playerTeam);
    }
    
    @Override
    public @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.NORMAL;
    }
    
    public static class TaskFieldSurveyProgress extends TaskProgressTrackerUnique<EntityType> {
        
        TaskFieldSurveyProgress(@NotNull Task task, @NotNull PlayerTeam team) {
            super(task, team);
        }
        
        @Override
        public int getGoal() {
            return NUMBER_OF_SURVEYS;
        }
        
        @Override
        public @NotNull Component supplyComponent(@NotNull EntityType entityType) {
            return Component.translatable(entityType);
        }
        
    }
    
}