package me.hapyl.lockout.board.task;

import me.hapyl.lockout.Lockout;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.Board;
import me.hapyl.lockout.board.ProgressAccumulator;
import me.hapyl.lockout.board.ProgressConstructor;
import me.hapyl.lockout.util.Described;
import me.hapyl.lockout.util.ItemCreator;
import me.hapyl.lockout.util.Key;
import me.hapyl.lockout.util.Named;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface Task extends Named, Described, ItemCreator {
    
    @NotNull Key getKey();
    
    @Override
    @NotNull Component getName();
    
    @Override
    @NotNull Component getDescription();
    
    @NotNull TaskProgress computeProgress(@NotNull Task task, @NotNull PlayerTeam playerTeam);
    
    default @NotNull TaskDifficulty getDifficulty() {
        return TaskDifficulty.EASY;
    }
    
    default @NotNull TaskCategory getCategory() {
        return TaskCategory.MISCELLANEOUS;
    }
    
    @ApiStatus.NonExtendable
    default <P extends TaskProgress> void progress(@NotNull Player player, @NotNull ProgressConstructor<P> constructor, @NotNull ProgressAccumulator<P> accumulator) {
        final Board board = Lockout.getBoard();
        
        if (board == null) {
            return;
        }
        
        board.progressTask(player, this, constructor, accumulator);
    }
    
}