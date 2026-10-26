package me.hapyl.lockout.board.task;

import me.hapyl.lockout.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;

public enum TaskDifficulty implements ComponentLike, MaximumNumberOfTasks {
    
    EASY("Easy", 1, 12),
    NORMAL("Normal", 2, 8),
    HARD("Hard", 3, 5);
    
    public static final Iterable<? extends TaskDifficulty> VALUES_IN_REVERSE_ORDER = Arrays.stream(values()).sorted(Comparator.reverseOrder()).toList();
    
    private final Component component;
    private final int points;
    private final int maximumNumberOfTasks;
    
    TaskDifficulty(@NotNull String string, int points, int maximumNumberOfTasks) {
        this.component = Component.text("%s (%s)".formatted(string, points), Colors.DARK_GRAY);
        this.points = points;
        this.maximumNumberOfTasks = maximumNumberOfTasks;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return component;
    }
    
    public int getPoints() {
        return points;
    }
    
    @Override
    public int getMaximumNumberOfTasks() {
        return maximumNumberOfTasks;
    }
    
}