package me.hapyl.lockout.util;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Set;

public enum EntityTags implements Named {
    
    HOSTILE(
            Component.text("Hostile"),
            Set.of(
                    EntityType.BLAZE, EntityType.BOGGED, EntityType.BREEZE, EntityType.CAVE_SPIDER,
                    EntityType.CREAKING, EntityType.CREEPER, EntityType.DROWNED, EntityType.ELDER_GUARDIAN,
                    EntityType.ENDER_DRAGON, EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.EVOKER,
                    EntityType.GHAST, EntityType.GIANT, EntityType.GUARDIAN, EntityType.HOGLIN,
                    EntityType.HUSK, EntityType.ILLUSIONER, EntityType.MAGMA_CUBE, EntityType.PARCHED,
                    EntityType.PHANTOM, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.PILLAGER,
                    EntityType.RAVAGER, EntityType.SHULKER, EntityType.SILVERFISH, EntityType.SKELETON,
                    EntityType.SLIME, EntityType.SPIDER, EntityType.STRAY, EntityType.SULFUR_CUBE,
                    EntityType.VEX, EntityType.VINDICATOR, EntityType.WARDEN, EntityType.WITCH,
                    EntityType.WITHER, EntityType.WITHER_SKELETON, EntityType.ZOGLIN, EntityType.ZOMBIE,
                    EntityType.ZOMBIE_NAUTILUS, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIFIED_PIGLIN
            )
    ),
    
    FRIENDLY(
            Component.text("Friendly"),
            Set.of(
                    EntityType.ALLAY, EntityType.ARMADILLO, EntityType.AXOLOTL, EntityType.BAT,
                    EntityType.CAMEL, EntityType.CAMEL_HUSK, EntityType.CAT, EntityType.CHICKEN,
                    EntityType.COD, EntityType.COPPER_GOLEM, EntityType.COW, EntityType.DONKEY,
                    EntityType.FOX, EntityType.FROG, EntityType.GLOW_SQUID, EntityType.HAPPY_GHAST,
                    EntityType.HORSE, EntityType.MOOSHROOM, EntityType.MULE, EntityType.NAUTILUS,
                    EntityType.OCELOT, EntityType.PANDA, EntityType.PARROT, EntityType.PIG,
                    EntityType.RABBIT, EntityType.SALMON, EntityType.SHEEP, EntityType.SKELETON_HORSE,
                    EntityType.SNIFFER, EntityType.SNOW_GOLEM, EntityType.SQUID, EntityType.STRIDER,
                    EntityType.TADPOLE, EntityType.TRADER_LLAMA, EntityType.TROPICAL_FISH, EntityType.TURTLE,
                    EntityType.VILLAGER, EntityType.WANDERING_TRADER
            )
    ),
    
    NEUTRAL(
            Component.text("Neutral"),
            Set.of(
                    EntityType.BEE, EntityType.DOLPHIN, EntityType.ENDERMAN, EntityType.GOAT,
                    EntityType.IRON_GOLEM, EntityType.LLAMA, EntityType.PIGLIN, EntityType.POLAR_BEAR,
                    EntityType.PUFFERFISH, EntityType.SPIDER, EntityType.TRADER_LLAMA, EntityType.WOLF,
                    EntityType.ZOMBIE_HORSE, EntityType.ZOMBIFIED_PIGLIN
            )
    ),
    
    AQUATIC(
            Component.text("Aquatic"),
            Set.of(
                    EntityType.AXOLOTL, EntityType.COD, EntityType.DOLPHIN, EntityType.DROWNED,
                    EntityType.ELDER_GUARDIAN, EntityType.GLOW_SQUID, EntityType.GUARDIAN, EntityType.NAUTILUS,
                    EntityType.PUFFERFISH, EntityType.SALMON, EntityType.SQUID, EntityType.TADPOLE,
                    EntityType.TROPICAL_FISH, EntityType.TURTLE, EntityType.ZOMBIE_NAUTILUS
            )
    ),
    
    FLYING(
            Component.text("Flying"),
            Set.of(
                    EntityType.ALLAY, EntityType.BAT, EntityType.BEE, EntityType.BLAZE,
                    EntityType.BREEZE, EntityType.ENDER_DRAGON, EntityType.GHAST, EntityType.HAPPY_GHAST,
                    EntityType.PARROT, EntityType.PHANTOM, EntityType.VEX, EntityType.WITHER
            )
    );
    
    private final Component name;
    private final @Unmodifiable Set<? extends EntityType> entities;
    
    EntityTags(@NotNull Component name, @NotNull @Unmodifiable Set<? extends EntityType> entities) {
        this.name = name;
        this.entities = entities;
    }
    
    public @NotNull Set<? extends EntityType> getEntities() {
        // Unmodifiable, don't need to copy
        return entities;
    }
    
    public boolean isTagged(@NonNull EntityType entityType) {
        return this.getEntities().contains(entityType);
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
}