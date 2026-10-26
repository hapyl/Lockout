package me.hapyl.lockout.board;

import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.task.Task;
import me.hapyl.lockout.board.task.TaskProgress;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BoardTask implements ComponentLike {
    
    private final Task task;
    private final Map<PlayerTeam, TaskProgress> progressMap;
    
    private @Nullable PlayerTeam completionTeam;
    
    public BoardTask(@NotNull Task task, @NotNull List<? extends PlayerTeam> playerTeams) {
        this.task = task;
        this.progressMap = precomputeProgress(task, playerTeams.stream());
    }
    
    public @NotNull Task getTask() {
        return task;
    }
    
    public <P extends TaskProgress> @NotNull ProgressResponse progress(@NotNull PlayerTeam team, @NotNull ProgressConstructor<P> constructor, @NotNull ProgressAccumulator<P> accumulator) {
        // If the task is already completed, return
        if (this.completionTeam != null) {
            return this.completionTeam == team
                   ? ProgressResponse.FAILED_TO_PROGRESS_TASK_ALREADY_COMPLETE_BY_OWN_TEAM
                   : ProgressResponse.FAILED_TO_PROGRESS_TASK_ALREADY_COMPLETE_BY_OTHER_TEAM;
        }
        
        final P progress = getProgress0(team, constructor);
        final ProgressAccumulator.Result result = accumulator.accumulate(progress);
        
        return switch (result) {
            case PROGRESS -> ProgressResponse.SUCCESSFULLY_PROGRESSED;
            case SKIP -> ProgressResponse.FAILED_TO_PROGRESS_SKIPPED;
            case COMPLETE -> {
                this.completionTeam = team;
                yield ProgressResponse.SUCCESSFULLY_COMPLETED;
            }
        };
    }
    
    public @NotNull TaskProgress getProgress(@NotNull PlayerTeam playerTeam) {
        return getProgress0(playerTeam, task::computeProgress);
    }
    
    public @Nullable PlayerTeam getCompletionTeam() {
        return completionTeam;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return Component.empty()
                        .append(task.getName())
                        .hoverEvent(HoverEvent.showText(task.getDescription()));
    }
    
    @SuppressWarnings("unchecked")
    private <P extends TaskProgress> @NotNull P getProgress0(@NotNull PlayerTeam playerTeam, @NotNull ProgressConstructor<P> constructor) {
        return (P) progressMap.computeIfAbsent(playerTeam, _ -> constructor.construct(task, playerTeam));
    }
    
    private static @NotNull Map<PlayerTeam, TaskProgress> precomputeProgress(@NotNull Task task, @NotNull Stream<? extends PlayerTeam> playerTeams) {
        return playerTeams.collect(Collectors.toMap(Function.identity(), playerTeam -> task.computeProgress(task, playerTeam)));
    }
    
    public enum ProgressResponse {
        
        FAILED_TO_PROGRESS_TASK_ALREADY_COMPLETE_BY_OWN_TEAM,
        FAILED_TO_PROGRESS_TASK_ALREADY_COMPLETE_BY_OTHER_TEAM,
        FAILED_TO_PROGRESS_SKIPPED,
        SUCCESSFULLY_PROGRESSED,
        SUCCESSFULLY_COMPLETED
        
    }
    
}
