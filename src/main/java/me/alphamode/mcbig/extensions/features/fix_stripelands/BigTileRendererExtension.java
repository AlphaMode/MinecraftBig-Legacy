package me.alphamode.mcbig.extensions.features.fix_stripelands;

import net.minecraft.world.level.tile.Tile;

import java.math.BigDecimal;

public interface BigTileRendererExtension {

    default void renderFaceDown(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        throw new UnsupportedOperationException();
    }

    default void renderFaceUp(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        throw new UnsupportedOperationException();
    }

    default void renderNorth(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        throw new UnsupportedOperationException();
    }

    default void renderSouth(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        throw new UnsupportedOperationException();
    }

    default void renderWest(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        throw new UnsupportedOperationException();
    }

    default void renderEast(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        throw new UnsupportedOperationException();
    }
}
