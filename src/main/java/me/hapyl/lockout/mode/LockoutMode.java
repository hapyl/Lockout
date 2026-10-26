package me.hapyl.lockout.mode;

import me.hapyl.lockout.util.Named;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public interface LockoutMode extends Named {
    
    @NotNull Component getName();
    
    @Range(from = -1, to = Integer.MAX_VALUE) int timeLimit();
    
    static @NotNull LockoutMode timeLimit(@Range(from = 1, to = Integer.MAX_VALUE) int minutes) {
        return new LockoutModeTimeLimitImpl(minutes);
    }
    
}