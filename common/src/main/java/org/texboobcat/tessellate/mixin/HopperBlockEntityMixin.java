package org.texboobcat.tessellate.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.texboobcat.tessellate.region.RedstoneUpdates;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {
    // Inventory lookup can load the output/above chunk, and a double chest also reads
    // its other half. Probe ownership and FULL availability before changing cooldowns
    // or items; replay the entire tick on main rather than treating missing chunks as air.
    @WrapMethod(method = "pushItemsTick")
    private static void tessellate$deferBoundaryHopper(Level level, BlockPos pos,
            BlockState state, HopperBlockEntity hopper, Operation<Void> original) {
        if (RedstoneUpdates.canRun(level, pos, 2)) {
            original.call(level, pos, state, hopper);
            return;
        }
        BlockPos target = pos.immutable();
        RedstoneUpdates.defer(() -> {
            if (!hopper.isRemoved() && level.getBlockEntity(target) == hopper) {
                original.call(level, target, level.getBlockState(target), hopper);
            }
        });
    }
}
