package me.alphamode.mcbig.extensions;

import java.math.BigInteger;

public interface BigPerlinNoiseExtension {
    //? >=1.0.0-beta.8.0.r {
    /*default double[] getRegion(double[] buffer, BigInteger x, BigInteger y, int z, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale) {
        throw new UnsupportedOperationException();
    }

    default double[] getRegion(double[] buffer, BigInteger x, int y, BigInteger z, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale) {
        throw new UnsupportedOperationException();
    }
    *///? }

    default double[] getRegion(double[] sr, BigInteger x, BigInteger z, int xSize, int zSize, double xScale, double zScale, double pow) {
        throw new UnsupportedOperationException();
    }
}
