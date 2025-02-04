package io.github.yuko1101.nightmare.utils;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.RangedWeaponItem;

public class EntityUtils {
    public static boolean isRangedAttack(MobEntity mob) {
        return mob.getMainHandStack().getItem() instanceof RangedWeaponItem rangedWeaponItem && mob.canUseRangedWeapon(rangedWeaponItem);
    }
}
