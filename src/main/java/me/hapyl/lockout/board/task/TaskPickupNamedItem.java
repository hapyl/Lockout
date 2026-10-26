package me.hapyl.lockout.board.task;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.hapyl.lockout.Lockout;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.function.Supplier;

public final class TaskPickupNamedItem extends TaskImpl {
    
    static {
        // Bump ItemNames
        ItemNames.getItemName(Material.STICK);
        
        // Bundle into a single event listener
        MonitorEventListener.listen(
                EntityPickupItemEvent.class,
                ev -> {
                    if (!(ev.getEntity() instanceof Player player)) {
                        return;
                    }
                    
                    final Material material = ev.getItem().getItemStack().getType();
                    
                    for (Letter letter : Letter.values()) {
                        tryProgress(letter.taskSupplier.get(), player, material);
                    }
                }
        );
    }
    
    private final Letter letter;
    
    TaskPickupNamedItem(@NotNull Key key, @NotNull Letter letter) {
        super(
                key,
                Icon.ofTexture(letter.textureUrl),
                Component.text("Collector (%s)".formatted(letter)),
                Component.text("Pick up an item whose name starts with the letter `%s`!".formatted(letter))
        );
        
        this.letter = letter;
    }
    
    @Override
    public @NotNull TaskPickupNamedItemProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskPickupNamedItemProgress(task, playerTeam);
    }
    
    @Override
    public @NotNull TaskCategory getCategory() {
        return TaskCategory.PICKUP_NAMED_ITEM;
    }
    
    private static void tryProgress(@NotNull TaskPickupNamedItem task, @NotNull Player player, @NotNull Material material) {
        if (!task.letter.matches(material)) {
            return;
        }
        
        task.progress(player, task::computeProgress, progress -> {
            progress.picked = PlayerTuple.create(player, material);
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    public enum Letter {
        
        B("B", "50c1b584f13987b466139285b2f3f28df6787123d0b32283d8794e3374e23", () -> (TaskPickupNamedItem) Tasks.PICKUP_NAMED_ITEM_B),
        C("C", "abe983ec478024ec6fd046fcdfa4842676939551b47350447c77c13af18e6f", () -> (TaskPickupNamedItem) Tasks.PICKUP_NAMED_ITEM_C),
        G("G", "1ca3f324beeefb6a0e2c5b3c46abc91ca91c14eba419fa4768ac3023dbb4b2",  () -> (TaskPickupNamedItem) Tasks.PICKUP_NAMED_ITEM_G),
        L("L", "319f50b432d868ae358e16f62ec26f35437aeb9492bce1356c9aa6bb19a386",  () -> (TaskPickupNamedItem) Tasks.PICKUP_NAMED_ITEM_L),
        P("P", "a0a7989b5d6e621a121eedae6f476d35193c97c1a7cb8ecd43622a485dc2e912",() -> (TaskPickupNamedItem) Tasks.PICKUP_NAMED_ITEM_P);
        
        private final String letter;
        private final String textureUrl;
        private final Supplier<? extends TaskPickupNamedItem> taskSupplier;
        
        Letter(@NotNull String letter, @NotNull String textureUrl,  @NotNull Supplier<? extends TaskPickupNamedItem> taskSupplier) {
            this.letter = letter;
            this.textureUrl = textureUrl;
            this.taskSupplier = taskSupplier;
        }
        
        public boolean matches(@NotNull Material material) {
            return ItemNames.getItemName(material).startsWith(letter);
        }
        
        @Override
        public String toString() {
            return letter;
        }
        
    }
    
    public static class TaskPickupNamedItemProgress extends TaskProgress {
        
        private @Nullable PlayerTuple<Material> picked;
        
        TaskPickupNamedItemProgress(@NotNull Task task, @NotNull PlayerTeam team) {
            super(task, team);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return picked != null
                   ? ComponentList.builder(Component.text("Picked Item")).append(picked)
                   : null;
        }
    }
    
    public static class ItemNames {
        
        private static final Map<Material, String> ITEM_NAMES;
        private static final Gson GSON;
        private static final String DEFAULT_ITEM_NAME;
        
        static {
            ITEM_NAMES = Maps.newHashMap();
            GSON = new Gson();
            DEFAULT_ITEM_NAME = "_Unknown";
            
            Lockout.logger().info("Loading item translations...");
            
            try {
                final InputStream resource = Bukkit.class.getClassLoader().getResourceAsStream("assets/minecraft/lang/en_us.json");
                
                if (resource != null) {
                    final JsonObject json = GSON.fromJson(new InputStreamReader(resource), JsonObject.class);
                    
                    for (Material material : Material.values()) {
                        if (material.isLegacy() || !material.isItem()) {
                            continue;
                        }
                        
                        final JsonElement jsonElement = json.get(material.translationKey());
                        
                        if (jsonElement != null) {
                            ITEM_NAMES.put(material, jsonElement.getAsString());
                        }
                    }
                    
                    Lockout.logger().info("Loaded %s translations!".formatted(ITEM_NAMES.size()));
                }
            }
            catch (Exception ex) {
                Lockout.logger().severe("Failed to load item translations!");
            }
        }
        
        public static @NotNull String getItemName(@NotNull Material material) {
            return ITEM_NAMES.getOrDefault(material, DEFAULT_ITEM_NAME);
        }
        
    }
    
}
