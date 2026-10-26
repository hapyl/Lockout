package me.hapyl.lockout.board.task;

import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.ComponentList;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import me.hapyl.lockout.util.PlayerTuple;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TaskTheAuthor extends TaskImpl implements Listener {
    
    TaskTheAuthor(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.WRITABLE_BOOK), Component.text("The Author"), Component.text("Write a masterpiece of a book and sign it."));
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handlePlayerEditBookEvent(PlayerEditBookEvent ev) {
        final Player player = ev.getPlayer();
        final BookMeta newBookMeta = ev.getNewBookMeta();
        final String title = newBookMeta.getTitle();
        
        if (title == null) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> {
            progress.signed = PlayerTuple.create(player, "`" + title + "`");
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.HARD;
    }
    
    @Override
    public @NotNull TaskTheAuthorProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskTheAuthorProgress(task, playerTeam);
    }
    
    public static class TaskTheAuthorProgress extends TaskProgress {
        
        private @Nullable PlayerTuple<String> signed;
        
        TaskTheAuthorProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
            super(task, playerTeam);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return signed != null
                   ? ComponentList.builder(Component.text("Signed Book"))
                                  .append(signed)
                   : null;
        }
    }
    
}