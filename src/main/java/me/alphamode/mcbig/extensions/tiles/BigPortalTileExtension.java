package me.alphamode.mcbig.extensions.tiles;

import net.minecraft.world.level.Level;

import java.math.BigInteger;

public interface BigPortalTileExtension {
    default boolean isPortal(Level level, BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }
}
