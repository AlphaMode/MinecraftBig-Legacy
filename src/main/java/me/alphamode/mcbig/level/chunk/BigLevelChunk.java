package me.alphamode.mcbig.level.chunk;

import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.math.BigMath;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.tile.Tile;

import java.math.BigInteger;
import java.math.RoundingMode;

public class BigLevelChunk extends LevelChunk {
    public final BigInteger bigX;
    public final BigInteger bigZ;

    public BigLevelChunk(Level level, BigInteger x, BigInteger z) {
        super(level, x.intValue(), z.intValue());
        this.bigX = x;
        this.bigZ = z;
    }

    public BigLevelChunk(Level level, byte[] tiles, BigInteger x, BigInteger z) {
        super(level, tiles, x.intValue(), z.intValue());
        this.bigX = x;
        this.bigZ = z;
    }

    public boolean isAt(BigInteger x, BigInteger z) {
        return x.equals(this.bigX) && z.equals(this.bigZ);
    }

    @Override
    protected void lightGaps(int x, int z) {
        int height = getHeightmap(x, z);
        BigInteger xt = this.bigX.multiply(BigInteger.valueOf(16 + x));
        BigInteger zt = this.bigZ.multiply(BigInteger.valueOf(16 + z));
        lightGap(xt.subtract(BigInteger.ONE), zt, height);
        lightGap(xt.add(BigInteger.ONE), zt, height);
        lightGap(xt, zt.subtract(BigInteger.ONE), height);
        lightGap(xt, zt.add(BigInteger.ONE), height);
    }

    private void lightGap(BigInteger x, BigInteger z, int k) {
        int height = this.level.getHeightmap(x, z);
        if (height > k) {
            this.level.updateLight(LightLayer.SKY, x, k, z, x, height, z);
            this.unsaved = true;
        } else if (height < k) {
            this.level.updateLight(LightLayer.SKY, x, height, z, x, k, z);
            this.unsaved = true;
        }
    }

    @Override
    public boolean setTileAndData(int x, int y, int z, int id, int meta) {
        byte var6 = (byte)id;
        int var7 = this.heightMap[z << 4 | x] & 255;
        int var8 = this.blocks[x << 11 | z << 7 | y] & 255;
        if (var8 == id && this.data.get(x, y, z) == meta) {
            return false;
        } else {
            BigInteger var9 = this.bigX.multiply(BigInteger.valueOf(16 + x));
            BigInteger var10 = this.bigZ.multiply(BigInteger.valueOf(16 + z));
            this.blocks[x << 11 | z << 7 | y] = (byte)(var6 & 255);
            if (var8 != 0 && !this.level.isClientSide) {
                Tile.tiles[var8].onRemove(this.level, var9, y, var10);
            }

            this.data.set(x, y, z, meta);
            if (!this.level.dimension.hasCeiling) {
                if (Tile.lightBlock[var6 & 255] != 0) {
                    if (y >= var7) {
                        this.recalcHeight(x, y + 1, z);
                    }
                } else if (y == var7 - 1) {
                    this.recalcHeight(x, y, z);
                }

                this.level.updateLight(LightLayer.SKY, var9, y, var10, var9, y, var10);
            }

            this.level.updateLight(LightLayer.BLOCK, var9, y, var10, var9, y, var10);
            this.lightGaps(x, z);
            this.data.set(x, y, z, meta);
            if (id != 0) {
                Tile.tiles[id].onPlace(this.level, var9, y, var10);
            }

            this.unsaved = true;
            return true;
        }
    }

    @Override
    public boolean setTile(int x, int y, int z, int id) {
        byte var5 = (byte)id;
        int var6 = this.heightMap[z << 4 | x] & 255;
        int var7 = this.blocks[x << 11 | z << 7 | y] & 255;
        if (var7 == id) {
            return false;
        } else {
            BigInteger xt = this.bigX.multiply(BigInteger.valueOf(16 + x));
            BigInteger var9 = this.bigZ.multiply(BigInteger.valueOf(16 + z));
            this.blocks[x << 11 | z << 7 | y] = (byte)(var5 & 255);
            if (var7 != 0) {
                Tile.tiles[var7].onRemove(this.level, xt, y, var9);
            }

            this.data.set(x, y, z, 0);
            if (Tile.lightBlock[var5 & 255] != 0) {
                if (y >= var6) {
                    this.recalcHeight(x, y + 1, z);
                }
            } else if (y == var6 - 1) {
                this.recalcHeight(x, y, z);
            }

            this.level.updateLight(LightLayer.SKY, xt, y, var9, xt, y, var9);
            this.level.updateLight(LightLayer.BLOCK, xt, y, var9, xt, y, var9);
            this.lightGaps(x, z);
            if (id != 0 && !this.level.isClientSide) {
                Tile.tiles[id].onPlace(this.level, xt, y, var9);
            }

            this.unsaved = true;
            return true;
        }
    }

    private void recalcHeight(int x, int y, int z) {
        int var4 = this.heightMap[z << 4 | x] & 255;
        int var5 = var4;
        if (y > var4) {
            var5 = y;
        }

        int xz = x << 11 | z << 7;

        while(var5 > 0 && Tile.lightBlock[this.blocks[xz + var5 - 1] & 255] == 0) {
            --var5;
        }

        if (var5 != var4) {
            this.level.lightColumnChanged(x, z, var5, var4);
            this.heightMap[z << 4 | x] = (byte)var5;
            if (var5 < this.minHeight) {
                this.minHeight = var5;
            } else {
                int var7 = 127;

                for(int var8 = 0; var8 < 16; ++var8) {
                    for(int var9 = 0; var9 < 16; ++var9) {
                        if ((this.heightMap[var9 << 4 | var8] & 255) < var7) {
                            var7 = this.heightMap[var9 << 4 | var8] & 255;
                        }
                    }
                }

                this.minHeight = var7;
            }

            BigInteger var12 = this.bigX.multiply(BigInteger.valueOf(16 + x));
            BigInteger var13 = this.bigZ.multiply(BigInteger.valueOf(16 + z));
            if (var5 < var4) {
                for(int var14 = var5; var14 < var4; ++var14) {
                    this.skyLight.set(x, var14, z, 15);
                }
            } else {
                this.level.updateLight(LightLayer.SKY, var12, var4, var13, var12, var5, var13);

                for(int var15 = var4; var15 < var5; ++var15) {
                    this.skyLight.set(x, var15, z, 0);
                }
            }

            int var16 = 15;

            int var10;
            for(var10 = var5; var5 > 0 && var16 > 0; this.skyLight.set(x, var5, z, var16)) {
                int var11 = Tile.lightBlock[this.getTile(x, --var5, z)];
                if (var11 == 0) {
                    var11 = 1;
                }

                var16 -= var11;
                if (var16 < 0) {
                    var16 = 0;
                }
            }

            while(var5 > 0 && Tile.lightBlock[this.getTile(x, var5 - 1, z)] == 0) {
                --var5;
            }

            if (var5 != var10) {
                this.level.updateLight(LightLayer.SKY, var12.subtract(BigInteger.ONE), var5, var13.subtract(BigInteger.ONE), var12.add(BigInteger.ONE), var10, var13.add(BigInteger.ONE));
            }

            this.unsaved = true;
        }
    }

    @Override
    public void addEntity(Entity entity) {
        this.lastSaveHadEntities = true;
        BigInteger xt;
        BigInteger zt;
        if (entity instanceof Player && entity instanceof BigEntityExtension bigEntity) {
            xt = BigMath.floor(bigEntity.getX().divide(BigConstants.SIXTEEN_F, RoundingMode.HALF_UP));
            zt = BigMath.floor(bigEntity.getZ().divide(BigConstants.SIXTEEN_F, RoundingMode.HALF_UP));
        } else {
            xt = BigMath.floor(entity.x / 16.0);
            zt = BigMath.floor(entity.z / 16.0);
        }
        if (!xt.equals(this.bigX) || !zt.equals(this.bigZ)) {
            System.out.println("Wrong location! " + entity);
            Thread.dumpStack();
        }

        int yt = Mth.floor(entity.y / 16.0);
        if (yt < 0) {
            yt = 0;
        }

        if (yt >= this.entityBlocks.length) {
            yt = this.entityBlocks.length - 1;
        }

        entity.inChunk = true;
        entity.setXChunk(this.bigX);
        entity.yChunk = yt;
        entity.setZChunk(this.bigZ);
        this.entityBlocks[yt].add(entity);
    }
}
