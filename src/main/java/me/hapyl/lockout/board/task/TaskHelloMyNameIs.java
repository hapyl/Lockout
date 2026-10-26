package me.hapyl.lockout.board.task;

import io.papermc.paper.event.player.PlayerNameEntityEvent;
import me.hapyl.lockout.Colors;
import me.hapyl.lockout.Locust;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.ComponentList;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TaskHelloMyNameIs extends TaskImpl implements Listener {
    
    TaskHelloMyNameIs(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.NAME_TAG), Component.text("Hello, My Name Is"), Component.text("Craft or find a name tag and rename any mob."));
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handlePlayerNameEntityEvent(PlayerNameEntityEvent ev) {
        final Player player = ev.getPlayer();
        final Component name = ev.getName();
        
        if (name == null) {
            return;
        }
        
        final String nameAsString = PlainTextComponentSerializer.plainText().serialize(name);
        
        this.progress(player, this::computeProgress, progress -> {
            progress.stamped = new Stamped(player, ev.getEntity(), nameAsString);
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.HARD;
    }
    
    @Override
    public @NotNull TaskHelloMyNameIsProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskHelloMyNameIsProgress(task, playerTeam);
    }
    
    public static class TaskHelloMyNameIsProgress extends TaskProgress {
        
        private @Nullable Stamped stamped;
        
        TaskHelloMyNameIsProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
            super(task, playerTeam);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return stamped != null
                   ? ComponentList.builder(Component.text("Renamed"))
                                  .append(stamped)
                   : null;
        }
        
    }
    
    public static class Stamped implements ComponentLike {
        
        private final Component component;
        
        Stamped(@NotNull Player player, @NotNull Entity entity, @NotNull String newName) {
            this.component = Component.space()
                                      .append(Component.translatable(entity.getType(), Colors.RED))
                                      .append(Component.text(" -> "))
                                      .append(Component.text("`%s`".formatted(newName), Colors.GREEN))
                                      .appendSpace()
                                      .append(Locust.formatPlayerNameInBrackets(player));
        }
        
        @Override
        public @NotNull Component asComponent() {
            return component;
        }
    }
    
}