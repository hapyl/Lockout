package me.hapyl.lockout.board.task;

import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import me.hapyl.lockout.util.MonitorEventListener;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class TaskGourmand extends TaskFoodImpl {
    
    public static final int FOODS_TO_EAT = 5;
    
    TaskGourmand(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.APPLE), Component.text("Gourmand"), Component.text("Eat %s unique foods.".formatted(FOODS_TO_EAT)));
        
        listenToFoodLevelChangeEvent((food, player) -> {
            this.progress(player, this::computeProgress, progress -> progress.track(food, player));
        });
    }
    
    @Override
    public @NotNull TaskProgressGourmand computeProgress(@NotNull PlayerTeam playerTeam) {
        return new TaskProgressGourmand(playerTeam);
    }
    
    public static class TaskProgressGourmand extends TaskProgressTrackerUnique<Material> {
        
        TaskProgressGourmand(@NotNull PlayerTeam team) {
            super(team);
        }
        
        @Override
        public @NotNull Component getProgressComponent() {
            return Component.text("Foods Eaten");
        }
        
        @Override
        public int getGoal() {
            return FOODS_TO_EAT;
        }
        
        @Override
        public @NotNull Component supplyComponent(@NotNull Material material) {
            return Component.translatable(material);
        }
        
    }
    
}
