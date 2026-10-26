package me.hapyl.lockout.world;

import com.google.common.collect.Sets;
import me.hapyl.lockout.Lockout;
import me.hapyl.lockout.util.HexId;
import me.hapyl.lockout.util.InternalTasks;
import org.bukkit.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class LockoutWorld {
    
    private static final int CHUNK_RADIUS = 8;
    private static final String WORLD_PREFIX = "lockout_";
    
    private final HexId id;
    private final World world;
    
    public LockoutWorld() {
        this.id = HexId.ofRandom();
        
        // Create the base of the world synchronously
        final WorldCreator worldCreator = new WorldCreator(WORLD_PREFIX + id);
        
        this.world = Objects.requireNonNull(worldCreator.createWorld());
        this.world.setGameRule(GameRules.PVP, false);
        this.world.setGameRule(GameRules.IMMEDIATE_RESPAWN, true);
        this.world.setGameRule(GameRules.KEEP_INVENTORY, true);
        this.world.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false);
        
        final Location location = this.world.getSpawnLocation();
        
        // Generate chunks asynchronously
        final int centreX = location.getBlockX() >> 4;
        final int centreZ = location.getBlockZ() >> 4;
        
        final Set<CompletableFuture<Chunk>> loadedChunks = Sets.newHashSet();
        
        for (int x = centreX - CHUNK_RADIUS; x <= centreX + CHUNK_RADIUS; x++) {
            for (int z = centreZ - CHUNK_RADIUS; z <= centreZ + CHUNK_RADIUS; z++) {
                loadedChunks.add(world.getChunkAtAsync(x, z));
            }
        }
        
        CompletableFuture.allOf(loadedChunks.toArray(new CompletableFuture[0])).thenRun(() -> InternalTasks.synchronize(() -> onWorldGenerated(world)));
    }
    
    public @NotNull HexId getId() {
        return id;
    }
    
    public abstract void onWorldGenerated(@NotNull World world);
    
    public static boolean isLockoutWorld(@NotNull World world) {
        return world.getName().startsWith(WORLD_PREFIX);
    }
    
}
