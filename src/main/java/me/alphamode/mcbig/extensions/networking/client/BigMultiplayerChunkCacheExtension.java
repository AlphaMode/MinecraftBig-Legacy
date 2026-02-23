package me.alphamode.mcbig.extensions.networking.client;

import java.math.BigInteger;

public interface BigMultiplayerChunkCacheExtension {
    default void unloadChunk(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }
}
