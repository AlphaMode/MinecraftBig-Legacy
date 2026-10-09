package me.alphamode.mcbig.level.cube;

import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.math.BigMath;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.TilePos;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.TileEntityTile;
import net.minecraft.world.level.tile.entity.TileEntity;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class LevelCube {

    public static final int SIZE = 16;
    public static final BigInteger _SIZE = BigInteger.valueOf(SIZE);

    public byte[] blocks;

    public boolean loaded;
    public Level level;

    public CubicDataLayer data;
    public CubicDataLayer skyLight;
    public CubicDataLayer blockLight;

    private final BigInteger x;
    private final BigInteger y;
    private final BigInteger z;

    public Short2ObjectMap<TileEntity> tileEntities = new Short2ObjectOpenHashMap<>();
    public List<Entity> entities = new ArrayList<>();

    public boolean terrainPopulated = false;
    public boolean unsaved = false;
    public boolean dontSave;
    public boolean lastSaveHadEntities = false;
    public long lastSaveTime = 0L;

    public LevelCube(Level level, BigInteger x, BigInteger y, BigInteger z) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public LevelCube(Level level, byte[] blocks, BigInteger x, BigInteger y, BigInteger z) {
        this(level, x, y, z);
        this.blocks = blocks;
        this.data = new CubicDataLayer(blocks.length);
        this.skyLight = new CubicDataLayer(blocks.length);
        this.blockLight = new CubicDataLayer(blocks.length);
    }

    public int getTile(int x, int y, int z) {
        return this.blocks[x << 8 | z << 4 | y] & 0xFF;
    }

    public boolean setTileAndData(int x, int y, int z, int tile_, int data_) {
        byte tile = (byte) tile_;

//        int oldHeight = this.heightMap[z << 4 | x] & 255;
        int old = this.blocks[x << 8 | z << 4 | y] & 255;
        if (old == tile_ && this.data.get(x, y, z) == data_) return false;
        var xOffs = this.x.multiply(BigConstants.SIXTEEN).add(BigInteger.valueOf(x));
        var yOffs = this.y.multiply(BigConstants.SIXTEEN).add(BigInteger.valueOf(y));
        var zOffs = this.z.multiply(BigConstants.SIXTEEN).add(BigInteger.valueOf(z));
        this.blocks[x << 11 | z << 7 | y] = (byte) (tile & 255);
//        if (old != 0 && !this.level.isClientSide) {
//            Tile.tiles[old].onRemove(this.level, xOffs, yOffs, zOffs);
//        }

        this.data.set(x, y, z, data_);
//        if (!this.level.dimension.hasCeiling) {
//            if (Tile.lightBlock[tile & 255] != 0) {
//                if (y >= oldHeight) {
//                    this.recalcHeight(x, y + 1, z);
//                }
//            } else if (y == oldHeight - 1) {
//                this.recalcHeight(x, y, z);
//            }
//
//            this.level.updateLight(LightLayer.SKY, xOffs, yOffs, zOffs, xOffs, yOffs, zOffs);
//        }
//
//        this.level.updateLight(LightLayer.BLOCK, xOffs, yOffs, zOffs, xOffs, yOffs, zOffs);
//        this.lightGaps(x, z);
        this.data.set(x, y, z, data_);
        if (tile_ != 0) {
//            Tile.tiles[tile_].onPlace(this.level, xOffs, yOffs, zOffs);
        }

        this.unsaved = true;
        return true;
    }

    public boolean setTile(int x, int y, int z, int tile_) {
        byte tile = (byte) tile_;
//        int oldHeight = this.heightMap[z << 4 | x] & 255;

        int old = this.blocks[x << 11 | z << 7 | y] & 255;
        if (old == tile_) {
            return false;
        }
//        var xOffs = this.x.multiply(BigConstants.SIXTEEN).add(BigInteger.valueOf(x));
//        var yOffs = this.y.multiply(BigConstants.SIXTEEN).add(BigInteger.valueOf(y));
//        var zOffs = this.z.multiply(BigConstants.SIXTEEN).add(BigInteger.valueOf(z));
        this.blocks[x << 11 | z << 7 | y] = (byte)(tile & 255);
        if (old != 0) {
//            Tile.tiles[old].onRemove(this.level, xOffs, y, zOffs);
        }

        this.data.set(x, y, z, 0);
//        if (Tile.lightBlock[tile & 255] != 0) {
//            if (y >= oldHeight) {
//                this.recalcHeight(x, y + 1, z);
//            }
//        } else if (y == oldHeight - 1) {
//            this.recalcHeight(x, y, z);
//        }

//        this.level.updateLight(LightLayer.SKY, xOffs, y, zOffs, xOffs, y, zOffs);
//        this.level.updateLight(LightLayer.BLOCK, xOffs, y, zOffs, xOffs, y, zOffs);
//        this.lightGaps(x, z);
//        if (tile_ != 0 && !this.level.isClientSide) {
//            Tile.tiles[tile_].onPlace(this.level, xOffs, y, zOffs);
//        }

        this.unsaved = true;
        return true;
    }

    public int getData(int x, int y, int z) {
        return this.data.get(x, y, z);
    }

    public void setData(int x, int y, int z, int meta) {
        this.unsaved = true;
        this.data.set(x, y, z, meta);
    }

    public int getBrightness(LightLayer layer, int x, int y, int z) {
        if (layer == LightLayer.SKY) {
            return this.skyLight.get(x, y, z);
        } else {
            return layer == LightLayer.BLOCK ? this.blockLight.get(x, y, z) : 0;
        }
    }

    public void setBrightness(LightLayer layer, int x, int y, int z, int level) {
        this.unsaved = true;
        if (layer == LightLayer.SKY) {
            this.skyLight.set(x, y, z, level);
        } else {
            if (layer != LightLayer.BLOCK) {
                return;
            }

            this.blockLight.set(x, y, z, level);
        }
    }

    public int getRawBrightness(int x, int y, int z, int skyDampen) {
        int light = this.skyLight.get(x, y, z);
        if (light > 0) LevelChunk.touchedSky = true;
        light -= skyDampen;
        int block = this.blockLight.get(x, y, z);
        if (block > light) light = block;

        return light;
    }

    public void addEntity(Entity e) {
        this.lastSaveHadEntities = true;
        BigInteger xc;
        BigInteger yc;
        BigInteger zc;
        if (e.isBigMovementEnabled() && e instanceof BigEntityExtension bigEntity) {
            xc = BigMath.floor(bigEntity.getX().divide(BigConstants.SIXTEEN_F, RoundingMode.HALF_UP));
            yc = BigMath.floor(bigEntity.getY().divide(BigConstants.SIXTEEN_F, RoundingMode.HALF_UP));
            zc = BigMath.floor(bigEntity.getZ().divide(BigConstants.SIXTEEN_F, RoundingMode.HALF_UP));
        } else {
            xc = BigMath.floor(e.x / 16.0);
            yc = BigMath.floor(e.y / 16.0);
            zc = BigMath.floor(e.z / 16.0);
        }

        if (!xc.equals(this.x) || !yc.equals(this.y) || !zc.equals(this.z)) {
            System.out.println("Wrong location! " + e);
            Thread.dumpStack();
        }

        e.inChunk = true;
        e.setXChunk(this.x);
        e.setYChunk(this.y);
        e.setZChunk(this.z);
        this.entities.add(e);
    }

    public void removeEntity(Entity e) {
        this.entities.remove(e);
    }

    public TileEntity getTileEntity(int x, int y, int z) {
        short pos = (short) (x << 8 | z << 4 | y);
        TileEntity tileEntity = this.tileEntities.get(pos);
        if (tileEntity == null) {
            int t = getTile(x, y, z);
            if (!Tile.isEntityTile[t]) {
                return null;
            }

            TileEntityTile _t = (TileEntityTile)Tile.tiles[t];
//            _t.onPlace(this.level, this.x * 16 + x, this.y * 16 + y, this.z * 16 + z);
            tileEntity = this.tileEntities.get(pos);
        }

        if (tileEntity != null && tileEntity.isRemoved()) {
            this.tileEntities.remove(pos);
            return null;
        } else {
            return tileEntity;
        }
    }

    public void addTileEntity(TileEntity te) {
//        int xx = te.x - this.x * 16;
//        int yy = te.y - this.y * 16;
//        int zz = te.z - this.z * 16;
//        this.setTileEntity(xx, yy, zz, te);
        if (this.loaded) {
            this.level.tileEntityList.add(te);
        }
    }

    public void setTileEntity(int x, int y, int z, TileEntity tileEntity) {
        TilePos var5 = new TilePos(x, y, z);
        tileEntity.level = this.level;
//        tileEntity.x = this.x * 16 + x;
//        tileEntity.y = this.y * 16 + y;
//        tileEntity.z = this.z * 16 + z;
        if (this.getTile(x, y, z) != 0 && Tile.tiles[this.getTile(x, y, z)] instanceof TileEntityTile) {
            tileEntity.clearRemoved();
//            this.tileEntities.put(var5, tileEntity);
        } else {
            System.out.println("Attempted to place a tile entity where there was no entity tile!");
        }
    }

    public void removeTileEntity(int x, int y, int z) {
        TilePos var4 = new TilePos(x, y, z);
        if (this.loaded) {
            TileEntity var5 = (TileEntity)this.tileEntities.remove(var4);
            if (var5 != null) {
                var5.setRemoved();
            }
        }
    }
}
