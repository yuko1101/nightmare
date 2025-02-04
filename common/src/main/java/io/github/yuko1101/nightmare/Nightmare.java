package io.github.yuko1101.nightmare;

import io.github.yuko1101.nightmare.entity.goals.BlockBreakGoal;
import io.github.yuko1101.nightmare.utils.EntityUtils;
import io.github.yuko1101.nightmare.utils.Vec3dUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.Difficulty;

public final class Nightmare {
    public static final String MOD_ID = "nightmare";

    public static void init() {
    }

    public static BlockBreakGoal createBlockBreakGoal(MobEntity mob) {
        return new BlockBreakGoal(mob, difficulty -> difficulty == Difficulty.HARD, pos -> {
            if (!pos.isWithinDistance(mob.getPos(), 4.0)) return false;
            if (mob.getTarget() == null) return false;
            if (!mob.getTarget().getBlockPos().isWithinDistance(mob.getPos(), 8.0)) return false;
            if (Vec3dUtils.getAngle(mob.getPos(), mob.getTarget().getPos(), pos.toCenterPos()) > Math.PI / 2) return false;
            BlockState blockState = mob.getWorld().getBlockState(pos);
            return !blockState.isAir() && blockState.getHardness(mob.getWorld(), pos) < 100F; // TODO: better filtering
        }, mobEntity -> mobEntity.getTarget() != null && mobEntity.getTarget().isAlive() && !EntityUtils.isRangedAttack(mobEntity));
    }
}
