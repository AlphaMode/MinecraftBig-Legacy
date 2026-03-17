package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigLiquidTileExtension;
import me.alphamode.mcbig.extensions.BigTileExtension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.LiquidTileDynamic;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(LiquidTileDynamic.class)
public abstract class LiquidTileDynamicMixin extends Tile implements BigTileExtension, BigLiquidTileExtension {
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
        int dropOff = 1;
        if (this.material == Material.LAVA && !level.dimension.ultraWarm) {
            dropOff = 2;
        }

        boolean becomeStatic = true;
        if (depth > 0) {
            int highest = -100;
            this.maxCount = 0;
            highest = getHighest(level, x.subtract(BigInteger.ONE), y, z, highest);
            highest = getHighest(level, x.add(BigInteger.ONE), y, z, highest);
            highest = getHighest(level, x, y, z.subtract(BigInteger.ONE), highest);
            highest = getHighest(level, x, y, z.add(BigInteger.ONE), highest);
            int newDepth = highest + dropOff;
            if (newDepth >= 8 || highest < 0) {
                newDepth = -1;
            }

            if (getDepth(level, x, y + 1, z) >= 0) {
                int above = getDepth(level, x, y + 1, z);
                if (above >= 8) {
                    newDepth = above;
                } else {
                    newDepth = above + 8;
                }
            }

            if (this.maxCount >= 2 && this.material == Material.WATER) {
                if (level.getMaterial(x, y - 1, z).isSolid()) {
                    newDepth = 0;
                } else if (level.getMaterial(x, y - 1, z) == this.material && level.getData(x, y, z) == 0) {
                    newDepth = 0;
                }
            }

            if (this.material == Material.LAVA && depth < 8 && newDepth < 8 && newDepth > depth && random.nextInt(4) != 0) {
                newDepth = depth;
                becomeStatic = false;
            }

            if (newDepth != depth) {
                depth = newDepth;
                if (newDepth < 0) {
                    level.setTile(x, y, z, 0);
                } else {
                    level.setData(x, y, z, newDepth);
                    level.addToTickNextTick(x, y, z, this.id, getTickDelay());
                    level.updateNeighborsAt(x, y, z, this.id);
                }
            } else if (becomeStatic) {
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
            boolean[] spreads = getSpread(level, x, y, z);
            int neighbor = depth + dropOff;
            if (depth >= 8) {
                neighbor = 1;
            }

            if (neighbor >= 8) {
                return;
            }

            if (spreads[0]) {
                trySpreadTo(level, x.subtract(BigInteger.ONE), y, z, neighbor);
            }

            if (spreads[1]) {
                trySpreadTo(level, x.add(BigInteger.ONE), y, z, neighbor);
            }

            if (spreads[2]) {
                trySpreadTo(level, x, y, z.subtract(BigInteger.ONE), neighbor);
            }

            if (spreads[3]) {
                trySpreadTo(level, x, y, z.add(BigInteger.ONE), neighbor);
            }
        }
    }

    private void trySpreadTo(Level level, BigInteger x, int y, BigInteger z, int data) {
        if (canSpreadTo(level, x, y, z)) {
            int old = level.getTile(x, y, z);
            if (old > 0) {
                if (this.material == Material.LAVA) {
                    fizz(level, x, y, z);
                } else {
                    Tile.tiles[old].dropResources(level, x, y, z, level.getData(x, y, z));
                }
            }

            level.setTileAndData(x, y, z, this.id, data);
        }
    }

    private int getSlopeDistance(Level level, BigInteger x, int y, BigInteger z, int pass, int from) {
        int lowest = 1000;

        for(int d = 0; d < 4; ++d) {
            if ((d != 0 || from != 1) && (d != 1 || from != 0) && (d != 2 || from != 3) && (d != 3 || from != 2)) {
                BigInteger xx = x;
                BigInteger zz = z;
                if (d == 0) {
                    xx = x.subtract(BigInteger.ONE);
                }

                if (d == 1) {
                    xx = xx.add(BigInteger.ONE);
                }

                if (d == 2) {
                    zz = z.subtract(BigInteger.ONE);
                }

                if (d == 3) {
                    zz = zz.add(BigInteger.ONE);
                }

                if (!this.isWaterBlocking(level, xx, y, zz) && (level.getMaterial(xx, y, zz) != this.material || level.getData(xx, y, zz) != 0)) {
                    if (!this.isWaterBlocking(level, xx, y - 1, zz)) {
                        return pass;
                    }

                    if (pass < 4) {
                        int v = this.getSlopeDistance(level, xx, y, zz, pass + 1, d);
                        if (v < lowest) {
                            lowest = v;
                        }
                    }
                }
            }
        }

        return lowest;
    }

    private boolean[] getSpread(Level level, BigInteger x, int y, BigInteger z) {
        for(int d = 0; d < 4; ++d) {
            this.dist[d] = 1000;
            BigInteger xx = x;
            BigInteger zz = z;
            if (d == 0) {
                xx = x.subtract(BigInteger.ONE);
            }

            if (d == 1) {
                xx = xx.add(BigInteger.ONE);
            }

            if (d == 2) {
                zz = z.subtract(BigInteger.ONE);
            }

            if (d == 3) {
                zz = zz.add(BigInteger.ONE);
            }

            if (!this.isWaterBlocking(level, xx, y, zz) && (level.getMaterial(xx, y, zz) != this.material || level.getData(xx, y, zz) != 0)) {
                if (!this.isWaterBlocking(level, xx, y - 1, zz)) {
                    this.dist[d] = 0;
                } else {
                    this.dist[d] = this.getSlopeDistance(level, xx, y, zz, 1, d);
                }
            }
        }

        int lowest = this.dist[0];

        for(int d = 1; d < 4; ++d) {
            if (this.dist[d] < lowest) {
                lowest = this.dist[d];
            }
        }

        for(int d = 0; d < 4; ++d) {
            this.result[d] = this.dist[d] == lowest;
        }

        return this.result;
    }

    private boolean isWaterBlocking(Level level, BigInteger x, int y, BigInteger z) {
        int t = level.getTile(x, y, z);
        if (t == Tile.DOOR.id || t == Tile.IRON_DOOR.id || t == Tile.SIGN.id || t == Tile.LADDER.id || t == Tile.REEDS.id) {
            return true;
        } else if (t == 0) {
            return false;
        } else {
            Material m = Tile.tiles[t].material;
            return m.blocksMotion();
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
        Material target = level.getMaterial(x, y, z);
        if (target == this.material) {
            return false;
        } else if (target == Material.LAVA) {
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
