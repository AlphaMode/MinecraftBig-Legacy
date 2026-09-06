package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.biome.BigBiomeSourceExtension;
import me.alphamode.mcbig.level.BigTilePos;
import net.minecraft.world.level.TilePos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Mixin(FixedBiomeSource.class)
public abstract class FixedBiomeSourceMixin extends BiomeSource implements BigBiomeSourceExtension {
    @Shadow private Biome biome;

    //~ if >=1.0.0-beta.8.0.r 'double' -> 'float' {
    @Shadow private double temperature;
    @Shadow private double downfall;
    //~ }

    @Override
    public Biome getBiome(ChunkPos pos) {
        return this.biome;
    }

    @Override
    public Biome getBiome(BigInteger x, BigInteger z) {
        return this.biome;
    }

    @Override
    //~ if >=1.0.0-beta.8.0.r 'double' -> 'float'
    public double getTemperature(BigInteger x, BigInteger z) {
        return this.temperature;
    }

    @Override
    public Biome[] getBiomeBlock(BigInteger x, BigInteger z, int k, int l) {
        this.biomes = this.getBiomeBlock(this.biomes, x, z, k, l);
        return this.biomes;
    }

    //~ if >=1.0.0-beta.8.0.r 'double' -> 'float' {
    @Override
    public double[] getTemperatureBlock(double[] ds, BigInteger x, BigInteger z, int k, int l) {
        if (ds == null || ds.length < k * l) {
            ds = new double[k * l];
        }

        Arrays.fill(ds, 0, k * l, this.temperature);
        return ds;
    }
    //~ }

    @Override
    public Biome[] getBiomeBlock(Biome[] biomes, BigInteger x, BigInteger z, int w, int h) {
        if (biomes == null || biomes.length < w * h) {
            biomes = new Biome[w * h];
        }

        //? <1.0.0-beta.8.0.r {
        if (this.temperatures == null || this.temperatures.length < w * h) {
            this.temperatures = new double[w * h];
            this.downfalls = new double[w * h];
        }
        //? }

        Arrays.fill(biomes, 0, w * h, this.biome);
        //? <1.0.0-beta.8.0.r {
        Arrays.fill(this.downfalls, 0, w * h, this.downfall);
        Arrays.fill(this.temperatures, 0, w * h, this.temperature);
        //? }
        return biomes;
    }

    //? >=1.0.0-beta.8.0.r {
    /*@Override
    public float[] getDownfallBlock(float[] downfalls, BigInteger x, BigInteger z, int w, int h) {
        if (downfalls == null || downfalls.length < w * h) {
            downfalls = new float[w * h];
        }

        Arrays.fill(downfalls, 0, w * h, this.downfall);
        return downfalls;
    }

    @Override
    public float getDownfall(BigInteger x, BigInteger z) {
        return this.downfall;
    }

    @Override
    public BigTilePos findBiome(BigInteger x, BigInteger z, int r, List<Biome> toFind, Random random) {
        BigInteger rb = BigInteger.valueOf(r);
        return toFind.contains(this.biome) ? new BigTilePos(x.subtract(rb).add(BigInteger.valueOf(random.nextInt(r * 2 + 1))), 0, z.subtract(rb).add(BigInteger.valueOf(random.nextInt(r * 2 + 1)))) : null;
    }

    @Override
    public boolean containsOnly(BigInteger x, BigInteger z, int r, List<Biome> allowed) {
        return allowed.contains(this.biome);
    }
    *///? }
}
