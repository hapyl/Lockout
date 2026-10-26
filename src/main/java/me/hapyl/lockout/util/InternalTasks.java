package me.hapyl.lockout.util;

import me.hapyl.lockout.Lockout;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

public final class InternalTasks {
    
    private InternalTasks() {
    }
    
    public static void synchronize(@NotNull Runnable runnable) {
        runTask((scheduler, plugin) -> scheduler.runTask(plugin, runnable));
    }
    
    public static void asynchronously(@NotNull Runnable runnable) {
        runTask((scheduler, plugin) -> scheduler.runTaskAsynchronously(plugin, runnable));
    }
    
    public static void later(@NotNull Runnable runnable, int delay) {
        runTask(((scheduler, plugin) -> scheduler.runTaskLater(plugin, runnable, delay)));
    }
    
    private static void runTask(@NotNull Scheduler scheduler) {
        scheduler.schedule(Bukkit.getScheduler(), Lockout.plugin());
    }
    
    public interface Scheduler {
        
        void schedule(@NotNull BukkitScheduler scheduler, @NotNull Plugin plugin);
        
    }
    
}
