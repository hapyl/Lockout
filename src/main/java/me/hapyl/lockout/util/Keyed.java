package me.hapyl.lockout.util;

import org.jetbrains.annotations.NotNull;

public interface Keyed {
    
    @NotNull Key getKey();
    
    default boolean compare(@NotNull Keyed that) {
        return this.getKey().equals(that.getKey());
    }
    
}
