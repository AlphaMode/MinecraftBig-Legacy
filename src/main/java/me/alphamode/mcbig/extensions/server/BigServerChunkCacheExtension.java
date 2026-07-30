package me.alphamode.mcbig.extensions.server;

import java.math.BigInteger;

public interface BigServerChunkCacheExtension {
    default void drop(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }
}
