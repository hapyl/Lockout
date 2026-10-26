package me.hapyl.lockout.board.task;

import com.google.common.collect.Maps;
import me.hapyl.lockout.Colors;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.ComponentList;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class TaskProgressTracker<E> extends TaskProgress {
    
    public static final Component DEFAULT_PROGRESS_COMPONENT = Component.text("Progress", Colors.GRAY);
    
    protected final Map<E, Player> tracked;
    
    TaskProgressTracker(@NotNull Task task, @NotNull PlayerTeam team) {
        super(task, team);
        
        this.tracked = Maps.newLinkedHashMap();
    }
    
    public @NotNull ProgressAccumulator.Result track(@NotNull E e, @NotNull Player player) {
        if (tracked.containsKey(e)) {
            return ProgressAccumulator.Result.SKIP;
        }
        
        tracked.put(e, player);
        
        return isComplete() ? ProgressAccumulator.Result.COMPLETE : ProgressAccumulator.Result.PROGRESS;
    }
    
    public boolean isComplete() {
        return tracked.size() >= getGoal();
    }
    
    @Override
    public final int getProgress() {
        return tracked.size();
    }
    
    public @NotNull Component getProgressComponent() {
        return DEFAULT_PROGRESS_COMPONENT;
    }
    
    @Override
    public abstract int getGoal();
    
    @Override
    public abstract @Nullable ComponentList getComponents();
    
    public abstract @NotNull Component supplyComponent(@NotNull E e);
    
}