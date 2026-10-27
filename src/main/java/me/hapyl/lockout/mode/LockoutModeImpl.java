package me.hapyl.lockout.mode;

import me.hapyl.lockout.util.Tick;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class LockoutModeImpl implements LockoutMode {
    
    private final Component name;
    
    private final Tick timeLimit;
    private final Tick gracePeriod;
    
    LockoutModeImpl(@NotNull Component name, @NotNull Tick timeLimit, @NotNull Tick gracePeriod) {
        this.name = name;
        this.timeLimit = timeLimit;
        this.gracePeriod = gracePeriod;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull Tick timeLimit() {
        return timeLimit;
    }
    
    @Override
    public @NotNull Tick gracePeriod() {
        return gracePeriod;
    }
    
}