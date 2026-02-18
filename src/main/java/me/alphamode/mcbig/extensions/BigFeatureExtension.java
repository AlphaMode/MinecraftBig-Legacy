package me.alphamode.mcbig.extensions;

import net.minecraft.world.level.Level;

import java.math.BigInteger;
import java.util.Random;

public interface BigFeatureExtension {
    default boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        return false;
    }
}
