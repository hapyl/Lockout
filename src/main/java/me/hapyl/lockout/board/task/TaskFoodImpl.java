package me.hapyl.lockout.board.task;

import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import me.hapyl.lockout.util.MonitorEventListener;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class TaskFoodImpl extends TaskImpl {
    
    TaskFoodImpl(@NotNull Key key, @NotNull Icon icon, @NotNull Component name, @NotNull Component description) {
        super(key, icon, name, description);
    }
    
    @Override
    public abstract @NotNull TaskProgress computeProgress(@NotNull PlayerTeam playerTeam);
    
    @Override
    public @NotNull TaskCategory getCategory() {
        return TaskCategory.FOOD;
    }
    
    static void listenToFoodLevelChangeEvent(@NotNull FoodLevelChangeEventHandler handler) {
        MonitorEventListener.listen(FoodLevelChangeEvent.class, ev -> {
            if (!(ev.getEntity() instanceof Player player)) {
                return;
            }
            
            if (!(ev.getItem() instanceof ItemStack itemStack)) {
                return;
            }
            
            handler.handle(itemStack.getType(), player);
        });
    }
    
    public interface FoodLevelChangeEventHandler {
        
        void handle(@NotNull Material food, @NotNull Player player);
        
    }
    
}