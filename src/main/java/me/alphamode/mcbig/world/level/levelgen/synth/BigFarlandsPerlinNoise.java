package me.alphamode.mcbig.world.level.levelgen.synth;

import me.alphamode.mcbig.math.BigMath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Random;

public class BigFarlandsPerlinNoise {
    private BigFarlandsImprovedNoise[] noiseLevels;
    private int levels;

    public BigFarlandsPerlinNoise(Random random, int levels) {
        this.levels = levels;
        this.noiseLevels = new BigFarlandsImprovedNoise[levels];

        for (int i = 0; i < levels; i++) {
            this.noiseLevels[i] = new BigFarlandsImprovedNoise(random);
        }
    }

    public BigDecimal getValue(BigDecimal x, BigDecimal y) {
        BigDecimal value = BigDecimal.ZERO;
        double pow = 1.0;

        for (int i = 0; i < this.levels; i++) {
            BigDecimal bigPow = new BigDecimal(pow);
            value = value.add(this.noiseLevels[i].getValue(x.multiply(bigPow), y.multiply(bigPow)).divide(bigPow, RoundingMode.HALF_EVEN));
            pow /= 2.0;
        }

        return value;
    }

    public BigDecimal getValue(BigDecimal x, BigDecimal y, BigDecimal z) {
        BigDecimal value = BigDecimal.ZERO;
        double pow = 1;

        for (int i = 0; i < levels; i++) {
            BigDecimal bigPow = new BigDecimal(pow);
            value = value.add(noiseLevels[i].getValue(x.multiply(bigPow), y.multiply(bigPow), z.multiply(bigPow)).divide(bigPow, RoundingMode.HALF_EVEN));
            pow /= 2;
        }

        return value;
    }

    public BigDecimal[] getRegion(BigDecimal[] buffer, BigDecimal x, BigDecimal y, BigDecimal z, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale) {
        if (buffer == null) {
            buffer = new BigDecimal[xSize * ySize * zSize];
            Arrays.fill(buffer, BigDecimal.ZERO);
        } else {
            Arrays.fill(buffer, BigDecimal.ZERO);
        }

        double pow = 1.0;

        for (int i = 0; i < this.levels; i++) {
            this.noiseLevels[i].add(buffer, x, y, z, xSize, ySize, zSize, xScale * pow, yScale * pow, zScale * pow, pow);
            pow /= 2.0;
        }

        return buffer;
    }

    public BigDecimal[] getRegion(BigDecimal[] sr, BigInteger x, BigInteger z, int xSize, int zSize, double xScale, double zScale, double pow) {
        return this.getRegion(sr, BigMath.decimalW(x), BigDecimal.TEN, BigMath.decimalW(z), xSize, 1, zSize, xScale, 1.0, zScale);
    }
}
