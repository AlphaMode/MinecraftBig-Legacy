package me.alphamode.mcbig.extensions.biome.newbiome;

import java.math.BigInteger;

public interface BigLayerExtension {
    default int[] getArea(BigInteger xo, BigInteger yo, int w, int h) {
        throw new UnsupportedOperationException();
    }
}
