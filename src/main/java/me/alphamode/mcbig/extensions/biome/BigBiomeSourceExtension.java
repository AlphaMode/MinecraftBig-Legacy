package me.alphamode.mcbig.extensions.biome;

import me.alphamode.mcbig.level.BigTilePos;
import me.alphamode.mcbig.level.chunk.BigChunkPos;
import net.minecraft.world.level.TilePos;
import net.minecraft.world.level.biome.Biome;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

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

    //~ if >=1.0.0-beta.8.0.r 'double' -> 'float'
    default double getTemperature(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    //~ if >=1.0.0-beta.8.0.r 'double' -> 'float'
    default double[] getTemperatureBlock(double[] temperatures, BigInteger x, BigInteger z, int w, int h) {
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

    /^*
     * Checks if an area around a block contains only the specified biomes.
     * Useful for placing elements like towns.
     *
     * This is a bit of a rough check, to make it as fast as possible. To ensure
     * NO other biomes, add a margin of at least four blocks to the radius
     ^/
    default boolean containsOnly(BigInteger x, BigInteger z, int r, List<Biome> allowed) {
        throw new UnsupportedOperationException();
    }

    default BigTilePos findBiome(BigInteger x, BigInteger z, int r, List<Biome> toFind, Random random) {
        throw new UnsupportedOperationException();
    }

    *///? }
}
