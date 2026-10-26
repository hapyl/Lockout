package me.hapyl.lockout.mode;

import net.kyori.adventure.text.Component;

public class LockoutModeTimeLimitImpl extends LockoutModeImpl {
    
    private static final int MINUTES_TO_TICKS_RATIO = 1200;
    
    LockoutModeTimeLimitImpl(int minutes) {
        super(Component.text("Time Limit (%s)".formatted(minutes)), minutes * MINUTES_TO_TICKS_RATIO);
    }
    
}