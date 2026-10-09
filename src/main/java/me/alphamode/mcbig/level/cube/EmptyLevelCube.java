package me.alphamode.mcbig.level.cube;

import net.minecraft.world.level.Level;

import java.math.BigInteger;

public class EmptyLevelCube extends LevelCube {
    public EmptyLevelCube(Level level, BigInteger x, BigInteger y, BigInteger z) {
        super(level, x, y, z);
        this.dontSave = true;
    }

    public EmptyLevelCube(Level level, byte[] blocks, BigInteger x, BigInteger y, BigInteger z) {
        super(level, blocks, x, y, z);
        this.dontSave = true;
    }
}
