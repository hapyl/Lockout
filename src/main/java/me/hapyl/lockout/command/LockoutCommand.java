package me.hapyl.lockout.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.hapyl.lockout.Lockout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.Predicate;

public interface LockoutCommand {
    
    @NotNull LiteralCommandNode<CommandSourceStack> build();
    
    default @Nullable String description() {
        return null;
    }
    
    default @NotNull @Unmodifiable List<String> aliases() {
        return List.of();
    }
    
    class Requires {
        
        public static final Predicate<CommandSourceStack> OPERATOR = context -> context.getSender().isOp();
        public static final Predicate<CommandSourceStack> NO_BOARD = _ -> Lockout.board() == null;
        
        @SafeVarargs
        public static @NotNull Predicate<CommandSourceStack> allOf(@NotNull Predicate<CommandSourceStack>... many) {
            return context -> {
                for (Predicate<CommandSourceStack> predicate : many) {
                    if (!predicate.test(context)) {
                        return false;
                    }
                }
                
                return true;
            };
        }
        
    }
    
}