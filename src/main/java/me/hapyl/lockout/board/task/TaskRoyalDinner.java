package me.hapyl.lockout.board.task;

import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.ImmutableArray;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class TaskRoyalDinner extends TaskFoodImpl {
    
    public static final ImmutableArray<Material> FOODS = ImmutableArray.create(
            Material.GOLDEN_APPLE,
            Material.GOLDEN_CARROT
    );
    
    TaskRoyalDinner(@NotNull Key key) {
        super(key, Icon.ofMultipleMaterials(FOODS), Component.text("Royal Dinner"), Component.text("Eat a Golden Apple and a Golden Carrot."));
        
        listenToFoodLevelChangeEvent((food, player) -> {
            if (FOODS.contains(food)) {
                this.progress(player, this::computeProgress, progress -> progress.track(food, player));
            }
        });
    }
    
    @Override
    public @NotNull TaskRoyalDinnerProgress computeProgress(@NotNull PlayerTeam playerTeam) {
        return new TaskRoyalDinnerProgress(playerTeam);
    }
    
    @Override
    public @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.NORMAL;
    }
    
    public static class TaskRoyalDinnerProgress extends TaskProgressTrackerChecklist<Material> {
        
        TaskRoyalDinnerProgress(@NotNull PlayerTeam team) {
            super(team, FOODS);
        }
        
        @Override
        public @NotNull Component supplyComponent(@NotNull Material material) {
            return Component.translatable(material);
        }
        
    }
    
}