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
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TaskQuietPlace extends TaskImpl implements Listener {
    
    TaskQuietPlace(@NotNull Key key) {
        super(
                key,
                Icon.ofMaterial(Material.SCULK_SHRIEKER),
                Component.text("Quiet Place"),
                Component.text("Get Darkness effect by activating a Sculk Shrieker in the Ancient City.")
        );
    }
    
    @EventHandler
    public void handleEntityPotionEffectEvent(EntityPotionEffectEvent ev) {
        if (!(ev.getEntity() instanceof Player player)) {
            return;
        }
        
        if (ev.getAction() != EntityPotionEffectEvent.Action.ADDED) {
            return;
        }
        
        if (!(ev.getNewEffect() instanceof PotionEffect potionEffect) || potionEffect.getType() != PotionEffectType.DARKNESS) {
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
    public @NotNull TaskQuietPlaceProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskQuietPlaceProgress(task, playerTeam);
    }
    
    public static class TaskQuietPlaceProgress extends TaskProgress {
        
        private @Nullable Player player;
        
        TaskQuietPlaceProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
            super(task, playerTeam);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return player != null
                   ? ComponentList.builder(Component.text("Disturber"))
                                  .append(Component.space().append(Locust.formatPlayerName(player)))
                   : null;
        }
    }
    
}