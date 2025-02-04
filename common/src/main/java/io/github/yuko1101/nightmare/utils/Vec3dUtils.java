package io.github.yuko1101.nightmare.utils;

import net.minecraft.util.math.Vec3d;

public class Vec3dUtils {
    public static double getAngle(Vec3d origin, Vec3d target1, Vec3d target2) {
        Vec3d v1 = target1.subtract(origin).normalize();
        Vec3d v2 = target2.subtract(origin).normalize();
        return Math.acos(v1.dotProduct(v2));
    }
}
