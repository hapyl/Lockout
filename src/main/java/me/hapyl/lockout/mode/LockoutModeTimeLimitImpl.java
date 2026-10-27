package me.hapyl.lockout.mode;

import me.hapyl.lockout.util.Tick;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class LockoutModeTimeLimitImpl extends LockoutModeImpl {
    
    LockoutModeTimeLimitImpl(@NotNull Tick timeLimit, @NotNull Tick gracePeriod) {
        super(Component.text("Time Limit (%s)".formatted(timeLimit.minutes())), timeLimit, gracePeriod);
    }
    
}