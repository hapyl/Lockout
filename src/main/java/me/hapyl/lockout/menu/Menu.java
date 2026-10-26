package me.hapyl.lockout.menu;

import com.google.common.collect.Maps;
import me.hapyl.lockout.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.Map;

public abstract class Menu implements InventoryHolder {
    
    public static final ItemStack BLACK_BAR = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).hideTooltip().build();
    
    static final Map<Player, Menu> PLAYER_MENUS = Maps.newHashMap();
    
    public final Player player;
    
    private final Inventory inventory;
    private final int rows;
    private final Map<Integer, MenuAction> actions;
    
    public Menu(@NotNull Player player, @NotNull Component title, @Range(from = 1, to = 6) int rows) {
        this.player = player;
        this.rows = rows;
        this.inventory = Bukkit.createInventory(this, rows * 9, title);
        this.actions = Maps.newHashMap();
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getSize() {
        return inventory.getSize();
    }
    
    public @NotNull Player getPlayer() {
        return this.player;
    }
    
    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
    
    public abstract void updateMenu();
    
    public void setItem(int slot, @Nullable ItemStack itemStack, @Nullable MenuAction menuAction) {
        if (itemStack != null) {
            this.inventory.setItem(slot, itemStack);
        }
        
        if (menuAction != null) {
            this.actions.put(slot, menuAction);
        }
    }
    
    public void setItem(int slot, @NotNull ItemStack itemStack) {
        this.setItem(slot, itemStack, null);
    }
    
    public void setAction(int slot, @NotNull MenuAction menuAction) {
        this.setItem(slot, null, menuAction);
    }
    
    public void fillRow(@Range(from = 1, to = 6) int row, @NotNull ItemStack itemStack) {
        final int from = Math.max(0, 9 * row - 9);
        final int to = Math.min(this.getSize(), from + 9);
        
        for (int slot = from; slot < to; slot++) {
            this.setItem(slot, itemStack, null);
        }
    }
    
    public void fillColumn(@Range(from = 1, to = 9) int column, @NotNull ItemStack itemStack) {
        for (int row = 0; row < rows; row++) {
            final int slot = row * 9 + column - 1;
            
            this.setItem(slot, itemStack, null);
        }
    }
    
    public void openMenu() {
        // Clear the menu completely
        this.inventory.clear();
        this.actions.clear();
        
        // Call update method
        this.updateMenu();
        
        // Open the menu
        this.player.openInventory(this.inventory);
        
        PLAYER_MENUS.put(player, this);
    }
    
    public void closeMenu() {
        PLAYER_MENUS.remove(this.player);
        
        this.player.closeInventory();
    }
    
    public void broadcastUpdate() {
        final List<Menu> menusOfTheSameType = PLAYER_MENUS.values().stream().filter(menu -> this.getClass().isInstance(menu)).toList();
        
        menusOfTheSameType.forEach(Menu::openMenu);
    }
    
    public void onClick0(@NotNull Player player, int slot, @NotNull InventoryAction action, @NotNull ClickType clickType) {
        final MenuAction menuAction = this.actions.get(slot);
        
        if (menuAction == null) {
            return;
        }
        
        menuAction.perform(this, player, slot, action, clickType);
    }
    
}