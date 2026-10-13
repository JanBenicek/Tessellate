package org.texboobcat.tessellate.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.texboobcat.tessellate.Config;
import org.texboobcat.tessellate.region.DeferredMainThreadWork;
import org.texboobcat.tessellate.region.MainThreadBoundaries;
import org.texboobcat.tessellate.region.RegionWorkers;

import java.util.Set;

// Long-distance and cross-dimension teleports are handed back to the main thread.
@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "teleport", at = @At("HEAD"), cancellable = true)
    private void tessellate$deferDimensionChange(TeleportTransition transition,
                                               CallbackInfoReturnable<Entity> cir) {
        if (!Config.asyncRegionLoops || !RegionWorkers.isWorkerThread()) {
            return;
        }
        Entity self = (Entity) (Object) this;
        DeferredMainThreadWork.defer(MainThreadBoundaries.Boundary.TELEPORT_DIMENSION,
            () -> self.teleport(transition));
        cir.setReturnValue(null);
    }

    @Inject(
        method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDD"
            + "Ljava/util/Set;FFZ)Z",
        at = @At("HEAD"), cancellable = true)
    private void tessellate$deferLevelTeleport(ServerLevel level, double x, double y, double z,
                                             Set<Relative> movement, float yaw, float pitch, boolean resetCamera,
                                             CallbackInfoReturnable<Boolean> cir) {
        if (!Config.asyncRegionLoops || !RegionWorkers.isWorkerThread()) {
            return;
        }
        Entity self = (Entity) (Object) this;
        DeferredMainThreadWork.defer(MainThreadBoundaries.Boundary.TELEPORT_DIMENSION,
            () -> self.teleportTo(level, x, y, z, movement, yaw, pitch, resetCamera));
        cir.setReturnValue(true);
    }

    @Inject(method = "teleportTo(DDD)V", at = @At("HEAD"), cancellable = true)
    private void tessellate$deferLocalTeleport(double x, double y, double z, CallbackInfo ci) {
        if (!Config.asyncRegionLoops || !RegionWorkers.isWorkerThread()) {
            return;
        }
        Entity self = (Entity) (Object) this;
        DeferredMainThreadWork.defer(MainThreadBoundaries.Boundary.TELEPORT_DIMENSION,
            () -> self.teleportTo(x, y, z));
        ci.cancel();
    }
}
