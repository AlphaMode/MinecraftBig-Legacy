package me.alphamode.mcbig.extensions;

import me.alphamode.mcbig.level.cube.LevelCube;
import me.alphamode.mcbig.world.phys.BigAABB;
import me.alphamode.mcbig.world.phys.BigVec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.tile.entity.TileEntity;

import java.math.BigInteger;
import java.util.List;

public interface BigCubicLevelExtension {
    default boolean setTile(BigInteger x, BigInteger y, BigInteger z, int tile) {
        throw new UnsupportedOperationException();
    }

    default boolean setTileNoUpdate(BigInteger x, BigInteger y, BigInteger z, int tile) {
        throw new UnsupportedOperationException();
    }

    default boolean setTileAndData(BigInteger x, BigInteger y, BigInteger z, int id, int data) {
        throw new UnsupportedOperationException();
    }

    default boolean setTileAndDataNoUpdate(BigInteger x, BigInteger y, BigInteger z, int tile, int data) {
        throw new UnsupportedOperationException();
    }

    default void playMusic(String music, BigInteger x, BigInteger y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void sendTileUpdated(BigInteger x, BigInteger y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void tileUpdated(BigInteger x, BigInteger y, BigInteger z, int tile) {
        throw new UnsupportedOperationException();
    }

    default void setTileDirty(BigInteger x, BigInteger y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void setTilesDirty(BigInteger minX, BigInteger minY, BigInteger minZ, BigInteger maxX, BigInteger maxY, BigInteger maxZ) {
        throw new UnsupportedOperationException();
    }

    default void updateNeighborsAt(BigInteger x, BigInteger y, BigInteger z, int tile) {
        throw new UnsupportedOperationException();
    }

    default void neighborChanged(BigInteger x, BigInteger y, BigInteger z, int tile) {
        throw new UnsupportedOperationException();
    }

    //? <1.0.0-beta.8.0.r {
    default boolean isEmptyTile(BigInteger x, BigInteger y, BigInteger z) {
        throw new UnsupportedOperationException();
    }
    //? }

    default boolean hasCube(BigInteger x, BigInteger y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default boolean hasCubeAt(BigInteger x, BigInteger y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default boolean hasCubesAt(BigInteger x, BigInteger y, BigInteger z, int range) {
        throw new UnsupportedOperationException();
    }

    default boolean hasCubesAt(BigInteger minX, BigInteger minY, BigInteger minZ, BigInteger maxX, BigInteger maxY, BigInteger maxZ) {
        throw new UnsupportedOperationException();
    }

    default LevelCube getCube(BigInteger x, BigInteger y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default LevelCube getCubeAt(BigInteger x, BigInteger y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default int getLightLevel(BigInteger x, BigInteger y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default int getRawBrightness(BigInteger x, BigInteger y, BigInteger z, boolean combineNeighbours) {
        throw new UnsupportedOperationException();
    }

    //? <1.0.0-beta.8.0.r {
    default boolean isSkyLit(BigInteger x, BigInteger y, BigInteger z) {
        throw new UnsupportedOperationException();
    }
    //? }

    default int getHeightmap(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default boolean canSeeSky(BigInteger x, BigInteger y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void setData(BigInteger x, BigInteger y, BigInteger z, int data) {
        throw new UnsupportedOperationException();
    }

    default boolean setDataNoUpdate(BigInteger x, BigInteger y, BigInteger z, int data) {
        throw new UnsupportedOperationException();
    }

    default int getRawBrightness(BigInteger x, BigInteger y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default int getBrightness(LightLayer type, BigInteger x, BigInteger y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void setBrightness(LightLayer layer, BigInteger x, BigInteger y, BigInteger z, int level) {
        throw new UnsupportedOperationException();
    }

    //? >=1.0.0-beta.8.0.r {
    /*default void checkLight(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default int getBrightnessPropagate(LightLayer layer, BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    *///? } else {
    default void updateLightIfOtherThan(LightLayer layer, BigInteger x, BigInteger y, BigInteger z, int level) {
        throw new UnsupportedOperationException();
    }
    //? }

    default void animateTick(BigInteger x, BigInteger y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default boolean mayPlace(int tileId, BigInteger x, BigInteger y, BigInteger z, boolean ignoreObstructed, int face) {
        throw new UnsupportedOperationException();
    }

    default boolean getDirectSignal(BigInteger x, BigInteger y, BigInteger z, int direction) {
        throw new UnsupportedOperationException();
    }

    default boolean hasDirectSignal(BigInteger x, BigInteger y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default boolean getSignal(BigInteger x, BigInteger y, BigInteger z, int direction) {
        throw new UnsupportedOperationException();
    }

    default boolean hasNeighborSignal(BigInteger x, BigInteger y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void setBlocksAndData(BigInteger x, BigInteger y, BigInteger z, int xs, int ys, int zs, byte[] buffer) {
        throw new UnsupportedOperationException();
    }

    default byte[] getBlocksAndData(BigInteger x, BigInteger y, BigInteger z, int xs, int yz, int zs) {
        throw new UnsupportedOperationException();
    }

    default List<Entity> getEntities(Entity entity, BigAABB area) {
        throw new UnsupportedOperationException();
    }

    default void setTileEntity(BigInteger x, BigInteger y, BigInteger z, TileEntity tileEntity) {
        throw new UnsupportedOperationException();
    }

    default void removeTileEntity(BigInteger x, BigInteger y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void tileEntityChanged(BigInteger x, BigInteger y, BigInteger z, TileEntity te) {
        throw new UnsupportedOperationException();
    }

    default List<BigAABB> getCubes(Entity entity, BigAABB area) {
        throw new UnsupportedOperationException();
    }

    default int getTopRainBlock(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    //? >=1.0.0-beta.8.0.r {
    /*default int getTopSolidBlock(BigInteger x, BigInteger z) {
        throw new UnsupportedOperationException();
    }
    *///? }

    default void updateLight(LightLayer type, BigInteger x0, BigInteger y0, BigInteger z0, BigInteger x1, BigInteger y1, BigInteger z1) {
        throw new UnsupportedOperationException();
    }

    //? <1.0.0-beta.8.0.r {
    default void updateLight(LightLayer type, BigInteger x0, BigInteger y0, BigInteger z0, BigInteger x1, BigInteger y1, BigInteger z1, boolean expand) {
        throw new UnsupportedOperationException();
    }
    //? }

    default boolean isRainingAt(BigInteger x, BigInteger y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void extinguishFire(Player player, BigInteger x, BigInteger y, BigInteger z, int face) {
        throw new UnsupportedOperationException();
    }

    default void addToTickNextTick(BigInteger x, BigInteger y, BigInteger z, int tileId, int delay) {
        throw new UnsupportedOperationException();
    }

    default void levelEvent(int event, BigInteger x, BigInteger y, BigInteger z, int data) {
        throw new UnsupportedOperationException();
    }

    default void levelEvent(Player player, int event, BigInteger x, BigInteger y, BigInteger z, int data) {
        throw new UnsupportedOperationException();
    }

    default BigVec3i getBigSpawnPos() {
        throw new UnsupportedOperationException();
    }

    default boolean mayInteract(Player player, BigInteger x, BigInteger y, BigInteger z) {
        return true;
    }

    default void tileEvent(BigInteger x, BigInteger y, BigInteger z, int b0, int b1) {
        throw new UnsupportedOperationException();
    }
}
