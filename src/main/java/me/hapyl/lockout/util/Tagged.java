package me.hapyl.lockout.util;

import org.jetbrains.annotations.NotNull;

public interface Tagged<E> {
    
    boolean isTagged(@NotNull E e);
    
}
