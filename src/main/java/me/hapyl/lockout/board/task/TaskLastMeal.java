package me.hapyl.lockout.board.task;

import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.ImmutableArray;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class TaskLastMeal extends TaskFoodImpl {
    
    public static final ImmutableArray<Material> FOODS = ImmutableArray.create(
            Material.POISONOUS_POTATO,
            Material.SPIDER_EYE,
            Material.PUFFERFISH
    );
    
    TaskLastMeal(@NotNull Key key) {
        super(key, Icon.ofMultipleMaterials(FOODS), Component.text("Last Meal"), Component.text("Eat three kind of poisonous foods."));
        
        listenToFoodLevelChangeEvent((food, player) -> {
            if (FOODS.contains(food)) {
                this.progress(player, this::computeProgress, progress -> progress.track(food, player));
            }
        });
    }
    
    @Override
    public @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.HARD;
    }
    
    @Override
    public @NotNull TaskLastMealProgress computeProgress(@NotNull PlayerTeam playerTeam) {
        return new TaskLastMealProgress(playerTeam);
    }
    
    public static class TaskLastMealProgress extends TaskProgressTrackerChecklist<Material> {
        
        TaskLastMealProgress(@NotNull PlayerTeam team) {
            super(team, FOODS);
        }
        
        @Override
        public @NotNull Component getProgressComponent() {
            return Component.text("Risks Taken");
        }
        
        @Override
        public @NotNull Component supplyComponent(@NotNull Material material) {
            return Component.translatable(material);
        }
        
    }
    
}