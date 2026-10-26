package me.hapyl.lockout.board.task;

import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.ImmutableArray;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class TaskSouperChef extends TaskFoodImpl {
    
    public static final ImmutableArray<Material> SOUPS = ImmutableArray.create(
            Material.MUSHROOM_STEW,
            Material.RABBIT_STEW,
            Material.BEETROOT_SOUP
    );
    
    TaskSouperChef(@NotNull Key key) {
        super(key, Icon.ofMultipleMaterials(SOUPS), Component.text("Souper Chef"), Component.text("Eat three different kinds of soups or stews."));
        
        listenToFoodLevelChangeEvent((food, player) -> {
            if (SOUPS.contains(food)) {
                this.progress(player, this::computeProgress, progress -> progress.track(food, player));
            }
        });
    }
    
    @Override
    public @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.HARD;
    }
    
    @Override
    public @NotNull TaskSouperChefProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskSouperChefProgress(task, playerTeam);
    }
    
    public static class TaskSouperChefProgress extends TaskProgressTrackerChecklist<Material> {
        
        TaskSouperChefProgress(@NotNull Task task, @NotNull PlayerTeam team) {
            super(task, team, SOUPS);
        }
        
        @Override
        public @NotNull Component supplyComponent(@NotNull Material material) {
            return Component.translatable(material);
        }
        
    }
    
}