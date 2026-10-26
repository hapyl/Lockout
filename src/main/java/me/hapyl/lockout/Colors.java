package me.hapyl.lockout;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.w3c.dom.Text;

public final class Colors {
    
    public static final TextColor BLACK = NamedTextColor.BLACK;
    public static final TextColor DARK_BLUE = NamedTextColor.DARK_BLUE;
    public static final TextColor DARK_GREEN = NamedTextColor.DARK_GREEN;
    public static final TextColor DARK_AQUA = NamedTextColor.DARK_AQUA;
    public static final TextColor DARK_RED = NamedTextColor.DARK_RED;
    public static final TextColor DARK_PURPLE = NamedTextColor.DARK_PURPLE;
    public static final TextColor GOLD = NamedTextColor.GOLD;
    public static final TextColor GRAY = NamedTextColor.GRAY;
    public static final TextColor DARK_GRAY = NamedTextColor.DARK_GRAY;
    public static final TextColor BLUE = NamedTextColor.BLUE;
    public static final TextColor GREEN = NamedTextColor.GREEN;
    public static final TextColor AQUA = NamedTextColor.AQUA;
    public static final TextColor RED = NamedTextColor.RED;
    public static final TextColor LIGHT_PURPLE = NamedTextColor.LIGHT_PURPLE;
    public static final TextColor YELLOW = NamedTextColor.YELLOW;
    public static final TextColor WHITE = NamedTextColor.WHITE;
    
    public static final TextColor DEFAULT = TextColor.color(0xABCDEF);
    public static final TextColor SUCCESS = TextColor.color(0x55E070);
    public static final TextColor ERROR = TextColor.color(0xFF5555);
    public static final TextColor WARNING = TextColor.color(0xFFB830);
    
    public static final TextColor TEAM_RED = TextColor.color(0xC0392B);
    public static final TextColor TEAM_GREEN = TextColor.color(0x2ECC71);
    public static final TextColor TEAM_BLUE = TextColor.color(0x3498DB);
    public static final TextColor TEAM_ORANGE = TextColor.color(0xE67E22);
    public static final TextColor TEAM_PURPLE = TextColor.color(0x8E44AD);
    
    public static final TextColor BUTTON_LEFT = TextColor.color(0xFFE95C);
    public static final TextColor BUTTON_RIGHT = TextColor.color(0xE0B000);
    public static final TextColor BUTTON_SHIFT_LEFT = TextColor.color(0x6EF2F2);
    public static final TextColor BUTTON_SHIFT_RIGHT = TextColor.color(0x1FA3A3);
    public static final TextColor BUTTON_MIDDLE = TextColor.color(0xE070FF);
    
    private Colors() {
    }
    
}
