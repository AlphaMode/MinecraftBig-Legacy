package me.alphamode.mcbig.extensions;

import me.alphamode.mcbig.world.phys.BigAABB;
import net.minecraft.stats.Stats;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

public interface BigTileExtension {

    default AABB getTileAABB(Level level, BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException("");
    }

    default BigAABB getTileBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException("");
    }

    default void addAABBs(Level level, BigInteger x, int y, BigInteger z, AABB bb, List<AABB> boxes) {
        throw new UnsupportedOperationException();
    }

    default void addBigAABBs(Level level, BigInteger x, int y, BigInteger z, BigAABB bb, List<BigAABB> boxes) {
        throw new UnsupportedOperationException();
    }

    default AABB getAABB(Level level, BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default BigAABB getBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default float getBrightness(LevelSource level, BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default boolean shouldRenderFace(LevelSource level, BigInteger x, int y, BigInteger z, int face) {
        throw new UnsupportedOperationException();
    }

    default boolean isSolid(LevelSource level, BigInteger x, int y, BigInteger z, int direction) {
        return level.getMaterial(x, y, z).isSolid();
    }

    default int getTexture(LevelSource level, BigInteger x, int y, BigInteger z, int side) {
        throw new UnsupportedOperationException();
    }

    default void tick(Level level, BigInteger x, int y, BigInteger z, Random random) {}

    default void animateTick(Level level, BigInteger x, int y, BigInteger z, Random random) {};

    default void destroy(Level level, BigInteger x, int y, BigInteger z, int data) {}

    default void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int tile) {}

    default void onPlace(Level level, BigInteger x, int y, BigInteger z) {
    }

    default void onRemove(Level level, BigInteger x, int y, BigInteger z) {
    }

    default boolean use(Level level, BigInteger x, int y, BigInteger z, Player player) {
        return false;
    }

    default void stepOn(Level level, BigInteger x, int y, BigInteger z, Entity entity) {}

    default void setPlacedOnFace(Level level, BigInteger x, int y, BigInteger z, int facing) {}

    default void attack(Level level, BigInteger x, int y, BigInteger z, Player player) {}

    default void handleEntityInside(Level level, BigInteger x, int y, BigInteger z, Entity entity, Vec3 delta) {}

    default void updateShape(LevelSource source, BigInteger x, int y, BigInteger z) {}

    default int getFoliageColor(LevelSource level, BigInteger x, int y, BigInteger z) {
        return 16777215;
    }

    default boolean getSignal(LevelSource levelReader, BigInteger x, int y, BigInteger z, int direction) {
        return false;
    }

    default boolean canPlace(Level level, BigInteger x, int y, BigInteger z, int face) {
        return this.mayPlace(level, x, y, z);
    }

    default boolean mayPlace(Level level, BigInteger x, int y, BigInteger z) {
        int tt = level.getTile(x, y, z);
        return tt == 0 || Tile.tiles[tt].material.isReplaceable();
    }

    default void entityInside(Level level, BigInteger x, int y, BigInteger z, Entity entity) {}

    default boolean getDirectSignal(Level level, BigInteger x, int y, BigInteger z, int direction) {
        return false;
    }

    default void playerDestroy(Level level, Player player, BigInteger x, int y, BigInteger z, int meta) {}

    default boolean canPlace(Level level, BigInteger x, int y, BigInteger z) {
        return true;
    }

    default void setPlacedBy(Level level, BigInteger x, int y, BigInteger z, Mob entity) {
    }

    default void dropResources(Level level, BigInteger x, int y, BigInteger z, int meta) {
        throw new UnsupportedOperationException();
    }

    default void dropResources(Level level, BigInteger x, int y, BigInteger z, int meta, float f) {
        throw new UnsupportedOperationException();
    }

    default void popResource(Level level, BigInteger x, int y, BigInteger z, ItemInstance item) {
        throw new UnsupportedOperationException();
    }

    default HitResult clip(Level level, BigInteger x, int y, BigInteger z, Vec3 vec1, Vec3 vec2) {
        throw new UnsupportedOperationException();
    }
}
