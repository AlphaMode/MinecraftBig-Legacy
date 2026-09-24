package me.alphamode.mcbig.world.level.levelgen.synth;

import me.alphamode.mcbig.math.BigMath;
import net.minecraft.util.Mth;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class BigPerlinNoise implements BigSynth {
    public static final int MAX_SCALE = 10;
    public static final BigInteger MODULO = BigInteger.valueOf(16777216L);
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

    //~ if >=1.0.0-beta.8.0.r 'BigDecimal' -> 'BigInteger'
    public double[] getRegion(double[] buffer, BigDecimal x, BigDecimal y, BigDecimal z, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale) {
        if (buffer == null) {
            buffer = new double[xSize * ySize * zSize];
        } else {
            Arrays.fill(buffer, 0.0);
        }

        double pow = 1.0;
        //? >=1.0.0-beta.8.0.r {
        /*BigDecimal _x = new BigDecimal(x);
        BigDecimal _y = new BigDecimal(y);
        BigDecimal _z = new BigDecimal(z);
        *///? } else {
        BigDecimal _x = x;
        BigDecimal _y = y;
        BigDecimal _z = z;
        //? }


        //? >=1.0.0-beta.8.0.r {
        /*BigDecimal xScaleb = new BigDecimal(xScale);
        BigDecimal yScaleb = new BigDecimal(yScale);
        BigDecimal zScaleb = new BigDecimal(zScale);
        *///? }

        for (int i = 0; i < this.levels; i++) {
            //? >=1.0.0-beta.8.0.r {
            /*BigDecimal powb = new BigDecimal(pow);
            var xx = _x.multiply(powb).multiply(xScaleb);
            var yy = _y.multiply(powb).multiply(yScaleb);
            var zz = _z.multiply(powb).multiply(zScaleb);
            BigInteger xb = BigMath.floor(xx);
            BigInteger zb = BigMath.floor(zz);
            xx = xx.subtract(new BigDecimal(xb));
            zz = zz.subtract(new BigDecimal(zb));
            xb = xb.remainder(MODULO);
            zb = zb.remainder(MODULO);
            xx = xx.add(new BigDecimal(xb));
            zz = zz.add(new BigDecimal(zb));
            this.noiseLevels[i].add(buffer, xx, yy, zz, xSize, ySize, zSize, xScale * pow, yScale * pow, zScale * pow, pow);
            pow /= 2.0;
            *///? } else {
            this.noiseLevels[i].add(buffer, _x, _y, _z, xSize, ySize, zSize, xScale * pow, yScale * pow, zScale * pow, pow);
            pow /= 2.0;
            //? }
        }

        return buffer;
    }

    public double[] getRegion(double[] sr, BigInteger x, BigInteger z, int xSize, int zSize, double xScale, double zScale, double pow) {
        //? >=1.0.0-beta.8.0.r {
        /*return this.getRegion(sr, x, BigInteger.TEN, z, xSize, 1, zSize, xScale, 1.0, zScale);
        *///? } else
        return this.getRegion(sr, new BigDecimal(x), BigDecimal.TEN, new BigDecimal(z), xSize, 1, zSize, xScale, 1.0, zScale);
    }
}
