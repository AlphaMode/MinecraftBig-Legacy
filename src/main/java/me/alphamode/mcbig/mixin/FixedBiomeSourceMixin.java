package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.BigBiomeSourceExtension;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Arrays;

@Mixin(FixedBiomeSource.class)
public abstract class FixedBiomeSourceMixin extends BiomeSource implements BigBiomeSourceExtension {
    @Shadow private Biome biome;

    @Shadow
    private double f_61667819;

    @Shadow
    private double f_95648801;

    @Override
    public Biome getBiome(ChunkPos pos) {
        return this.biome;
    }

    @Override
    public Biome getBiome(BigInteger x, BigInteger z) {
        return this.biome;
    }

    @Override
    public double getTemperature(BigInteger x, BigInteger z) {
        return this.f_61667819;
    }

    @Override
    public Biome[] getBiomeBlock(BigInteger x, BigInteger z, int k, int l) {
        this.biomes = this.getBiomeBlock(this.biomes, x, z, k, l);
        return this.biomes;
    }

    @Override
    public double[] getTemperatureBlock(double[] ds, BigInteger x, BigInteger z, int k, int l) {
        if (ds == null || ds.length < k * l) {
            ds = new double[k * l];
        }

        Arrays.fill(ds, 0, k * l, this.f_61667819);
        return ds;
    }

    @Override
    public Biome[] getBiomeBlock(Biome[] biomes, BigInteger x, BigInteger z, int k, int l) {
        if (biomes == null || biomes.length < k * l) {
            biomes = new Biome[k * l];
        }

        if (this.temperatures == null || this.temperatures.length < k * l) {
            this.temperatures = new double[k * l];
            this.downfalls = new double[k * l];
        }

        Arrays.fill(biomes, 0, k * l, this.biome);
        Arrays.fill(this.downfalls, 0, k * l, this.f_95648801);
        Arrays.fill(this.temperatures, 0, k * l, this.f_61667819);
        return biomes;
    }
}
