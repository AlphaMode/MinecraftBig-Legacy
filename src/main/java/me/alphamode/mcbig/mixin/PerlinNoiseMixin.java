package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.BigPerlinNoiseExtension;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(PerlinNoise.class)
public abstract class PerlinNoiseMixin implements BigPerlinNoiseExtension {
    @Shadow public abstract double[] getRegion(double[] ds, double d, double e, double f, int i, int j, int k, double g, double h, double l);

    @Override
    public double[] getRegion(double[] ds, BigInteger i, BigInteger j, int k, int l, double d, double e, double f) {
        return this.getRegion(ds, i.doubleValue(), 10.0, j.doubleValue(), k, 1, l, d, 1.0, e);
    }
}
