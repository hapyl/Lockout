package me.hapyl.lockout.event;

import io.papermc.paper.event.packet.ClientTickEndEvent;
import me.hapyl.lockout.util.MonitorEventListener;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

public class PlayerSpyglassEntityEvent extends PlayerEvent {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private static final double MAX_DISTANCE = 100;
    private static final double PRECISION = 0.1;
    
    static {
        MonitorEventListener.listen(ClientTickEndEvent.class, ev -> {
            final Player player = ev.getPlayer();
            final ItemStack activeItem = player.getActiveItem();
            
            if (activeItem.isEmpty() || activeItem.getType() != Material.SPYGLASS) {
                return;
            }
            
            final Location location = player.getEyeLocation();
            final RayTraceResult rayTraceResult = player.getWorld().rayTrace(location, location.getDirection(), MAX_DISTANCE, FluidCollisionMode.NEVER, true, PRECISION, entity -> player != entity && !entity.isDead() && !(entity instanceof Player playerEntity && playerEntity.getGameMode() == GameMode.SURVIVAL));
            
            if (rayTraceResult == null) {
                return;
            }
            
            final Entity targetEntity = rayTraceResult.getHitEntity();
            
            if (targetEntity == null) {
                return;
            }
            
            new PlayerSpyglassEntityEvent(player, activeItem, targetEntity).callEvent();
        });
    }
    
    private final ItemStack spyglass;
    private final Entity entity;
    
    public PlayerSpyglassEntityEvent(@NotNull Player player, @NotNull ItemStack spyglass, @NotNull Entity entity) {
        super(player);
        this.spyglass = spyglass;
        this.entity = entity;
    }
    
    public @NotNull ItemStack getSpyglass() {
        return spyglass;
    }
    
    public @NotNull Entity getEntity() {
        return entity;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}