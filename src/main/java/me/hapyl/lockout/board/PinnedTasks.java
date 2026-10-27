package me.hapyl.lockout.board;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class PinnedTasks {
    
    public static final int PIN_LIMIT = 5;
    
    private final Player player;
    private final Board board;
    private final List<BoardTask> tasks;
    
    PinnedTasks(@NotNull Player player, @NotNull Board board) {
        this.player = player;
        this.board = board;
        this.tasks = Lists.newArrayList();
    }
    
    public @NotNull List<? extends BoardTask> getTasks() {
        return tasks;
    }
    
    public boolean isPinned(@NotNull BoardTask task) {
        return tasks.contains(task);
    }
    
    public boolean pin(@NotNull BoardTask task) {
        if (tasks.size() >= PIN_LIMIT || tasks.remove(task)) {
            return false;
        }
        
        tasks.add(task);
        return true;
    }
    
}