package me.hapyl.lockout.command;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.hapyl.lockout.Colors;
import me.hapyl.lockout.Lockout;
import me.hapyl.lockout.Main;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.Board;
import me.hapyl.lockout.board.task.TaskPickupNamedItem;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CommandRegistry {
    
    private final Main main;
    
    public CommandRegistry(@NotNull Main main) {
        this.main = main;
        
        this.main.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            register(event, LockoutCommandWorld::new);
            register(event, LockoutCommandBoard::new);
            register(event, LockoutCommandTeam::new);
            
            register(event,
                     () -> () -> Commands.literal("itemtranslations")
                                         .executes(context -> {
                                             if (!(context.getSource().getSender() instanceof Player player)) {
                                                 return 0;
                                             }
                                             
                                             final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                                             
                                             if (itemInMainHand.isEmpty()) {
                                                 player.sendMessage(Component.text("You must be holding an item!", Colors.ERROR));
                                                 return 0;
                                             }
                                             
                                             final String materialName = TaskPickupNamedItem.ItemNames.getItemName(itemInMainHand.getType());
                                             
                                             player.sendMessage(Component.text("Held item name is `%s`!".formatted(materialName), Colors.SUCCESS));
                                             return 1;
                                         })
                                         .build()
            );
            
            register(
                    event,
                    () -> () -> Commands.literal("testplayerteaminfo")
                                        .executes(context -> {
                                            final Player player = context.getSource().getPlayerOrThrow();
                                            
                                            if (!(Lockout.getBoard() instanceof Board board)) {
                                                player.sendMessage(Component.text("There is no board!", Colors.ERROR));
                                                return 0;
                                            }
                                            
                                            player.sendMessage(board.playerTeamInfo(PlayerTeam.getPlayerTeam(player).orElseThrow()).asComponent());
                                            
                                            return 1;
                                        })
                                        .build()
            );
            
        });
    }
    
    private static void register(@NotNull ReloadableRegistrarEvent<Commands> event, @NotNull Supplier<LockoutCommand> supplier) {
        final LockoutCommand command = supplier.get();
        
        event.registrar().register(command.build(), command.description(), command.aliases());
    }
    
}