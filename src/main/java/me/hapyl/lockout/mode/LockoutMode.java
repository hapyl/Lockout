package me.hapyl.lockout.mode;

import me.hapyl.lockout.util.Named;
import me.hapyl.lockout.util.Tick;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public interface LockoutMode extends Named {
    
    Tick DEFAULT_TIME_LIMIT = Tick.fromMinutes(30);
    Tick DEFAULT_GRACE_PERIOD = Tick.fromSeconds(30);
    
    @NotNull Component getName();
    
    @NotNull Tick timeLimit();
    
    @NotNull Tick gracePeriod();
    
    static @NotNull LockoutMode timeLimit(@NotNull Tick timeLimit, @NotNull Tick gracePeriod) {
        return new LockoutModeTimeLimitImpl(timeLimit, gracePeriod);
    }
    
    static @NotNull LockoutMode timeLimit(@NotNull Tick timeLimit) {
        return timeLimit(timeLimit, DEFAULT_GRACE_PERIOD);
    }
    
}