package me.alphamode.mcbig.world.level.levelgen.synth;

import me.alphamode.mcbig.math.BigMath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class BigPerlinNoise implements BigSynth {
    private BigImprovedNoise[] noiseLevels;
    private int levels;

    public BigPerlinNoise(Random random, int levels) {
        this.levels = levels;
        this.noiseLevels = new BigImprovedNoise[levels];

        for (int i = 0; i < levels; i++) {
            this.noiseLevels[i] = new BigImprovedNoise(random);
        }
    }

    public double getValue(BigDecimal x, BigDecimal y) {
        double value = 0.0;
        double pow = 1.0;

        for (int i = 0; i < this.levels; i++) {
            BigDecimal bigPow = new BigDecimal(pow);
            value += this.noiseLevels[i].getValue(x.multiply(bigPow), y.multiply(bigPow)) / pow;
            pow /= 2.0;
        }

        return value;
    }

    public double getValue(BigDecimal x, BigDecimal y, BigDecimal z) {
        double value = 0;
        double pow = 1;

        for (int i = 0; i < levels; i++) {
            BigDecimal bigPow = new BigDecimal(pow);
            value += noiseLevels[i].getValue(x.multiply(bigPow), y.multiply(bigPow), z.multiply(bigPow)) / pow;
            pow /= 2;
        }

        return value;
    }

    public double[] getRegion(double[] buffer, BigDecimal x, BigDecimal y, BigDecimal z, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale) {
        if (buffer == null) {
            buffer = new double[xSize * ySize * zSize];
        } else {
            Arrays.fill(buffer, 0.0);
        }

        double pow = 1.0;

        for (int i = 0; i < this.levels; i++) {
            this.noiseLevels[i].add(buffer, x, y, z, xSize, ySize, zSize, xScale * pow, yScale * pow, zScale * pow, pow);
            pow /= 2.0;
        }

        return buffer;
    }

    public double[] getRegion(double[] sr, BigInteger x, BigInteger z, int xSize, int zSize, double xScale, double zScale, double pow) {
        return this.getRegion(sr, BigMath.decimalW(x), BigDecimal.TEN, BigMath.decimalW(z), xSize, 1, zSize, xScale, 1.0, zScale);
    }
}
