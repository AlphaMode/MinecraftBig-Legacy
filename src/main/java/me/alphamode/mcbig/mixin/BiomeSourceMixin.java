package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.biome.BigBiomeSourceExtension;
import me.alphamode.mcbig.level.chunk.BigChunkPos;

import me.alphamode.mcbig.math.BigConstants;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
//? <1.0.0-beta.8.0.r {
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
//?} else {
/*import net.minecraft.util.IntCache;
import net.minecraft.world.level.newbiome.layer.Layer;
import me.alphamode.mcbig.world.level.biome.BigBiomeCache;
*///?}


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(BiomeSource.class)
public class BiomeSourceMixin implements BigBiomeSourceExtension {
    @Shadow
    public Biome[] biomes;

    //? >=1.0.0-beta.8.0.r {
    /*private BigBiomeCache cache = new BigBiomeCache((BiomeSource) (Object) this);

    @Shadow
    private Layer downfallLayer;
    @Shadow
    private Layer tempLayer;
    @Shadow
    private Layer layer;
    @Shadow
    private Layer zoomedLayer;

    *///? } else {
    @Shadow
    private PerlinSimplexNoise temperatureMap;
    @Shadow
    private PerlinSimplexNoise downfallMap;

    @Shadow
    public double[] temperatures;

    @Shadow
    private PerlinSimplexNoise noiseMap;

    @Shadow
    public double[] noises;

    @Shadow
    public double[] downfalls;
    //? }

    @Override
    public Biome getBiome(BigChunkPos pos) {
        return getBiome(pos.x().shiftRight(4), pos.z().shiftRight(4));
    }

    @Override
    public Biome getBiome(BigInteger x, BigInteger z) {
        //? >=1.0.0-beta.8.0.r {
        /*return this.cache.getBiome(x, z);
         *///? } else {
        return getBiomeBlock(x, z, 1, 1)[0];
        //? }
    }

    //? >=1.0.0-beta.8.0.r {
    /*@Override
    public float getDownfall(BigInteger x, BigInteger z) {
        return this.cache.getDownfall(x, z);
    }

    @Override
    public float[] getDownfallBlock(float[] downfalls, BigInteger x, BigInteger z, int w, int h) {
        IntCache.releaseAll();
        if (downfalls == null || downfalls.length < w * h) {
            downfalls = new float[w * h];
        }

        int[] result = this.downfallLayer.getArea(x, z, w, h);

        for (int i = 0; i < w * h; i++) {
            float d = result[i] / 65536.0F;
            if (d > 1.0F) {
                d = 1.0F;
            }

            downfalls[i] = d;
        }

        return downfalls;
    }

    @Override
    public float getTemperature(BigInteger x, BigInteger z) {
        return this.cache.getTemperature(x, z);
    }

    @Override
    public float[] getTemperatureBlock(float[] temperatures, BigInteger x, BigInteger z, int w, int h) {
        IntCache.releaseAll();
        if (temperatures == null || temperatures.length < w * h) {
            temperatures = new float[w * h];
        }

        int[] result = this.tempLayer.getArea(x, z, w, h);

        for (int i = 0; i < w * h; i++) {
            float t = result[i] / 65536.0F;
            if (t > 1.0F) {
                t = 1.0F;
            }

            temperatures[i] = t;
        }

        return temperatures;
    }

    @Override
    public Biome[] getRawBiomeBlock(Biome[] biomes, BigInteger x, BigInteger z, int w, int h) {
        IntCache.releaseAll();
        if (biomes == null || biomes.length < w * h) {
            biomes = new Biome[w * h];
        }

        int[] result = this.layer.getArea(x, z, w, h);

        for (int i = 0; i < w * h; i++) {
            biomes[i] = Biome.biomes[result[i]];
        }

        return biomes;
    }
    *///? } else {
    @Override
    public double getTemperature(BigInteger x, BigInteger z) {
        this.temperatures = this.temperatureMap.getRegion(this.temperatures, x.doubleValue(), z.doubleValue(), 1, 1, 0.025F, 0.025F, 0.5);
        return this.temperatures[0];
    }
    //? }

    @Override
    public Biome[] getBiomeBlock(BigInteger x, BigInteger z, int w, int h) {
        //? >=1.0.0-beta.8.0.r {
        /*if (w == 16 && h == 16 && (x.and(BigConstants.FIFTEEN).intValue()) == 0 && (z.and(BigConstants.FIFTEEN).intValue()) == 0) {
            return this.cache.getBiomeBlockAt(x, z);
        }
        *///? }

        this.biomes = getBiomeBlock(this.biomes, x, z, w, h);
        return this.biomes;
    }

    //? <1.0.0-beta.8.0.r {
    @Override
    public double[] getTemperatureBlock(double[] temperatures, BigInteger x, BigInteger z, int w, int h) {
        if (temperatures == null || temperatures.length < w * h) {
            temperatures = new double[w * h];
        }

        temperatures = this.temperatureMap.getRegion(temperatures, (double) x.doubleValue(), (double) z.doubleValue(), w, h, 0.025F, 0.025F, 0.25);
        this.noises = this.noiseMap.getRegion(this.noises, (double) x.doubleValue(), (double) z.doubleValue(), w, h, 0.25, 0.25, 0.5882352941176471);

        int pp = 0;
        for (int yy = 0; yy < w; ++yy) {
            for (int xx = 0; xx < h; ++xx) {
                double noise = this.noises[pp] * 1.1 + 0.5;

                double split2 = 0.01;
                double split1 = 1.0 - split2;
                double temperature = (temperatures[pp] * 0.15 + 0.7) * split1 + noise * split2;
                temperature = 1 - (1 - temperature) * (1 - temperature);

                if (temperature < 0) temperature = 0;
                if (temperature > 1) temperature = 1;

                temperatures[pp] = temperature;
                ++pp;
            }
        }

        return temperatures;
    }
    //? }

    @Override
    public Biome[] getBiomeBlock(Biome[] biomes, BigInteger x, BigInteger z, int w, int h) {
        //? >=1.0.0-beta.8.0.r {
        /*return this.getBiomeBlock(biomes, x, z, w, h, true);
         *///? } else {
        if (biomes == null || biomes.length < w * h) {
            biomes = new Biome[w * h];
        }

        this.temperatures = this.temperatureMap.getRegion(this.temperatures, x.doubleValue(), z.doubleValue(), w, w, 0.025F, 0.025F, 0.25);
        this.downfalls = this.downfallMap.getRegion(this.downfalls, x.doubleValue(), z.doubleValue(), w, w, 0.05F, 0.05F, 0.3333333333333333);
        this.noises = this.noiseMap.getRegion(this.noises, x.doubleValue(), z.doubleValue(), w, w, 0.25, 0.25, 0.5882352941176471);

        int pp = 0;
        for (int yy = 0; yy < w; yy++) {
            for (int xx = 0; xx < h; xx++) {
                double noise = this.noises[pp] * 1.1 + 0.5;

                double split2 = 0.01;
                double split1 = 1.0 - split2;
                double temperature = (this.temperatures[pp] * 0.15 + 0.7) * split1 + noise * split2;
                split2 = 0.002;
                split1 = 1.0 - split2;
                double downfall = (this.downfalls[pp] * 0.15 + 0.5) * split1 + noise * split2;
                temperature = 1.0 - (1.0 - temperature) * (1.0 - temperature);
                if (temperature < 0.0) temperature = 0.0;
                if (downfall < 0.0) downfall = 0.0;
                if (temperature > 1.0) temperature = 1.0;
                if (downfall > 1.0) downfall = 1.0;

                this.temperatures[pp] = temperature;
                this.downfalls[pp] = downfall;
                biomes[pp++] = Biome.getBiome(temperature, downfall);
            }
        }

        return biomes;
        //? }
    }

    //? >=1.0.0-beta.8.0.r {
    /*@Override
    public Biome[] getBiomeBlock(Biome[] biomes, BigInteger x, BigInteger z, int w, int h, boolean useCache) {
        IntCache.releaseAll();
        if (biomes == null || biomes.length < w * h) {
            biomes = new Biome[w * h];
        }

        if (useCache && w == 16 && h == 16 && (x.and(BigConstants.FIFTEEN).intValue()) == 0 && (z.and(BigConstants.FIFTEEN).intValue()) == 0) {
            Biome[] tmp = this.cache.getBiomeBlockAt(x, z);
            System.arraycopy(tmp, 0, biomes, 0, w * h);
            return biomes;
        } else {
            int[] result = this.zoomedLayer.getArea(x, z, w, h);

            for (int i = 0; i < w * h; i++) {
                biomes[i] = Biome.biomes[result[i]];
            }

            return biomes;
        }
    }
    *///? }
}
