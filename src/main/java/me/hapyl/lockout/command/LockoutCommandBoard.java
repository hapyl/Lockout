package me.hapyl.lockout.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.hapyl.lockout.Colors;
import me.hapyl.lockout.Lockout;
import me.hapyl.lockout.board.Board;
import me.hapyl.lockout.board.task.Tasks;
import me.hapyl.lockout.menu.MenuBoard;
import me.hapyl.lockout.mode.LockoutMode;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class LockoutCommandBoard implements LockoutCommand {
    
    private static final List<String> ALIASES = List.of("b");
    
    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("board")
                       .executes(context -> {
                           final CommandSender sender = context.getSource().getSender();
                           final Board board = Lockout.getBoard();
                           
                           if (board == null) {
                               sender.sendMessage(Component.text("There is currently no board!", Colors.RED));
                               return 0;
                           }
                           
                           if (!(sender instanceof Player player)) {
                               return 0;
                           }
                           
                           new MenuBoard(player);
                           return 1;
                       })
                       .then(
                               Commands.literal("generate")
                                       .requires(Requires.OPERATOR)
                                       .then(
                                               Commands.argument("type", Arguments.enums(GeneratorType.class))
                                                       .executes(context -> {
                                                           final CommandSender sender = context.getSource().getSender();
                                                           final GeneratorType generatorType = context.getArgument("type", GeneratorType.class);
                                                           
                                                           switch (generatorType) {
                                                               case NATURAL -> {
                                                                   sender.sendMessage(Component.text("Generating board naturally...", Colors.YELLOW));
                                                                   
                                                                   Lockout.setBoard(new Board(LockoutMode.timeLimit(Board.DEFAULT_TIME_LIMIT_IN_MINUTES), Tasks.generateTasks()));
                                                               }
                                                               
                                                               case HARDCODED -> {
                                                                   sender.sendMessage(Component.text("Generating board with hardcoded tasks...", Colors.YELLOW));
                                                                   
                                                                   Lockout.setBoard(new Board(
                                                                           LockoutMode.timeLimit(6969),
                                                                           List.of(
                                                                                   Tasks.LEVEL_UP, Tasks.ROYAL_DINNER, Tasks.SOUPER_CHEF, Tasks.HAVE_A_SEAT, Tasks.ACHIEVEMENT_HUNTER,
                                                                                   Tasks.MANNEQUIN, Tasks.TRY_NOT_TO_DIE_CHALLENGE, Tasks.DEEP_POCKETS, Tasks.LOCKED_AND_LOADED, Tasks.FULLY_STACKED,
                                                                                   Tasks.PICKUP_NAMED_ITEM_B, Tasks.PICKUP_NAMED_ITEM_C, Tasks.PICKUP_NAMED_ITEM_G, Tasks.PICKUP_NAMED_ITEM_L, Tasks.PICKUP_NAMED_ITEM_P,
                                                                                   Tasks.TOUCH_THE_SKY, Tasks.BUCKET_LIST, Tasks.THE_AUTHOR, Tasks.HELLO_MY_NAME_IS, Tasks.FLAG_WEAVER,
                                                                                   Tasks.EGOISTIC, Tasks.IRON_MAN, Tasks.ARCHEOLOGIST, Tasks.TOMB_RAIDER, Tasks.THE_RAINBOW
                                                                           )
                                                                   ));
                                                               }
                                                           }
                                                           
                                                           return 1;
                                                       })
                                       )
                       )
                       .then(
                               Commands.literal("destroy")
                                       .requires(Requires.OPERATOR)
                                       .executes(context -> {
                                           final CommandSender sender = context.getSource().getSender();
                                           final Board board = Lockout.getBoard();
                                           
                                           if (board == null) {
                                               sender.sendMessage(Component.text("No board currently exists!", Colors.RED));
                                               return 0;
                                           }
                                           
                                           sender.sendMessage(Component.text("Destroyed current board!", Colors.SUCCESS));
                                           Lockout.setBoard(null);
                                           return 1;
                                       })
                       )
                       .build();
    }
    
    @Override
    public @NotNull @Unmodifiable List<String> aliases() {
        return ALIASES;
    }
    
    public enum GeneratorType {
        
        NATURAL,
        HARDCODED
        
    }
    
}