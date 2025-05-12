package me.alphamode.mcbig.extensions;

import java.math.BigInteger;

public interface BigPerlinNoiseExtension {
    double[] getRegion(double[] ds, BigInteger i, BigInteger j, int k, int l, double d, double e, double f);
}
