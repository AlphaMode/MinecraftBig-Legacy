package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigLiquidTileExtension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.LiquidTileDynamic;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(LiquidTileDynamic.class)
public abstract class LiquidTileDynamicMixin extends Tile implements BigLiquidTileExtension {
    @Shadow private int maxCount;

    @Shadow private boolean[] result;

    @Shadow private int[] dist;

    protected LiquidTileDynamicMixin(int id, Material material) {
        super(id, material);
    }

    private void setStatic(Level level, BigInteger x, int y, BigInteger z) {
        int data = level.getData(x, y, z);
        level.setTileAndDataNoUpdate(x, y, z, this.id + 1, data);
        level.setTilesDirty(x, y, z, x, y, z);
        level.sendTileUpdated(x, y, z);
    }

    @Override
    public void tick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        int depth = getDepth(level, x, y, z);
        byte var7 = 1;
        if (this.material == Material.LAVA && !level.dimension.ultraWarm) {
            var7 = 2;
        }

        boolean var8 = true;
        if (depth > 0) {
            int var9 = -100;
            this.maxCount = 0;
            var9 = getHighest(level, x.subtract(BigInteger.ONE), y, z, var9);
            var9 = getHighest(level, x.add(BigInteger.ONE), y, z, var9);
            var9 = getHighest(level, x, y, z.subtract(BigInteger.ONE), var9);
            var9 = getHighest(level, x, y, z.add(BigInteger.ONE), var9);
            int var10 = var9 + var7;
            if (var10 >= 8 || var9 < 0) {
                var10 = -1;
            }

            if (getDepth(level, x, y + 1, z) >= 0) {
                int var11 = getDepth(level, x, y + 1, z);
                if (var11 >= 8) {
                    var10 = var11;
                } else {
                    var10 = var11 + 8;
                }
            }

            if (this.maxCount >= 2 && this.material == Material.WATER) {
                if (level.getMaterial(x, y - 1, z).isSolid()) {
                    var10 = 0;
                } else if (level.getMaterial(x, y - 1, z) == this.material && level.getData(x, y, z) == 0) {
                    var10 = 0;
                }
            }

            if (this.material == Material.LAVA && depth < 8 && var10 < 8 && var10 > depth && random.nextInt(4) != 0) {
                var10 = depth;
                var8 = false;
            }

            if (var10 != depth) {
                depth = var10;
                if (var10 < 0) {
                    level.setTile(x, y, z, 0);
                } else {
                    level.setData(x, y, z, var10);
                    level.addToTickNextTick(x, y, z, this.id, getTickDelay());
                    level.updateNeighborsAt(x, y, z, this.id);
                }
            } else if (var8) {
                setStatic(level, x, y, z);
            }
        } else {
            setStatic(level, x, y, z);
        }

        if (canSpreadTo(level, x, y - 1, z)) {
            if (depth >= 8) {
                level.setTileAndData(x, y - 1, z, this.id, depth);
            } else {
                level.setTileAndData(x, y - 1, z, this.id, depth + 8);
            }
        } else if (depth >= 0 && (depth == 0 || isWaterBlocking(level, x, y - 1, z))) {
            boolean[] var16 = getSpread(level, x, y, z);
            int var17 = depth + var7;
            if (depth >= 8) {
                var17 = 1;
            }

            if (var17 >= 8) {
                return;
            }

            if (var16[0]) {
                trySpreadTo(level, x.subtract(BigInteger.ONE), y, z, var17);
            }

            if (var16[1]) {
                trySpreadTo(level, x.add(BigInteger.ONE), y, z, var17);
            }

            if (var16[2]) {
                trySpreadTo(level, x, y, z.subtract(BigInteger.ONE), var17);
            }

            if (var16[3]) {
                trySpreadTo(level, x, y, z.add(BigInteger.ONE), var17);
            }
        }
    }

    private void trySpreadTo(Level level, BigInteger x, int y, BigInteger z, int data) {
        if (canSpreadTo(level, x, y, z)) {
            int var6 = level.getTile(x, y, z);
            if (var6 > 0) {
                if (this.material == Material.LAVA) {
                    fizz(level, x, y, z);
                } else {
                    Tile.tiles[var6].dropResources(level, x, y, z, level.getData(x, y, z));
                }
            }

            level.setTileAndData(x, y, z, this.id, data);
        }
    }

    private int getSlopeDistance(Level level, BigInteger x, int y, BigInteger z, int l, int m) {
        int var7 = 1000;

        for(int var8 = 0; var8 < 4; ++var8) {
            if ((var8 != 0 || m != 1) && (var8 != 1 || m != 0) && (var8 != 2 || m != 3) && (var8 != 3 || m != 2)) {
                BigInteger var9 = x;
                BigInteger var11 = z;
                if (var8 == 0) {
                    var9 = x.subtract(BigInteger.ONE);
                }

                if (var8 == 1) {
                    var9 = var9.add(BigInteger.ONE);
                }

                if (var8 == 2) {
                    var11 = z.subtract(BigInteger.ONE);
                }

                if (var8 == 3) {
                    var11 = var11.add(BigInteger.ONE);
                }

                if (!this.isWaterBlocking(level, var9, y, var11) && (level.getMaterial(var9, y, var11) != this.material || level.getData(var9, y, var11) != 0)) {
                    if (!this.isWaterBlocking(level, var9, y - 1, var11)) {
                        return l;
                    }

                    if (l < 4) {
                        int var12 = this.getSlopeDistance(level, var9, y, var11, l + 1, var8);
                        if (var12 < var7) {
                            var7 = var12;
                        }
                    }
                }
            }
        }

        return var7;
    }

    private boolean[] getSpread(Level level, BigInteger x, int y, BigInteger z) {
        for(int var5 = 0; var5 < 4; ++var5) {
            this.dist[var5] = 1000;
            BigInteger var6 = x;
            BigInteger var8 = z;
            if (var5 == 0) {
                var6 = x.subtract(BigInteger.ONE);
            }

            if (var5 == 1) {
                var6 = var6.add(BigInteger.ONE);
            }

            if (var5 == 2) {
                var8 = z.subtract(BigInteger.ONE);
            }

            if (var5 == 3) {
                var8 = var8.add(BigInteger.ONE);
            }

            if (!this.isWaterBlocking(level, var6, y, var8) && (level.getMaterial(var6, y, var8) != this.material || level.getData(var6, y, var8) != 0)) {
                if (!this.isWaterBlocking(level, var6, y - 1, var8)) {
                    this.dist[var5] = 0;
                } else {
                    this.dist[var5] = this.getSlopeDistance(level, var6, y, var8, 1, var5);
                }
            }
        }

        int var9 = this.dist[0];

        for(int var10 = 1; var10 < 4; ++var10) {
            if (this.dist[var10] < var9) {
                var9 = this.dist[var10];
            }
        }

        for(int var11 = 0; var11 < 4; ++var11) {
            this.result[var11] = this.dist[var11] == var9;
        }

        return this.result;
    }

    private boolean isWaterBlocking(Level level, BigInteger x, int y, BigInteger z) {
        int var5 = level.getTile(x, y, z);
        if (var5 == Tile.DOOR.id || var5 == Tile.IRON_DOOR.id || var5 == Tile.SIGN.id || var5 == Tile.LADDER.id || var5 == Tile.REEDS.id) {
            return true;
        } else if (var5 == 0) {
            return false;
        } else {
            Material var6 = Tile.tiles[var5].material;
            return var6.blocksMotion();
        }
    }

    protected int getHighest(Level level, BigInteger x, int y, BigInteger z, int l) {
        int depth = this.getDepth(level, x, y, z);
        if (depth < 0) {
            return l;
        } else {
            if (depth == 0) {
                ++this.maxCount;
            }

            if (depth >= 8) {
                depth = 0;
            }

            return l >= 0 && depth >= l ? l : depth;
        }
    }

    private boolean canSpreadTo(Level level, BigInteger x, int y, BigInteger z) {
        Material var5 = level.getMaterial(x, y, z);
        if (var5 == this.material) {
            return false;
        } else if (var5 == Material.LAVA) {
            return false;
        } else {
            return !this.isWaterBlocking(level, x, y, z);
        }
    }

    @Override
    public void onPlace(Level level, int x, int y, int z) {
        super.onPlace(level, x, y, z);
        if (level.getTile(x, y, z) == this.id) {
            level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
        }
    }
}
