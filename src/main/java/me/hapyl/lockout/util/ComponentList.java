package me.hapyl.lockout.util;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class ComponentList implements ComponentLike, Iterable<Component> {
    
    private final List<Component> components;
    
    ComponentList() {
        this.components = Lists.newArrayList();
    }
    
    public @NotNull ComponentList append(@NotNull Component component) {
        return this.append0(component);
    }
    
    public @NotNull ComponentList append(@NotNull ComponentLike componentLike) {
        return this.append0(componentLike.asComponent());
    }
    
    public @NotNull ComponentList append() {
        return this.append(Component.empty());
    }
    
    public @NotNull ComponentList append(@NotNull ComponentList componentList) {
        this.components.addAll(componentList.components);
        return this;
    }
    
    public @NotNull ComponentList append(@NotNull Collection<? extends Component> iterable) {
        this.components.addAll(iterable);
        return this;
    }
    
    @Override
    public @NotNull Component asComponent() {
        return Component.join(JoinConfiguration.newlines(), components);
    }
    
    public @NotNull Stream<? extends Component> stream() {
        return components.stream();
    }
    
    public @NotNull List<? extends Component> asList() {
        return components; // Care to copy?
    }
    
    @Override
    public @NotNull Iterator<Component> iterator() {
        return components.iterator();
    }
    
    private @NotNull ComponentList append0(@NotNull Component component) {
        this.components.add(component);
        return this;
    }
    
    public static @NotNull ComponentList builder() {
        return new ComponentList();
    }
    
    public static @NotNull ComponentList builder(@NotNull Component component) {
        return new ComponentList().append(component);
    }
}