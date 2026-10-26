package me.hapyl.lockout.util;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Consumer;

public interface Icon extends ItemCreator {
    
    static @NotNull Icon ofMaterial(@NotNull Material material) {
        return () -> new ItemBuilder(material);
    }
    
    static @NotNull Icon ofMaterial(@NotNull Material material, @NotNull Consumer<ItemBuilder> consumer) {
        return () -> {
            final ItemBuilder builder = new ItemBuilder(material);
            consumer.accept(builder);
            
            return builder;
        };
    }
    
    static @NotNull Icon ofTexture(@NotNull String textureUrl) {
        return () -> ItemBuilder.texture(textureUrl);
    }
    
    static @NotNull Icon ofMultipleMaterials(@NotNull ImmutableArray<Material> materials) {
        return new IconMultipleMaterialsImpl(materials);
    }
    
    class IconMultipleMaterialsImpl implements Icon {
        
        private final ImmutableArray<Material> materials;
        
        IconMultipleMaterialsImpl(@NotNull ImmutableArray<Material> materials) {
            this.materials = materials;
        }
        
        @Override
        public @NotNull ItemBuilder createBuilder() {
            return new ItemBuilder(materials.randomChoise());
        }
        
    }
}