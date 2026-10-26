package me.hapyl.lockout.board.task;

import me.hapyl.lockout.*;
import me.hapyl.lockout.util.ComponentList;
import me.hapyl.lockout.util.ImmutableArray;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TaskProgressTrackerChecklist<E> extends TaskProgressTracker<E> {
    
    private final ImmutableArray<E> checklist;
    
    TaskProgressTrackerChecklist(@NotNull PlayerTeam team, @NotNull ImmutableArray<E> checklist) {
        super(team);
        
        this.checklist = checklist;
    }
    
    @Override
    public int getGoal() {
        return checklist.length;
    }
    
    @Override
    public boolean isComplete() {
        for (E e : checklist) {
            if (!tracked.containsKey(e)) {
                return false;
            }
        }
        
        
        return true;
    }
    
    @Override
    public @Nullable ComponentList getComponents() {
        final ComponentList builder = ComponentList.builder();
        
        builder.append(this.getProgressComponent());
        
        for (E e : checklist) {
            final Player player = tracked.get(e);
            final Component componentObject = supplyComponent(e);
            
            builder.append(
                    Component.space()
                             .append(componentObject.color(Colors.WHITE))
                             .appendSpace()
                             .append(player != null ? Locust.CHECKMARK_TRUE.appendSpace().append(Locust.formatPlayerNameInBrackets(player)) : Locust.CHECKMARK_FALSE)
            );
        }
        
        return builder;
    }
    
}