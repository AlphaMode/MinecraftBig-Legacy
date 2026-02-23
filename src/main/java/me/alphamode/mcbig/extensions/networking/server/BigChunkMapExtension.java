package me.alphamode.mcbig.extensions.networking.server;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import me.alphamode.mcbig.level.chunk.BigChunkPos;
import me.alphamode.mcbig.server.level.BigServerChunk;

import java.math.BigInteger;
import java.util.List;

public interface BigChunkMapExtension {
    default Object2ObjectMap<BigChunkPos, BigServerChunk> getChunkMap() {
        throw new UnsupportedOperationException();
    }

    default List<BigServerChunk> getBigChunks() {
        throw new UnsupportedOperationException();
    }

    default void blockChanged(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }
}
