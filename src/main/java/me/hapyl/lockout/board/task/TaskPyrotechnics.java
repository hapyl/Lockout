package me.hapyl.lockout.board.task;

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
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TaskPyrotechnics extends TaskImpl implements Listener {
    
    TaskPyrotechnics(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.FIREWORK_ROCKET), Component.text("Pyrotechnics"), Component.text("Shoot a Firework Rocket out of a Crossbow."));
    }
    
    @EventHandler
    public void handleEntityShootBowEvent(EntityShootBowEvent ev) {
        if (!(ev.getEntity() instanceof Player player)) {
            return;
        }
        
        if (!(ev.getBow() instanceof ItemStack bow) || bow.getType() != Material.CROSSBOW) {
            return;
        }
        
        if (!(ev.getConsumable() instanceof ItemStack consumable) || consumable.getType() != Material.FIREWORK_ROCKET) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> {
            progress.player = player;
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.NORMAL;
    }
    
    @Override
    public @NotNull TaskPyrotechnicsProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskPyrotechnicsProgress(task, playerTeam);
    }
    
    public static class TaskPyrotechnicsProgress extends TaskProgress {
        
        private @Nullable Player player;
        
        TaskPyrotechnicsProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
            super(task, playerTeam);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return player != null
                   ? ComponentList.builder(Component.text("Pyrotechnician"))
                                  .append(Component.space().append(Locust.formatPlayerName(player)))
                   : null;
        }
        
    }
    
    
}