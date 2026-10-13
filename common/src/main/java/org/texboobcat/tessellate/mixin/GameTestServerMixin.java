package org.texboobcat.tessellate.mixin;

import net.minecraft.gametest.framework.GameTestServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.texboobcat.tessellate.Config;

import java.util.concurrent.locks.LockSupport;

// Independent region loops use real-time deadlines. Vanilla's unpaced GameTestServer can
// consume a test's simulation-tick budget before those workers have time to simulate it.
// Only the dedicated test server is paced; ordinary client and server ticks are unaffected.
@Mixin(GameTestServer.class)
public abstract class GameTestServerMixin {
    @Unique
    private long tessellate$nextTestTick;

    @Inject(method = "waitUntilNextTick", at = @At("HEAD"))
    private void tessellate$paceIndependentRegionTests(CallbackInfo ci) {
        if (!Config.asyncRegionLoops) {
            return;
        }
        long now = System.nanoTime();
        if (this.tessellate$nextTestTick == 0L) {
            this.tessellate$nextTestTick = now;
        }
        this.tessellate$nextTestTick = Math.max(this.tessellate$nextTestTick + 50_000_000L, now);
        long remaining = this.tessellate$nextTestTick - now;
        while (remaining > 0L && !Thread.currentThread().isInterrupted()) {
            LockSupport.parkNanos(remaining);
            remaining = this.tessellate$nextTestTick - System.nanoTime();
        }
    }
}
