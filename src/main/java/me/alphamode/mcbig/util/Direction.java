package me.alphamode.mcbig.util;

import net.minecraft.util.Vec3i;

// This class isn't used it's mostly just for me to remember vanilla facing values :P
public enum Direction {
    DOWN(0, new Vec3i(0, -1, 0)),
    UP(1, new Vec3i(0, 1, 0)),
    NORTH(2, new Vec3i(0, 0, -1)),
    SOUTH(3, new Vec3i(0, 0, 1)),
    WEST(4, new Vec3i(-1, 0, 0)),
    EAST(5, new Vec3i(1, 0, 0));

    private final int direction;
    private final Vec3i axis;

    Direction(int direction, Vec3i axis) {
        this.direction = direction;
        this.axis = axis;
    }

    public int direction() {
        return direction;
    }

    public Vec3i axis() {
        return axis;
    }
}
