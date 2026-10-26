package me.hapyl.lockout.board.task;

import me.hapyl.lockout.Colors;
import me.hapyl.lockout.Locust;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.util.ComponentList;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class TaskEgoistic extends TaskImpl implements Listener {
    
    public static final String THING_TO_WRITE_WITHOUT_PLACEHOLDER = "is the best";
    
    TaskEgoistic(@NotNull Key key) {
        super(
                key,
                Icon.ofMaterial(Material.OAK_SIGN),
                Component.text("Egoistic"),
                Component.empty()
                         .append(Component.text("Use a sign to tell the world that you are the best."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Write `YourName %s` on it.".formatted(THING_TO_WRITE_WITHOUT_PLACEHOLDER), Colors.DARK_GRAY))
        );
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handleSignChangeEvent(SignChangeEvent ev) {
        final Player player = ev.getPlayer();
        
        final String expectedString = normalize("%s %s".formatted(player.getName(), THING_TO_WRITE_WITHOUT_PLACEHOLDER));
        final String lines = normalize(ev.lines().stream().map(PlainTextComponentSerializer.plainText()::serialize).filter(Predicate.not(String::isEmpty)).collect(Collectors.joining()));
        
        if (!expectedString.equals(lines)) {
            return;
        }
        
        this.progress(player, this::computeProgress, progress -> {
            progress.egoist = player;
            
            return ProgressAccumulator.Result.COMPLETE;
        });
    }
    
    @Override
    public @NotNull TaskEgoisticProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
        return new TaskEgoisticProgress(task, playerTeam);
    }
    
    private static @NotNull String normalize(@NotNull String string) {
        return string.replaceAll("\\s+", "").toLowerCase();
    }
    
    public static class TaskEgoisticProgress extends TaskProgress {
        
        private @Nullable Player egoist;
        
        TaskEgoisticProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam) {
            super(task, playerTeam);
        }
        
        @Override
        public @Nullable ComponentList getComponents() {
            return egoist != null
                   ? ComponentList.builder(Component.text("Most Egoistic Player"))
                                  .append(Component.space().append(Locust.formatPlayerName(egoist)))
                   : null;
        }
    }
    
}