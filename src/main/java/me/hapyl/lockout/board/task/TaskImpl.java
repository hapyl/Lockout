package me.hapyl.lockout.board.task;

import me.hapyl.lockout.Locust;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.ItemBuilder;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public abstract class TaskImpl implements Task {
    
    private final Key key;
    private final Icon icon;
    private final Component name;
    private final Component description;
    
    TaskImpl(@NotNull Key key, @NotNull Icon icon, @NotNull Component name, @NotNull Component description) {
        this.key = key;
        this.icon = icon;
        this.name = name;
        this.description = description;
        
        Locust.registerEvents(this);
    }
    
    @Override
    public @NotNull ItemBuilder createBuilder() {
        final ItemBuilder builder = icon.createBuilder();
        
        builder.setName(name);
        builder.addLore(this.getDifficulty().asComponent());
        
        builder.addLore();
        builder.addWrappedLore(this.getDescription());
        
        return builder;
    }
    
    @Override
    public @NotNull Key getKey() {
        return key;
    }
    
    @Override
    public @NotNull Component getName() {
        return name;
    }
    
    @Override
    public @NotNull Component getDescription() {
        return description;
    }
    
}