package me.alphamode.mcbig.util;

import net.minecraft.Pos;

// This class isn't used it's mostly just for me to remember vanilla facing values :P
public enum Direction {
    DOWN(0, new Pos(0, -1, 0)),
    UP(1, new Pos(0, 1, 0)),
    NORTH(2, new Pos(0, 0, -1)),
    SOUTH(3, new Pos(0, 0, 1)),
    WEST(4, new Pos(-1, 0, 0)),
    EAST(5, new Pos(1, 0, 0));

    private final int direction;
    private final Pos axis;

    Direction(int direction, Pos axis) {
        this.direction = direction;
        this.axis = axis;
    }

    public int direction() {
        return direction;
    }

    public Pos axis() {
        return axis;
    }
}
