package me.hapyl.lockout.board.task;

import me.hapyl.lockout.Colors;
import me.hapyl.lockout.Locust;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.util.ComponentList;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class TaskProgressTrackerUnique<E> extends TaskProgressTracker<E> {
    
    TaskProgressTrackerUnique(@NotNull Task task, @NotNull PlayerTeam team) {
        super(task, team);
    }
    
    @Override
    public @Nullable ComponentList getComponents() {
        final ComponentList builder = ComponentList.builder();
        
        builder.append(this.getProgressComponent());
        
        if (tracked.isEmpty()) {
            builder.append(Component.text(" None!", Colors.DARK_GRAY));
        }
        else {
            int index = 0;
            
            for (Map.Entry<E, Player> entry : tracked.entrySet()) {
                final E e = entry.getKey();
                final Player player = entry.getValue();
                
                final Component componentIndex = supplyIndex(index++);
                final Component componentObject = supplyComponent(e);
                
                builder.append(
                        Component.space()
                                 .append(componentIndex)
                                 .append(componentObject.color(Colors.GREEN))
                                 .appendSpace()
                                 .append(Locust.formatPlayerNameInBrackets(player))
                );
            }
        }
        
        return builder;
    }
    
    public @NotNull Component supplyIndex(int index) {
        return Component.text((index + 1) + ". ", Colors.WHITE);
    }
    
}
