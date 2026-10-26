package me.hapyl.lockout.menu;

import me.hapyl.lockout.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public final class ButtonComponents {
    
    private static final Component BULLET = Component.text("▶");
    
    private ButtonComponents() {
    }
    
    public static @NotNull Component left(@NotNull String clickTo) {
        return create(Component.text("Left-click to " + clickTo), Style.style(Colors.BUTTON_LEFT));
    }
    
    public static @NotNull Component right(@NotNull String clickTo) {
        return create(Component.text("Right-click to " + clickTo), Style.style(Colors.BUTTON_RIGHT));
    }
    
    public static @NotNull Component shiftLeft(@NotNull String clickTo) {
        return create(Component.text("Shift left-click to " + clickTo), Style.style(Colors.BUTTON_SHIFT_LEFT));
    }
    
    public static @NotNull Component shiftRight(@NotNull String clickTo) {
        return create(Component.text("Shift right-click to " + clickTo), Style.style(Colors.BUTTON_SHIFT_RIGHT));
    }
    
    public static @NotNull Component middle(@NotNull String clickTo) {
        return create(Component.text("Middle click to " + clickTo), Style.style(Colors.BUTTON_MIDDLE));
    }
    
    private static @NotNull Component create(@NotNull Component component, @NotNull Style style) {
        return Component.empty()
                        .append(BULLET.style(style))
                        .appendSpace()
                        .append(component.style(style));
    }
    
}