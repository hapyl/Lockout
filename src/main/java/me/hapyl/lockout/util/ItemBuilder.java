package me.hapyl.lockout.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.collect.Lists;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import me.hapyl.lockout.Colors;
import me.hapyl.lockout.Lockout;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.object.ObjectContents;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemBuilder {
    
    private static final TextColor DEFAULT_NAME_COLOR = Colors.DARK_GREEN;
    private static final TextColor DEFAULT_LORE_COLOR = Colors.GRAY;
    
    private static final String URL_TEXTURE_LINK = "https://textures.minecraft.net/texture/";
    
    private static final Pattern WRAP_PATTERN = Pattern.compile("\\S+|\\s+");
    private static final int WRAP_LENGTH = 35; // 38 -> 35; Shorter looks better here
    
    private static final Set<DataComponentType> DATA_COMPONENTS_WITH_TOOLTIPS = Set.of(
            DataComponentTypes.DAMAGE,
            DataComponentTypes.UNBREAKABLE,
            DataComponentTypes.ENCHANTMENTS,
            DataComponentTypes.CAN_PLACE_ON,
            DataComponentTypes.CAN_BREAK,
            DataComponentTypes.ATTRIBUTE_MODIFIERS,
            DataComponentTypes.STORED_ENCHANTMENTS,
            DataComponentTypes.DYED_COLOR,
            DataComponentTypes.MAP_ID,
            DataComponentTypes.CHARGED_PROJECTILES,
            DataComponentTypes.BUNDLE_CONTENTS,
            DataComponentTypes.POTION_CONTENTS,
            DataComponentTypes.POTION_DURATION_SCALE,
            DataComponentTypes.WRITABLE_BOOK_CONTENT,
            DataComponentTypes.WRITTEN_BOOK_CONTENT,
            DataComponentTypes.TRIM,
            DataComponentTypes.PROVIDES_TRIM_MATERIAL,
            DataComponentTypes.PROVIDES_BANNER_PATTERNS,
            DataComponentTypes.FIREWORKS,
            DataComponentTypes.BANNER_PATTERNS,
            DataComponentTypes.JUKEBOX_PLAYABLE,
            DataComponentTypes.INSTRUMENT
    );
    
    private static final Key NAMESPACE = Key.key("items");
    
    private final ItemStack itemStack;
    
    public ItemBuilder(@NotNull Material material) {
        this.itemStack = new ItemStack(material);
        
        // Always hide tooltips, because fuck tooltips
        this.itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hiddenComponents(DATA_COMPONENTS_WITH_TOOLTIPS).build());
    }
    
    public @NotNull ItemBuilder setName(@NotNull Component name) {
        return this.editMeta(meta -> meta.customName(unfuckName(name)));
    }
    
    public @NotNull ItemBuilder setLore(@NotNull List<? extends Component> lore) {
        return this.editMeta(meta -> meta.lore(unfuckLore(lore)));
    }
    
    public @NotNull ItemBuilder addLore(@NotNull List<? extends Component> lore) {
        return this.editLore(existingLore -> existingLore.addAll(unfuckLore(lore)));
    }
    
    public @NotNull ItemBuilder addLore(@NotNull Component component) {
        return this.addLore(List.of(component));
    }
    
    public @NotNull ItemBuilder addLore() {
        return this.addLore(Component.empty());
    }
    
    public @NotNull ItemBuilder addWrappedLore(@NotNull Component component) {
        return addLore(wrap(component, WRAP_LENGTH));
    }
    
    public @NotNull ItemStack build() {
        return itemStack;
    }
    
    public <M extends ItemMeta> @NotNull ItemBuilder editMeta(@NotNull Class<M> metaClass, @NotNull MetaEdit<M> edit) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        
        if (metaClass.isInstance(itemMeta)) {
            edit.edit(metaClass.cast(itemMeta));
            
            itemStack.setItemMeta(itemMeta);
        }
        
        return this;
    }
    
    public @NotNull ItemBuilder editMeta(@NotNull MetaEdit<ItemMeta> edit) {
        return this.editMeta(ItemMeta.class, edit);
    }
    
    public @NotNull ItemBuilder editLore(@NotNull LoreEdit edit) {
        final List<Component> existingLore = Objects.requireNonNullElseGet(itemStack.lore(), Lists::newArrayList);
        
        // Apply lore edit
        edit.edit(existingLore);
        
        // Apply the lore without unfucking to avoid unfucking already unfucked lore
        itemStack.lore(existingLore);
        
        return this;
    }
    
    public @NotNull ItemBuilder unsetComponents() {
        for (DataComponentType dataComponentType : DATA_COMPONENTS_WITH_TOOLTIPS) {
            itemStack.unsetData(dataComponentType);
        }
        
        return this;
    }
    
    public @NotNull ItemBuilder hideTooltip() {
        return editMeta(meta -> meta.setHideTooltip(true));
    }
    
    public @NotNull ItemBuilder glow() {
        return editMeta(meta -> meta.setEnchantmentGlintOverride(true));
    }
    
    public @NotNull Component sprite() {
        return Component.object(ObjectContents.sprite(NAMESPACE, Key.key("item/" + itemStack.getType().getKey().getKey())));
    }
    
    public static @NotNull ItemBuilder builder(@NotNull Material material) {
        return new ItemBuilder(material);
    }
    
    public static @NotNull ItemBuilder texture(@NotNull String textureUrl) {
        final ItemBuilder builder = new ItemBuilder(Material.PLAYER_HEAD);
        
        try {
            final PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            final PlayerTextures textures = profile.getTextures();
            
            textures.setSkin(URI.create(URL_TEXTURE_LINK + textureUrl).toURL());
            profile.setTextures(textures);
            
            builder.editMeta(SkullMeta.class, skullMeta -> skullMeta.setPlayerProfile(profile));
        }
        catch (Exception ex) {
            Lockout.logger().severe("Failed to parse head texture! " + ex.getMessage());
        }
        
        return builder;
    }
    
    private static @NotNull Component unfuckName(@NotNull Component name) {
        return unfuckComponent(name, DEFAULT_NAME_COLOR);
    }
    
    private static @NotNull List<? extends Component> unfuckLore(@NotNull Collection<? extends Component> lore) {
        return lore.stream().map(component -> unfuckComponent(component, DEFAULT_LORE_COLOR)).toList();
    }
    
    private static @NotNull Component unfuckComponent(@NotNull Component component, @NotNull TextColor defaultColor) {
        final TextColor color = component.color();
        
        // Unfuck ugly colors by applying a default color
        if (color == null) {
            component = component.color(defaultColor);
        }
        
        // Unfuck ugly italic by explicitly settings it to false
        if (component.decoration(TextDecoration.ITALIC) == TextDecoration.State.NOT_SET) {
            component = component.decoration(TextDecoration.ITALIC, false);
        }
        
        return component;
    }
    
    public interface MetaEdit<M extends ItemMeta> {
        
        void edit(@NotNull M meta);
        
    }
    
    public interface LoreEdit {
        
        void edit(@NotNull List<? super Component> existingLore);
        
    }
    
    public static @NotNull List<? extends Component> wrap(@NotNull Component component, int maxLength) {
        if (!(component instanceof TextComponent)) {
            return List.of(component);
        }
        
        final List<Component> output = Lists.newArrayList();
        final List<Component> children = flatten(component);
        
        TextComponent.Builder builder = Component.text();
        int counter = 0;
        
        for (Component child : children) {
            // Append non-text components as is
            if (!(child instanceof TextComponent textComponent)) {
                builder.append(child);
                continue;
            }
            
            final String content = textComponent.content();
            
            // Handle newlines
            if (content.equals("\n")) {
                // If the builder isn't empty, we're inserting a manual linebreak
                if (!builder.children().isEmpty()) {
                    output.add(builder.build());
                }
                // Otherwise, trailing newlines append an empty line
                else {
                    output.add(Component.empty());
                }
                
                builder = Component.text();
                counter = 0;
                continue;
            }
            
            // Match the pattern
            final Matcher matcher = WRAP_PATTERN.matcher(content);
            
            while (matcher.find()) {
                final String group = matcher.group();
                final int length = group.length();
                
                // If `counter` + `length` will overflow, wrap on the next line,
                // unless we're on a whitespace
                if (counter + length > maxLength && !group.isBlank()) {
                    output.add(builder.build());
                    
                    builder = Component.text();
                    counter = 0;
                }
                
                // Append the content with the `child` style
                builder.append(Component.text(group, child.style()));
                counter += length;
            }
        }
        
        // Append the remaining children
        if (!builder.children().isEmpty()) {
            output.add(builder.build());
        }
        
        return output;
    }
    
    private static @NotNull List<Component> flatten(@NotNull Component component) {
        class Flattener {
            static List<Component> flatten(@NotNull Component component) {
                final List<Component> flattened = Lists.newArrayList();
                appendChildren(component, flattened, Style.empty());
                
                return List.copyOf(flattened);
            }
            
            static void appendChildren(@NotNull Component root, @NotNull List<Component> list, @NotNull Style style) {
                final Style styleMerged = root.style().merge(style);
                
                // If the root is non-text component, append as it
                if (!(root instanceof TextComponent textComponent)) {
                    list.add(root.style(styleMerged));
                }
                // Otherwise, remove the children and append the component
                else if (!textComponent.content().isEmpty()) {
                    list.add(textComponent.children(List.of()).style(styleMerged));
                }
                
                // Append children recursively
                for (Component child : root.children()) {
                    appendChildren(child, list, styleMerged);
                }
            }
        }
        
        return Flattener.flatten(component);
    }
    
}