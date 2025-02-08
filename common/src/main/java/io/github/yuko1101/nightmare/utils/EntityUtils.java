package io.github.yuko1101.nightmare.utils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.RangedWeaponItem;

import java.util.Objects;

public class EntityUtils {
    public static boolean isRangedAttack(MobEntity mob) {
        return mob.getMainHandStack().getItem() instanceof RangedWeaponItem rangedWeaponItem && mob.canUseRangedWeapon(rangedWeaponItem);
    }

    public static boolean canNavigateOnlyByBreakingBlocks(MobEntity mob, LivingEntity target) {
        if (target == null) {
            return false;
        }
        Path path = mob.getNavigation().findPathTo(target, 0);
        if (path == null) {
            return false;
        }

        if (path.getLength() == 0) {
            return false;
        }

        return Objects.requireNonNull(path.getEnd()).getPos().distanceTo(target.getPos()) > 2.0;
    }
}
