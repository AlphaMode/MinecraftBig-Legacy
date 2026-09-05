package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.BigPerlinNoiseExtension;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(PerlinNoise.class)
public abstract class PerlinNoiseMixin implements BigPerlinNoiseExtension {

    //? <1.0.0-beta.8.0.r {
    @Shadow public abstract double[] getRegion(double[] buffer, double x, double y, double z, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale);
    //? }

    //? >=1.0.0-beta.8.0.r {
    /*@Shadow
    private int levels;

    @Shadow
    private ImprovedNoise[] noiseLevels;

    public double[] getRegion(double[] buffer, BigInteger x, BigInteger y, int z, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale) {
        if (buffer == null) {
            buffer = new double[xSize * ySize * zSize];
        } else {
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = 0.0;
            }
        }

        double pow = 1.0;

        for (int i = 0; i < this.levels; i++) {
            double xx = x.doubleValue() * pow * xScale;
            double yy = y.doubleValue() * pow * yScale;
            double zz = z * pow * zScale;
            long xb = Mth.lfloor(xx);
            long zb = Mth.lfloor(zz);
            xx -= xb;
            zz -= zb;
            xb %= 16777216L;
            zb %= 16777216L;
            xx += xb;
            zz += zb;
            this.noiseLevels[i].add(buffer, xx, yy, zz, xSize, ySize, zSize, xScale * pow, yScale * pow, zScale * pow, pow);
            pow /= 2.0;
        }

        return buffer;
    }

    @Override
    public double[] getRegion(double[] buffer, BigInteger x, int y, BigInteger z, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale) {
        if (buffer == null) {
            buffer = new double[xSize * ySize * zSize];
        } else {
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = 0.0;
            }
        }

        double pow = 1.0;

        for (int i = 0; i < this.levels; i++) {
            double xx = x.doubleValue() * pow * xScale;
            double yy = y * pow * yScale;
            double zz = z.doubleValue() * pow * zScale;
            long xb = Mth.lfloor(xx);
            long zb = Mth.lfloor(zz);
            xx -= xb;
            zz -= zb;
            xb %= 16777216L;
            zb %= 16777216L;
            xx += xb;
            zz += zb;
            this.noiseLevels[i].add(buffer, xx, yy, zz, xSize, ySize, zSize, xScale * pow, yScale * pow, zScale * pow, pow);
            pow /= 2.0;
        }

        return buffer;
    }
    *///? }


    @Override
    public double[] getRegion(double[] sr, BigInteger x, BigInteger z, int xSize, int zSize, double xScale, double zScale, double pow) {
        //? >=1.0.0-beta.8.0.r {
        /*return this.getRegion(sr, x, 10, z, xSize, 1, zSize, xScale, 1.0, zScale);
        *///? } else
        return this.getRegion(sr, x.doubleValue(), 10.0, z.doubleValue(), xSize, 1, zSize, xScale, 1.0, zScale);
    }
}
