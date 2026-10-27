package me.hapyl.lockout.util;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ItemCreator {
    
    @NotNull ItemBuilder createBuilder();
    
    default @NotNull ItemStack createItemStack() {
        return this.createBuilder().build();
    }
    
    default @NotNull Component createSprite() {
        return this.createBuilder().sprite();
    }
    
}
