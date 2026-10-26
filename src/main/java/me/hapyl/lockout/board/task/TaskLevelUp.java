package me.hapyl.lockout.board.task;

import me.hapyl.lockout.Lockout;
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
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TaskLevelUp extends TaskImpl implements Listener {
    
    public static final int LEVEL_TO_GET = 10;
    
    TaskLevelUp(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.EXPERIENCE_BOTTLE), Component.text("LEVEL UP!"), Component.text("Level your experience to level %s.".formatted(LEVEL_TO_GET)));
    }
    
    @EventHandler
    public void handlePlayerLevelChangeEvent(PlayerLevelChangeEvent ev) {
        final Player player = ev.getPlayer();
        
        if (ev.getNewLevel() < LEVEL_TO_GET) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> {
            progress.player = player;
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskLevelUpProgress computeProgress(@NotNull PlayerTeam playerTeam) {
        return new TaskLevelUpProgress(playerTeam);
    }
    
    public static class TaskLevelUpProgress extends TaskProgress {
        
        private @Nullable Player player;
        
        TaskLevelUpProgress(@NotNull PlayerTeam playerTeam) {
            super(playerTeam);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return player != null
                   ? ComponentList.builder(Component.text("Experienced By"))
                                  .append(Component.space().append(Locust.formatPlayerName(player)))
                   : null;
        }
        
    }
    
}