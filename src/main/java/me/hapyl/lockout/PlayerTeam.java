package me.hapyl.lockout;

import com.google.common.collect.Lists;
import me.hapyl.lockout.annotate.MustBePrimitive;
import me.hapyl.lockout.util.*;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public enum PlayerTeam implements Named, Styled, ForwardingAudience, ItemCreator, ComponentLike {
    
    RED(20, Icon.ofMaterial(Material.RED_BANNER), Colors.TEAM_RED),
    GREEN(30, Icon.ofMaterial(Material.GREEN_BANNER), Colors.TEAM_GREEN),
    BLUE(22, Icon.ofMaterial(Material.BLUE_BANNER), Colors.TEAM_BLUE),
    ORANGE(32, Icon.ofMaterial(Material.ORANGE_BANNER), Colors.TEAM_ORANGE),
    PURPLE(24, Icon.ofMaterial(Material.PURPLE_BANNER), Colors.TEAM_PURPLE);
    
    public static final int MAX_PLAYERS = 4;
    public static final @MustBePrimitive String STRING_FLAG = "\uD83C\uDFF4";
    
    private static final Component SEPARATOR = Component.text(", ", Colors.GRAY);
    
    private final int menuSlot;
    private final Icon menuIcon;
    private final Component name;
    private final Component component;
    private final Style style;
    private final List<Player> players;
    
    PlayerTeam(int menuSlot, @NotNull Icon menuIcon, @NotNull TextColor color) {
        this.menuSlot = menuSlot;
        this.menuIcon = menuIcon;
        this.name = Locust.capitalizeStringAsComponent(this.name());
        this.component = createComponent(name, color);
        this.style = Style.style(color);
        this.players = Lists.newArrayList();
    }
    
    public int getMenuSlot() {
        return menuSlot;
    }
    
    public @NotNull JoinResponse canJoin(@NotNull Player player) {
        if (players.contains(player)) {
            return JoinResponse.CANNOT_JOIN_ALREADY_IN_TEAM;
        }
        else if (players.size() >= MAX_PLAYERS) {
            return JoinResponse.CANNOT_JOIN_TEAM_FULL;
        }
        
        return JoinResponse.CAN_JOIN;
    }
    
    public void join(@NotNull Player player) {
        // Always remove the player from the existing team
        getPlayerTeam(player).ifPresent(previousTeam -> {
            previousTeam.players.remove(player);
        });
        
        players.add(player);
        
        // Notify player
        player.sendMessage(
                Component.empty()
                         .append(Component.text("Joined ", Colors.DEFAULT))
                         .append(component)
                         .append(Component.text(" team!", Colors.DEFAULT))
        );
    }
    
    public void leave(@NotNull Player player) {
        players.remove(player);
        
        // Notify player
        player.sendMessage(
                Component.empty()
                         .append(Component.text("Left ", Colors.DEFAULT))
                         .append(component)
                         .append(Component.text(" team!", Colors.DEFAULT))
        );
    }
    
    public @NotNull Stream<? extends Player> players() {
        return players.stream();
    }
    
    public int size() {
        return players.size();
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull Style getStyle() {
        return style;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return component;
    }
    
    public @NotNull Component asComponentWithPlayers() {
        return this.asComponent()
                   .appendSpace()
                   .append(Component.text("(", Colors.DARK_GRAY))
                   .append(players.stream().map(Locust::formatPlayerName).collect(Component.toComponent(SEPARATOR)))
                   .append(Component.text(")", Colors.DARK_GRAY));
    }
    
    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return players;
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder() {
        final ItemBuilder builder = menuIcon.createBuilder();
        
        builder.setName(name.style(style));
        
        // Show members
        builder.addLore();
        builder.addLore(Component.text("Members (%s/%s)".formatted(players.size(), MAX_PLAYERS)));
        
        for (int i = 0; i < MAX_PLAYERS; i++) {
            builder.addLore(
                    Component.empty()
                             .append(Component.text(" • ", Colors.DARK_GRAY))
                             .append(i >= players.size() ? Component.text("Empty!", Colors.DARK_GRAY) : Locust.formatPlayerName(players.get(i)))
            );
        }
        
        return builder;
    }
    
    public boolean isInTeam(@NotNull Player player) {
        return players.contains(player);
    }
    
    public boolean isEmpty() {
        return players.isEmpty();
    }
    
    public boolean isFull() {
        return players.size() >= MAX_PLAYERS;
    }
    
    public static @NotNull Optional<PlayerTeam> getPlayerTeam(@NotNull Player player) {
        for (PlayerTeam team : values()) {
            if (team.players.contains(player)) {
                return Optional.of(team);
            }
        }
        
        return Optional.empty();
    }
    
    public static @Nullable PlayerTeam getSmallestTeam() {
        PlayerTeam smallestTeam = null;
        int smallestTeamSize = Integer.MAX_VALUE;
        
        for (PlayerTeam playerTeam : values()) {
            if (playerTeam.size() < smallestTeamSize) {
                smallestTeam = playerTeam;
                smallestTeamSize = playerTeam.size();
            }
        }
        
        return smallestTeam;
    }
    
    public static @NotNull Stream<? extends PlayerTeam> nonEmptyTeams() {
        return Arrays.stream(values()).filter(Predicate.not(PlayerTeam::isEmpty));
    }
    
    private static @NotNull Component createComponent(@NotNull Component name, @NotNull TextColor color) {
        return Component.empty()
                        .append(Component.text(STRING_FLAG, color))
                        .appendSpace()
                        .append(name.color(color));
    }
    
    public enum JoinResponse {
        
        CANNOT_JOIN_ALREADY_IN_TEAM,
        CANNOT_JOIN_TEAM_FULL,
        CAN_JOIN
        
    }
    
}