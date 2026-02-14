package me.alphamode.mcbig.extensions;

import me.alphamode.mcbig.prelaunch.Features;
import net.minecraft.world.level.tile.Tile;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface BigTileRendererExtension {

    boolean FIX_STRIPELANDS = Features.FIX_STRIPELANDS.isEnabled();

    default void tesselateInWorld(Tile tile, BigInteger x, int y, BigInteger z, int destroyProgress) {
        throw new UnsupportedOperationException();
    }

    default boolean tesselateInWorld(Tile tile, BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default boolean tesselateBlockInWorld(Tile tile, BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default boolean tesselateBlockInWorldWithAmbienceOcclusion(Tile tile, BigInteger x, int y, BigInteger z, float r, float g, float b) {
        throw new UnsupportedOperationException();
    }

    default boolean tesselateBlockInWorld(Tile tile, BigInteger x, int y, BigInteger z, float r, float g, float b) {
        throw new UnsupportedOperationException();
    }

    default boolean tesselateWaterInWorld(Tile tile, BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }
}
