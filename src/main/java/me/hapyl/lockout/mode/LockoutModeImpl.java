package me.hapyl.lockout.mode;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class LockoutModeImpl implements LockoutMode {
    
    private final Component name;
    private final int timeLimit;
    
    LockoutModeImpl(Component name, int timeLimit) {
        this.name = name;
        this.timeLimit = timeLimit;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @Range(from = 1, to = Integer.MAX_VALUE) int timeLimit() {
        return timeLimit;
    }
}
