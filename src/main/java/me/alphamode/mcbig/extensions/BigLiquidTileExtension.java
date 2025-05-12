package me.alphamode.mcbig.extensions;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.phys.Vec3;

import java.math.BigInteger;

public interface BigLiquidTileExtension {
    Vec3 getFlow(LevelSource level, BigInteger x, int y, BigInteger z);

    int getDepth(Level level, BigInteger x, int y, BigInteger z);

    void fizz(Level level, BigInteger x, int y, BigInteger z);
}
