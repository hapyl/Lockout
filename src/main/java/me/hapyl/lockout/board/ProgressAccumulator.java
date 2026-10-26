package me.hapyl.lockout.board;

import me.hapyl.lockout.board.task.TaskProgress;
import org.jetbrains.annotations.NotNull;

public interface ProgressAccumulator<P extends TaskProgress> {
    
    @NotNull Result accumulate(@NotNull P p);
    
    enum Result {
        
        /**
         * Results in accumulation of progress of the task.
         */
        PROGRESS,
        
        /**
         * Results in ignoring of progress; used for multi-goal tasks and when a goal is already fulfilled.
         */
        SKIP,
        
        /**
         * Results in completion of the task.
         */
        COMPLETE
        
    }
    
}
