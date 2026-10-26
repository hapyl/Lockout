package me.hapyl.lockout;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Stream;

public final class Logger {
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss");
    
    private Logger() {
    }
    
    public static void info(@NotNull Audience audience, @NotNull Component component) {
        audience.sendMessage(component.color(Colors.DEFAULT));
    }
    
    public static void success(@NotNull Audience audience, @NotNull Component component) {
        audience.sendMessage(Locust.CHECKMARK_TRUE.appendSpace().append(component.color(Colors.SUCCESS)));
    }
    
    public static void error(@NotNull Audience audience, @NotNull Component component) {
        audience.sendMessage(Locust.CHECKMARK_FALSE.appendSpace().append(component.color(Colors.ERROR)));
    }
    
    public static void debug(@Nullable Object object) {
        final TextComponent message = Component.empty()
                                               .append(Component.text("[DEBUG] ", Colors.RED))
                                               .append(Component.text("%s ".formatted(TIME_FORMATTER.format(LocalTime.now())), Colors.DARK_GRAY))
                                               .append(Component.text(objectToString(object), Colors.YELLOW));
        
        Stream.concat(Stream.of(Bukkit.getConsoleSender()), Bukkit.getOnlinePlayers().stream()).filter(ServerOperator::isOp).map(CommandSender.class::cast).forEach(operator -> operator.sendMessage(message));
    }
    
    private static @NotNull String objectToString(@Nullable Object object) {
        return switch (object) {
            case null -> "null";
            case Object[] arrayObject -> Arrays.toString(arrayObject);
            case int[] array -> Arrays.toString(array);
            case byte[] array -> Arrays.toString(array);
            case char[] array -> Arrays.toString(array);
            case long[] array -> Arrays.toString(array);
            case float[] array -> Arrays.toString(array);
            case short[] array -> Arrays.toString(array);
            case double[] array -> Arrays.toString(array);
            case boolean[] array -> Arrays.toString(array);
            default -> String.valueOf(object);
        };
    }
    
}
