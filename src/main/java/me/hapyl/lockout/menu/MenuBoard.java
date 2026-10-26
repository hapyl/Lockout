package me.hapyl.lockout.menu;

import me.hapyl.lockout.Colors;
import me.hapyl.lockout.Lockout;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.Board;
import me.hapyl.lockout.board.BoardTask;
import me.hapyl.lockout.board.task.Task;
import me.hapyl.lockout.board.task.TaskProgress;
import me.hapyl.lockout.util.ComponentList;
import me.hapyl.lockout.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class MenuBoard extends Menu {
    
    public MenuBoard(@NotNull Player player) {
        super(player, Component.text("Lockout Board"), 5);
        
        this.openMenu();
    }
    
    @Override
    public void updateMenu() {
        fillColumn(1, Menu.BLACK_BAR);
        fillColumn(2, Menu.BLACK_BAR);
        fillColumn(8, Menu.BLACK_BAR);
        fillColumn(9, Menu.BLACK_BAR);
        
        final Board board = Lockout.getBoard();
        
        if (board == null) {
            return;
        }
        
        final List<? extends BoardTask> boardTasks = board.getTasks();
        final PlayerTeam playerTeam = PlayerTeam.getPlayerTeam(player).orElse(PlayerTeam.RED);
        
        int index = 0;
        
        for (BoardTask boardTask : boardTasks) {
            final TaskProgress taskProgress = boardTask.getProgress(playerTeam);
            final Task task = boardTask.getTask();
            
            final ItemBuilder builder = task.createBuilder();
            builder.addLore();
            
            TextColor nameColor = Colors.YELLOW;
            
            // Append completion status
            final PlayerTeam completionTeam = boardTask.getCompletionTeam();
            
            if (completionTeam != null) {
                if (completionTeam == playerTeam) {
                    nameColor = Colors.GREEN;
                    
                    builder.addLore(Component.text("COMPLETED!", Colors.GREEN, TextDecoration.BOLD));
                    builder.addLore(Component.text("Good job!", Colors.DARK_GREEN));
                }
                else {
                    nameColor = Colors.RED;
                    
                    builder.addLore(Component.text("LOCKED OUT!", Colors.RED, TextDecoration.BOLD));
                    builder.addLore(Component.text("By ", Colors.DARK_RED).append(completionTeam.asComponent()));
                }
                
                // Glow the item builder if completed, regardless which team compelte it
                builder.glow();
            }
            else {
                // Append progress
                builder.addLore(Component.text("PROGRESS", Colors.YELLOW, TextDecoration.BOLD));
                builder.addLore(taskProgress.asComponent());
            }
            
            // Append additional components
            final ComponentList components = taskProgress.getComponents();
            
            if (components != null) {
                builder.addLore();
                builder.addLore(components.asList());
            }
            
            // Set task name
            builder.setName(task.getName().color(nameColor));
            
            setItem(2 + (index % 5) + (index / 5 * 9), builder.build());
            index++;
        }
        
    }
    
}