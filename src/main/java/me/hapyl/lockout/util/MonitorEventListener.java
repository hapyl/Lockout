package me.hapyl.lockout.util;

import me.hapyl.lockout.Lockout;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

public interface MonitorEventListener<E extends Event> {
    
    void listen(@NotNull E ev);
    
    static <E extends Event> void listen(@NotNull Class<E> eventClass, @NotNull MonitorEventListener<E> listener) {
        Bukkit.getPluginManager().registerEvent(
                eventClass,
                MonitorEventExecutor.LISTENER,
                EventPriority.MONITOR,
                new MonitorEventExecutor<>(listener),
                Lockout.plugin(),
                true
        );
    }
    
    class MonitorEventExecutor<E extends Event> implements EventExecutor {
        
        private static final Listener LISTENER = new Listener() {};
        
        private final MonitorEventListener<E> listener;
        
        MonitorEventExecutor(@NotNull MonitorEventListener<E> listener) {
            this.listener = listener;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void execute(@NotNull Listener listener, @NotNull Event event) {
            this.listener.listen((E) event);
        }
        
    }
    
    
}
