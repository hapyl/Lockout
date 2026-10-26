package me.hapyl.lockout.util;

import me.hapyl.lockout.Colors;
import me.hapyl.lockout.Locust;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerTuple<E> implements ComponentLike {
    
    private final Player player;
    private final E e;
    private final Component component;
    
    private PlayerTuple(@NotNull Player player, @NotNull E e) {
        this.player = player;
        this.e = e;
        this.component = createComponent(player, e);
    }
    
    public @NotNull Player getPlayer() {
        return player;
    }
    
    public @NotNull E get() {
        return e;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return component;
    }
    
    public static <E> @NotNull PlayerTuple<E> create(@NotNull Player player, @NotNull E e) {
        return new PlayerTuple<>(player, e);
    }
    
    private static @NotNull Component createComponent(@NotNull Player player, @NotNull Object object) {
        return Component.space().append(createObjectName(object).color(Colors.GREEN)).appendSpace().append(Locust.formatPlayerNameInBrackets(player));
    }
    
    private static Component createObjectName(@NotNull Object object) {
        return object instanceof Translatable translatable ? Component.translatable(translatable) : Locust.capitalizeStringAsComponent(object.toString());
    }
    
}
