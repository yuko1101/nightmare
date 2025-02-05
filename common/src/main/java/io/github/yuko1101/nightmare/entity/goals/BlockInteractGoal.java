package io.github.yuko1101.nightmare.entity.goals;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class BlockInteractGoal extends Goal {
    protected final MobEntity mob;
    @Nullable
    protected BlockPos blockPos = null;
    protected Predicate<BlockPos> blockPosPredicate;
    protected boolean shouldStop;
    private double offsetX;
    private double offsetZ;

    public BlockInteractGoal(MobEntity mob, Predicate<BlockPos> blockPosPredicate) {
        this.mob = mob;
        this.blockPosPredicate = blockPosPredicate;
        if (!NavigationConditions.hasMobNavigation(mob)) {
            throw new IllegalArgumentException("BlockInteractGoal requires mob to have MobNavigation");
        }
    }

    @Override
    public boolean canStart() {
        if (!NavigationConditions.hasMobNavigation(this.mob)) {
            return false;
        } else if (!this.mob.horizontalCollision) {
            return false;
        } else {
            MobNavigation mobNavigation = (MobNavigation) this.mob.getNavigation();
            Path path = mobNavigation.getCurrentPath();
            if (path != null && !path.isFinished()) {
                for (int i = 0; i < Math.min(path.getCurrentNodeIndex() + 2, path.getLength()); i++) {
                    PathNode pathNode = path.getNode(i);
                    for (int ix = 0; ix <= 2; ix++) {
                        for (int iy = 0; iy <= 3; iy++) {
                            for (int iz = 0; iz <= 2; iz++) {
                                BlockPos blockPos = new BlockPos(pathNode.x + getZigzag(ix), pathNode.y - getZigzag(iy) + 2, pathNode.z + getZigzag(iz));
                                if (blockPosPredicate.test(blockPos)) {
                                    this.blockPos = blockPos;
                                    return true;
                                }
                            }
                        }
                    }
                }

                BlockPos blockPos = this.mob.getBlockPos().up();
                if (blockPosPredicate.test(blockPos)) {
                    this.blockPos = blockPos;
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean shouldContinue() {
        return !this.shouldStop;
    }

    @Override
    public void start() {
        if (this.blockPos == null) {
            throw new IllegalStateException("BlockInteractGoal#canStart should be called before BlockInteractGoal#start");
        }
        this.shouldStop = false;
        this.offsetX = this.blockPos.getX() + 0.5 - this.mob.getX();
        this.offsetZ = this.blockPos.getZ() + 0.5 - this.mob.getZ();
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.blockPos == null) {
            throw new IllegalStateException("BlockInteractGoal#canStart should be called before BlockInteractGoal#tick");
        }
        double f = this.blockPos.getX() + 0.5 - this.mob.getX();
        double g = this.blockPos.getZ() + 0.5 - this.mob.getZ();
        double h = this.offsetX * f + this.offsetZ * g;
        if (h < 0.0F) {
            this.shouldStop = true;
        }
    }

    // 0, 1, -1, 2, -2, 3, -3, ...
    private int getZigzag(int index) {
        if (index % 2 == 0) {
            return -index / 2;
        } else {
            return index / 2 + 1;
        }
    }

    @Nullable
    protected BlockState getBlockState() {
        if (this.blockPos == null) {
            return null;
        }
        return this.mob.getWorld().getBlockState(this.blockPos);
    }
}
