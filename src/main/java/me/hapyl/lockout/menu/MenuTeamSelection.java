package me.hapyl.lockout.menu;

import me.hapyl.lockout.Colors;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class MenuTeamSelection extends Menu {
    
    public MenuTeamSelection(@NotNull Player player) {
        super(player, Component.text("Select Team"), 6);
        
        this.openMenu();
    }
    
    @Override
    public void updateMenu() {
        fillRow(1, Menu.BLACK_BAR);
        fillRow(6, Menu.BLACK_BAR);
        
        for (PlayerTeam playerTeam : PlayerTeam.values()) {
            final ItemBuilder builder = playerTeam.createBuilder();
            
            builder.addLore();
            
            if (playerTeam.isInTeam(player)) {
                builder.addLore(Component.text("Already in this team!", Colors.RED));
            }
            else {
                if (playerTeam.isFull()) {
                    builder.addLore(Component.text("Team is full!", Colors.DARK_RED));
                }
                else {
                    builder.addLore(ButtonComponents.left("join"));
                }
            }
            
            setItem(
                    playerTeam.getMenuSlot(),
                    builder.build(),
                    MenuAction.action(player -> {
                        final PlayerTeam.JoinResponse joinResponse = playerTeam.canJoin(player);
                        
                        switch (joinResponse) {
                            case CANNOT_JOIN_ALREADY_IN_TEAM -> player.sendMessage(Component.text("You are already in this team!", Colors.ERROR));
                            case CANNOT_JOIN_TEAM_FULL -> player.sendMessage(Component.text("This team is full!", Colors.ERROR));
                            case CAN_JOIN -> playerTeam.join(player);
                        }
                        
                        this.broadcastUpdate();
                    })
            );
        }
        
    }
    
}