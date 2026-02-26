package me.alphamode.mcbig.extensions;

import me.alphamode.mcbig.world.phys.BigAABB;
import me.alphamode.mcbig.world.phys.BigVec3;
import me.alphamode.mcbig.world.phys.BigVec3i;
import net.minecraft.util.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelListener;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.math.BigInteger;
import java.util.List;

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

    default void playMusic(String music, BigInteger x, int y, BigInteger z) {
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

    boolean isEmptyTile(BigInteger x, int y, BigInteger z);

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

    default boolean mayPlace(int tileId, BigInteger x, int y, BigInteger z, boolean ignoreObstructed, int face) {
        throw new UnsupportedOperationException();
    }

    default boolean getDirectSignal(BigInteger x, int y, BigInteger z, int direction) {
        throw new UnsupportedOperationException();
    }

    default boolean hasDirectSignal(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default boolean getSignal(BigInteger x, int y, BigInteger z, int direction) {
        throw new UnsupportedOperationException();
    }

    default boolean hasNeighborSignal(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void setBlocksAndData(BigInteger x, int y, BigInteger z, int xs, int ys, int zs, byte[] buffer) {
        throw new UnsupportedOperationException();
    }

    default byte[] getBlocksAndData(BigInteger x, int y, BigInteger z, int xs, int yz, int zs) {
        throw new UnsupportedOperationException();
    }

    default List<Entity> getEntities(Entity entity, BigAABB area) {
        throw new UnsupportedOperationException();
    }

    default void setTileEntity(BigInteger x, int y, BigInteger z, TileEntity tileEntity) {
        throw new UnsupportedOperationException();
    }

    default void removeTileEntity(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default void tileEntityChanged(BigInteger x, int y, BigInteger z, TileEntity te) {
        throw new UnsupportedOperationException();
    }

    default List<BigAABB> getCubes(Entity entity, BigAABB area) {
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

    default BigVec3i getBigSpawnPos() {
        throw new UnsupportedOperationException();
    }

    default boolean mayInteract(Player player, BigInteger x, int y, BigInteger z) {
        return true;
    }

    default void tileEvent(BigInteger x, int y, BigInteger z, int b0, int b1) {
        throw new UnsupportedOperationException();
    }

    default boolean containsAnyTiles(BigAABB area) {
        throw new UnsupportedOperationException();
    }

    default HitResult clip(BigVec3 from, BigVec3 to) {
        return this.clip(from, to, false, false);
    }

    default HitResult clip(BigVec3 from, BigVec3 to, boolean checkLiquid) {
        return this.clip(from, to, checkLiquid, false);
    }

    default HitResult clip(BigVec3 from, BigVec3 to, boolean checkLiquid, boolean bl) {
        throw new UnsupportedOperationException();
    }
}
