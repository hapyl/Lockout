package me.hapyl.lockout.board.task;

import me.hapyl.lockout.Locust;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.util.ComponentList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TaskProgress implements ComponentLike {
    
    private final PlayerTeam playerTeam;
    
    TaskProgress(@NotNull PlayerTeam playerTeam) {
        this.playerTeam = playerTeam;
    }
    
    public @NotNull PlayerTeam getPlayerTeam() {
        return playerTeam;
    }
    
    public int getProgress() {
        return 0;
    }
    
    public int getGoal() {
        return 1;
    }
    
    public @Nullable ComponentList getComponents() {
        return null;
    }
    
    @Override
    public final @NotNull Component asComponent() {
        return Locust.makeComponentFractional(this.getProgress(), getGoal());
    }
    
}