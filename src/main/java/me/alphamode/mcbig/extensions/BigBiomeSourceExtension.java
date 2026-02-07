package me.alphamode.mcbig.extensions;

import me.alphamode.mcbig.level.chunk.BigChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkPos;

import java.math.BigInteger;

public interface BigBiomeSourceExtension {

    default Biome getBiome(BigChunkPos pos) {
        throw new UnsupportedOperationException();
    }

    default Biome getBiome(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default double getTemperature(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default Biome[] getBiomeBlock(BigInteger x, BigInteger z, int k, int l) {
        throw new UnsupportedOperationException();
    }

    default double[] getTemperatureBlock(double[] ds, BigInteger x, BigInteger z, int k, int l) {
        throw new UnsupportedOperationException();
    }

    default Biome[] getBiomeBlock(Biome[] biomes, BigInteger x, BigInteger z, int k, int l) {
        throw new UnsupportedOperationException();
    }
}
