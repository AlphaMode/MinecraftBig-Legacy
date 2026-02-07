package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.BigBiomeSourceExtension;
import me.alphamode.mcbig.level.chunk.BigChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkPos;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(BiomeSource.class)
public class BiomeSourceMixin implements BigBiomeSourceExtension {
    @Shadow private PerlinSimplexNoise temperatureMap;

    @Shadow public double[] temperatures;

    @Shadow public Biome[] biomes;

    @Shadow private PerlinSimplexNoise noiseMap;

    @Shadow public double[] noises;

    @Shadow public double[] downfalls;

    @Shadow private PerlinSimplexNoise downfallMap;

    @Override
    public Biome getBiome(BigChunkPos pos) {
        return getBiome(pos.x().shiftRight(4), pos.z().shiftRight(4));
    }

    @Override
    public Biome getBiome(BigInteger x, BigInteger z) {
        return getBiomeBlock(x, z, 1, 1)[0];
    }

    @Override
    public double getTemperature(BigInteger x, BigInteger z) {
        this.temperatures = this.temperatureMap.getRegion(this.temperatures, x.doubleValue(), z.doubleValue(), 1, 1, 0.025F, 0.025F, 0.5);
        return this.temperatures[0];
    }

    @Override
    public Biome[] getBiomeBlock(BigInteger x, BigInteger z, int k, int l) {
        this.biomes = getBiomeBlock(this.biomes, x, z, k, l);
        return this.biomes;
    }

    @Override
    public double[] getTemperatureBlock(double[] temp, BigInteger x, BigInteger z, int k, int l) {
        if (temp == null || temp.length < k * l) {
            temp = new double[k * l];
        }

        temp = this.temperatureMap.getRegion(temp, (double)x.doubleValue(), (double)z.doubleValue(), k, l, 0.025F, 0.025F, 0.25);
        this.noises = this.noiseMap.getRegion(this.noises, (double)x.doubleValue(), (double)z.doubleValue(), k, l, 0.25, 0.25, 0.5882352941176471);
        int var6 = 0;

        for(int var7 = 0; var7 < k; ++var7) {
            for(int var8 = 0; var8 < l; ++var8) {
                double var9 = this.noises[var6] * 1.1 + 0.5;
                double var11 = 0.01;
                double var13 = 1.0 - var11;
                double var15 = (temp[var6] * 0.15 + 0.7) * var13 + var9 * var11;
                var15 = 1.0 - (1.0 - var15) * (1.0 - var15);
                if (var15 < 0.0) {
                    var15 = 0.0;
                }

                if (var15 > 1.0) {
                    var15 = 1.0;
                }

                temp[var6] = var15;
                ++var6;
            }
        }

        return temp;
    }

    @Override
    public Biome[] getBiomeBlock(Biome[] biomes, BigInteger x, BigInteger z, int k, int l) {
        if (biomes == null || biomes.length < k * l) {
            biomes = new Biome[k * l];
        }

        this.temperatures = this.temperatureMap.getRegion(this.temperatures, (double)x.doubleValue(), (double)z.doubleValue(), k, k, 0.025F, 0.025F, 0.25);
        this.downfalls = this.downfallMap.getRegion(this.downfalls, (double)x.doubleValue(), (double)z.doubleValue(), k, k, 0.05F, 0.05F, 0.3333333333333333);
        this.noises = this.noiseMap.getRegion(this.noises, (double)x.doubleValue(), (double)z.doubleValue(), k, k, 0.25, 0.25, 0.5882352941176471);
        int var6 = 0;

        for(int var7 = 0; var7 < k; ++var7) {
            for(int var8 = 0; var8 < l; ++var8) {
                double var9 = this.noises[var6] * 1.1 + 0.5;
                double var11 = 0.01;
                double var13 = 1.0 - var11;
                double var15 = (this.temperatures[var6] * 0.15 + 0.7) * var13 + var9 * var11;
                var11 = 0.002;
                var13 = 1.0 - var11;
                double var17 = (this.downfalls[var6] * 0.15 + 0.5) * var13 + var9 * var11;
                var15 = 1.0 - (1.0 - var15) * (1.0 - var15);
                if (var15 < 0.0) {
                    var15 = 0.0;
                }

                if (var17 < 0.0) {
                    var17 = 0.0;
                }

                if (var15 > 1.0) {
                    var15 = 1.0;
                }

                if (var17 > 1.0) {
                    var17 = 1.0;
                }

                this.temperatures[var6] = var15;
                this.downfalls[var6] = var17;
                biomes[var6++] = Biome.getBiome(var15, var17);
            }
        }

        return biomes;
    }
}
