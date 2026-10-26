package me.hapyl.lockout.board.task;

import me.hapyl.lockout.Colors;
import me.hapyl.lockout.Locust;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.ComponentList;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.InternalTasks;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public final class TaskMannequin extends TaskImpl implements Listener {
    
    TaskMannequin(@NotNull Key key) {
        super(key, Icon.ofMaterial(Material.ARMOR_STAND), Component.text("Mannequin"), Component.text("Equip a full set of armor on an Armor Stand."));
    }
    
    @Override
    public @NotNull TaskMannequinProgress computeProgress(@NotNull PlayerTeam playerTeam) {
        return new TaskMannequinProgress(playerTeam);
    }
    
    @Override
    public @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.NORMAL;
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handlePlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent ev) {
        final ArmorStand armorStand = ev.getRightClicked();
        
        // We have to run the checks at the next server tick because the event is called before interaction
        InternalTasks.synchronize(() -> {
            if (!armorStand.isValid()) {
                return;
            }
            
            final ItemStack[] armorContents = armorStand.getEquipment().getArmorContents();
            
            for (ItemStack armorContent : armorContents) {
                if (armorContent == null || armorContent.isEmpty()) {
                    return;
                }
            }
            
            final Player player = ev.getPlayer();
            
            this.progress(player, this::computeProgress, progress -> {
                progress.armorContents = Arrays.stream(armorContents).map(ItemStack::getType).toArray(Material[]::new);
                progress.player = player;
                
                return ProgressAccumulator.Result.COMPLETE;
            });
        });
    }
    
    public static class TaskMannequinProgress extends TaskProgress {
        
        private @NotNull Material @Nullable [] armorContents;
        private @Nullable Player player;
        
        TaskMannequinProgress(@NotNull PlayerTeam team) {
            super(team);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            if (armorContents == null || player == null) {
                return null;
            }
            
            final ComponentList builder = ComponentList.builder(Component.text("Armor Contents").appendSpace().append(Locust.formatPlayerNameInBrackets(player)));
            
            for (Material armorContent : armorContents) {
                builder.append(Component.space().append(Component.translatable(armorContent, Colors.GREEN)));
            }
            
            return builder;
        }
    }
    
}