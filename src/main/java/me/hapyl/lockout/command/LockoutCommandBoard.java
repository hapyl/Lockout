package me.hapyl.lockout.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.hapyl.lockout.Colors;
import me.hapyl.lockout.Lockout;
import me.hapyl.lockout.Locust;
import me.hapyl.lockout.board.Board;
import me.hapyl.lockout.board.task.Task;
import me.hapyl.lockout.board.task.Tasks;
import me.hapyl.lockout.menu.MenuBoard;
import me.hapyl.lockout.mode.LockoutMode;
import me.hapyl.lockout.util.Tick;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ServerTickManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class LockoutCommandBoard implements LockoutCommand {
    
    private static final List<String> ALIASES = List.of("b");
    private static final SimpleCommandExceptionType EXCEPTION_TYPE_NO_BOARD = new SimpleCommandExceptionType(() -> "Unable to execute this command in pre-game!");
    
    private static final List<? extends Task> TASKS_HARDCODED = List.of(
            Tasks.LEVEL_UP, Tasks.ROYAL_DINNER, Tasks.SOUPER_CHEF, Tasks.HAVE_A_SEAT, Tasks.ONE_MANS_TRASH,
            Tasks.MANNEQUIN, Tasks.TRY_NOT_TO_DIE_CHALLENGE, Tasks.DEEP_POCKETS, Tasks.LOCKED_AND_LOADED, Tasks.FULLY_STACKED,
            Tasks.PICKUP_NAMED_ITEM_B, Tasks.PICKUP_NAMED_ITEM_C, Tasks.PICKUP_NAMED_ITEM_G, Tasks.PICKUP_NAMED_ITEM_L, Tasks.PICKUP_NAMED_ITEM_P,
            Tasks.TOUCH_THE_SKY, Tasks.BUCKET_LIST, Tasks.THE_AUTHOR, Tasks.HELLO_MY_NAME_IS, Tasks.FLAG_WEAVER,
            Tasks.EGOISTIC, Tasks.IRON_MAN, Tasks.ARCHEOLOGIST, Tasks.TOMB_RAIDER, Tasks.THE_RAINBOW
    );
    
    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("board")
                       .executes(context -> {
                           final CommandSender sender = context.getSource().getSender();
                           final Board board = boardOrThrow();
                           
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
                                                                   
                                                                   Lockout.board(new Board(LockoutMode.timeLimit(LockoutMode.DEFAULT_TIME_LIMIT), Tasks.generateTasks()));
                                                               }
                                                               
                                                               case HARDCODED -> {
                                                                   sender.sendMessage(Component.text("Generating board with hardcoded tasks...", Colors.YELLOW));
                                                                   
                                                                   Lockout.board(new Board(LockoutMode.timeLimit(Tick.fromMinutes(6969), Tick.fromSeconds(3)), TASKS_HARDCODED));
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
                                           
                                           if (Lockout.board() == null) {
                                               sender.sendMessage(Component.text("No board currently exists!", Colors.RED));
                                               return 0;
                                           }
                                           
                                           sender.sendMessage(Component.text("Destroyed current board!", Colors.SUCCESS));
                                           Lockout.board(null);
                                           return 1;
                                       })
                       )
                       .then(
                               Commands.literal("pause")
                                       .requires(Requires.OPERATOR)
                                       .executes(context -> {
                                           boardOrThrow();
                                           
                                           final ServerTickManager serverTickManager = Bukkit.getServerTickManager();
                                           final boolean newValue = !serverTickManager.isFrozen();
                                           
                                           // Notify before freezing/unfreezing
                                           if (newValue) {
                                               Locust.everyone().showTitle(Title.title(Component.empty(), Component.text("ᴘᴀᴜꜱᴇᴅ", Colors.YELLOW, TextDecoration.BOLD), 0, 20, 0));
                                           }
                                           else {
                                               Locust.everyone().clearTitle();
                                           }
                                           
                                           context.getSource().getSender().sendMessage(newValue ? Component.text("Paused the game!", Colors.SUCCESS) : Component.text("Un-paused the game.", Colors.SUCCESS));
                                           serverTickManager.setFrozen(newValue);
                                           return 1;
                                       })
                       )
                       .build();
    }
    
    @Override
    public @NotNull @Unmodifiable List<String> aliases() {
        return ALIASES;
    }
    
    private static @NotNull Board boardOrThrow() throws CommandSyntaxException {
        if (Lockout.board() instanceof Board board) {
            return board;
        }
        
        throw EXCEPTION_TYPE_NO_BOARD.create();
    }
    
    public enum GeneratorType {
        
        NATURAL,
        HARDCODED
        
    }
    
}