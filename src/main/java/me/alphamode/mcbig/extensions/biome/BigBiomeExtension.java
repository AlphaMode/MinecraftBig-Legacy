package me.alphamode.mcbig.extensions.biome;

import net.minecraft.world.level.Level;

import java.math.BigInteger;
import java.util.Random;

public interface BigBiomeExtension {
    default void decorate(Level level, Random random, BigInteger xo, BigInteger zo) {
        throw new UnsupportedOperationException();
    }
}
