package org.texboobcat.tessellate.mixin;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkMap.class)
public interface ChunkMapInvoker {

    @org.spongepowered.asm.mixin.gen.Accessor("visibleChunkMap")
    it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap<ChunkHolder> tessellate$getVisibleChunks();

    @Invoker("getVisibleChunkIfPresent")
    ChunkHolder tessellate$getVisibleChunkIfPresent(long chunkPos);

    @Invoker("anyPlayerCloseEnoughForSpawning")
    boolean tessellate$anyPlayerCloseEnoughForSpawning(ChunkPos chunkPos);
}
