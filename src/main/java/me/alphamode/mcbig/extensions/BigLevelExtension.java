package me.alphamode.mcbig.extensions;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelListener;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LevelChunk;

import java.math.BigInteger;

public interface BigLevelExtension {
    default boolean setTile(BigInteger x, int y, BigInteger z, int tile) {
        throw new UnsupportedOperationException();
    }

    default boolean setTileNoUpdate(BigInteger x, int y, BigInteger z, int tile) {
        throw new UnsupportedOperationException();
    }

    default boolean setTileAndData(BigInteger x, int y, BigInteger z, int id, int data) {
        throw new UnsupportedOperationException();
    }

    default boolean setTileAndDataNoUpdate(BigInteger x, int y, BigInteger z, int tile, int data) {
        throw new UnsupportedOperationException();
    }

    default void sendTileUpdated(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void tileUpdated(BigInteger x, int y, BigInteger z, int tile) {
        throw new UnsupportedOperationException();
    }

    default void setTileDirty(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void setTilesDirty(BigInteger minX, int minY, BigInteger minZ, BigInteger maxX, int maxY, BigInteger maxZ) {
        throw new UnsupportedOperationException();
    }

    default void updateNeighborsAt(BigInteger x, int y, BigInteger z, int tile) {
        throw new UnsupportedOperationException();
    }

    default void neighborChanged(BigInteger x, int y, BigInteger z, int tile) {
        throw new UnsupportedOperationException();
    }

    boolean hasChunkAt(BigInteger x, int y, BigInteger z);

    boolean hasChunksAt(BigInteger x, int y, BigInteger z, int range);

    boolean hasChunksAt(BigInteger minX, int minY, BigInteger minZ, BigInteger maxX, int maxY, BigInteger maxZ);

    default LevelChunk getChunk(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default LevelChunk getChunkAt(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default int getLightLevel(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default int getRawBrightness(BigInteger x, int y, BigInteger z, boolean combineNeighbours) {
        throw new UnsupportedOperationException();
    }

    default boolean isSkyLit(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default int getHeightmap(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default boolean canSeeSky(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void setData(BigInteger x, int y, BigInteger z, int data) {
        throw new UnsupportedOperationException();
    }

    default boolean setDataNoUpdate(BigInteger x, int y, BigInteger z, int data) {
        throw new UnsupportedOperationException();
    }

    default int getRawBrightness(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void updateLightIfOtherThan(LightLayer layer, BigInteger x, int y, BigInteger z, int level) {
        throw new UnsupportedOperationException();
    }

    default int getBrightness(LightLayer type, BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void setBrightness(LightLayer layer, BigInteger x, int y, BigInteger z, int level) {
        throw new UnsupportedOperationException();
    }

    default void animateTick(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default int getTopSolidBlock(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void updateLight(LightLayer type, BigInteger x0, int y0, BigInteger z0, BigInteger x1, int y1, BigInteger z1) {
        throw new UnsupportedOperationException();
    }

    default void updateLight(LightLayer type, BigInteger x0, int y0, BigInteger z0, BigInteger x1, int y1, BigInteger z1, boolean bl) {
        throw new UnsupportedOperationException();
    }

    default boolean isRainingAt(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void extinguishFire(Player player, BigInteger x, int y, BigInteger z, int face) {
        throw new UnsupportedOperationException();
    }

    default void addToTickNextTick(BigInteger x, int y, BigInteger z, int tileId, int delay) {
        throw new UnsupportedOperationException();
    }

    default void levelEvent(int event, BigInteger x, int y, BigInteger z, int data) {
        throw new UnsupportedOperationException();
    }

    default void levelEvent(Player player, int event, BigInteger x, int y, BigInteger z, int data) {
        throw new UnsupportedOperationException();
    }
}
