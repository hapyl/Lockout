package me.hapyl.lockout.util;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface Described {
    
    @NotNull Component getDescription();
    
}
