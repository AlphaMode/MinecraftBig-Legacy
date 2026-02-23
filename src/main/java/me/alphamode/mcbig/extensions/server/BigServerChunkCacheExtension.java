package me.alphamode.mcbig.extensions.server;

import java.math.BigInteger;

public interface BigServerChunkCacheExtension {
    default void dropNoneSpawnChunk(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }
}
