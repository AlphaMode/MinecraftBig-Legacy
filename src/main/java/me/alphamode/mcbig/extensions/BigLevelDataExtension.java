package me.alphamode.mcbig.extensions;

import java.math.BigInteger;

public interface BigLevelDataExtension {
    default BigInteger getBigSpawnX() {
        throw new UnsupportedOperationException();
    }

    default BigInteger getBigSpawnZ() {
        throw new UnsupportedOperationException();
    }

    default void setBigSpawnX(BigInteger x) {
        throw new UnsupportedOperationException();
    }

    default void setBigSpawnZ(BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void setBigSpawnXYZ(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }
}
