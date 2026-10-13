package org.texboobcat.tessellate.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Unique;
import org.texboobcat.tessellate.Config;
import org.texboobcat.tessellate.region.RegionTracker;
import org.texboobcat.tessellate.region.RegionWorkers;
import org.texboobcat.tessellate.region.PhaseStats;

import java.util.Set;

// Measures the one method through which every PathNavigation path search is executed.
@Mixin(PathNavigation.class)
public abstract class PathNavigationPhaseMixin {
    @Shadow
    @Final
    protected Mob mob;

    // A block-entity update can call sendBlockUpdated directly, bypassing setBlock's
    // positional lease. Recompute must not mutate this mob's navigation/path heap while
    // its owning region is running AI on a worker.
    @WrapMethod(method = "recomputePath")
    private void tessellate$leasePathRecompute(Operation<Void> original) {
        tessellate$leaseNavigationOwner();
        original.call();
    }

    @Unique
    private void tessellate$leaseNavigationOwner() {
        if (!Config.asyncRegionLoops || RegionWorkers.isWorkerThread()
            || !(this.mob.level() instanceof ServerLevel level)) {
            return;
        }
        var index = RegionTracker.index(level);
        if (index != null) {
            index.leaseChunk(this.mob.chunkPosition().pack());
        }
    }

    @WrapMethod(method = "createPath(Ljava/util/Set;IZIF)Lnet/minecraft/world/level/pathfinder/Path;")
    private Path tessellate$measurePathSearch(Set<BlockPos> targets, int regionOffset,
                                           boolean offsetUpward, int accuracy, float followRange,
                                           Operation<Path> original) {
        tessellate$leaseNavigationOwner();
        long start = PhaseStats.begin(PhaseStats.Phase.PATHFINDING);
        try {
            return original.call(targets, regionOffset, offsetUpward, accuracy, followRange);
        } catch (Throwable failure) {
            PhaseStats.failed(PhaseStats.Phase.PATHFINDING);
            throw failure;
        } finally {
            PhaseStats.end(PhaseStats.Phase.PATHFINDING, start);
        }
    }
}
