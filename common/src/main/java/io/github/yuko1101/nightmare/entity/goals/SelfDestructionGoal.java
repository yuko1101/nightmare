package io.github.yuko1101.nightmare.entity.goals;

import io.github.yuko1101.nightmare.mixin.accessors.CreeperEntityAccessor;
import io.github.yuko1101.nightmare.utils.EntityUtils;
import io.github.yuko1101.nightmare.utils.Vec3dUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

public class SelfDestructionGoal extends BlockInteractGoal implements IPathCreateGoal {
    public SelfDestructionGoal(CreeperEntity mob) {
        super(mob, (pos) -> {
            if (!pos.isWithinDistance(mob.getPos(), 2.0)) return false;
            if (mob.getTarget() == null) return false;
            if (!mob.getTarget().getBlockPos().isWithinDistance(mob.getPos(), 16.0)) return false;
            if (Vec3dUtils.getAngle(mob.getPos(), mob.getTarget().getPos(), pos.toCenterPos()) > Math.PI / 2) return false;
            BlockState blockState = mob.getWorld().getBlockState(pos);
            return !blockState.isAir() && blockState.getBlock().getBlastResistance() < 9;
        });
    }

    @Override
    public boolean canStart() {
        if (!getServerWorld(this.mob).getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            return false;
        }
        if (this.mob.getWorld().getDifficulty() != Difficulty.HARD) {
            return false;
        }
        if (this.mob.getTarget() == null || !this.mob.getTarget().isAlive()) {
            return false;
        }
        if (!super.canStart()) {
            return false;
        }

        return EntityUtils.canNavigateOnlyByBreakingBlocks(mob, mob.getTarget());
    }

    @Override
    public void start() {
        super.start();
        ((CreeperEntity) this.mob).ignite();
    }

    @Override
    public boolean shouldContinue() {
        return EntityUtils.canNavigateOnlyByBreakingBlocks(mob, mob.getTarget());
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.getDataTracker().set(CreeperEntityAccessor.getIgnited(), false);
    }
}
