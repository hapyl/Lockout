package me.hapyl.lockout;

import me.hapyl.lockout.board.Board;
import me.hapyl.lockout.menu.MenuBoard;
import me.hapyl.lockout.util.ComponentList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.Random;
import java.util.logging.Logger;

public final class Lockout implements Runnable {
    
    private static final TablistHandler DEFAULT_TABLIST_HANDLER = new TablistHandler() {
        @Override
        public void tablistFooter(@NotNull Player player, @NotNull ComponentList footer) {
            footer.append(Component.text("Waiting for the game to start...", Colors.YELLOW));
        }
    };
    
    private static final Component PLUGIN_NAME = Component.text("ʟᴏᴄᴋᴏᴜᴛ", Colors.BRAND_COLOR, TextDecoration.BOLD);
    
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
        // If the server is paused, don't run
        if (Bukkit.getServerTickManager().isFrozen()) {
            return;
        }
        
        if (currentBoard != null) {
            currentBoard.run();
            
            // If board is complete, clear it
            final Board.State state = currentBoard.getState();
            
            if (state == Board.State.FINISHED) {
                currentBoard.onFinished();
                currentBoard = null;
            }
        }
        
        if (Bukkit.getCurrentTick() % 5 == 0) {
            this.run5();
        }
    }
    
    private void run5() {
        final TablistHandler tablistHandler = currentBoard != null ? currentBoard : DEFAULT_TABLIST_HANDLER;
        final Component header = createTablistHeader();
        
        // Update player's tablist
        for (Player player : Bukkit.getOnlinePlayers()) {
            final ComponentList footer = ComponentList.builder().append();
            
            tablistHandler.tablistFooter(player, footer);
            
            // Always append meta at the end
            footer.append();
            footer.append(Component.text("www.totallyrealwebsite.com", Colors.YELLOW));
            
            player.sendPlayerListHeaderAndFooter(header, footer);
        }
    }
    
    public static @Nullable Board board() {
        return LOCKOUT.currentBoard;
    }
    
    public static void board(@Nullable Board board) {
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
    
    private static @NotNull Component createTablistHeader() {
        final TextComponent.Builder builder = Component.text();
        
        // Append plugin name
        builder.append(PLUGIN_NAME);
        builder.appendNewline();
        
        // Append date & version
        final LocalDate localDate = LocalDate.now();
        final String version = PLUGIN.getPluginMeta().getVersion().replace("-SNAPSHOT", "");
        
        builder.append(Component.text("%s/%s/%s  v%s".formatted(localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear(), version), Colors.DARK_GRAY));
        builder.appendNewline();
        
        return builder.build();
    }
    
}