package me.hapyl.lockout.util;

import org.jetbrains.annotations.NotNull;

public interface Tick {
    
    int ticks();
    
    int seconds();
    
    int minutes();
    
    static @NotNull Tick fromTicks(int ticks) {
        return new TickImpl(ticks);
    }
    
    static @NotNull Tick fromSeconds(int seconds) {
        return new TickImpl((int) (seconds * TickImpl.RATIO_TICKS_TO_SECONDS));
    }
    
    static @NotNull Tick fromMinutes(int minutes) {
        return new TickImpl((int) (minutes * TickImpl.RATIO_TICKS_TO_MINUTES));
    }
    
    class TickImpl implements Tick {
        
        private static final double RATIO_TICKS_TO_SECONDS = 20;
        private static final double RATIO_TICKS_TO_MINUTES = 1200;
        
        private final int ticks;
        
        TickImpl(int ticks) {
            this.ticks = ticks;
        }
        
        @Override
        public int ticks() {
            return ticks;
        }
        
        @Override
        public int seconds() {
            return (int) (ticks / RATIO_TICKS_TO_SECONDS);
        }
        
        @Override
        public int minutes() {
            return (int) (ticks / RATIO_TICKS_TO_MINUTES);
        }
        
    }
    
}
