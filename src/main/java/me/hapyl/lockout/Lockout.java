package me.hapyl.lockout;

import me.hapyl.lockout.board.Board;
import me.hapyl.lockout.menu.MenuBoard;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.logging.Logger;

public final class Lockout implements Runnable {
    
    static Main PLUGIN;
    static Lockout LOCKOUT;
    
    private final Logger logger;
    private final Random random;
    private final World world;
    
    private @Nullable Board currentBoard;
    
    Lockout(@NotNull Main instance) {
        this.logger = instance.getLogger();
        this.random = new Random();
        this.world = Bukkit.getWorlds().getFirst();
        
        // Create a small world border for pre-game
        this.world.getWorldBorder().setSize(Board.WORLD_BORDER_SIZE_PREGAME);
    }
    
    @Override
    public void run() {
        // Tick board if it exists
        if (currentBoard != null) {
            currentBoard.run();
            
            // If board is complete, clear it
            final Board.State state = currentBoard.getState();
            
            if (state == Board.State.FINISHED) {
                currentBoard.onFinished();
                currentBoard = null;
            }
        }
    }
    
    public static @Nullable Board getBoard() {
        return LOCKOUT.currentBoard;
    }
    
    public static void setBoard(@Nullable Board board) {
        if (LOCKOUT.currentBoard != null) {
            LOCKOUT.currentBoard.onFinished();
            LOCKOUT.currentBoard = null;
        }
        
        LOCKOUT.currentBoard = board;
        
        // Forcefully open the board for all players
        if (board != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                new MenuBoard(player);
            }
        }
    }
    
    public static @NotNull Plugin plugin() {
        return PLUGIN;
    }
    
    public static @NotNull Random random() {
        return LOCKOUT.random;
    }
    
    public static @NotNull World world() {
        return LOCKOUT.world;
    }
    
    public static @NotNull Logger logger() {
        return LOCKOUT.logger;
    }
    
}