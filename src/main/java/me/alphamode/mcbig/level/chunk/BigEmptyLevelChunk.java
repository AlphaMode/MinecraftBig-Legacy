package me.alphamode.mcbig.level.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.phys.AABB;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BigEmptyLevelChunk extends BigLevelChunk {
    public BigEmptyLevelChunk(Level level, BigInteger x, BigInteger z) {
        super(level, x, z);
        this.dontSave = true;
    }

    public BigEmptyLevelChunk(Level level, byte[] tiles, BigInteger x, BigInteger z) {
        super(level, tiles, x, z);
        this.dontSave = true;
    }

    @Override
    public boolean isAt(int x, int z) {
        return x == this.x && z == this.z;
    }

    @Override
    public int getHeightmap(int x, int z) {
        return 0;
    }

    @Override
    public void recalcBlockLights() {
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void recalcHeightmapOnly() {
    }

    @Override
    public void recalcHeightmap() {
    }

    @Override
    public void lightLava() {
    }

    @Override
    public int getTile(int x, int y, int z) {
        return 0;
    }

    @Override
    public boolean setTileAndData(int x, int y, int z, int id, int meta) {
        return true;
    }

    @Override
    public boolean setTile(int x, int y, int z, int id) {
        return true;
    }

    @Override
    public int getData(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setData(int x, int y, int z, int meta) {
    }

    @Override
    public int getBrightness(LightLayer layer, int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBrightness(LightLayer layer, int x, int y, int z, int level) {
    }

    @Override
    public int getRawBrightness(int x, int y, int z, int skyDarken) {
        return 0;
    }

    @Override
    public void addEntity(Entity entity) {
    }

    @Override
    public void removeEntity(Entity entity) {
    }

    @Override
    public void removeEntity(Entity entity, int section) {
    }

    @Override
    public boolean isSkyLit(int x, int y, int z) {
        return false;
    }

    @Override
    public TileEntity getTileEntity(int x, int y, int z) {
        return null;
    }

    @Override
    public void addTileEntity(TileEntity tileEntity) {
    }

    @Override
    public void setTileEntity(int x, int y, int z, TileEntity tileEntity) {
    }

    @Override
    public void removeTileEntity(int x, int y, int z) {
    }

    @Override
    public void load() {
    }

    @Override
    public void unload() {
    }

    @Override
    public void markUnsaved() {
    }

    @Override
    public void getEntities(Entity ignore, AABB area, List entities) {
    }

    @Override
    public void getEntitiesOfClass(Class type, AABB area, List entities) {
    }

    @Override
    public boolean shouldSave(boolean force) {
        return false;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public int setBlocksAndData(byte[] chunkData, int x0, int y0, int z0, int x1, int y1, int z1, int size) {
        int var9 = x1 - x0;
        int var10 = y1 - y0;
        int var11 = z1 - z0;
        int var12 = var9 * var10 * var11;
        return var12 + var12 / 2 * 3;
    }

    @Environment(EnvType.SERVER)
    @Override
    public int getBlocksAndData(byte[] chunkData, int x0, int y0, int z0, int x1, int y1, int z1, int size) {
        int var9 = x1 - x0;
        int var10 = y1 - y0;
        int var11 = z1 - z0;
        int var12 = var9 * var10 * var11;
        int var13 = var12 + var12 / 2 * 3;
        Arrays.fill(chunkData, size, size + var13, (byte)0);
        return var13;
    }

    @Override
    public Random getRandom(long pow) {
        return new Random(this.level.getSeed() + this.x * this.x * 4987142 + this.x * 5947611 + this.z * this.z * 4392871L + this.z * 389711 ^ pow);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
