package me.alphamode.mcbig.extensions.tiles;

import net.minecraft.world.level.Level;

import java.math.BigInteger;

public interface BigRecordPlayerTileExtension {
    default void setRecord(Level level, BigInteger x, int y, BigInteger z, int recordId) {
        throw new UnsupportedOperationException();
    }

    default void dropRecording(Level level, BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }
}
