package me.hapyl.lockout.board.task;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import me.hapyl.lockout.*;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.ComponentList;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class TaskStylist extends TaskImpl implements Listener {
    
    private static final Color ARMOR_COLOR = Color.fromRGB(90, 66, 245);
    
    TaskStylist(@NotNull Key key) {
        super(
                key,
                Icon.ofMaterial(Material.LEATHER_HELMET, builder -> builder.editMeta(LeatherArmorMeta.class, meta -> meta.setColor(ARMOR_COLOR))),
                Component.text("Stylist"),
                Component.text("Wear a full set of armor where every piece is a different type.")
        );
    }
    
    @EventHandler
    public void handlePlayerInventorySlotChangeEvent(PlayerInventorySlotChangeEvent ev) {
        final Player player = ev.getPlayer();
        final ItemStack[] armorContents = player.getInventory().getArmorContents();
        
        final int slot = ev.getSlot();
        
        // Ignore non-armor modifications
        if (slot < 36 || slot > 39) {
            return;
        }
        
        // Check whether all armor types are distinct
        if (Arrays.stream(armorContents).map(ArmorType::getArmorType).filter(Objects::nonNull).collect(Collectors.toSet()).size() != 4) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> {
            progress.stylist = new Stylist(player, armorContents);
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.NORMAL;
    }
    
    @Override
    public @NotNull TaskStylistProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskStylistProgress(task, playerTeam);
    }
    
    public static class TaskStylistProgress extends TaskProgress {
        
        private @Nullable Stylist stylist;
        
        TaskStylistProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
            super(task, playerTeam);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return stylist != null
                   ? ComponentList.builder(Component.text("Stylist").append(stylist))
                                  .append(stylist.armorContents)
                   : null;
        }
        
    }
    
    public static class Stylist implements ComponentLike {
        
        private final Component component;
        private final List<? extends Component> armorContents;
        
        Stylist(@NotNull Player player, @Nullable ItemStack[] armorContents) {
            this.component = Component.space().append(Locust.formatPlayerNameInBrackets(player));
            this.armorContents = IntStream.range(0, armorContents.length)
                                          .mapToObj(i -> armorContents[armorContents.length - i - 1])
                                          .filter(Objects::nonNull)
                                          .map(itemStack -> Component.space().append(Component.translatable(itemStack, Colors.GREEN)))
                                          .toList();
        }
        
        @Override
        public @NotNull Component asComponent() {
            return component;
        }
        
    }
    
    public enum ArmorType {
        
        LEATHER(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS),
        COPPER(Material.COPPER_HELMET, Material.COPPER_CHESTPLATE, Material.COPPER_LEGGINGS, Material.COPPER_BOOTS),
        CHAINMAIL(Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS),
        IRON(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS),
        GOLDEN(Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS),
        DIAMOND(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS),
        NETHERITE(Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS),
        TURTLE(Material.TURTLE_SCUTE);
        
        private final Set<Material> materials;
        
        ArmorType(@NotNull Material... materials) {
            this.materials = Set.of(materials);
        }
        
        public static @Nullable ArmorType getArmorType(@Nullable ItemStack itemStack) {
            if (itemStack == null) {
                return null;
            }
            
            final Material material = itemStack.getType();
            
            for (ArmorType armorType : values()) {
                if (armorType.materials.contains(material)) {
                    return armorType;
                }
            }
            
            return null;
        }
        
    }
    
}