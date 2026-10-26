package me.hapyl.lockout.board;

import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.task.Task;
import me.hapyl.lockout.board.task.TaskProgress;
import org.jetbrains.annotations.NotNull;

public interface ProgressConstructor<P extends TaskProgress> {
    
    @NotNull P construct(@NotNull Task task, @NotNull PlayerTeam team);
    
}
