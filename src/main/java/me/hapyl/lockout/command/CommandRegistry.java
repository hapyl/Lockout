package me.hapyl.lockout.command;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.hapyl.lockout.Main;
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
        });
    }
    
    private static void register(@NotNull ReloadableRegistrarEvent<Commands> event, @NotNull Supplier<LockoutCommand> supplier) {
        final LockoutCommand command = supplier.get();
        
        event.registrar().register(command.build(), command.description(), command.aliases());
    }
    
}