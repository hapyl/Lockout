package me.hapyl.lockout;

import me.hapyl.lockout.util.ComponentList;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface TablistHandler {
    
    void tablistFooter(@NotNull Player player, @NotNull ComponentList footer);
    
}
