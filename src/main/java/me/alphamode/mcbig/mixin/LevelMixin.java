package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.BigLevelExtension;
import me.alphamode.mcbig.extensions.BigLevelSourceExtension;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.level.BigLightUpdate;
import me.alphamode.mcbig.level.BigTickNextTickData;
import me.alphamode.mcbig.level.chunk.BigChunkPos;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.math.BigMath;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkPos;
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
    public int skyDarken;

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
    public abstract int getTopSolidBlock(int x, int z);

    @Shadow
    private Set<BigChunkPos> chunksToPoll;

    @Shadow
    private int delayUntilNextMoodSound;

    @Shadow
    public abstract Player getNearestPlayer(double x, double y, double z, double range);

    @Shadow
    public abstract void playSound(double x, double y, double z, String soundId, float volume, float pitch);

    @Shadow
    public abstract boolean isThundering();

    @Shadow
    public abstract boolean setTile(int x, int y, int z, int tile);

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
    private static int maxLoop;

    @Shadow
    private List<BigLightUpdate> lightUpdates;

    @Shadow
    private int maxRecurse;

    @Shadow
    public abstract void updateLight(LightLayer type, int x0, int y0, int z0, int x1, int y1, int z1, boolean bl);

    @Shadow
    private ArrayList<AABB> boxes;

    @Shadow
    private List<Entity> es;

    @Shadow public boolean instaTick;

    @Shadow private Set<BigTickNextTickData> tickNextTickSet;

    @Shadow private TreeSet<BigTickNextTickData> tickNextTickList;

    @Shadow protected LevelData levelData;

    @Shadow public List globalEntities;

    @Shadow private List entitiesToRemove;

    @Shadow protected abstract void entityRemoved(Entity entity);

    @Shadow public abstract void tick(Entity entity);

    @Shadow private boolean updatingTileEntities;

    @Shadow public List tileEntityList;

    @Shadow private List<TileEntity> pendingTileEntities;

    private boolean hasChunk(BigInteger x, BigInteger z) {
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

    @Override
    public boolean setTileNoUpdate(BigInteger x, int y, BigInteger z, int tile) {
        if (y < 0) {
            return false;
        } else if (y >= 128) {
            return false;
        } else {
            LevelChunk chunk = getChunk(x.shiftRight(4), z.shiftRight(4));
            return chunk.setTile(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue(), tile);
        }
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

    @Override
    public boolean setTileAndDataNoUpdate(BigInteger x, int y, BigInteger z, int tile, int data) {
        if (y < 0) {
            return false;
        } else if (y >= 128) {
            return false;
        } else {
            LevelChunk chunk = this.getChunk(x.shiftRight(4), z.shiftRight(4));
            return chunk.setTileAndData(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue(), tile, data);
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

    @Override
    public int getLightLevel(BigInteger x, int y, BigInteger z) {
        return getRawBrightness(x, y, z, true);
    }

    @Override
    public int getRawBrightness(BigInteger x, int y, BigInteger z, boolean combineNeighbours) {
        if (combineNeighbours) {
            int tile = getTile(x, y, z);
            if (tile == Tile.SLAB.id || tile == Tile.FARMLAND.id || tile == Tile.COBBLESTONE_STAIRS.id || tile == Tile.WOOD_STAIRS.id) {
                int var6 = getRawBrightness(x, y + 1, z, false);
                int var7 = getRawBrightness(x.add(BigInteger.ONE), y, z, false);
                int var8 = getRawBrightness(x.subtract(BigInteger.ONE), y, z, false);
                int var9 = getRawBrightness(x, y, z.add(BigInteger.ONE), false);
                int var10 = getRawBrightness(x, y, z.subtract(BigInteger.ONE), false);
                if (var7 > var6) {
                    var6 = var7;
                }

                if (var8 > var6) {
                    var6 = var8;
                }

                if (var9 > var6) {
                    var6 = var9;
                }

                if (var10 > var6) {
                    var6 = var10;
                }

                return var6;
            }
        }

        if (y < 0) {
            return 0;
        } else {
            if (y >= 128) {
                y = 127;
            }

            LevelChunk var13 = this.getChunk(x.shiftRight(4), z.shiftRight(4));
            return var13.getRawBrightness(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue(), this.skyDarken);
        }
    }

    @Override
    public boolean isSkyLit(BigInteger x, int y, BigInteger z) {
        if (y < 0) {
            return false;
        } else if (y >= 128) {
            return true;
        } else if (!this.hasChunk(x.shiftRight(4), z.shiftRight(4))) {
            return false;
        } else {
            LevelChunk chunk = getChunk(x.shiftRight(4), z.shiftRight(4));
            return chunk.isSkyLit(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue());
        }
    }

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
    public float getBrightness(BigInteger x, int y, BigInteger z, int max) {
        int level = getLightLevel(x, y, z);
        if (level < max) {
            level = max;
        }

        return this.dimension.brightnessRamp[level];
    }

    @Override
    public float getBrightness(BigInteger x, int y, BigInteger z) {
        return this.dimension.brightnessRamp[this.getLightLevel(x, y, z)];
    }

    @Override
    public void updateLightIfOtherThan(LightLayer layer, BigInteger x, int y, BigInteger z, int level) {
        if (!this.dimension.hasCeiling || layer != LightLayer.SKY) {
            if (this.hasChunkAt(x, y, z)) {
                if (layer == LightLayer.SKY) {
                    if (this.isSkyLit(x, y, z)) {
                        level = 15;
                    }
                } else if (layer == LightLayer.BLOCK) {
                    int var6 = this.getTile(x, y, z);
                    if (Tile.lightEmission[var6] > level) {
                        level = Tile.lightEmission[var6];
                    }
                }

                if (this.getBrightness(layer, x, y, z) != level) {
                    this.updateLight(layer, x, y, z, x, y, z);
                }
            }
        }
    }

    @Override
    public int getBrightness(LightLayer type, BigInteger x, int y, BigInteger z) {
        if (y < 0) {
            y = 0;
        }

        if (y >= 128) {
            y = 127;
        }

        if (y >= 0 && y < 128) {
            BigInteger chunkX = x.shiftRight(4);
            BigInteger chunkZ = z.shiftRight(4);
            if (!hasChunk(chunkX, chunkZ)) {
                return 0;
            } else {
                LevelChunk chunk = this.getChunk(chunkX, chunkZ);
                return chunk.getBrightness(type, x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue());
            }
        } else {
            return type.surrounding;
        }
    }

    @Override
    public void setBrightness(LightLayer layer, BigInteger x, int y, BigInteger z, int level) {
        if (y >= 0) {
            if (y < 128) {
                if (hasChunk(x.shiftRight(4), z.shiftRight(4))) {
                    LevelChunk var6 = this.getChunk(x.shiftRight(4), z.shiftRight(4));
                    var6.setBrightness(layer, x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue(), level);

                    for (LevelListener listener : this.listeners) {
                        listener.tileChanged(x, y, z);
                    }
                }
            }
        }
    }

    @Override
    public boolean canSeeSky(BigInteger x, int y, BigInteger z) {
        return getChunk(x.shiftRight(4), z.shiftRight(4)).isSkyLit(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue());
    }

    @Override
    public int getRawBrightness(BigInteger x, int y, BigInteger z) {
        if (y < 0) {
            return 0;
        } else {
            if (y >= 128) {
                y = 127;
            }

            return this.getChunk(x.shiftRight(4), z.shiftRight(4)).getRawBrightness(x.and(BigConstants.FIFTEEN).intValue(), y, z.and(BigConstants.FIFTEEN).intValue(), 0);
        }
    }

    @Override
    public boolean isSolidTile(BigInteger x, int y, BigInteger z) {
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
        return tile == 0 ? Material.AIR : Tile.tiles[tile].material;
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

    /**
     * @author
     * @reason
     */
    @Overwrite
    public HitResult clip(Vec3 from, Vec3 to, boolean checkLiquid, boolean bl2) {
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
            if ((!bl2 || tile == null || tile.getAABB((Level) (Object) this, fromX, fromY, fromZ) != null) && tileId > 0 && tile.mayPick(data, checkLiquid)) {
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
                if ((!bl2 || var37 == null || var37.getAABB((Level) (Object) this, fromX, fromY, fromZ) != null) && var35 > 0 && var37.mayPick(var36, checkLiquid)) {
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
            if (tile > 0) {
                Tile.tiles[tile].animateTick((Level) (Object) this, xt, yt, zt, rand);
            }
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
    public int getTopSolidBlock(BigInteger x, BigInteger z) {
        LevelChunk chunk = getChunkAt(x, z);
        int xt = x.and(BigConstants.FIFTEEN).intValue();
        int zt = z.and(BigConstants.FIFTEEN).intValue();

        for (int yt = 127; yt > 0; --yt) {
            int tile = chunk.getTile(xt, yt, zt);
            Material material = tile == 0 ? Material.AIR : Tile.tiles[tile].material;
            if (material.blocksMotion() || material.isLiquid()) {
                return yt + 1;
            }
        }

        return -1;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tickTiles() {
        this.chunksToPoll.clear();

        for (Player player : this.players) {
            BigInteger var3 = BigMath.floor(player.x / 16.0);
            BigInteger var4 = BigMath.floor(player.z / 16.0);
            byte var5 = 9;

            for (int var6 = -var5; var6 <= var5; ++var6) {
                for (int var7 = -var5; var7 <= var5; ++var7) {
                    this.chunksToPoll.add(new BigChunkPos(BigInteger.valueOf(var6).add(var3), BigInteger.valueOf(var7).add(var4)));
                }
            }
        }

        if (this.delayUntilNextMoodSound > 0) {
            --this.delayUntilNextMoodSound;
        }

        for (BigChunkPos pos : this.chunksToPoll) {
            BigInteger startX = pos.x().multiply(BigConstants.SIXTEEN);
            BigInteger startZ = pos.z().multiply(BigConstants.SIXTEEN);
            LevelChunk chunk = this.getChunk(pos.x(), pos.z());
            if (this.delayUntilNextMoodSound == 0) {
                this.randValue = this.randValue * 3 + 1013904223;
                int randPos = this.randValue >> 2;
                BigInteger xt = BigInteger.valueOf(randPos & 15);
                BigInteger zt = BigInteger.valueOf(randPos >> 8 & 15);
                int yt = randPos >> 16 & 127;
                int tile = chunk.getTile(xt.intValue(), yt, zt.intValue());
                xt = xt.add(startX);
                zt = zt.add(startZ);
                if (tile == 0 && getRawBrightness(xt, yt, zt) <= this.random.nextInt(8) && getBrightness(LightLayer.SKY, xt, yt, zt) <= 0) {
                    Player var11 = getNearestPlayer((double) xt.doubleValue() + 0.5, (double) yt + 0.5, (double) zt.doubleValue() + 0.5, 8.0);
                    if (var11 != null && var11.distanceToSqr((double) xt.doubleValue() + 0.5, (double) yt + 0.5, (double) zt.doubleValue() + 0.5) > 4.0) {
                        playSound((double) xt.doubleValue() + 0.5, (double) yt + 0.5, (double) zt.doubleValue() + 0.5, "ambient.cave.cave", 0.7F, 0.8F + this.random.nextFloat() * 0.2F);
                        this.delayUntilNextMoodSound = this.random.nextInt(12000) + 6000;
                    }
                }
            }

            if (this.random.nextInt(100000) == 0 && this.isRaining() && this.isThundering()) {
                this.randValue = this.randValue * 3 + 1013904223;
                int var18 = this.randValue >> 2;
                BigInteger var23 = startX.add(BigInteger.valueOf((var18 & 15)));
                BigInteger var27 = startZ.add(BigInteger.valueOf(var18 >> 8 & 15));
                int var30 = getTopSolidBlock(var23, var27);
                if (isRainingAt(var23, var30, var27)) {
                    addGlobalEntity(new LightningBolt((Level) (Object) this, (double) var23.doubleValue(), (double) var30, (double) var27.doubleValue()));
                    this.lightingCooldown = 2;
                }
            }

            if (this.random.nextInt(16) == 0) {
                this.randValue = this.randValue * 3 + 1013904223;
                int var19 = this.randValue >> 2;
                int var24 = var19 & 15;
                int var28 = var19 >> 8 & 15;
                int var31 = getTopSolidBlock(BigInteger.valueOf(var24).add(startX), BigInteger.valueOf(var28).add(startZ));
                if (getBiomeSource().getBiome(var24 + startX.intValue(), var28 + startZ.intValue()).hasPrecipitation()
                        && var31 >= 0
                        && var31 < 128
                        && chunk.getBrightness(LightLayer.BLOCK, var24, var31, var28) < 10) {
                    int var33 = chunk.getTile(var24, var31 - 1, var28);
                    int var35 = chunk.getTile(var24, var31, var28);
                    if (isRaining()
                            && var35 == 0
                            && Tile.SNOW_LAYER.mayPlace((Level) (Object) this, var24 + startX.intValue(), var31, var28 + startZ.intValue())
                            && var33 != 0
                            && var33 != Tile.ICE.id
                            && Tile.tiles[var33].material.blocksMotion()) {
                        setTile(BigInteger.valueOf(var24).add(startX), var31, BigInteger.valueOf(var28).add(startZ), Tile.SNOW_LAYER.id);
                    }

                    if (var33 == Tile.WATER.id && chunk.getData(var24, var31 - 1, var28) == 0) {
                        setTile(BigInteger.valueOf(var24).add(startX), var31 - 1, BigInteger.valueOf(var28).add(startZ), Tile.ICE.id);
                    }
                }
            }

            for (int var20 = 0; var20 < 80; ++var20) {
                this.randValue = this.randValue * 3 + 1013904223;
                int var25 = this.randValue >> 2;
                int var29 = var25 & 15;
                int var32 = var25 >> 8 & 15;
                int var34 = var25 >> 16 & 127;
                int var36 = chunk.blocks[var29 << 11 | var32 << 7 | var34] & 255;
                if (Tile.shouldTick[var36]) {
                    Tile.tiles[var36].tick((Level) (Object) this, BigInteger.valueOf(var29).add(startX), var34, BigInteger.valueOf(var32).add(startZ), this.random);
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean updateLights() {
        if (this.maxRecurse >= 50) {
            return false;
        } else {
            ++this.maxRecurse;

            try {
                int var1 = 500;

                while (this.lightUpdates.size() > 0) {
                    if (--var1 <= 0) {
                        return true;
                    }

                    this.lightUpdates.remove(this.lightUpdates.size() - 1).update((Level) (Object) this);
                }

                return false;
            } finally {
                --this.maxRecurse;
            }
        }
    }

    @Override
    public void updateLight(LightLayer type, BigInteger x0, int y0, BigInteger z0, BigInteger x1, int y1, BigInteger z1) {
        this.updateLight(type, x0, y0, z0, x1, y1, z1, true);
    }

    @Overwrite
    public void updateLight(LightLayer type, int x0, int y0, int z0, int x1, int y1, int z1) {
        this.updateLight(type, x0, y0, z0, x1, y1, z1, true);
    }

    @Override
    public void updateLight(LightLayer type, BigInteger x0, int y0, BigInteger z0, BigInteger x1, int y1, BigInteger z1, boolean bl) {
        if (!this.dimension.hasCeiling || type != LightLayer.SKY) {
            ++maxLoop;

            try {
                if (maxLoop != 50) {
                    BigInteger x = (x1.add(x0)).divide(BigInteger.TWO);
                    BigInteger z = (z1.add(z0)).divide(BigInteger.TWO);
                    if (hasChunkAt(x, 64, z)) {
                        if (!getChunkAt(x, z).isEmpty()) {
                            int var11 = this.lightUpdates.size();
                            if (bl) {
                                int var12 = 5;
                                if (var12 > var11) {
                                    var12 = var11;
                                }

                                for (int var13 = 0; var13 < var12; ++var13) {
                                    BigLightUpdate update = this.lightUpdates.get(this.lightUpdates.size() - var13 - 1);
                                    if (update.type == type && update.expandToContain(x0, y0, z0, x1, y1, z1)) {
                                        return;
                                    }
                                }
                            }

                            this.lightUpdates.add(new BigLightUpdate(type, x0, y0, z0, x1, y1, z1));
                            int updates = 1000000;
                            if (this.lightUpdates.size() > 1000000) {
                                System.out.println("More than " + updates + " updates, aborting lighting updates");
                                this.lightUpdates.clear();
                            }
                        }
                    }
                }
            } finally {
                --maxLoop;
            }
        }
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

    @Override
    public boolean isRainingAt(BigInteger x, int y, BigInteger z) {
        if (!isRaining()) {
            return false;
        } else if (!canSeeSky(x, y, z)) {
            return false;
        } else if (getTopSolidBlock(x, z) > y) {
            return false;
        } else {
            Biome biome = getBiomeSource().getBiome(x.intValue(), z.intValue());
            return biome.hasPrecipitation() ? false : biome.hasRain();
        }
    }

    /**
     * @author
     * @reason
     */
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
    public boolean containsAnyTiles(AABB area) {
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
                        if (tile == Tile.FIRE.id || tile == Tile.FLOWING_LAVA.id || tile == Tile.LAVA.id) {
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
        if (face == 0) {
            --y;
        }

        if (face == 1) {
            ++y;
        }

        if (face == 2) {
            z = z.subtract(BigInteger.ONE);
        }

        if (face == 3) {
            z = z.add(BigInteger.ONE);
        }

        if (face == 4) {
            x = x.subtract(BigInteger.ONE);
        }

        if (face == 5) {
            x = x.add(BigInteger.ONE);
        }

        if (getTile(x, y, z) == Tile.FIRE.id) {
            levelEvent(player, LevelEvent.SOUND_LAVA_FIZZ, x, y, z, 0);
            setTile(x, y, z, 0);
        }
    }

    @Override
    public void addToTickNextTick(BigInteger x, int y, BigInteger z, int tileId, int delay) {
        BigTickNextTickData var6 = new BigTickNextTickData(x, y, z, tileId);
        byte var7 = 8;
        BigInteger bigRange = BigInteger.valueOf(var7);
        if (this.instaTick) {
            if (this.hasChunksAt(var6.xBig.subtract(bigRange), var6.y - var7, var6.zBig.subtract(bigRange), var6.xBig.add(bigRange), var6.y + var7, var6.zBig.add(bigRange))) {
                int var8 = this.getTile(var6.xBig, var6.y, var6.zBig);
                if (var8 == var6.priority && var8 > 0) {
                    Tile.tiles[var8].tick((Level) (Object) this, var6.xBig, var6.y, var6.zBig, this.random);
                }
            }
        } else {
            if (this.hasChunksAt(x.subtract(bigRange), y - var7, z.subtract(bigRange), x.add(bigRange), y + var7, z.add(bigRange))) {
                if (tileId > 0) {
                    var6.delay((long)delay + this.levelData.getTime());
                }

                if (!this.tickNextTickSet.contains(var6)) {
                    this.tickNextTickSet.add(var6);
                    this.tickNextTickList.add(var6);
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tickEntities() {
        for(int var1 = 0; var1 < this.globalEntities.size(); ++var1) {
            Entity var2 = (Entity)this.globalEntities.get(var1);
            var2.tick();
            if (var2.removed) {
                this.globalEntities.remove(var1--);
            }
        }

        this.entities.removeAll(this.entitiesToRemove);

        for(int var5 = 0; var5 < this.entitiesToRemove.size(); ++var5) {
            Entity entity = (Entity)this.entitiesToRemove.get(var5);
            BigInteger xc = entity.getXChunk();
            BigInteger zc = entity.getZChunk();
            if (entity.inChunk && this.hasChunk(xc, zc)) {
                this.getChunk(xc, zc).removeEntity(entity);
            }
        }

        for(int var6 = 0; var6 < this.entitiesToRemove.size(); ++var6) {
            this.entityRemoved((Entity)this.entitiesToRemove.get(var6));
        }

        this.entitiesToRemove.clear();

        for(int var7 = 0; var7 < this.entities.size(); ++var7) {
            Entity entity = this.entities.get(var7);
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

                this.entities.remove(var7--);
                this.entityRemoved(entity);
            }
        }

        this.updatingTileEntities = true;
        Iterator var8 = this.tileEntityList.iterator();

        while(var8.hasNext()) {
            TileEntity te = (TileEntity)var8.next();
            if (!te.isRemoved()) {
                te.tick();
            }

            if (te.isRemoved()) {
                var8.remove();
                LevelChunk var14 = this.getChunk(te.x >> 4, te.z >> 4);
                if (var14 != null) {
                    var14.removeTileEntity(te.x & 15, te.y, te.z & 15);
                }
            }
        }

        this.updatingTileEntities = false;
        if (!this.pendingTileEntities.isEmpty()) {
            for(TileEntity var15 : this.pendingTileEntities) {
                if (!var15.isRemoved()) {
                    if (!this.tileEntityList.contains(var15)) {
                        this.tileEntityList.add(var15);
                    }

                    LevelChunk var17 = this.getChunk(var15.x >> 4, var15.z >> 4);
                    if (var17 != null) {
                        var17.setTileEntity(var15.x & 15, var15.y, var15.z & 15, var15);
                    }

                    this.sendTileUpdated(BigInteger.valueOf(var15.x), var15.y, BigInteger.valueOf(var15.z));
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

            if (Double.isNaN((double)entity.xRot) || Double.isInfinite((double)entity.xRot)) {
                entity.xRot = entity.xRotO;
            }

            if (Double.isNaN((double)entity.yRot) || Double.isInfinite((double)entity.yRot)) {
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
    public void tick(Entity entity, boolean tick) {
        if (entity instanceof Player) {
            tickPlayer((Player)entity, tick);
            return;
        }
        BigInteger var3 = BigMath.floor(entity.x);
        BigInteger var4 = BigMath.floor(entity.z);
        BigInteger var5 = BigInteger.valueOf(32);
        if (!tick || this.hasChunksAt(var3.subtract(var5), 0, var4.subtract(var5), var3.add(var5), 128, var4.add(var5))) {
            entity.xOld = entity.x;
            entity.yOld = entity.y;
            entity.zOld = entity.z;
            entity.yRotO = entity.yRot;
            entity.xRotO = entity.xRot;
            if (tick && entity.inChunk) {
                if (entity.riding != null) {
                    entity.rideTick();
                } else {
                    entity.tick();
                }
            }

            if (Double.isNaN(entity.x) || Double.isInfinite(entity.x)) {
                entity.x = entity.xOld;
            }

            if (Double.isNaN(entity.y) || Double.isInfinite(entity.y)) {
                entity.y = entity.yOld;
            }

            if (Double.isNaN(entity.z) || Double.isInfinite(entity.z)) {
                entity.z = entity.zOld;
            }

            if (Double.isNaN((double)entity.xRot) || Double.isInfinite((double)entity.xRot)) {
                entity.xRot = entity.xRotO;
            }

            if (Double.isNaN((double)entity.yRot) || Double.isInfinite((double)entity.yRot)) {
                entity.yRot = entity.yRotO;
            }

            BigInteger xc = BigMath.floor(entity.x / 16.0);
            int yc = Mth.floor(entity.y / 16.0);
            BigInteger zc = BigMath.floor(entity.z / 16.0);
            if (!entity.inChunk || !entity.getXChunk().equals(xc) || entity.yChunk != yc || !entity.getZChunk().equals(zc)) {
                if (entity.inChunk && this.hasChunk(entity.getXChunk(), entity.getZChunk())) {
                    this.getChunk(entity.getXChunk(), entity.getZChunk()).removeEntity(entity, entity.yChunk);
                }

                if (this.hasChunk(xc, zc)) {
                    entity.inChunk = true;
                    this.getChunk(xc, zc).addEntity(entity);
                } else {
                    entity.inChunk = false;
                }
            }

            if (tick && entity.inChunk && entity.rider != null) {
                if (!entity.rider.removed && entity.rider.riding == entity) {
                    this.tick(entity.rider);
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
    @Environment(EnvType.SERVER)
    @Overwrite
    public void removeEntityImmediately(Entity entity) {
        entity.remove();
        if (entity instanceof Player) {
            this.players.remove((Player)entity);
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
    @Overwrite
    public void removeAllPendingEntityRemovals() {
        this.entities.removeAll(this.entitiesToRemove);

        for(int i = 0; i < this.entitiesToRemove.size(); ++i) {
            Entity entity = (Entity)this.entitiesToRemove.get(i);
            BigInteger xc = entity.getXChunk();
            BigInteger zc = entity.getZChunk();
            if (entity.inChunk && hasChunk(xc, zc)) {
                getChunk(xc, zc).removeEntity(entity);
            }
        }

        for(int i = 0; i < this.entitiesToRemove.size(); ++i) {
            entityRemoved((Entity)this.entitiesToRemove.get(i));
        }

        this.entitiesToRemove.clear();

        for(int i = 0; i < this.entities.size(); ++i) {
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
