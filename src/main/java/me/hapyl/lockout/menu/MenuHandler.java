package me.hapyl.lockout.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public final class MenuHandler implements Listener {
    
    public MenuHandler() {
    }
    
    @EventHandler
    public void handleInventoryClickEvent(InventoryClickEvent ev) {
        if (!(ev.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        final Inventory inventory = ev.getInventory();
        final Menu menu = Menu.PLAYER_MENUS.get(player);
        
        if (menu == null || !menu.getInventory().equals(inventory)) {
            return;
        }
        
        menu.onClick0(player, ev.getSlot(), ev.getAction(), ev.getClick());
        
        // Always cancel clicks, menus aren't item interactable
        ev.setCancelled(true);
    }
    
}
