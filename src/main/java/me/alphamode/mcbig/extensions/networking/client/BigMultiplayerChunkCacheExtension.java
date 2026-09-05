package me.alphamode.mcbig.extensions.networking.client;

import java.math.BigInteger;

public interface BigMultiplayerChunkCacheExtension {
    default void drop(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }
}
