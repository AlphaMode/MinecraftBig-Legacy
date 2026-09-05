package me.alphamode.mcbig.extensions.biome;

import me.alphamode.mcbig.level.chunk.BigChunkPos;
import net.minecraft.world.level.biome.Biome;

import java.math.BigInteger;

public interface BigBiomeSourceExtension {

    default Biome getBiome(BigChunkPos pos) {
        throw new UnsupportedOperationException();
    }

    default Biome getBiome(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    //? >=1.0.0-beta.8.0.r {
    /*default float getDownfall(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default float[] getDownfallBlock(float[] downfalls, BigInteger x, BigInteger z, int w, int h) {
        throw new UnsupportedOperationException();
    }
    *///? }

    //? >=1.0.0-beta.8.0.r {
    /*default float getTemperature(BigInteger x, BigInteger z) {
    *///? } else {
    default double getTemperature(BigInteger x, BigInteger z) {
    //? }
        throw new UnsupportedOperationException();
    }

    //? >=1.0.0-beta.8.0.r {
    /*default float[] getTemperatureBlock(float[] temperatures, BigInteger x, BigInteger z, int w, int h) {
    *///? } else {
    default double[] getTemperatureBlock(double[] temperatures, BigInteger x, BigInteger z, int w, int h) {
    //? }
        throw new UnsupportedOperationException();
    }

    default Biome[] getRawBiomeBlock(Biome[] biomes, BigInteger x, BigInteger z, int w, int h) {
        throw new UnsupportedOperationException();
    }

    default Biome[] getBiomeBlock(BigInteger x, BigInteger z, int w, int h) {
        throw new UnsupportedOperationException();
    }

    default Biome[] getBiomeBlock(Biome[] biomes, BigInteger x, BigInteger z, int w, int h) {
        throw new UnsupportedOperationException();
    }

    //? >=1.0.0-beta.8.0.r {
    /*default Biome[] getBiomeBlock(Biome[] biomes, BigInteger x, BigInteger z, int w, int h, boolean useCache) {
        throw new UnsupportedOperationException();
    }
    *///? }
}
