package me.hapyl.lockout.board.task;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.hapyl.lockout.Locust;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.ComponentList;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class TaskFlagWeaver extends TaskImpl implements Listener {
    
    public static final int MINIMUM_PATTERNS = 3;
    
    TaskFlagWeaver(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.WHITE_BANNER), Component.text("Flag Weaver"), Component.text("Use a loom to apply at least %s patterns to a banner and place it on the ground.".formatted(Locust.numberToString(MINIMUM_PATTERNS))));
    }
    
    @Override
    public @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.HARD;
    }
    
    @EventHandler
    public void handleBlockPlaceEvent(BlockPlaceEvent ev) {
        if (!(ev.getBlockPlaced().getState() instanceof Banner banner)) {
            return;
        }
        
        final List<Pattern> patterns = banner.getPatterns();
        
        if (patterns.size() < MINIMUM_PATTERNS) {
            return;
        }
        
        final Player player = ev.getPlayer();
        
        this.progress(player, this::computeProgress, progress -> {
            progress.placedBanner = new PlacedBanner(player, patterns);
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskFlagWeaverProgress computeProgress(@NotNull PlayerTeam playerTeam) {
        return new TaskFlagWeaverProgress(playerTeam);
    }
    
    public static class TaskFlagWeaverProgress extends TaskProgress {
        
        private @Nullable PlacedBanner placedBanner;
        
        TaskFlagWeaverProgress(@NotNull PlayerTeam playerTeam) {
            super(playerTeam);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return placedBanner != null
                   ? ComponentList.builder(Component.text("Banner Patterns").appendSpace().append(placedBanner))
                                  .append(placedBanner.patterns)
                   : null;
        }
        
    }
    
    public static class PlacedBanner implements ComponentLike {
        
        private static final Registry<PatternType> REGISTRY_PATTERN = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN);
        
        private final Component component;
        private final List<? extends Component> patterns;
        
        PlacedBanner(@NotNull Player player, @NotNull List<? extends Pattern> patterns) {
            this.component = Locust.formatPlayerNameInBrackets(player);
            this.patterns = patterns.stream().map(pattern -> {
                                        final NamespacedKey patternKey = REGISTRY_PATTERN.getKey(pattern.getPattern());
                                        
                                        return patternKey != null
                                               ? Component.space().append(Component.text(Locust.capitalizeString(patternKey.getKey()), TextColor.color(pattern.getColor().getColor())))
                                               : Component.empty();
                                    })
                                    .toList();
        }
        
        @Override
        public @NotNull Component asComponent() {
            return component;
        }
        
    }
    
}