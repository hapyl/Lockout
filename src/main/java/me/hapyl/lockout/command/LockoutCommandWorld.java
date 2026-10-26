package me.hapyl.lockout.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import me.hapyl.lockout.Colors;
import me.hapyl.lockout.Lockout;
import me.hapyl.lockout.util.InternalTasks;
import me.hapyl.lockout.world.LockoutWorld;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public final class LockoutCommandWorld implements LockoutCommand {
    
    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("world")
                       .requires(context -> context.getSender().isOp())
                       .then(
                               Commands.literal("generate")
                                       .executes(context -> {
                                           final CommandSender sender = context.getSource().getSender();
                                           
                                           sender.sendMessage(Component.text("Generating world...", Colors.YELLOW));
                                           
                                           new LockoutWorld() {
                                               @Override
                                               public void onWorldGenerated(@NotNull World world) {
                                                   sender.sendMessage(Component.text("World generated!", Colors.GREEN));
                                                   
                                                   if (sender instanceof Player player) {
                                                       player.teleport(world.getSpawnLocation());
                                                   }
                                               }
                                           };
                                           
                                           return 1;
                                       })
                       )
                       .then(
                               Commands.literal("delete").then(
                                       Commands.argument("name", ArgumentTypes.world())
                                               .executes(context -> {
                                                   final CommandSender sender = context.getSource().getSender();
                                                   final World world = context.getArgument("name", World.class);
                                                   
                                                   if (world == null) {
                                                       sender.sendMessage(Component.text("Unknown world.", Colors.RED));
                                                       return 0;
                                                   }
                                                   
                                                   if (!LockoutWorld.isLockoutWorld(world)) {
                                                       sender.sendMessage(Component.text("Cannot delete non-lockout worlds!", Colors.RED));
                                                       return 0;
                                                   }
                                                   
                                                   sender.sendMessage(Component.text("Deleting world `%s`...".formatted(world.getName()), Colors.YELLOW));
                                                   
                                                   final World firstWorld = Bukkit.getWorlds().getFirst();
                                                   final Location location = firstWorld.getSpawnLocation();
                                                   
                                                   // Evacuate players
                                                   for (Player player : world.getPlayers()) {
                                                       player.teleport(location);
                                                   }
                                                   
                                                   final File worldFolder = world.getWorldFolder();
                                                   
                                                   Bukkit.unloadWorld(world, false);
                                                   
                                                   InternalTasks.asynchronously(() -> {
                                                       try (Stream<Path> walker = Files.walk(worldFolder.toPath())) {
                                                           walker.sorted(Comparator.reverseOrder()).forEach(path -> path.toFile().delete());
                                                           
                                                           sender.sendMessage(Component.text("Successfully delete the world!"));
                                                       }
                                                       catch (IOException ex) {
                                                           sender.sendMessage(
                                                                   Component.empty()
                                                                            .append(Component.text("Failed to delete the world!", Colors.RED))
                                                                            .append(Component.text(ex.getMessage(), Colors.DARK_RED))
                                                           );
                                                       }
                                                   });
                                                   
                                                   return 1;
                                               })
                               )
                       )
                       .build();
    }
    
}