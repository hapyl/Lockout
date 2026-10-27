package me.hapyl.lockout;

import com.google.common.collect.ImmutableSortedMap;
import me.hapyl.lockout.annotate.ObjectMaybe;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.object.ObjectContents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/* Represents a LOCkout Utility STatic helper class */
public final class Locust {
    
    public static final Component CHECKMARK_TRUE = Component.text("✔", Colors.GREEN);
    public static final Component CHECKMARK_FALSE = Component.text("✘", Colors.RED);
    
    private static final ImmutableSortedMap<Double, TextColor> FRACTION_MAP = ImmutableSortedMap.<Double, TextColor>naturalOrder()
                                                                                                .put(0.00, NamedTextColor.RED)
                                                                                                .put(0.50, NamedTextColor.YELLOW)
                                                                                                .put(0.75, NamedTextColor.GOLD)
                                                                                                .put(1.00, NamedTextColor.GREEN)
                                                                                                .build();
    private static final Audience EVERYONE = new ForwardingAudience() {
        @Override
        public @NotNull Iterable<? extends Audience> audiences() {
            return Bukkit.getOnlinePlayers();
        }
    };
    
    private Locust() {
    }
    
    public static void registerEvents(@NotNull @ObjectMaybe(Listener.class) Object object) {
        if (!(object instanceof Listener listener)) {
            //LOCKOUT.logger.warning("Tried to register events for non-listener class `%s`!".formatted(object.getClass().getSimpleName()));
            return;
        }
        
        Bukkit.getPluginManager().registerEvents(listener, Lockout.PLUGIN);
    }
    
    public static @NotNull Component formatPlayerName(@NotNull Player player) {
        final PlayerTeam playerTeam = PlayerTeam.getPlayerTeam(player).orElse(PlayerTeam.RED);
        
        return Component.empty()
                        .append(Component.object(ObjectContents.playerHead(player)).color(Colors.WHITE))
                        .appendSpace()
                        .append(player.name().style(playerTeam.getStyle()));
    }
    
    public static @NotNull Component formatPlayerNameInBrackets(Player player) {
        return Component.empty()
                        .append(Component.text("(", Colors.DARK_GRAY))
                        .append(formatPlayerName(player))
                        .append(Component.text(")", Colors.DARK_GRAY));
    }
    
    public static void kickPlayer(@NotNull Player player, @NotNull Component reason) {
        player.kick(
                Component.empty()
                         .append(Component.text("You have been kicked!", Colors.RED))
                         .appendNewline()
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("ʀᴇᴀꜱᴏɴ", Colors.WHITE))
                         .appendNewline()
                         .appendNewline()
                         .append(reason.color(Colors.DARK_RED))
        );
    }
    
    public static @NotNull String capitalizeString(@NotNull String string) {
        final String[] words = string.toLowerCase().replace("_", " ").split(" ");
        final StringBuilder builder = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            }
            
            builder.append(" ");
        }
        
        return builder.toString().trim();
    }
    
    public static @NotNull Component capitalizeStringAsComponent(@NotNull String string) {
        return Component.text(capitalizeString(string));
    }
    
    public static @NotNull Component checkmark(@Nullable Boolean condition) {
        return condition != null ? condition ? CHECKMARK_TRUE : CHECKMARK_FALSE : Component.empty();
    }
    
    public static @NotNull Component makeComponentFractional(@NotNull Number current, @NotNull Number max) {
        final double fraction = current.doubleValue() / max.doubleValue();
        
        return Component.text(current.intValue(), Objects.requireNonNull(FRACTION_MAP.floorEntry(fraction)).getValue())
                        .append(Component.text("/", NamedTextColor.GRAY))
                        .append(Component.text(max.intValue(), NamedTextColor.GREEN));
    }
    
    public static @NotNull String numberToString(int number) {
        return switch (number) {
            case 0 -> "zero";
            case 1 -> "one";
            case 2 -> "two";
            case 3 -> "three";
            case 4 -> "four";
            case 5 -> "five";
            case 6 -> "six";
            case 7 -> "seven";
            case 8 -> "eight";
            case 9 -> "nine";
            case 10 -> "ten";
            default -> number + " times";
        };
    }
    
    public static <E extends Enum<E>> Optional<E> enumValue(@NotNull Class<E> enumClass, @NotNull String name) {
        try {
            return Optional.of(Enum.valueOf(enumClass, name.toLowerCase(Locale.ROOT)));
        }
        catch (Exception ex) {
            return Optional.empty();
        }
    }
    
    public static @NotNull Audience everyone() {
        return EVERYONE;
    }
    
}