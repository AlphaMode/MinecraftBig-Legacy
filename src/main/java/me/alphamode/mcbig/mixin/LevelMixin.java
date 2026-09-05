package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.constants.LevelConstants;
import me.alphamode.mcbig.extensions.BigLevelExtension;
import me.alphamode.mcbig.extensions.BigLevelSourceExtension;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.level.BigTickNextTickData;
import me.alphamode.mcbig.level.chunk.BigChunkPos;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.world.phys.BigAABB;
import me.alphamode.mcbig.world.phys.BigVec3;
import me.alphamode.mcbig.world.phys.BigVec3i;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Facing;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.tile.LevelEvent;
import net.minecraft.world.level.tile.LiquidTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

@Mixin(Level.class)
public abstract class LevelMixin implements BigLevelExtension, BigLevelSourceExtension {

    @Shadow
    protected ChunkSource chunkSource;

    @Shadow
    public List<Player> players;

    @Shadow
    public abstract void updateSleepingPlayerList();

    @Shadow
    public List<Entity> entities;

    @Shadow
    protected abstract void entityAdded(Entity entity);

    @Shadow
    @Final
    public Dimension dimension;

    @Shadow
    protected int randValue;

    @Shadow
    public Random random;

    @Shadow
    public abstract boolean isRaining();

    @Shadow
    private Set<BigChunkPos> chunksToPoll;

    @Shadow
    private int delayUntilNextMoodSound;

    @Shadow
    public abstract void playSound(double x, double y, double z, String soundId, float volume, float pitch);

    @Shadow
    public abstract boolean isThundering();

    @Shadow
    protected List<LevelListener> listeners;

    @Shadow
    public boolean noNeighborUpdate;

    @Shadow
    public boolean isClientSide;

    @Shadow
    public abstract boolean addGlobalEntity(Entity entity);

    @Shadow
    protected int lightingCooldown;

    @Shadow
    public abstract BiomeSource getBiomeSource();

    @Shadow
    private ArrayList<AABB> boxes;

    private ArrayList<BigAABB> bigBoxes = new ArrayList<>();

    @Shadow
    private List<Entity> es;

    @Shadow
    public boolean instaTick;

    @Shadow
    private Set<BigTickNextTickData> tickNextTickSet;

    @Shadow
    private TreeSet<BigTickNextTickData> tickNextTickList;

    @Shadow
    protected LevelData levelData;

    @Shadow
    public List<Entity> globalEntities;

    @Shadow
    private List<Entity> entitiesToRemove;

    @Shadow
    protected abstract void entityRemoved(Entity entity);

    @Shadow
    public abstract void tick(Entity entity);

    @Shadow
    private boolean updatingTileEntities;

    @Shadow
    public List<TileEntity> tileEntityList;

    @Shadow
    private List<TileEntity> pendingTileEntities;

    @Shadow
    public abstract boolean isUnobstructed(AABB aabb);

    @Override
    public boolean hasChunk(BigInteger x, BigInteger z) {
        return this.chunkSource.hasChunk(x, z);
    }

    @Override
    public boolean setTile(BigInteger x, int y, BigInteger z, int tile) {
        if (this.setTileNoUpdate(x, y, z, tile)) {
            this.tileUpdated(x, y, z, tile);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @author AlphaMode
     * @reason Redirect to big int method
     */
    @Overwrite
    public boolean setTile(int x, int y, int z, int tile) {
        return setTile(BigInteger.valueOf(x), y, BigInteger.valueOf(z), tile);
    }

    @Override
    public boolean setTileNoUpdate(BigInteger x, int y, BigInteger z, int tile) {
        if (y < 0) return false;
        if (y >= 128) return false;

        LevelChunk c = getChunk(x.shiftRight(4), z.shiftRight(4));
        boolean replaced = c.setTile(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue(), tile);
        //? >=1.0.0-beta.8.0.r
        //checkLight(x, y, z);
        return replaced;
    }

    /**
     * @author AlphaMode
     * @reason Redirect to big int method
     */
    @Overwrite
    public boolean setTileNoUpdate(int x, int y, int z, int tile) {
        return setTileNoUpdate(BigInteger.valueOf(x), y, BigInteger.valueOf(z), tile);
    }

    @Override
    public boolean setTileAndData(BigInteger x, int y, BigInteger z, int id, int data) {
        if (setTileAndDataNoUpdate(x, y, z, id, data)) {
            this.tileUpdated(x, y, z, id);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @author AlphaMode
     * @reason Redirect to big int method
     */
    @Overwrite
    public boolean setTileAndData(int x, int y, int z, int id, int data) {
        return setTileAndData(BigInteger.valueOf(x), y, BigInteger.valueOf(z), id, data);
    }

    @Override
    public boolean setTileAndDataNoUpdate(BigInteger x, int y, BigInteger z, int tile, int data) {
        if (y < 0) return false;
        if (y >= 128) return false;

        LevelChunk c = this.getChunk(x.shiftRight(4), z.shiftRight(4));
        boolean replaced = c.setTileAndData(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue(), tile, data);
        //? >=1.0.0-beta.8.0.r
        //checkLight(x, y, z);
        return replaced;
    }

    /**
     * @author AlphaMode
     * @reason Redirect to big int method
     */
    @Overwrite
    public boolean setTileAndDataNoUpdate(int x, int y, int z, int tile, int data) {
        return setTileAndDataNoUpdate(BigInteger.valueOf(x), y, BigInteger.valueOf(z), tile, data);
    }

    @Override
    public void playMusic(String music, BigInteger x, int y, BigInteger z) {
        for (LevelListener listener : this.listeners) {
            listener.playStreamingMusic(music, x, y, z);
        }
    }

    @Override
    public void sendTileUpdated(BigInteger x, int y, BigInteger z) {
        for (LevelListener listener : this.listeners) {
            listener.tileChanged(x, y, z);
        }
    }

    @Override
    public void tileUpdated(BigInteger x, int y, BigInteger z, int tile) {
        sendTileUpdated(x, y, z);
        updateNeighborsAt(x, y, z, tile);
    }

    @Override
    public void setTileDirty(BigInteger x, int y, BigInteger z) {
        for (LevelListener listener : this.listeners) {
            listener.setTilesDirty(x, y, z, x, y, z);
        }
    }

    @Override
    public void setTilesDirty(BigInteger minX, int minY, BigInteger minZ, BigInteger maxX, int maxY, BigInteger maxZ) {
        for (LevelListener listener : this.listeners) {
            listener.setTilesDirty(minX, minY, minZ, maxX, maxY, maxZ);
        }
    }

    @Override
    public void updateNeighborsAt(BigInteger x, int y, BigInteger z, int tile) {
        this.neighborChanged(x.subtract(BigInteger.ONE), y, z, tile);
        this.neighborChanged(x.add(BigInteger.ONE), y, z, tile);
        this.neighborChanged(x, y - 1, z, tile);
        this.neighborChanged(x, y + 1, z, tile);
        this.neighborChanged(x, y, z.subtract(BigInteger.ONE), tile);
        this.neighborChanged(x, y, z.add(BigInteger.ONE), tile);
    }

    @Override
    public void neighborChanged(BigInteger x, int y, BigInteger z, int tile) {
        if (!this.noNeighborUpdate && !this.isClientSide) {
            Tile t = Tile.tiles[this.getTile(x, y, z)];
            if (t != null) {
                t.neighborChanged((Level) (Object) this, x, y, z, tile);
            }
        }
    }

    @Override
    public int getTile(BigInteger x, int y, BigInteger z) {
        if (y < 0) {
            return 0;
        } else {
            return y >= 128 ? 0 : getChunk(x.shiftRight(4), z.shiftRight(4)).getTile(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue());
        }
    }

    @Override
    public TileEntity getTileEntity(BigInteger x, int y, BigInteger z) {
        LevelChunk chunk = this.getChunk(x.shiftRight(4), z.shiftRight(4));
        return chunk != null ? chunk.getTileEntity(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue()) : null;
    }

    @Override
    public void setTileEntity(BigInteger x, int y, BigInteger z, TileEntity tileEntity) {
        //? >=1.0.0-beta.8.0.r {
        /*if (tileEntity != null && !tileEntity.isRemoved()) {
        *///? } else
        if (!tileEntity.isRemoved()) {
            if (this.updatingTileEntities) {
                tileEntity.setX(x);
                tileEntity.y = y;
                tileEntity.setZ(z);
                this.pendingTileEntities.add(tileEntity);
            } else {
                this.tileEntityList.add(tileEntity);
                LevelChunk chunk = this.getChunk(x.shiftRight(4), z.shiftRight(4));
                if (chunk != null) {
                    chunk.setTileEntity(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue(), tileEntity);
                }
            }
        }

    }

    @Override
    public void removeTileEntity(BigInteger x, int y, BigInteger z) {
        TileEntity te = this.getTileEntity(x, y, z);
        if (te != null && this.updatingTileEntities) {
            te.setRemoved();
        } else {
            if (te != null) {
                this.tileEntityList.remove(te);
            }

            LevelChunk chunk = this.getChunk(x.shiftRight(4), z.shiftRight(4));
            if (chunk != null) {
                chunk.removeTileEntity(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue());
            }
        }

    }

    @Override
    public int getData(BigInteger x, int y, BigInteger z) {
        if (y < 0) {
            return 0;
        } else if (y >= 128) {
            return 0;
        } else {
            return getChunk(x.shiftRight(4), z.shiftRight(4)).getData(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue());
        }
    }

    @Override
    public void setData(BigInteger x, int y, BigInteger z, int data) {
        if (setDataNoUpdate(x, y, z, data)) {
            int tt = getTile(x, y, z);
            if (Tile.blockUpdate[tt & 0xFF]) {
                tileUpdated(x, y, z, tt);
            } else {
                updateNeighborsAt(x, y, z, tt);
            }
        }
    }

    @Override
    public boolean setDataNoUpdate(BigInteger x, int y, BigInteger z, int data) {
        if (y < 0) {
            return false;
        } else if (y >= 128) {
            return false;
        } else {
            getChunk(x.shiftRight(4), z.shiftRight(4)).setData(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue(), data);
            return true;
        }
    }

    //? >1.0.0-beta.8.0.r {
    /*@Override
    public boolean isSkyLit(BigInteger x, int y, BigInteger z) {
        if (y < 0) return false;
        if (y >= 128) return true;
        if (!this.hasChunk(x.shiftRight(4), z.shiftRight(4))) return false;

        LevelChunk c = getChunk(x.shiftRight(4), z.shiftRight(4));
        return c.isSkyLit(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue());
    }
    *///? }

    @Override
    public int getHeightmap(BigInteger x, BigInteger z) {
        if (!hasChunk(x.shiftRight(4), z.shiftRight(4))) {
            return 0;
        } else {
            LevelChunk chunk = getChunk(x.shiftRight(4), z.shiftRight(4));
            return chunk.getHeightmap(x.and(BigConstants.FIFTEEN).intValue(), z.and(BigConstants.FIFTEEN).intValue());
        }
    }

    @Override
    public boolean canSeeSky(BigInteger x, int y, BigInteger z) {
        return getChunk(x.shiftRight(4), z.shiftRight(4)).isSkyLit(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue());
    }



    @Override
    public boolean isSolidRenderTile(BigInteger x, int y, BigInteger z) {
        Tile tile = Tile.tiles[getTile(x, y, z)];
        return tile == null ? false : tile.isSolidRender();
    }

    @Override
    public boolean isSolidBlockingTile(BigInteger x, int y, BigInteger z) {
        Tile tile = Tile.tiles[getTile(x, y, z)];
        if (tile == null) {
            return false;
        } else {
            return tile.material.isSolidBlocking() && tile.isCubeShaped();
        }
    }

    @Override
    public Material getMaterial(BigInteger x, int y, BigInteger z) {
        int tile = getTile(x, y, z);
        return tile == 0 ? Material.air : Tile.tiles[tile].material;
    }

    @Override
    public boolean isEmptyTile(BigInteger x, int y, BigInteger z) {
        return this.getTile(x, y, z) == 0;
    }

    @Override
    public boolean hasChunkAt(BigInteger x, int y, BigInteger z) {
        return y >= 0 && y < 128 ? hasChunk(x.shiftRight(4), z.shiftRight(4)) : false;
    }

    @Override
    public boolean hasChunksAt(BigInteger x, int y, BigInteger z, int range) {
        BigInteger bigRange = BigInteger.valueOf(range);
        return this.hasChunksAt(x.subtract(bigRange), y - range, z.subtract(bigRange), x.add(bigRange), y + range, z.add(bigRange));
    }

    @Override
    public boolean hasChunksAt(BigInteger minX, int minY, BigInteger minZ, BigInteger maxX, int maxY, BigInteger maxZ) {
        if (maxY >= 0 && minY < 128) {
            minX = minX.shiftRight(4);
            minY >>= 4;
            minZ = minZ.shiftRight(4);
            maxX = maxX.shiftRight(4);
            maxY >>= 4;
            maxZ = maxZ.shiftRight(4);

            for (BigInteger var7 = minX; var7.compareTo(maxX) <= 0; var7 = var7.add(BigInteger.ONE)) {
                for (BigInteger var8 = minZ; var8.compareTo(maxZ) <= 0; var8 = var8.add(BigInteger.ONE)) {
                    if (!hasChunk(var7, var8)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public LevelChunk getChunk(BigInteger x, BigInteger z) {
        return this.chunkSource.getChunk(x, z);
    }

    @Override
    public LevelChunk getChunkAt(BigInteger x, BigInteger z) {
        return getChunk(x.shiftRight(4), z.shiftRight(4));
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public LevelChunk getChunk(int x, int z) {
        return this.chunkSource.getChunk(BigInteger.valueOf(x), BigInteger.valueOf(z));
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public LevelChunk getChunkAt(int x, int z) {
        return getChunk(x >> 4, z >> 4);
    }

    /**
     * @author AlphaMode
     * @reason
     */
    @Overwrite
    public boolean addEntity(Entity entity) {
        BigInteger chunkX = BigMath.floor(entity.x / 16.0);
        BigInteger chunkZ = BigMath.floor(entity.z / 16.0);
        boolean isPlayer = entity instanceof Player;

        if (!isPlayer && !hasChunk(chunkX, chunkZ)) {
            return false;
        } else {
            if (entity instanceof Player player) {
                this.players.add(player);
                updateSleepingPlayerList();
            }

            getChunk(chunkX, chunkZ).addEntity(entity);
            this.entities.add(entity);
            entityAdded(entity);
            return true;
        }
    }

    @Override
    public HitResult clip(BigVec3 a, BigVec3 b, boolean checkLiquid, boolean bl2) {
        if (Double.isNaN(a.y)) {
            return null;
        } else if (!Double.isNaN(b.y)) {
            BigInteger xTile1 = BigMath.floor(b.x);
            int yTile1 = Mth.floor(b.y);
            BigInteger zTile1 = BigMath.floor(b.z);
            BigInteger xTile0 = BigMath.floor(a.x);
            int yTile0 = Mth.floor(a.y);
            BigInteger zTile0 = BigMath.floor(a.z);
            int tileId = getTile(xTile0, yTile0, zTile0);
            int data = this.getData(xTile0, yTile0, zTile0);
            Tile tile = Tile.tiles[tileId];
            if ((!bl2 || tile == null || tile.getAABB((Level) (Object) this, xTile0, yTile0, zTile0) != null) && tileId > 0 && tile.mayPick(data, checkLiquid)) {
                HitResult var14 = tile.clip((Level) (Object) this, xTile0, yTile0, zTile0, a, b);
                if (var14 != null) {
                    return var14;
                }
            }

            int maxIterations = 200;

            while (maxIterations-- >= 0) {
                if (Double.isNaN(a.y)) {
                    return null;
                }

                if (xTile0.equals(xTile1) && yTile0 == yTile1 && zTile0.equals(zTile1)) {
                    return null;
                }

                boolean var40 = true;
                boolean var41 = true;
                boolean var42 = true;
                BigDecimal xClip = BigConstants.CLIP;
                double yClip = 999.0;
                BigDecimal zClip = BigConstants.CLIP;
                if (xTile1.compareTo(xTile0) > 0) {
                    xClip = new BigDecimal(xTile0).add(BigDecimal.ONE);
                } else if (xTile1.compareTo(xTile0) < 0) {
                    xClip = new BigDecimal(xTile0);
                } else {
                    var40 = false;
                }

                if (yTile1 > yTile0) {
                    yClip = (double) yTile0 + 1.0;
                } else if (yTile1 < yTile0) {
                    yClip = (double) yTile0 + 0.0;
                } else {
                    var41 = false;
                }

                if (zTile1.compareTo(zTile0) > 0) {
                    zClip = new BigDecimal(zTile0).add(BigDecimal.ONE);
                } else if (zTile1.compareTo(zTile0) < 0) {
                    zClip = new BigDecimal(zTile0);
                } else {
                    var42 = false;
                }

                double xDist = 999.0;
                double yDist = 999.0;
                double zDist = 999.0;
                double xd = b.x.subtract(a.x).doubleValue();
                double yd = b.y - a.y;
                double zd = b.z.subtract(a.z).doubleValue();
                if (var40) {
                    xDist = (xClip.subtract(a.x)).doubleValue() / xd;
                }

                if (var41) {
                    yDist = (yClip - a.y) / yd;
                }

                if (var42) {
                    zDist = (zClip.subtract(a.z)).doubleValue() / zd;
                }

                int face = 0;
                if (xDist < yDist && xDist < zDist) {
                    if (xTile1.compareTo(xTile0) > 0) {
                        face = Facing.WEST;
                    } else {
                        face = Facing.EAST;
                    }

                    a.x = xClip;
                    a.y += yd * xDist;
                    a.z = a.z.add(BigMath.decimal(zd * xDist));
                } else if (yDist < zDist) {
                    if (yTile1 > yTile0) {
                        face = 0;
                    } else {
                        face = 1;
                    }

                    a.x = a.x.add(BigMath.decimal(xd * yDist));
                    a.y = yClip;
                    a.z = a.z.add(BigMath.decimal(zd * yDist));
                } else {
                    if (zTile1.compareTo(zTile0) > 0) {
                        face = Facing.NORTH;
                    } else {
                        face = Facing.SOUTH;
                    }

                    a.x = a.x.add(BigMath.decimal(xd * zDist));
                    a.y += yd * zDist;
                    a.z = zClip;
                }

                BigVec3 var34 = BigVec3.newTemp(a.x, a.y, a.z);
                xTile0 = BigMath.floor(a.x);
                var34.x = new BigDecimal(xTile0);
                if (face == 5) {
                    xTile0 = xTile0.subtract(BigInteger.ONE);
                    var34.x = var34.x.add(BigDecimal.ONE);
                }

                yTile0 = (int) (var34.y = (double) Mth.floor(a.y));
                if (face == 1) {
                    --yTile0;
                    ++var34.y;
                }

                zTile0 = BigMath.floor(a.z);
                var34.z = new BigDecimal(zTile0);
                if (face == Facing.SOUTH) {
                    zTile0 = zTile0.subtract(BigInteger.ONE);
                    var34.z = var34.z.add(BigDecimal.ONE);
                }

                int t = this.getTile(xTile0, yTile0, zTile0);
                int var36 = this.getData(xTile0, yTile0, zTile0);
                Tile var37 = Tile.tiles[t];
                if ((!bl2 || var37 == null || var37.getAABB((Level) (Object) this, xTile0, yTile0, zTile0) != null) && t > 0 && var37.mayPick(var36, checkLiquid)) {
                    HitResult var38 = var37.clip((Level) (Object) this, xTile0, yTile0, zTile0, a, b);
                    if (var38 != null) {
                        return var38;
                    }
                }
            }

            return null;
        } else {
            return null;
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public HitResult clip(Vec3 from, Vec3 to, boolean liquid, boolean solidOnly) {
        if (Double.isNaN(from.x) || Double.isNaN(from.y) || Double.isNaN(from.z)) {
            return null;
        } else if (!Double.isNaN(to.x) && !Double.isNaN(to.y) && !Double.isNaN(to.z)) {
            BigInteger toX = BigMath.floor(to.x);
            int toY = Mth.floor(to.y);
            BigInteger toZ = BigMath.floor(to.z);
            BigInteger fromX = BigMath.floor(from.x);
            int fromY = Mth.floor(from.y);
            BigInteger fromZ = BigMath.floor(from.z);
            int tileId = getTile(fromX, fromY, fromZ);
            int data = this.getData(fromX, fromY, fromZ);
            Tile tile = Tile.tiles[tileId];
            if ((!solidOnly || tile == null || tile.getAABB((Level) (Object) this, fromX, fromY, fromZ) != null) && tileId > 0 && tile.mayPick(data, liquid)) {
                HitResult var14 = tile.clip((Level) (Object) this, fromX, fromY, fromZ, from, to);
                if (var14 != null) {
                    return var14;
                }
            }

            tileId = 200;

            while (tileId-- >= 0) {
                if (Double.isNaN(from.x) || Double.isNaN(from.y) || Double.isNaN(from.z)) {
                    return null;
                }

                if (fromX.equals(toX) && fromY == toY && fromZ.equals(toZ)) {
                    return null;
                }

                boolean var40 = true;
                boolean var41 = true;
                boolean var42 = true;
                double var15 = 999.0;
                double var17 = 999.0;
                double var19 = 999.0;
                if (toX.compareTo(fromX) > 0) {
                    var15 = (double) fromX.doubleValue() + 1.0;
                } else if (toX.compareTo(fromX) < 0) {
                    var15 = (double) fromX.doubleValue() + 0.0;
                } else {
                    var40 = false;
                }

                if (toY > fromY) {
                    var17 = (double) fromY + 1.0;
                } else if (toY < fromY) {
                    var17 = (double) fromY + 0.0;
                } else {
                    var41 = false;
                }

                if (toZ.compareTo(fromZ) > 0) {
                    var19 = (double) fromZ.doubleValue() + 1.0;
                } else if (toZ.compareTo(fromZ) < 0) {
                    var19 = (double) fromZ.doubleValue() + 0.0;
                } else {
                    var42 = false;
                }

                double var21 = 999.0;
                double var23 = 999.0;
                double var25 = 999.0;
                double var27 = to.x - from.x;
                double var29 = to.y - from.y;
                double var31 = to.z - from.z;
                if (var40) {
                    var21 = (var15 - from.x) / var27;
                }

                if (var41) {
                    var23 = (var17 - from.y) / var29;
                }

                if (var42) {
                    var25 = (var19 - from.z) / var31;
                }

                byte var33 = 0;
                if (var21 < var23 && var21 < var25) {
                    if (toX.compareTo(fromX) > 0) {
                        var33 = 4;
                    } else {
                        var33 = 5;
                    }

                    from.x = var15;
                    from.y += var29 * var21;
                    from.z += var31 * var21;
                } else if (var23 < var25) {
                    if (toY > fromY) {
                        var33 = 0;
                    } else {
                        var33 = 1;
                    }

                    from.x += var27 * var23;
                    from.y = var17;
                    from.z += var31 * var23;
                } else {
                    if (toZ.compareTo(fromZ) > 0) {
                        var33 = 2;
                    } else {
                        var33 = 3;
                    }

                    from.x += var27 * var25;
                    from.y += var29 * var25;
                    from.z = var19;
                }

                Vec3 var34 = Vec3.newTemp(from.x, from.y, from.z);
                fromX = BigInteger.valueOf((long) (var34.x = (double) BigMath.floor(from.x).doubleValue()));
                if (var33 == 5) {
                    fromX = fromX.subtract(BigInteger.ONE);
                    ++var34.x;
                }

                fromY = (int) (var34.y = (double) Mth.floor(from.y));
                if (var33 == 1) {
                    --fromY;
                    ++var34.y;
                }

                fromZ = BigInteger.valueOf((long) (var34.z = (double) BigMath.floor(from.z).doubleValue()));
                if (var33 == 3) {
                    fromZ = fromZ.subtract(BigInteger.ONE);
                    ++var34.z;
                }

                int var35 = this.getTile(fromX, fromY, fromZ);
                int var36 = this.getData(fromX, fromY, fromZ);
                Tile var37 = Tile.tiles[var35];
                if ((!solidOnly || var37 == null || var37.getAABB((Level) (Object) this, fromX, fromY, fromZ) != null) && var35 > 0 && var37.mayPick(var36, liquid)) {
                    HitResult var38 = var37.clip((Level) (Object) this, fromX, fromY, fromZ, from, to);
                    if (var38 != null) {
                        return var38;
                    }
                }
            }

            return null;
        } else {
            return null;
        }
    }

    @Override
    public void animateTick(BigInteger x, int y, BigInteger z) {
        byte range = 16;
        Random rand = new Random();

        for (int i = 0; i < 1000; ++i) {
            BigInteger xt = x.add(BigInteger.valueOf(this.random.nextInt(range) - this.random.nextInt(range)));
            int yt = y + this.random.nextInt(range) - this.random.nextInt(range);
            BigInteger zt = z.add(BigInteger.valueOf(this.random.nextInt(range) - this.random.nextInt(range)));
            int tile = getTile(xt, yt, zt);
            //? >=1.0.0-beta.8.0.r {
            /*if (this.random.nextInt(8) > y && tile == 0) {
                this.addParticle("depthsuspend", xt.doubleValue() + this.random.nextFloat(), yt + this.random.nextFloat(), zt.doubleValue() + this.random.nextFloat(), 0.0, 0.0, 0.0);
            }
            *///? }
            if (tile > 0) {
                Tile.tiles[tile].animateTick((Level) (Object) this, xt, yt, zt, rand);
            }
        }
    }

    @Override
    public boolean mayPlace(int tileId, BigInteger x, int y, BigInteger z, boolean ignoreEntities, int face) {
        int targetType = this.getTile(x, y, z);
        Tile targetTile = Tile.tiles[targetType];

        Tile tile = Tile.tiles[tileId];
        AABB aabb = tile.getAABB((Level) (Object) this, x, y, z);
        if (ignoreEntities) aabb = null;

        if (aabb != null && !this.isUnobstructed(aabb)) {
            return false;
        } else {
            if (targetTile == Tile.water || targetTile == Tile.calmWater || targetTile == Tile.lava || targetTile == Tile.calmLava || targetTile == Tile.fire || targetTile == Tile.topSnow
                    //? >=1.0.0-beta.8.0.r
                    //|| targetTile == Tile.vine
            ) {
                targetTile = null;
            }

            return tileId > 0 && targetTile == null && tile.canPlace((Level) (Object) this, x, y, z, face);
        }
    }

    @Override
    public boolean getDirectSignal(BigInteger x, int y, BigInteger z, int direction) {
        int tt = this.getTile(x, y, z);
        return tt == 0 ? false : Tile.tiles[tt].getDirectSignal((Level) (Object) this, x, y, z, direction);
    }

    @Override
    public boolean hasDirectSignal(BigInteger x, int y, BigInteger z) {
        if (this.getDirectSignal(x, y - 1, z, Facing.DOWN)) {
            return true;
        } else if (this.getDirectSignal(x, y + 1, z, Facing.UP)) {
            return true;
        } else if (this.getDirectSignal(x, y, z.subtract(BigInteger.ONE), Facing.NORTH)) {
            return true;
        } else if (this.getDirectSignal(x, y, z.add(BigInteger.ONE), Facing.SOUTH)) {
            return true;
        } else {
            return this.getDirectSignal(x.subtract(BigInteger.ONE), y, z, Facing.WEST) ? true : this.getDirectSignal(x.add(BigInteger.ONE), y, z, Facing.EAST);
        }
    }

    @Override
    public boolean getSignal(BigInteger x, int y, BigInteger z, int direction) {
        if (this.isSolidBlockingTile(x, y, z)) {
            return this.hasDirectSignal(x, y, z);
        } else {
            int tile = this.getTile(x, y, z);
            return tile == 0 ? false : Tile.tiles[tile].getSignal((Level) (Object) this, x, y, z, direction);
        }
    }

    @Override
    public boolean hasNeighborSignal(BigInteger x, int y, BigInteger z) {
        if (this.getSignal(x, y - 1, z, Facing.DOWN)) {
            return true;
        } else if (this.getSignal(x, y + 1, z, Facing.UP)) {
            return true;
        } else if (this.getSignal(x, y, z.subtract(BigInteger.ONE), Facing.NORTH)) {
            return true;
        } else if (this.getSignal(x, y, z.add(BigInteger.ONE), Facing.SOUTH)) {
            return true;
        } else {
            return this.getSignal(x.subtract(BigInteger.ONE), y, z, Facing.WEST) ? true : this.getSignal(x.add(BigInteger.ONE), y, z, Facing.EAST);
        }
    }

    @Override
    public void setBlocksAndData(BigInteger x, int y, BigInteger z, int xs, int ys, int zs, byte[] buffer) {
        BigInteger xsBig = BigInteger.valueOf(xs);
        BigInteger zsBig = BigInteger.valueOf(zs);
        BigInteger x0 = x.shiftRight(4);
        BigInteger z0 = z.shiftRight(4);
        BigInteger x1 = x.add(xsBig).subtract(BigInteger.ONE).shiftRight(4);
        BigInteger z1 = z.add(zsBig).subtract(BigInteger.ONE).shiftRight(4);
        int size = 0;
        int y0 = y;
        int y1 = y + ys;
        if (y < 0) {
            y0 = 0;
        }

        if (y1 > 128) {
            y1 = 128;
        }

        for (BigInteger xc = x0; xc.compareTo(x1) <= 0; xc = xc.add(BigInteger.ONE)) {
            BigInteger xcBlock = xc.multiply(BigConstants.SIXTEEN);
            int minX = x.subtract(xcBlock).intValue();
            int maxX = x.add(xsBig).subtract(xcBlock).intValue();
            if (minX < 0) {
                minX = 0;
            }

            if (maxX > 16) {
                maxX = 16;
            }
            BigInteger minXBlockBig = xcBlock.add(BigInteger.valueOf(minX));
            BigInteger maxXBlockBig = xcBlock.add(BigInteger.valueOf(maxX));

            for (BigInteger zc = z0; zc.compareTo(z1) <= 0; zc = zc.add(BigInteger.ONE)) {
                BigInteger zcBlock = zc.multiply(BigConstants.SIXTEEN);
                int minZ = z.subtract(zcBlock).intValue();
                int maxZ = z.add(zsBig).subtract(zcBlock).intValue();
                if (minZ < 0) {
                    minZ = 0;
                }

                if (maxZ > 16) {
                    maxZ = 16;
                }

                size = this.getChunk(xc, zc).setBlocksAndData(buffer, minX, y0, minZ, maxX, y1, maxZ, size);
                this.setTilesDirty(minXBlockBig, y0, zcBlock.add(BigInteger.valueOf(minZ)), maxXBlockBig, y1, zcBlock.add(BigInteger.valueOf(maxZ)));
            }
        }
    }

    @Override
    public byte[] getBlocksAndData(BigInteger x, int y, BigInteger z, int xs, int yz, int zs) {
        byte[] data = new byte[xs * yz * zs * 5 / 2];
        BigInteger xsBig = BigInteger.valueOf(xs);
        BigInteger zsBig = BigInteger.valueOf(zs);
        BigInteger x0 = x.shiftRight(4);
        BigInteger z0 = z.shiftRight(4);
        BigInteger x1 = x.add(xsBig).subtract(BigInteger.ONE).shiftRight(4);
        BigInteger z1 = z.add(zsBig).subtract(BigInteger.ONE).shiftRight(4);
        int size = 0;
        int y0 = y;
        int y1 = y + yz;
        if (y < 0) {
            y0 = 0;
        }

        if (y1 > 128) {
            y1 = 128;
        }

        for (BigInteger xc = x0; xc.compareTo(x1) <= 0; xc = xc.add(BigInteger.ONE)) {
            BigInteger xcBlock = xc.multiply(BigConstants.SIXTEEN);
            int minX = x.subtract(xcBlock).intValue();
            int maxX = x.add(xsBig).subtract(xcBlock).intValue();
            if (minX < 0) {
                minX = 0;
            }

            if (maxX > 16) {
                maxX = 16;
            }

            for (BigInteger zc = z0; zc.compareTo(z1) <= 0; zc = zc.add(BigInteger.ONE)) {
                BigInteger zcBlock = zc.multiply(BigConstants.SIXTEEN);
                int minZ = z.subtract(zcBlock).intValue();
                int maxZ = z.add(zsBig).subtract(zcBlock).intValue();
                if (minZ < 0) {
                    minZ = 0;
                }

                if (maxZ > 16) {
                    maxZ = 16;
                }

                size = this.getChunk(xc, zc).getBlocksAndData(data, minX, y0, minZ, maxX, y1, maxZ, size);
            }
        }

        return data;
    }

    @Override
    public List<Entity> getEntities(Entity entity, BigAABB area) {
        this.es.clear();
        BigInteger x0 = BigMath.floor(area.x0().subtract(BigDecimal.TWO).divide(BigConstants.SIXTEEN_F, RoundingMode.HALF_EVEN));
        BigInteger x1 = BigMath.floor(area.x1().add(BigDecimal.TWO).divide(BigConstants.SIXTEEN_F, RoundingMode.HALF_EVEN));
        BigInteger z0 = BigMath.floor(area.z0().subtract(BigDecimal.TWO).divide(BigConstants.SIXTEEN_F, RoundingMode.HALF_EVEN));
        BigInteger z1 = BigMath.floor(area.z1().add(BigDecimal.TWO).divide(BigConstants.SIXTEEN_F, RoundingMode.HALF_EVEN));

        for (BigInteger x = x0; x.compareTo(x1) <= 0; x = x.add(BigInteger.ONE)) {
            for (BigInteger z = z0; z.compareTo(z1) <= 0; z = z.add(BigInteger.ONE)) {
                if (hasChunk(x, z)) {
                    getChunk(x, z).getEntities(entity, area, this.es);
                }
            }
        }

        return this.es;
    }

    @Override
    public void tileEntityChanged(BigInteger x, int y, BigInteger z, TileEntity te) {
        if (this.hasChunkAt(x, y, z)) {
            this.getChunkAt(x, z).markUnsaved();
        }

        for (int i = 0; i < this.listeners.size(); i++) {
            this.listeners.get(i).tileEntityChanged(x, y, z, te);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public List<Entity> getEntities(Entity entity, AABB area) {
        this.es.clear();
        BigInteger x0 = BigMath.floor((area.x0 - 2.0) / 16.0);
        BigInteger x1 = BigMath.floor((area.x1 + 2.0) / 16.0);
        BigInteger z0 = BigMath.floor((area.z0 - 2.0) / 16.0);
        BigInteger z1 = BigMath.floor((area.z1 + 2.0) / 16.0);

        for (BigInteger x = x0; x.compareTo(x1) <= 0; x = x.add(BigInteger.ONE)) {
            for (BigInteger z = z0; z.compareTo(z1) <= 0; z = z.add(BigInteger.ONE)) {
                if (hasChunk(x, z)) {
                    getChunk(x, z).getEntities(entity, area, this.es);
                }
            }
        }

        return this.es;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public List<Entity> getEntitiesOfClass(Class entityClass, AABB area) {
        BigInteger x0 = BigMath.floor((area.x0 - 2.0) / 16.0);
        BigInteger x1 = BigMath.floor((area.x1 + 2.0) / 16.0);
        BigInteger z0 = BigMath.floor((area.z0 - 2.0) / 16.0);
        BigInteger z1 = BigMath.floor((area.z1 + 2.0) / 16.0);
        List<Entity> entityList = new ArrayList<>();

        for (BigInteger x = x0; x.compareTo(x1) <= 0; x = x.add(BigInteger.ONE)) {
            for (BigInteger z = z0; z.compareTo(z1) <= 0; z = z.add(BigInteger.ONE)) {
                if (hasChunk(x, z)) {
                    getChunk(x, z).getEntitiesOfClass(entityClass, area, entityList);
                }
            }
        }

        return entityList;
    }

    @Override
    public int getTopRainBlock(BigInteger x, BigInteger z) {
        //? >=1.0.0-beta.8.0.r {
        /*return this.getChunkAt(x, z).getTopRainBlock(x.and(BigConstants.FIFTEEN).intValue(), z.and(BigConstants.FIFTEEN).intValue());
        *///? } else {
        LevelChunk levelChunk = this.getChunkAt(x, z);
        int y = LevelConstants.MAX_BUILD_HEIGHT - 1;

        int xt = x.and(BigConstants.FIFTEEN).intValue();
        int zt = z.and(BigConstants.FIFTEEN).intValue();

        while (y > 0) {
            int t = levelChunk.getTile(xt, y, zt);
            Material m = t == 0 ? Material.air : Tile.tiles[t].material;
            if (!(m.blocksMotion() || m.isLiquid())) {
                y--;
            } else {
                return y + 1;
            }
        }

        return -1;
        //? }
    }

    //? >=1.0.0-beta.8.0.r {
    /*@Override
    public int getTopSolidBlock(BigInteger x, BigInteger z) {
        LevelChunk levelChunk = this.getChunkAt(x, z);
        int y = 127;
        int xt = x.and(BigConstants.FIFTEEN).intValue();
        int zt = z.and(BigConstants.FIFTEEN).intValue();

        while (y > 0) {
            int t = levelChunk.getTile(xt, y, zt);
            if (t == 0 || !(Tile.tiles[t].material.blocksMotion()) || Tile.tiles[t].material == Material.leaves) {
                y--;
            } else {
                return y + 1;
            }
        }

        return -1;
    }
    *///? }

    private static final int MAX_TICK_TILES_PER_TICK = 1000;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean tickPendingTicks(boolean force) {
        int count = this.tickNextTickList.size();
        if (count != this.tickNextTickSet.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        } else {
            if (count > MAX_TICK_TILES_PER_TICK) {
                count = MAX_TICK_TILES_PER_TICK;
            }

            for (int i = 0; i < count; i++) {
                BigTickNextTickData td = this.tickNextTickList.first();
                if (!force && td.delay > this.levelData.getTime()) {
                    break;
                }

                this.tickNextTickList.remove(td);
                this.tickNextTickSet.remove(td);
                int r = 8;
                BigInteger rBig = BigConstants.EIGHT;
                if (hasChunksAt(td.xBig.subtract(rBig), td.y - r, td.zBig.subtract(rBig), td.xBig.add(rBig), td.y + r, td.zBig.add(rBig))) {
                    int id = getTile(td.xBig, td.y, td.zBig);
                    if (id == td.tileId && id > 0) {
                        Tile.tiles[id].tick((Level) (Object) this, td.xBig, td.y, td.zBig, this.random);
                    }
                }
            }

            return this.tickNextTickList.size() != 0;
        }
    }

    public Player getNearestPlayer(BigDecimal x, double y, BigDecimal z, double maxDist) {
        double best = -1.0;
        Player result = null;

        for (int i = 0; i < this.players.size(); i++) {
            Player p = this.players.get(i);
            double dist = p.isBigMovementEnabled() ? ((BigEntityExtension)p).distanceToSqr(x, y, z) : p.distanceToSqr(x.doubleValue(), y, z.doubleValue());
            if ((maxDist < 0.0 || dist < maxDist * maxDist) && (best == -1.0 || dist < best)) {
                best = dist;
                result = p;
            }
        }

        return result;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tickTiles() {
        this.chunksToPoll.clear();

        for (Player player : this.players) {
            BigInteger xx = BigMath.floor(player.x / 16.0);
            BigInteger zz = BigMath.floor(player.z / 16.0);
            int r = 9;

            for (int xo = -r; xo <= r; ++xo) {
                for (int zo = -r; zo <= r; ++zo) {
                    this.chunksToPoll.add(new BigChunkPos(BigInteger.valueOf(xo).add(xx), BigInteger.valueOf(zo).add(zz)));
                }
            }
        }

        if (this.delayUntilNextMoodSound > 0) {
            --this.delayUntilNextMoodSound;
        }

        for (BigChunkPos cp : this.chunksToPoll) {
            BigInteger xo = cp.x().multiply(BigConstants.SIXTEEN);
            BigInteger zo = cp.z().multiply(BigConstants.SIXTEEN);
            LevelChunk lc = getChunk(cp.x(), cp.z());
            //? >=1.0.0-beta.8.0.r
            //lc.tick();
            if (this.delayUntilNextMoodSound == 0) {
                this.randValue = this.randValue * 3 + 1013904223;
                int val = this.randValue >> 2;
                BigInteger x = BigInteger.valueOf(val & 15);
                BigInteger z = BigInteger.valueOf(val >> 8 & 15);
                int y = val >> 16 & 127;
                int tile = lc.getTile(x.intValue(), y, z.intValue());
                x = x.add(xo);
                z = z.add(zo);
                if (tile == 0 && getRawBrightness(x, y, z) <= this.random.nextInt(8) && getBrightness(LightLayer.SKY, x, y, z) <= 0) {
                    Player player = getNearestPlayer(new BigDecimal(x).add(BigConstants.POINT_FIVE), (double) y + 0.5, new BigDecimal(z).add(BigConstants.POINT_FIVE), 8.0);
                    if (player != null && player.distanceToSqr(x.doubleValue() + 0.5, (double) y + 0.5, z.doubleValue() + 0.5) > 4.0) {
                        playSound((double) x.doubleValue() + 0.5, (double) y + 0.5, (double) z.doubleValue() + 0.5, "ambient.cave.cave", 0.7F, 0.8F + this.random.nextFloat() * 0.2F);
                        this.delayUntilNextMoodSound = this.random.nextInt(12000) + 6000;
                    }
                }
            }

            if (this.random.nextInt(100000) == 0 && this.isRaining() && this.isThundering()) {
                this.randValue = this.randValue * 3 + 1013904223;
                int packedPos = this.randValue >> 2;
                BigInteger x = xo.add(BigInteger.valueOf((packedPos & 15)));
                BigInteger z = zo.add(BigInteger.valueOf(packedPos >> 8 & 15));
                int y = getTopRainBlock(x, z);
                if (isRainingAt(x, y, z)) {
                    addGlobalEntity(new LightningBolt((Level) (Object) this, (double) x.doubleValue(), (double) y, (double) z.doubleValue()));
                    this.lightingCooldown = 2;
                }
            }

            if (this.random.nextInt(16) == 0) {
                this.randValue = this.randValue * 3 + 1013904223;
                int packedPos = this.randValue >> 2;
                final int x = packedPos & 15;
                final int z = packedPos >> 8 & 15;
                final BigInteger xB = BigInteger.valueOf(x);
                final BigInteger zB = BigInteger.valueOf(z);
                int y = getTopRainBlock(xB.add(xo), zB.add(zo));
                if (getBiomeSource().getBiome(xB.add(xo), zB.add(zo)).hasPrecipitation()
                        && y >= 0
                        && y < 128
                        && lc.getBrightness(LightLayer.BLOCK, x, y, z) < 10) {
                    int belowTile = lc.getTile(x, y - 1, z);
                    int tt = lc.getTile(x, y, z);
                    if (isRaining()
                            && tt == 0
                            && Tile.topSnow.mayPlace((Level) (Object) this, xB.add(xo), y, zB.add(zo))
                            && belowTile != 0
                            && belowTile != Tile.ice.id
                            && Tile.tiles[belowTile].material.blocksMotion()) {
                        setTile(xB.add(xo), y, zB.add(zo), Tile.topSnow.id);
                    }

                    if (belowTile == Tile.calmWater.id && lc.getData(x, y - 1, z) == 0) {
                        //? >=1.0.0-beta.8.0.r {
                        /*boolean surroundedByWater = true;
                        BigInteger xx = xB.add(xo);
                        BigInteger zz = zB.add(zo);
                        if (surroundedByWater && getMaterial(xx.subtract(BigInteger.ONE), y - 1, zz) != Material.water) surroundedByWater = false;
                        if (surroundedByWater && getMaterial(xx.add(BigInteger.ONE), y - 1, zz) != Material.water) surroundedByWater = false;
                        if (surroundedByWater && getMaterial(xx, y - 1, zz.subtract(BigInteger.ONE)) != Material.water) surroundedByWater = false;
                        if (surroundedByWater && getMaterial(xx, y - 1, zz.add(BigInteger.ONE)) != Material.water) surroundedByWater = false;

                        if (!surroundedByWater) setTile(xx, y - 1, zz, Tile.ice.id);
                        *///? } else
                        setTile(xB.add(xo), y - 1, zB.add(zo), Tile.ice.id);
                    }
                }
            }

            //? >=1.0.0-beta.8.0.r
            //checkLight(xo.add(BigInteger.valueOf(this.random.nextInt(16))), this.random.nextInt(128), zo.add(BigInteger.valueOf(this.random.nextInt(16))));

            for (int i = 0; i < 80; ++i) {
                this.randValue = this.randValue * 3 + 1013904223;
                int packedPos = this.randValue >> 2;
                int x = packedPos & 15;
                int z = packedPos >> 8 & 15;
                int y = packedPos >> 16 & 127;
                int tt = lc.blocks[x << 11 | z << 7 | y] & 255;
                if (Tile.shouldTick[tt]) {
                    Tile.tiles[tt].tick((Level) (Object) this, BigInteger.valueOf(x).add(xo), y, BigInteger.valueOf(z).add(zo), this.random);
                }
            }
        }
    }

    @Override
    public List<BigAABB> getCubes(Entity entity, BigAABB area) {
        this.bigBoxes.clear();
        BigInteger x0 = BigMath.floor(area.x0());
        BigInteger x1 = BigMath.floor(area.x1().add(BigDecimal.ONE));
        int y0 = Mth.floor(area.y0());
        int y1 = Mth.floor(area.y1() + 1.0);
        BigInteger z0 = BigMath.floor(area.z0());
        BigInteger z1 = BigMath.floor(area.z1().add(BigDecimal.ONE));

        for (BigInteger x = x0; x.compareTo(x1) < 0; x = x.add(BigInteger.ONE)) {
            for (BigInteger z = z0; z.compareTo(z1) < 0; z = z.add(BigInteger.ONE)) {
                if (this.hasChunkAt(x, 64, z)) {
                    for (int y = y0 - 1; y < y1; ++y) {
                        Tile tt = Tile.tiles[this.getTile(x, y, z)];
                        if (tt != null) {
                            tt.addBigAABBs((Level) (Object) this, x, y, z, area, this.bigBoxes);
                        }
                    }
                }
            }
        }

        double range = 0.25;
        List<Entity> entityList = this.getEntities(entity, area.inflate(range, range, range));

        for (int i = 0; i < entityList.size(); ++i) {
            AABB collideBox = entityList.get(i).getCollideBox();
            AABB vanillaArea = area.toVanilla();
            if (collideBox != null && collideBox.intersects(vanillaArea)) {
                this.bigBoxes.add(BigAABB.from(collideBox));
            }

            collideBox = entity.getCollideAgainstBox(entityList.get(i));
            if (collideBox != null && collideBox.intersects(vanillaArea)) {
                this.bigBoxes.add(BigAABB.from(collideBox));
            }
        }

        return this.bigBoxes;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public List<AABB> getCubes(Entity entity, AABB area) {
        this.boxes.clear();
        BigInteger x0 = BigMath.floor(area.x0);
        BigInteger x1 = BigMath.floor(area.x1 + 1.0);
        int y0 = Mth.floor(area.y0);
        int y1 = Mth.floor(area.y1 + 1.0);
        BigInteger z0 = BigMath.floor(area.z0);
        BigInteger z1 = BigMath.floor(area.z1 + 1.0);

        for (BigInteger x = x0; x.compareTo(x1) < 0; x = x.add(BigInteger.ONE)) {
            for (BigInteger z = z0; z.compareTo(z1) < 0; z = z.add(BigInteger.ONE)) {
                if (hasChunkAt(x, 64, z)) {
                    for (int var11 = y0 - 1; var11 < y1; ++var11) {
                        Tile tile = Tile.tiles[getTile(x, var11, z)];
                        if (tile != null) {
                            tile.addAABBs((Level) (Object) this, x, var11, z, area, this.boxes);
                        }
                    }
                }
            }
        }

        double range = 0.25;
        List<Entity> entityList = getEntities(entity, area.inflate(range, range, range));

        for (Entity e : entityList) {
            AABB collideBox = e.getCollideBox();
            if (collideBox != null && collideBox.intersects(area)) {
                this.boxes.add(collideBox);
            }

            collideBox = entity.getCollideAgainstBox(e);
            if (collideBox != null && collideBox.intersects(area)) {
                this.boxes.add(collideBox);
            }
        }

        return this.boxes;
    }

    @Shadow
    public abstract float getTimeOfDay(float a);

    @Shadow
    public abstract float getRainLevel(float a);

    @Shadow
    public abstract float getThunderLevel(float a);

    @Shadow
    public int skyFlashTime;

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Environment(EnvType.CLIENT)
    public Vec3 getSkyColor(Entity source, float a) {
        float td = this.getTimeOfDay(a);

        float br = Mth.cos(td * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
        if (br < 0.0F) br = 0.0F;

        if (br > 1.0F) br = 1.0F;

        BigEntityExtension bigSource = (BigEntityExtension) source;
        BigInteger xx = source.isBigMovementEnabled() ? BigMath.floor(bigSource.getX()) : BigMath.floor(source.x);
        BigInteger zz = source.isBigMovementEnabled() ? BigMath.floor(bigSource.getZ()) : BigMath.floor(source.z);
        //? >=1.0.0-beta.8.0.r {
        /*float temp = this.getBiomeSource().getTemperature(xx, zz);
        *///? } else {
        float temp = (float) this.getBiomeSource().getTemperature(xx, zz);
        //? }
        int skyColor = this.getBiomeSource().getBiome(xx, zz).getSkyColor(temp);
        float r = (skyColor >> 16 & 0xFF) / 255.0F;
        float g = (skyColor >> 8 & 0xFF) / 255.0F;
        float b = (skyColor & 0xFF) / 255.0F;
        r *= br;
        g *= br;
        b *= br;

        float rainLevel = this.getRainLevel(a);
        if (rainLevel > 0.0F) {
            float mid = (r * 0.3F + g * 0.59F + b * 0.11F) * 0.6F;

            float ba = 1.0F - rainLevel * 0.75F;
            r = r * ba + mid * (1.0F - ba);
            g = g * ba + mid * (1.0F - ba);
            b = b * ba + mid * (1.0F - ba);
        }

        float thunderLevel = this.getThunderLevel(a);
        if (thunderLevel > 0.0F) {
            float mid = (r * 0.3F + g * 0.59F + b * 0.11F) * 0.2F;

            float ba = 1.0F - thunderLevel * 0.75F;
            r = r * ba + mid * (1.0F - ba);
            g = g * ba + mid * (1.0F - ba);
            b = b * ba + mid * (1.0F - ba);
        }

        if (this.skyFlashTime > 0) {
            float f = this.skyFlashTime - a;
            if (f > 1.0F) f = 1.0F;

            f *= 0.45F;
            r = r * (1.0F - f) + 0.8F * f;
            g = g * (1.0F - f) + 0.8F * f;
            b = b * (1.0F - f) + 1.0F * f;
        }

        return Vec3.newTemp(r, g, b);
    }

    @Override
    public boolean isRainingAt(BigInteger x, int y, BigInteger z) {
        if (!isRaining()) {
            return false;
        } else if (!canSeeSky(x, y, z)) {
            return false;
        } else if (getTopRainBlock(x, z) > y) {
            return false;
        } else {
            Biome biome = getBiomeSource().getBiome(x, z);
            return biome.hasPrecipitation() ? false : biome.hasRain();
        }
    }

    /**
     * @author
     * @reason
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public void ensureAdded(Entity entity) {
        BigInteger x = BigMath.floor(entity.x / 16.0);
        BigInteger z = BigMath.floor(entity.z / 16.0);
        BigInteger range = BigInteger.TWO;

        for (BigInteger xChunk = x.subtract(range); xChunk.compareTo(x.add(range)) <= 0; xChunk = xChunk.add(BigInteger.ONE)) {
            for (BigInteger zChunk = z.subtract(range); zChunk.compareTo(z.add(range)) <= 0; zChunk = zChunk.add(BigInteger.ONE)) {
                getChunk(xChunk, zChunk);
            }
        }

        if (!this.entities.contains(entity)) {
            this.entities.add(entity);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Environment(EnvType.SERVER)
    public boolean containsAnyBlocks(AABB area) {
        BigInteger x0 = BigMath.floor(area.x0);
        BigInteger x1 = BigMath.floor(area.x1 + 1.0);
        int y0 = Mth.floor(area.y0);
        int y1 = Mth.floor(area.y1 + 1.0);
        BigInteger z0 = BigMath.floor(area.z0);
        BigInteger z1 = BigMath.floor(area.z1 + 1.0);
        if (area.x0 < 0.0) {
            x0 = x0.subtract(BigInteger.ONE);
        }

        if (area.y0 < 0.0) {
            --y0;
        }

        if (area.z0 < 0.0) {
            z0 = z0.subtract(BigInteger.ONE);
        }

        for (BigInteger x = x0; x.compareTo(x1) < 0; x = x.add(BigInteger.ONE)) {
            for (int y = y0; y < y1; ++y) {
                for (BigInteger z = z0; z.compareTo(z1) < 0; z = z.add(BigInteger.ONE)) {
                    Tile tile = Tile.tiles[getTile(x, y, z)];
                    if (tile != null) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean containsAnyBlocks(BigAABB area) {
        BigInteger x0 = BigMath.floor(area.x0());
        BigInteger x1 = BigMath.floor(area.x1().add(BigDecimal.ONE));
        int y0 = Mth.floor(area.y0());
        int y1 = Mth.floor(area.y1() + 1.0);
        BigInteger z0 = BigMath.floor(area.z0());
        BigInteger z1 = BigMath.floor(area.z1().add(BigDecimal.ONE));
        if (area.x0().compareTo(BigDecimal.ZERO) < 0) {
            x0 = x0.subtract(BigInteger.ONE);
        }

        if (area.y0() < 0.0) {
            --y0;
        }

        if (area.z0().compareTo(BigDecimal.ZERO) < 0) {
            z0 = z0.subtract(BigInteger.ONE);
        }

        for (BigInteger x = x0; x.compareTo(x1) < 0; x = x.add(BigInteger.ONE)) {
            for (int y = y0; y < y1; ++y) {
                for (BigInteger z = z0; z.compareTo(z1) < 0; z = z.add(BigInteger.ONE)) {
                    Tile tile = Tile.tiles[getTile(x, y, z)];
                    if (tile != null) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean containsAnyLiquid(AABB area) {
        BigInteger x0 = BigMath.floor(area.x0);
        BigInteger x1 = BigMath.floor(area.x1 + 1.0);
        int y0 = Mth.floor(area.y0);
        int y1 = Mth.floor(area.y1 + 1.0);
        BigInteger z0 = BigMath.floor(area.z0);
        BigInteger z1 = BigMath.floor(area.z1 + 1.0);
        if (area.x0 < 0.0) {
            x0 = x0.subtract(BigInteger.ONE);
        }

        if (area.y0 < 0.0) {
            --y0;
        }

        if (area.z0 < 0.0) {
            z0 = z0.subtract(BigInteger.ONE);
        }

        for (BigInteger x = x0; x.compareTo(x1) < 0; x = x.add(BigInteger.ONE)) {
            for (int y = y0; y < y1; ++y) {
                for (BigInteger z = z0; z.compareTo(z1) < 0; z = z.add(BigInteger.ONE)) {
                    Tile tile = Tile.tiles[getTile(x, y, z)];
                    if (tile != null && tile.material.isLiquid()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean containsFireTile(AABB area) {
        BigInteger x0 = BigMath.floor(area.x0);
        BigInteger x1 = BigMath.floor(area.x1 + 1.0);
        int y0 = Mth.floor(area.y0);
        int y1 = Mth.floor(area.y1 + 1.0);
        BigInteger z0 = BigMath.floor(area.z0);
        BigInteger z1 = BigMath.floor(area.z1 + 1.0);
        if (hasChunksAt(x0, y0, z0, x1, y1, z1)) {
            for (BigInteger x = x0; x.compareTo(x1) < 0; x = x.add(BigInteger.ONE)) {
                for (int y = y0; y < y1; ++y) {
                    for (BigInteger z = z0; z.compareTo(z1) < 0; z = z.add(BigInteger.ONE)) {
                        int tile = getTile(x, y, z);
                        if (tile == Tile.fire.id || tile == Tile.lava.id || tile == Tile.calmLava.id) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean checkAndHandleWater(AABB aabb, Material material, Entity entity) {
        BigInteger x0 = BigMath.floor(aabb.x0);
        BigInteger x1 = BigMath.floor(aabb.x1 + 1.0);
        int y0 = Mth.floor(aabb.y0);
        int y1 = Mth.floor(aabb.y1 + 1.0);
        BigInteger z0 = BigMath.floor(aabb.z0);
        BigInteger z1 = BigMath.floor(aabb.z1 + 1.0);
        if (!hasChunksAt(x0, y0, z0, x1, y1, z1)) {
            return false;
        } else {
            boolean isInside = false;
            Vec3 delta = Vec3.newTemp(0.0, 0.0, 0.0);

            for (BigInteger x = x0; x.compareTo(x1) < 0; x = x.add(BigInteger.ONE)) {
                for (int y = y0; y < y1; ++y) {
                    for (BigInteger z = z0; z.compareTo(z1) < 0; z = z.add(BigInteger.ONE)) {
                        Tile tile = Tile.tiles[getTile(x, y, z)];
                        if (tile != null && tile.material == material) {
                            double height = (float) (y + 1) - LiquidTile.getHeight(getData(x, y, z));
                            if ((double) y1 >= height) {
                                isInside = true;
                                tile.handleEntityInside((Level) (Object) this, x, y, z, entity, delta);
                            }
                        }
                    }
                }
            }

            if (delta.length() > 0.0) {
                delta = delta.normalize();
                double ep = 0.014;
                entity.xd += delta.x * ep;
                entity.yd += delta.y * ep;
                entity.zd += delta.z * ep;
            }

            return isInside;
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean containsMaterial(AABB aabb, Material material) {
        BigInteger x0 = BigMath.floor(aabb.x0);
        BigInteger x1 = BigMath.floor(aabb.x1 + 1.0);
        int y0 = Mth.floor(aabb.y0);
        int y1 = Mth.floor(aabb.y1 + 1.0);
        BigInteger z0 = BigMath.floor(aabb.z0);
        BigInteger z1 = BigMath.floor(aabb.z1 + 1.0);

        for (BigInteger x = x0; x.compareTo(x1) < 0; x = x.add(BigInteger.ONE)) {
            for (int y = y0; y < y1; ++y) {
                for (BigInteger z = z0; z.compareTo(z1) < 0; z = z.add(BigInteger.ONE)) {
                    Tile tile = Tile.tiles[getTile(x, y, z)];
                    if (tile != null && tile.material == material) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean containsLiquid(AABB aabb, Material material) {
        BigInteger x0 = BigMath.floor(aabb.x0);
        BigInteger x1 = BigMath.floor(aabb.x1 + 1.0);
        int y0 = Mth.floor(aabb.y0);
        int y1 = Mth.floor(aabb.y1 + 1.0);
        BigInteger z0 = BigMath.floor(aabb.z0);
        BigInteger z1 = BigMath.floor(aabb.z1 + 1.0);

        for (BigInteger x = x0; x.compareTo(x1) < 0; x = x.add(BigInteger.ONE)) {
            for (int y = y0; y < y1; ++y) {
                for (BigInteger z = z0; z.compareTo(z1) < 0; z = z.add(BigInteger.ONE)) {
                    Tile tile = Tile.tiles[getTile(x, y, z)];
                    if (tile != null && tile.material == material) {
                        int data = getData(x, y, z);
                        double h = (double) (y + 1);
                        if (data < 8) {
                            h = (double) (y + 1) - (double) data / 8.0;
                        }

                        if (h >= aabb.y0) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void extinguishFire(Player player, BigInteger x, int y, BigInteger z, int face) {
        if (face == Facing.DOWN) {
            --y;
        }

        if (face == Facing.UP) {
            ++y;
        }

        if (face == Facing.NORTH) {
            z = z.subtract(BigInteger.ONE);
        }

        if (face == Facing.SOUTH) {
            z = z.add(BigInteger.ONE);
        }

        if (face == Facing.WEST) {
            x = x.subtract(BigInteger.ONE);
        }

        if (face == Facing.EAST) {
            x = x.add(BigInteger.ONE);
        }

        if (getTile(x, y, z) == Tile.fire.id) {
            levelEvent(player, LevelEvent.SOUND_LAVA_FIZZ, x, y, z, 0);
            setTile(x, y, z, 0);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void addToTickNextTick(int x, int y, int z, int tileId, int delay) {
//        throw new RuntimeException("Big Level does not support addToTickNextTick");
    }

    @Override
    public void addToTickNextTick(BigInteger x, int y, BigInteger z, int tileId, int delay) {
        BigTickNextTickData data = new BigTickNextTickData(x, y, z, tileId);
        int range = 8;
        BigInteger bigRange = BigInteger.valueOf(range);
        if (this.instaTick) {
            if (this.hasChunksAt(data.xBig.subtract(bigRange), data.y - range, data.zBig.subtract(bigRange), data.xBig.add(bigRange), data.y + range, data.zBig.add(bigRange))) {
                int t = this.getTile(data.xBig, data.y, data.zBig);
                if (t == data.tileId && t > 0) {
                    Tile.tiles[t].tick((Level) (Object) this, data.xBig, data.y, data.zBig, this.random);
                }
            }
        } else {
            if (this.hasChunksAt(x.subtract(bigRange), y - range, z.subtract(bigRange), x.add(bigRange), y + range, z.add(bigRange))) {
                if (tileId > 0) {
                    data.delay((long) delay + this.levelData.getTime());
                }

                if (!this.tickNextTickSet.contains(data)) {
                    this.tickNextTickSet.add(data);
                    this.tickNextTickList.add(data);
                }
            }
        }
    }

    //? >=1.0.0-beta.8.0.r {
    /*@Shadow private List<TileEntity> tileEntitiesToUnload;
    @Shadow public abstract void addParticle(String id, double x, double y, double z, double xd, double yd, double zd);
    *///? }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tickEntities() {
        for (int i = 0; i < this.globalEntities.size(); ++i) {
            Entity e = this.globalEntities.get(i);
            e.tick();
            if (e.removed) {
                this.globalEntities.remove(i--);
            }
        }

        this.entities.removeAll(this.entitiesToRemove);

        for (int i = 0; i < this.entitiesToRemove.size(); ++i) {
            Entity entity = this.entitiesToRemove.get(i);
            BigInteger xc = entity.getXChunk();
            BigInteger zc = entity.getZChunk();
            if (entity.inChunk && this.hasChunk(xc, zc)) {
                this.getChunk(xc, zc).removeEntity(entity);
            }
        }

        for (int i = 0; i < this.entitiesToRemove.size(); ++i) {
            this.entityRemoved(this.entitiesToRemove.get(i));
        }

        this.entitiesToRemove.clear();

        for (int i = 0; i < this.entities.size(); ++i) {
            Entity entity = this.entities.get(i);
            if (entity.riding != null) {
                if (!entity.riding.removed && entity.riding.rider == entity) {
                    continue;
                }

                entity.riding.rider = null;
                entity.riding = null;
            }

            if (!entity.removed) {
                this.tick(entity);
            }

            if (entity.removed) {
                BigInteger xc = entity.getXChunk();
                BigInteger zc = entity.getZChunk();
                if (entity.inChunk && this.hasChunk(xc, zc)) {
                    this.getChunk(xc, zc).removeEntity(entity);
                }

                this.entities.remove(i--);
                this.entityRemoved(entity);
            }
        }

        this.updatingTileEntities = true;
        Iterator<TileEntity> teIterator = this.tileEntityList.iterator();

        while (teIterator.hasNext()) {
            TileEntity te = teIterator.next();
            //? >=1.0.0-beta.8.0.r {
            /*if (!te.isRemoved() && te.level != null) {
            *///? } else
            if (!te.isRemoved()) {
                te.tick();
            }

            if (te.isRemoved()) {
                teIterator.remove();
                //? >=1.0.0-beta.8.0.r
                //if (this.hasChunk(te.getX().shiftRight(4), te.getZ().shiftRight(4))) {
                    LevelChunk chunk = this.getChunk(te.getX().shiftRight(4), te.getZ().shiftRight(4));
                    if (chunk != null) {
                        chunk.removeTileEntity(te.getX().and(BigConstants.FIFTEEN).intValue(), te.y, te.getZ().and(BigConstants.FIFTEEN).intValue());
                    }
                //? >=1.0.0-beta.8.0.r
                //}
            }
        }

        this.updatingTileEntities = false;
        //? >=1.0.0-beta.8.0.r {
        /*if (!this.tileEntitiesToUnload.isEmpty()) {
            this.tileEntityList.removeAll(this.tileEntitiesToUnload);
            this.tileEntitiesToUnload.clear();
        }
        *///? }
        if (!this.pendingTileEntities.isEmpty()) {
            for (TileEntity te : this.pendingTileEntities) {
                if (!te.isRemoved()) {
                    if (!this.tileEntityList.contains(te)) {
                        this.tileEntityList.add(te);
                    }

                    //? >=1.0.0-beta.8.0.r
                    //if (this.hasChunk(te.getX().shiftRight(4), te.getZ().shiftRight(4))) {
                        LevelChunk chunk = this.getChunk(te.getX().shiftRight(4), te.getZ().shiftRight(4));
                        if (chunk != null) {
                            chunk.setTileEntity(te.getX().and(BigConstants.FIFTEEN).intValue(), te.y, te.getZ().and(BigConstants.FIFTEEN).intValue(), te);
                        }
                    //? >=1.0.0-beta.8.0.r
                    //}

                    this.sendTileUpdated(te.getX(), te.y, te.getZ());
                }
            }

            this.pendingTileEntities.clear();
        }
    }

    public void tickPlayer(Entity entity, boolean tick) {
        BigEntityExtension bigEntity = (BigEntityExtension) entity;
        BigInteger xt = BigMath.floor(bigEntity.getX());
        BigInteger zt = BigMath.floor(bigEntity.getZ());
        BigInteger range = BigInteger.valueOf(32);
        if (!tick || hasChunksAt(xt.subtract(range), 0, zt.subtract(range), xt.add(range), 128, zt.add(range))) {
            bigEntity.setXOld(bigEntity.getX());
            entity.yOld = entity.y;
            bigEntity.setZOld(bigEntity.getZ());
            entity.yRotO = entity.yRot;
            entity.xRotO = entity.xRot;
            if (tick && entity.inChunk) {
                if (entity.riding != null) {
                    entity.rideTick();
                } else {
                    entity.tick();
                }
            }

//            if (Double.isNaN(entity.x) || Double.isInfinite(entity.x)) {
//                entity.x = entity.xOld;
//            }

            if (Double.isNaN(entity.y) || Double.isInfinite(entity.y)) {
                entity.y = entity.yOld;
            }

//            if (Double.isNaN(entity.z) || Double.isInfinite(entity.z)) {
//                entity.z = entity.zOld;
//            }

            if (Double.isNaN((double) entity.xRot) || Double.isInfinite((double) entity.xRot)) {
                entity.xRot = entity.xRotO;
            }

            if (Double.isNaN((double) entity.yRot) || Double.isInfinite((double) entity.yRot)) {
                entity.yRot = entity.yRotO;
            }

            BigInteger xc = BigMath.floor(bigEntity.getX().divide(BigConstants.SIXTEEN_F, RoundingMode.HALF_UP));
            int yc = Mth.floor(entity.y / 16.0);
            BigInteger zc = BigMath.floor(bigEntity.getZ().divide(BigConstants.SIXTEEN_F, RoundingMode.HALF_UP));
            if (!entity.inChunk || !entity.getXChunk().equals(xc) || entity.yChunk != yc || !entity.getZChunk().equals(zc)) {
                if (entity.inChunk && this.hasChunk(entity.getXChunk(), entity.getZChunk())) {
                    getChunk(entity.getXChunk(), entity.getZChunk()).removeEntity(entity, entity.yChunk);
                }

                if (this.hasChunk(xc, zc)) {
                    entity.inChunk = true;
                    getChunk(xc, zc).addEntity(entity);
                } else {
                    entity.inChunk = false;
                }
            }

            if (tick && entity.inChunk && entity.rider != null) {
                if (!entity.rider.removed && entity.rider.riding == entity) {
                    tick(entity.rider);
                } else {
                    entity.rider.riding = null;
                    entity.rider = null;
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tick(Entity e, boolean actual) {
        if (e instanceof Player && e instanceof BigEntityExtension bigEntity) {
            tickPlayer((Player) e, actual);
            return;
        }
        BigInteger xc = BigMath.floor(e.x);
        BigInteger zc = BigMath.floor(e.z);
        BigInteger r = BigInteger.valueOf(32);
        if (!actual || this.hasChunksAt(xc.subtract(r), 0, zc.subtract(r), xc.add(r), 128, zc.add(r))) {
            e.xOld = e.x;
            e.yOld = e.y;
            e.zOld = e.z;
            if (e instanceof BigEntityExtension bigEntity && e.isBigMovementEnabled()) {
                bigEntity.setXOld(bigEntity.getX());
                bigEntity.setZOld(bigEntity.getZ());
            }
            e.yRotO = e.yRot;
            e.xRotO = e.xRot;
            if (actual && e.inChunk) {
                if (e.riding != null) {
                    e.rideTick();
                } else {
                    e.tick();
                }
            }

            if (Double.isNaN(e.x) || Double.isInfinite(e.x)) {
                e.x = e.xOld;
            }

            if (Double.isNaN(e.y) || Double.isInfinite(e.y)) {
                e.y = e.yOld;
            }

            if (Double.isNaN(e.z) || Double.isInfinite(e.z)) {
                e.z = e.zOld;
            }

            if (Double.isNaN(e.xRot) || Double.isInfinite(e.xRot)) {
                e.xRot = e.xRotO;
            }

            if (Double.isNaN(e.yRot) || Double.isInfinite(e.yRot)) {
                e.yRot = e.yRotO;
            }

            BigInteger xcn = BigMath.floor(e.x / 16.0);
            int ycn = Mth.floor(e.y / 16.0);
            BigInteger zcn = BigMath.floor(e.z / 16.0);
            if (!e.inChunk || !e.getXChunk().equals(xcn) || e.yChunk != ycn || !e.getZChunk().equals(zcn)) {
                if (e.inChunk && this.hasChunk(e.getXChunk(), e.getZChunk())) {
                    this.getChunk(e.getXChunk(), e.getZChunk()).removeEntity(e, e.yChunk);
                }

                if (this.hasChunk(xcn, zcn)) {
                    e.inChunk = true;
                    this.getChunk(xcn, zcn).addEntity(e);
                } else {
                    e.inChunk = false;
                }
            }

            if (actual && e.inChunk && e.rider != null) {
                if (!e.rider.removed && e.rider.riding == e) {
                    this.tick(e.rider);
                } else {
                    e.rider.riding = null;
                    e.rider = null;
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Environment(EnvType.SERVER)
    @Overwrite
    public void removeEntityImmediately(Entity entity) {
        entity.remove();
        if (entity instanceof Player) {
            this.players.remove((Player) entity);
            updateSleepingPlayerList();
        }

        BigInteger xc = entity.getXChunk();
        BigInteger zc = entity.getZChunk();
        if (entity.inChunk && hasChunk(xc, zc)) {
            getChunk(xc, zc).removeEntity(entity);
        }

        this.entities.remove(entity);
        entityRemoved(entity);
    }

    /**
     * @author
     * @reason
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public void removeAllPendingEntityRemovals() {
        this.entities.removeAll(this.entitiesToRemove);

        for (int i = 0; i < this.entitiesToRemove.size(); ++i) {
            Entity entity = (Entity) this.entitiesToRemove.get(i);
            BigInteger xc = entity.getXChunk();
            BigInteger zc = entity.getZChunk();
            if (entity.inChunk && hasChunk(xc, zc)) {
                getChunk(xc, zc).removeEntity(entity);
            }
        }

        for (int i = 0; i < this.entitiesToRemove.size(); ++i) {
            entityRemoved((Entity) this.entitiesToRemove.get(i));
        }

        this.entitiesToRemove.clear();

        for (int i = 0; i < this.entities.size(); ++i) {
            Entity entity = this.entities.get(i);
            if (entity.riding != null) {
                if (!entity.riding.removed && entity.riding.rider == entity) {
                    continue;
                }

                entity.riding.rider = null;
                entity.riding = null;
            }

            if (entity.removed) {
                BigInteger xc = entity.getXChunk();
                BigInteger zc = entity.getZChunk();
                if (entity.inChunk && hasChunk(xc, zc)) {
                    getChunk(xc, zc).removeEntity(entity);
                }

                this.entities.remove(i--);
                entityRemoved(entity);
            }
        }
    }

    @Override
    public BigVec3i getBigSpawnPos() {
        return new BigVec3i(this.levelData.getBigSpawnX(), this.levelData.getSpawnY(), this.levelData.getBigSpawnZ());
    }

    @Override
    public void tileEvent(BigInteger x, int y, BigInteger z, int b0, int b1) {
        int tile = this.getTile(x, y, z);
        if (tile > 0) {
            Tile.tiles[tile].triggerEvent((Level) (Object) this, x, y, z, b0, b1);
        }
    }

    @Override
    public void levelEvent(int event, BigInteger x, int y, BigInteger z, int data) {
        levelEvent(null, event, x, y, z, data);
    }

    @Override
    public void levelEvent(Player player, int event, BigInteger x, int y, BigInteger z, int data) {
        for (LevelListener listener : this.listeners) {
            listener.levelEvent(player, event, x, y, z, data);
        }
    }
}
