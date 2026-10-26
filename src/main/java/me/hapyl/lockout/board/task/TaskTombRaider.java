package me.hapyl.lockout.board.task;

import com.google.common.collect.Maps;
import me.hapyl.lockout.Colors;
import me.hapyl.lockout.Locust;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.ComponentList;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public final class TaskTombRaider extends TaskImpl implements Listener {
    
    TaskTombRaider(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.CHEST), Component.text("Tomb Raider"), Component.text("Open a chest in any structure."));
    }
    
    @EventHandler
    public void handleLootGenerateEvent(LootGenerateEvent ev) {
        if (!(ev.getEntity() instanceof Player player)) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> {
            progress.raider = new Raider(player, ev.getLoot());
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskTombRaiderProgress computeProgress(@NotNull PlayerTeam playerTeam) {
        return new TaskTombRaiderProgress(playerTeam);
    }
    
    public static class TaskTombRaiderProgress extends TaskProgress {
        
        private @Nullable Raider raider;
        
        TaskTombRaiderProgress(@NotNull PlayerTeam playerTeam) {
            super(playerTeam);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return raider != null
                   ? ComponentList.builder(Component.text("Raider").append(raider))
                                  .append(raider.items)
                   : null;
        }
    }
    
    public static class Raider implements ComponentLike {
        
        private final Component component;
        private final List<? extends Component> items;
        
        Raider(@NotNull Player player, @NotNull List<? extends ItemStack> items) {
            this.component = Component.space().append(Locust.formatPlayerNameInBrackets(player));
            this.items = items.stream()
                              .collect(Collectors.toMap(
                                      ItemStack::getType,
                                      ItemStack::getAmount,
                                      Integer::sum,
                                      () -> Maps.newEnumMap(Material.class)
                              ))
                              .entrySet()
                              .stream()
                              .map(entry -> {
                                  return Component.space()
                                                  .append(Component.translatable(entry.getKey(), Colors.GREEN))
                                                  .append(Component.text(" x ", Colors.DARK_GRAY))
                                                  .append(Component.text(entry.getValue()));
                              })
                              .toList();
        }
        
        @Override
        public @NotNull Component asComponent() {
            return component;
        }
        
    }
    
}