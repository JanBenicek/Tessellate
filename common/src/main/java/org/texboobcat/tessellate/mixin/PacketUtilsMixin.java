package org.texboobcat.tessellate.mixin;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.network.PacketProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.texboobcat.tessellate.Config;
import org.texboobcat.tessellate.region.LevelRegionIndex;
import org.texboobcat.tessellate.region.RegionTracker;

// A main-thread player packet leases the player's region before its handler continues.
@Mixin(PacketUtils.class)
public abstract class PacketUtilsMixin {

    @Inject(
        method = "ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;"
            + "Lnet/minecraft/network/PacketListener;"
            + "Lnet/minecraft/network/PacketProcessor;)V",
        at = @At("RETURN"))
    private static void tessellate$leasePlayerPacket(Packet<?> packet, PacketListener listener,
                                                   PacketProcessor processor, CallbackInfo ci) {
        if (!Config.asyncRegionLoops
            || !(listener instanceof ServerGamePacketListenerImpl game)) {
            return;
        }
        LevelRegionIndex index = RegionTracker.index(game.player.level());
        if (index != null) {
            index.leaseChunk(game.player.chunkPosition().pack());
        }
    }
}
