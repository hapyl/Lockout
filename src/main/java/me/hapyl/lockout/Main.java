package me.hapyl.lockout;

import me.hapyl.lockout.command.CommandRegistry;
import me.hapyl.lockout.event.PlayerSpyglassEntityEvent;
import me.hapyl.lockout.menu.MenuHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Main extends JavaPlugin {
    
    @Override
    public void onEnable() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, Lockout.LOCKOUT = new Lockout(Lockout.PLUGIN = this), 1, 1);
        
        // Register commands
        new CommandRegistry(this);
        
        // Register events
        final PluginManager pluginManager = Bukkit.getPluginManager();
        
        pluginManager.registerEvents(new ServerHandler(), this);
        pluginManager.registerEvents(new MenuHandler(), this);
        
        // Prepare worlds
        for (World world : Bukkit.getWorlds()) {
            prepareWorld(world);
        }
    }
    
    @Override
    public void onDisable() {
        // Cleanup board
    }
    
    private static void prepareWorld(@NotNull World world) {
        world.setGameRule(GameRules.PVP, false);
        world.setGameRule(GameRules.KEEP_INVENTORY, true);
        world.setGameRule(GameRules.LOCATOR_BAR, false);
        world.setGameRule(GameRules.RESPAWN_RADIUS, 0);
        world.setGameRule(GameRules.SPAWN_PHANTOMS, false);
    }
    
}
