package me.hapyl.lockout.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PlayerAction {
    
    void perform(@NotNull Player player);
    
}
