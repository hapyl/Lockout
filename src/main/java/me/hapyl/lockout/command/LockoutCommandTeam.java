package me.hapyl.lockout.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.hapyl.lockout.Colors;
import me.hapyl.lockout.Lockout;
import me.hapyl.lockout.menu.MenuTeamSelection;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class LockoutCommandTeam implements LockoutCommand {
    
    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("team")
                       .executes(context -> {
                           if (!(context.getSource().getSender() instanceof Player player)) {
                               return 0;
                           }
                           
                           if (Lockout.board() != null) {
                               player.sendMessage(Component.text("Cannot switch teams while in the game!", Colors.ERROR));
                               return 0;
                           }
                           
                           new MenuTeamSelection(player);
                           return 1;
                       })
                       .build();
    }
    
}