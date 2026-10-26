package me.hapyl.lockout.board.task;

import io.papermc.paper.event.inventory.ItemCraftedEvent;
import me.hapyl.lockout.Locust;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.ComponentList;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TaskTheLie extends TaskImpl implements Listener {
    
    TaskTheLie(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.CAKE), Component.text("The Lie"), Component.text("Craft a Cake."));
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handleItemCraftedEvent(ItemCraftedEvent ev) {
        if (ev.getCraftedItem().getType() != Material.CAKE) {
            return;
        }
        
        final Player player = ev.getPlayer();
        
        this.progress(player, this::computeProgress, progress -> {
            progress.whoCrafted = player;
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.NORMAL;
    }
    
    @Override
    public @NotNull TaskProgressTheLie computeProgress(@NotNull PlayerTeam playerTeam) {
        return new TaskProgressTheLie(playerTeam);
    }
    
    public static class TaskProgressTheLie extends TaskProgress {
        
        private @Nullable Player whoCrafted;
        
        TaskProgressTheLie(@NotNull PlayerTeam team) {
            super(team);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return whoCrafted != null
                   ? ComponentList.builder(Component.text("Crafted By")).append(Component.space().append(Locust.formatPlayerName(whoCrafted)))
                   : null;
        }
        
    }
    
}