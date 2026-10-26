package me.hapyl.lockout.board.task;

import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BrushableBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class TaskArcheologist extends TaskImpl implements Listener {
    
    TaskArcheologist(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.BRUSH), Component.text("Archeologist"), Component.text("Find a Pottery Sherd inside a Suspicious block."));
    }
    
    @EventHandler
    public void handleBlockDropItemEvent(BlockDropItemEvent ev) {
        final Block block = ev.getBlock();
        
        if (!(block.getState() instanceof BrushableBlock)) {
            return;
        }
        
        final List<Item> items = ev.getItems();
        
        if (items.isEmpty()) {
            return;
        }
        
        final Material material = items.getFirst().getItemStack().getType();
        
        if (!Tag.ITEMS_DECORATED_POT_SHERDS.isTagged(material)) {
            return;
        }
        
        final Player player = ev.getPlayer();
        
        this.progress(player, this::computeProgress, progress -> {
            progress.sherd = PlayerTuple.create(player, material);
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.HARD;
    }
    
    @Override
    public @NotNull TaskArcheologistProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskArcheologistProgress(task, playerTeam);
    }
    
    public static class TaskArcheologistProgress extends TaskProgress {
        
        private @Nullable PlayerTuple<Material> sherd;
        
        TaskArcheologistProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
            super(task, playerTeam);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return sherd != null
                   ? ComponentList.builder(Component.text("Found Sherd"))
                                  .append(sherd)
                   : null;
        }
        
    }
    
}