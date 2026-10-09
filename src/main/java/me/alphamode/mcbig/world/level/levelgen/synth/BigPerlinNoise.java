package me.alphamode.mcbig.world.level.levelgen.synth;

import me.alphamode.mcbig.math.BigMath;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Random;

public class BigPerlinNoise implements BigSynth {
    public static final BigInteger MODULO = BigInteger.valueOf(16777216L);
    private BigImprovedNoise[] noiseLevels;
    private int levels;
    private final boolean useModulo;

    public BigPerlinNoise(Random random, int levels) {
        this(random, levels, true);
    }

    public BigPerlinNoise(Random random, int levels, boolean useModulo) {
        this.levels = levels;
        this.useModulo = useModulo;
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
        /*BigDecimal xScaleb = new BigDecimal(xScale, MathContext.DECIMAL64);
        BigDecimal yScaleb = new BigDecimal(yScale, MathContext.DECIMAL64);
        BigDecimal zScaleb = new BigDecimal(zScale, MathContext.DECIMAL64);
        *///? }

        for (int i = 0; i < this.levels; i++) {
            //? >=1.0.0-beta.8.0.r {
            /*BigDecimal powb = new BigDecimal(pow, MathContext.DECIMAL64);
            var xx = _x.multiply(powb).multiply(xScaleb);
            var yy = _y.multiply(powb).multiply(yScaleb);
            var zz = _z.multiply(powb).multiply(zScaleb);
            // Modulo doesn't matter at all besides maybe keeping the precision down? but it doesn't matter at all and is just a waste of performance
            if (this.useModulo) {
                BigInteger xb = BigMath.floor(xx);
                BigInteger zb = BigMath.floor(zz);
                xx = xx.subtract(new BigDecimal(xb));
                zz = zz.subtract(new BigDecimal(zb));
                xb = xb.remainder(MODULO);
                zb = zb.remainder(MODULO);
                xx = xx.add(new BigDecimal(xb));
                zz = zz.add(new BigDecimal(zb));
            }
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
