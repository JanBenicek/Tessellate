package org.texboobcat.tessellate.region;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.CollectingNeighborUpdater;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.level.redstone.Orientation;

// CollectingNeighborUpdater keeps mutable traversal state, so each worker needs its own instance.
public final class ThreadLocalNeighborUpdater extends CollectingNeighborUpdater {

    private final Level level;
    private final ThreadLocal<CollectingNeighborUpdater> perThread;

    public ThreadLocalNeighborUpdater(Level level, int maxChainedNeighborUpdates) {
        super(level, maxChainedNeighborUpdates);
        this.level = level;
        this.perThread = ThreadLocal.withInitial(
            () -> new CollectingNeighborUpdater(level, maxChainedNeighborUpdates));
    }

    private CollectingNeighborUpdater current() {
        return this.perThread.get();
    }

    @Override
    public void shapeUpdate(Direction direction, BlockState state, BlockPos pos,
                            BlockPos neighborPos, int flags, int recursionLevel) {
        if (!RedstoneUpdates.canRun(this.level, pos)) {
            BlockPos target = pos.immutable();
            BlockPos source = neighborPos.immutable();
            RedstoneUpdates.defer(() -> shapeUpdate(direction, state, target, source, flags, recursionLevel));
            return;
        }
        this.current().shapeUpdate(direction, state, pos, neighborPos, flags, recursionLevel);
    }

    @Override
    public void neighborChanged(BlockPos pos, Block block, Orientation orientation) {
        if (!RedstoneUpdates.canRun(this.level, pos)) {
            BlockPos target = pos.immutable();
            RedstoneUpdates.defer(() -> neighborChanged(target, block, orientation));
            return;
        }
        this.current().neighborChanged(pos, block, orientation);
    }

    @Override
    public void neighborChanged(BlockState state, BlockPos pos, Block block, Orientation orientation,
                                boolean movedByPiston) {
        if (!RedstoneUpdates.canRun(this.level, pos)) {
            BlockPos target = pos.immutable();
            RedstoneUpdates.defer(() -> neighborChanged(state, target, block, orientation, movedByPiston));
            return;
        }
        this.current().neighborChanged(state, pos, block, orientation, movedByPiston);
    }

    @Override
    public void updateNeighborsAtExceptFromFacing(BlockPos pos, Block block, Direction facing, Orientation orientation) {
        if (!RedstoneUpdates.canRun(this.level, pos)) {
            BlockPos source = pos.immutable();
            RedstoneUpdates.defer(() -> updateNeighborsAtExceptFromFacing(source, block, facing, orientation));
            return;
        }
        // Keep vanilla's single queued batch and recursion budget, including on the main thread.
        this.current().updateNeighborsAtExceptFromFacing(pos, block, facing, orientation);
    }
}
