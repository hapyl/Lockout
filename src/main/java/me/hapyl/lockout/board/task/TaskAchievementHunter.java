package me.hapyl.lockout.board.task;

import me.hapyl.lockout.Locust;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.jetbrains.annotations.NotNull;

public final class TaskAchievementHunter extends TaskImpl implements Listener {
    
    public static final int NUMBER_OF_ACHIEVEMENTS_TO_GET = 5;
    
    private static final String RECIPE_FILTER = "recipes/";
    
    TaskAchievementHunter(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.MOJANG_BANNER_PATTERN), Component.text("Achievement Hunter"), Component.text("Complete %s unique Achievements.".formatted(Locust.numberToString(NUMBER_OF_ACHIEVEMENTS_TO_GET))));
    }
    
    @EventHandler
    public void handlePlayerAdvancementDoneEvent(PlayerAdvancementDoneEvent ev) {
        final Player player = ev.getPlayer();
        final Advancement advancement = ev.getAdvancement();
        final NamespacedKey advancementKey = advancement.getKey();
        
        // Recipes count as fucking advancements for some fucking reason, filter them
        if (advancementKey.getKey().startsWith(RECIPE_FILTER)) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> progress.track(advancementKey, player));
    }
    
    @Override
    public @NotNull TaskAchievementHunterProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskAchievementHunterProgress(task, playerTeam);
    }
    
    public static class TaskAchievementHunterProgress extends TaskProgressTrackerUnique<NamespacedKey> {
        
        TaskAchievementHunterProgress(@NotNull Task task, @NotNull PlayerTeam team) {
            super(task, team);
        }
        
        @Override
        public int getGoal() {
            return NUMBER_OF_ACHIEVEMENTS_TO_GET;
        }
        
        @Override
        public @NotNull Component supplyComponent(@NotNull NamespacedKey namespacedKey) {
            final Advancement advancement = Bukkit.getAdvancement(namespacedKey);
            
            return advancement != null ? advancement.displayName() : Component.text(namespacedKey.getKey());
        }
        
    }
    
}