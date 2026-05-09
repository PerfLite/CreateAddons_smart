package com.example.smartlogistics.client;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class CatnipMath {

    public static Vec3 voxelSpace(float x, float y, float z) {
        return new Vec3(x / 16f, y / 16f, z / 16f);
    }

    public static Vec3 rotateCentered(Vec3 vec, float deg, Direction.Axis axis) {
        double x = vec.x - 0.5;
        double y = vec.y - 0.5;
        double z = vec.z - 0.5;
        double rad = Math.toRadians(deg);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double nx = x, ny = y, nz = z;
        switch (axis) {
            case X -> {
                ny = y * cos - z * sin;
                nz = y * sin + z * cos;
            }
            case Y -> {
                nx = x * cos - z * sin;
                nz = x * sin + z * cos;
            }
            case Z -> {
                nx = x * cos - y * sin;
                ny = x * sin + y * cos;
            }
        }
        return new Vec3(nx + 0.5, ny + 0.5, nz + 0.5);
    }

    public static float horizontalAngle(Direction direction) {
        return direction.toYRot();
    }
}
