package me.hapyl.lockout;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.hapyl.lockout.board.Board;
import me.hapyl.lockout.util.InternalTasks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.regex.Pattern;

public final class ServerHandler implements Listener {
    
    private static final Pattern PATTERN_TEAM_MESSAGE = Pattern.compile("^#\\s*");
    
    private static final Component PREFIX_TEAM_CHAT = Component.text("[TEAM CHAT]", Colors.GOLD, TextDecoration.BOLD);
    
    private static final Style STYLE_MESSAGE_GLOBAL = Style.style(Colors.GRAY);
    private static final Style STYLE_MESSAGE_TEAM = Style.style(Colors.GRAY, TextDecoration.ITALIC);
    
    ServerHandler() {
    }
    
    @EventHandler
    public void handlePlayerJoinEvent(PlayerJoinEvent ev) {
        ev.joinMessage(null);
        
        final Player player = ev.getPlayer();
        final Board board = Lockout.board();
        
        final Component playerNameFormatted = Locust.formatPlayerName(player);
        
        // Handle in-game logic
        if (board != null) {
            final PlayerTeam existingTeam = PlayerTeam.getPlayerTeam(player).orElse(null);
            
            // If player do not have an existing team, it means they newly joined instead of re-joining the game, put the in a spectator
            if (existingTeam == null) {
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage(Component.text("You are currently spectating!", Colors.GREEN));
            }
            // Otherwise tell that player has rejoined
            else {
                ev.joinMessage(playerNameFormatted.append(Component.text(" has rejoined!", Colors.YELLOW)));
            }
            
            // Call update on the board
            board.onPlayerJoin(player);
        }
        // Handle pre-game logic
        else {
            // Find a team to join
            final PlayerTeam smallestTeam = PlayerTeam.getSmallestTeam();
            
            if (smallestTeam == null) {
                Locust.kickPlayer(player, Component.text("The server is full!"));
                return;
            }
            
            smallestTeam.join(player);
            
            // Set join message
            ev.joinMessage(playerNameFormatted.append(Component.text(" has joined!", Colors.YELLOW)));
        }
    }
    
    @EventHandler
    public void handlePlayerQuitEvent(PlayerQuitEvent ev) {
        ev.quitMessage(null);
        
        final Player player = ev.getPlayer();
        final Board board = Lockout.board();
        
        final Component playerNameFormatted = Locust.formatPlayerName(player);
        
        // Handle in-game logic
        if (board != null) {
            ev.quitMessage(playerNameFormatted.append(Component.text(" left; they may re-join and continue playing!", Colors.YELLOW)));
            
            board.onPlayerQuit(player);
        }
        // Handle pre-game logic
        else {
            // Set quit message before removing player from a team
            ev.quitMessage(playerNameFormatted.append(Component.text(" left!", Colors.YELLOW)));
            
            // Remove player from the team
            PlayerTeam.getPlayerTeam(player).ifPresent(playerTeam -> playerTeam.leave(player));
        }
    }
    
    @EventHandler
    public void handleAsyncChatEvent(AsyncChatEvent ev) {
        final Player player = ev.getPlayer();
        
        if (!(ev.message() instanceof TextComponent originalMessage)) {
            return;
        }
        
        // Always cancel the event
        ev.setCancelled(true);
        
        // Check for team message
        final boolean isTeamMessage = PATTERN_TEAM_MESSAGE.matcher(originalMessage.content()).find();
        final Component playerNameFormatted = Locust.formatPlayerName(player).append(Component.text(":", Colors.DARK_GRAY));
        
        final Component message;
        final Collection<? extends Player> recipients;
        
        if (isTeamMessage) {
            message = Component.empty().append(PREFIX_TEAM_CHAT).appendSpace().append(playerNameFormatted).appendSpace().append(originalMessage.replaceText(builder -> builder.match(PATTERN_TEAM_MESSAGE).replacement("")).style(STYLE_MESSAGE_TEAM));
            recipients = PlayerTeam.getPlayerTeam(player).orElse(PlayerTeam.RED).players().toList();
        }
        else {
            message = Component.empty().append(playerNameFormatted).appendSpace().append(originalMessage.style(STYLE_MESSAGE_GLOBAL));
            recipients = Bukkit.getOnlinePlayers();
        }
        
        // Send the message async
        InternalTasks.asynchronously(() -> {
            recipients.forEach(recipient -> recipient.sendMessage(message));
        });
        
    }
    
    @EventHandler
    public void handleEntityInteractEvent(EntityInteractEvent ev) {
        if (isPregame()) {
            ev.setCancelled(true);
        }
    }
    
    @EventHandler
    public void handleEntityDamageEvent(EntityDamageEvent ev) {
        if (isPregame()) {
            ev.setCancelled(true);
        }
    }
    
    @EventHandler
    public void handleBlockBreakEvent(BlockBreakEvent ev) {
        if (isPregame()) {
            ev.setCancelled(true);
        }
    }
    
    @EventHandler
    public void handleBlockPlaceEvent(BlockPlaceEvent ev) {
        if (isPregame()) {
            ev.setCancelled(true);
        }
    }
    
    @EventHandler
    public void handleFoodLevelChangeEvent(FoodLevelChangeEvent ev) {
        if (isPregame()) {
            ev.setCancelled(true);
        }
    }
    
    @EventHandler
    public void handlePlayerMoveEvent(PlayerMoveEvent ev) {
        // If the game is currently frozen, do not allow player movement
        if (Bukkit.getServerTickManager().isFrozen()) {
            
            // Don't cancel mouse movements
            if (!ev.hasChangedPosition()) {
                return;
            }
            
            ev.setCancelled(true);
        }
    }
    
    private static boolean isPregame() {
        final Board board = Lockout.board();
        
        return board == null || !board.hasStarted() || Bukkit.getServerTickManager().isFrozen();
    }
    
}