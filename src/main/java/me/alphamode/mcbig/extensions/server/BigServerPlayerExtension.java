package me.alphamode.mcbig.extensions.server;

import me.alphamode.mcbig.level.chunk.BigChunkPos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface BigServerPlayerExtension {
    default List<BigChunkPos> getBigChunks() {
        throw new UnsupportedOperationException();
    }

    default Set<BigChunkPos> getTrackedBigChunks() {
        throw new UnsupportedOperationException();
    }

    default void setLastX(BigDecimal x) {
        throw new UnsupportedOperationException();
    }

    default BigDecimal getLastX() {
        throw new UnsupportedOperationException();
    }

    default void setLastZ(BigDecimal z) {
        throw new UnsupportedOperationException();
    }

    default BigDecimal getLastZ() {
        throw new UnsupportedOperationException();
    }
}
