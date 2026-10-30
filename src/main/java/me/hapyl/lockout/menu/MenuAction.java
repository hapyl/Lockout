package me.hapyl.lockout.menu;

import com.google.common.collect.Maps;
import me.hapyl.lockout.util.PlayerAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface MenuAction {
    
    void perform(@NotNull Menu menu, @NotNull Player player, int slot, @NotNull InventoryAction action, @NotNull ClickType clickType);
    
    static @NotNull MenuAction action(@NotNull PlayerAction playerAction) {
        return (menu, player, slot, action, click) -> playerAction.perform(player);
    }
    
    static @NotNull MenuAction.Builder builder() {
        return new Builder();
    }
    
    class Builder implements MenuAction {
        
        private final Map<ClickType, PlayerAction> actionsMapped;
        
        Builder() {
            this.actionsMapped = Maps.newEnumMap(ClickType.class);
        }
        
        public @NotNull Builder left(@NotNull PlayerAction playerAction) {
            return mapAction(ClickType.LEFT, playerAction);
        }
        
        public @NotNull Builder right(@NotNull PlayerAction playerAction) {
            return mapAction(ClickType.RIGHT, playerAction);
        }
        
        public @NotNull Builder shiftLeft(@NotNull PlayerAction playerAction) {
            return mapAction(ClickType.SHIFT_LEFT, playerAction);
        }
        
        public @NotNull Builder shiftRight(@NotNull PlayerAction playerAction) {
            return mapAction(ClickType.SHIFT_RIGHT, playerAction);
        }
        
        @Override
        public void perform(@NotNull Menu menu, @NotNull Player player, int slot, @NotNull InventoryAction action, @NotNull ClickType clickType) {
            final PlayerAction playerAction = actionsMapped.get(clickType);
            
            if (playerAction != null) {
                playerAction.perform(player);
            }
        }
        
        private @NotNull Builder mapAction(@NotNull ClickType clickType, @NotNull PlayerAction playerAction) {
            this.actionsMapped.put(clickType, playerAction);
            return this;
        }
        
    }
    
}