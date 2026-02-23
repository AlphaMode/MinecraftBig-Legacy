package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import me.alphamode.mcbig.extensions.tiles.BigRedStoneDustTileExtension;
import me.alphamode.mcbig.world.phys.BigAABB;
import me.alphamode.mcbig.world.phys.BigVec3i;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.RedStoneDustTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.*;

@Mixin(RedStoneDustTile.class)
public abstract class RedStoneDustTileMixin extends Tile implements BigTileExtension, BigRedStoneDustTileExtension {
    private Set<BigVec3i> toUpdateBig = new HashSet<>();

    @Shadow
    private boolean shouldSignal;

    protected RedStoneDustTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public AABB getAABB(Level level, BigInteger x, int y, BigInteger z) {
        return null;
    }

    @Override
    public BigAABB getBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        return null;
    }

    @Override
    public int getFoliageColor(LevelSource level, BigInteger x, int y, BigInteger z) {
        return 8388608;
    }

    @Override
    public boolean mayPlace(Level level, BigInteger x, int y, BigInteger z) {
        return level.isSolidBlockingTile(x, y - 1, z);
    }

    private void updatePowerStrength(Level level, BigInteger x, int y, BigInteger z) {
        this.updatePowerStrengthImpl(level, x, y, z, x, y, z);
        ArrayList<BigVec3i> updates = new ArrayList<>(this.toUpdateBig);
        this.toUpdateBig.clear();

        for (int i = 0; i < updates.size(); i++) {
            BigVec3i pos = updates.get(i);
            level.updateNeighborsAt(pos.x(), pos.y(), pos.z(), this.id);
        }
    }

    private void updatePowerStrengthImpl(Level level, BigInteger x0, int y0, BigInteger z0, BigInteger x1, int y1, BigInteger z1) {
        int data = level.getData(x0, y0, z0);
        int side = 0;
        this.shouldSignal = false;
        boolean var10 = level.hasNeighborSignal(x0, y0, z0);
        this.shouldSignal = true;
        if (var10) {
            side = 15;
        } else {
            for (int facing = 0; facing < 4; facing++) {
                BigInteger x0_ = x0;
                BigInteger z0_ = z0;
                if (facing == 0) {
                    x0_ = x0.subtract(BigInteger.ONE);
                }

                if (facing == 1) {
                    x0_ = x0_.add(BigInteger.ONE);
                }

                if (facing == 2) {
                    z0_ = z0.subtract(BigInteger.ONE);
                }

                if (facing == 3) {
                    z0_ = z0_.add(BigInteger.ONE);
                }

                if (!x0_.equals(x1) || y0 != y1 || !Objects.equals(z0_, z1)) {
                    side = this.checkTarget(level, x0_, y0, z0_, side);
                }

                if (level.isSolidBlockingTile(x0_, y0, z0_) && !level.isSolidBlockingTile(x0, y0 + 1, z0)) {
                    if (!x0_.equals(x1) || y0 + 1 != y1 || !z0_.equals(z1)) {
                        side = this.checkTarget(level, x0_, y0 + 1, z0_, side);
                    }
                } else if (!level.isSolidBlockingTile(x0_, y0, z0_) && (!x0_.equals(x1) || y0 - 1 != y1 || !z0_.equals(z1))) {
                    side = this.checkTarget(level, x0_, y0 - 1, z0_, side);
                }
            }

            if (side > 0) {
                side--;
            } else {
                side = 0;
            }
        }

        if (data != side) {
            level.noNeighborUpdate = true;
            level.setData(x0, y0, z0, side);
            level.setTilesDirty(x0, y0, z0, x0, y0, z0);
            level.noNeighborUpdate = false;

            for (int facing = 0; facing < 4; facing++) {
                BigInteger x0_ = x0;
                BigInteger z0_ = z0;
                int y0_ = y0 - 1;
                if (facing == 0) {
                    x0_ = x0.subtract(BigInteger.ONE);
                }

                if (facing == 1) {
                    x0_ = x0_.add(BigInteger.ONE);
                }

                if (facing == 2) {
                    z0_ = z0.subtract(BigInteger.ONE);
                }

                if (facing == 3) {
                    z0_ = z0_.add(BigInteger.ONE);
                }

                if (level.isSolidBlockingTile(x0_, y0, z0_)) {
                    y0_ += 2;
                }

                int direction = 0;
                direction = this.checkTarget(level, x0_, y0, z0_, -1);
                side = level.getData(x0, y0, z0);
                if (side > 0) {
                    side--;
                }

                if (direction >= 0 && direction != side) {
                    this.updatePowerStrengthImpl(level, x0_, y0, z0_, x0, y0, z0);
                }

                direction = this.checkTarget(level, x0_, y0_, z0_, -1);
                side = level.getData(x0, y0, z0);
                if (side > 0) {
                    side--;
                }

                if (direction >= 0 && direction != side) {
                    this.updatePowerStrengthImpl(level, x0_, y0_, z0_, x0, y0, z0);
                }
            }

            if (data == 0 || side == 0) {
                this.toUpdateBig.add(new BigVec3i(x0, y0, z0));
                this.toUpdateBig.add(new BigVec3i(x0.subtract(BigInteger.ONE), y0, z0));
                this.toUpdateBig.add(new BigVec3i(x0.add(BigInteger.ONE), y0, z0));
                this.toUpdateBig.add(new BigVec3i(x0, y0 - 1, z0));
                this.toUpdateBig.add(new BigVec3i(x0, y0 + 1, z0));
                this.toUpdateBig.add(new BigVec3i(x0, y0, z0.subtract(BigInteger.ONE)));
                this.toUpdateBig.add(new BigVec3i(x0, y0, z0.add(BigInteger.ONE)));
            }
        }
    }

    private void checkCornerChangeAt(Level level, BigInteger x, int y, BigInteger z) {
        if (level.getTile(x, y, z) == this.id) {
            level.updateNeighborsAt(x, y, z, this.id);
            level.updateNeighborsAt(x.subtract(BigInteger.ONE), y, z, this.id);
            level.updateNeighborsAt(x.add(BigInteger.ONE), y, z, this.id);
            level.updateNeighborsAt(x, y, z.subtract(BigInteger.ONE), this.id);
            level.updateNeighborsAt(x, y, z.add(BigInteger.ONE), this.id);
            level.updateNeighborsAt(x, y - 1, z, this.id);
            level.updateNeighborsAt(x, y + 1, z, this.id);
        }
    }

    @Override
    public void onPlace(Level level, BigInteger x, int y, BigInteger z) {
        super.onPlace(level, x, y, z);
        if (!level.isClientSide) {
            this.updatePowerStrength(level, x, y, z);
            level.updateNeighborsAt(x, y + 1, z, this.id);
            level.updateNeighborsAt(x, y - 1, z, this.id);
            this.checkCornerChangeAt(level, x.subtract(BigInteger.ONE), y, z);
            this.checkCornerChangeAt(level, x.add(BigInteger.ONE), y, z);
            this.checkCornerChangeAt(level, x, y, z.subtract(BigInteger.ONE));
            this.checkCornerChangeAt(level, x, y, z.add(BigInteger.ONE));
            if (level.isSolidBlockingTile(x.subtract(BigInteger.ONE), y, z)) {
                this.checkCornerChangeAt(level, x.subtract(BigInteger.ONE), y + 1, z);
            } else {
                this.checkCornerChangeAt(level, x.subtract(BigInteger.ONE), y - 1, z);
            }

            if (level.isSolidBlockingTile(x.add(BigInteger.ONE), y, z)) {
                this.checkCornerChangeAt(level, x.add(BigInteger.ONE), y + 1, z);
            } else {
                this.checkCornerChangeAt(level, x.add(BigInteger.ONE), y - 1, z);
            }

            if (level.isSolidBlockingTile(x, y, z.subtract(BigInteger.ONE))) {
                this.checkCornerChangeAt(level, x, y + 1, z.subtract(BigInteger.ONE));
            } else {
                this.checkCornerChangeAt(level, x, y - 1, z.subtract(BigInteger.ONE));
            }

            if (level.isSolidBlockingTile(x, y, z.add(BigInteger.ONE))) {
                this.checkCornerChangeAt(level, x, y + 1, z.add(BigInteger.ONE));
            } else {
                this.checkCornerChangeAt(level, x, y - 1, z.add(BigInteger.ONE));
            }
        }
    }

    @Override
    public void onRemove(Level level, BigInteger x, int y, BigInteger z) {
        super.onRemove(level, x, y, z);
        if (!level.isClientSide) {
            level.updateNeighborsAt(x, y + 1, z, this.id);
            level.updateNeighborsAt(x, y - 1, z, this.id);
            this.updatePowerStrength(level, x, y, z);
            this.checkCornerChangeAt(level, x.subtract(BigInteger.ONE), y, z);
            this.checkCornerChangeAt(level, x.add(BigInteger.ONE), y, z);
            this.checkCornerChangeAt(level, x, y, z.subtract(BigInteger.ONE));
            this.checkCornerChangeAt(level, x, y, z.add(BigInteger.ONE));
            if (level.isSolidBlockingTile(x.subtract(BigInteger.ONE), y, z)) {
                this.checkCornerChangeAt(level, x.subtract(BigInteger.ONE), y + 1, z);
            } else {
                this.checkCornerChangeAt(level, x.subtract(BigInteger.ONE), y - 1, z);
            }

            if (level.isSolidBlockingTile(x.add(BigInteger.ONE), y, z)) {
                this.checkCornerChangeAt(level, x.add(BigInteger.ONE), y + 1, z);
            } else {
                this.checkCornerChangeAt(level, x.add(BigInteger.ONE), y - 1, z);
            }

            if (level.isSolidBlockingTile(x, y, z.subtract(BigInteger.ONE))) {
                this.checkCornerChangeAt(level, x, y + 1, z.subtract(BigInteger.ONE));
            } else {
                this.checkCornerChangeAt(level, x, y - 1, z.subtract(BigInteger.ONE));
            }

            if (level.isSolidBlockingTile(x, y, z.add(BigInteger.ONE))) {
                this.checkCornerChangeAt(level, x, y + 1, z.add(BigInteger.ONE));
            } else {
                this.checkCornerChangeAt(level, x, y - 1, z.add(BigInteger.ONE));
            }
        }
    }

    private int checkTarget(Level level, BigInteger x, int y, BigInteger z, int power) {
        if (level.getTile(x, y, z) != this.id) {
            return power;
        } else {
            int data = level.getData(x, y, z);
            return data > power ? data : power;
        }
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int tile) {
        if (!level.isClientSide) {
            int var6 = level.getData(x, y, z);
            boolean var7 = this.mayPlace(level, x, y, z);
            if (!var7) {
                this.dropResources(level, x, y, z, var6);
                level.setTile(x, y, z, 0);
            } else {
                this.updatePowerStrength(level, x, y, z);
            }

            super.neighborChanged(level, x, y, z, tile);
        }
    }

    @Override
    public boolean getDirectSignal(Level level, BigInteger x, int y, BigInteger z, int direction) {
        return !this.shouldSignal ? false : this.getSignal(level, x, y, z, direction);
    }

    @Override
    public boolean getSignal(LevelSource levelReader, BigInteger x, int y, BigInteger z, int direction) {
        if (!this.shouldSignal) {
            return false;
        } else if (levelReader.getData(x, y, z) == 0) {
            return false;
        } else if (direction == 1) {
            return true;
        } else {
            boolean var6 = BigRedStoneDustTileExtension.isPowerSourceAt(levelReader, x.subtract(BigInteger.ONE), y, z, 1)
                    || !levelReader.isSolidBlockingTile(x.subtract(BigInteger.ONE), y, z) && BigRedStoneDustTileExtension.isPowerSourceAt(levelReader, x.subtract(BigInteger.ONE), y - 1, z, -1);
            boolean var7 = BigRedStoneDustTileExtension.isPowerSourceAt(levelReader, x.add(BigInteger.ONE), y, z, 3)
                    || !levelReader.isSolidBlockingTile(x.add(BigInteger.ONE), y, z) && BigRedStoneDustTileExtension.isPowerSourceAt(levelReader, x.add(BigInteger.ONE), y - 1, z, -1);
            boolean var8 = BigRedStoneDustTileExtension.isPowerSourceAt(levelReader, x, y, z.subtract(BigInteger.ONE), 2)
                    || !levelReader.isSolidBlockingTile(x, y, z.subtract(BigInteger.ONE)) && BigRedStoneDustTileExtension.isPowerSourceAt(levelReader, x, y - 1, z.subtract(BigInteger.ONE), -1);
            boolean var9 = BigRedStoneDustTileExtension.isPowerSourceAt(levelReader, x, y, z.add(BigInteger.ONE), 0)
                    || !levelReader.isSolidBlockingTile(x, y, z.add(BigInteger.ONE)) && BigRedStoneDustTileExtension.isPowerSourceAt(levelReader, x, y - 1, z.add(BigInteger.ONE), -1);
            if (!levelReader.isSolidBlockingTile(x, y + 1, z)) {
                if (levelReader.isSolidBlockingTile(x.subtract(BigInteger.ONE), y, z) && BigRedStoneDustTileExtension.isPowerSourceAt(levelReader, x.subtract(BigInteger.ONE), y + 1, z, -1)) {
                    var6 = true;
                }

                if (levelReader.isSolidBlockingTile(x.add(BigInteger.ONE), y, z) && BigRedStoneDustTileExtension.isPowerSourceAt(levelReader, x.add(BigInteger.ONE), y + 1, z, -1)) {
                    var7 = true;
                }

                if (levelReader.isSolidBlockingTile(x, y, z.subtract(BigInteger.ONE)) && BigRedStoneDustTileExtension.isPowerSourceAt(levelReader, x, y + 1, z.subtract(BigInteger.ONE), -1)) {
                    var8 = true;
                }

                if (levelReader.isSolidBlockingTile(x, y, z.add(BigInteger.ONE)) && BigRedStoneDustTileExtension.isPowerSourceAt(levelReader, x, y + 1, z.add(BigInteger.ONE), -1)) {
                    var9 = true;
                }
            }

            if (!var8 && !var7 && !var6 && !var9 && direction >= 2 && direction <= 5) {
                return true;
            } else if (direction == 2 && var8 && !var6 && !var7) {
                return true;
            } else if (direction == 3 && var9 && !var6 && !var7) {
                return true;
            } else {
                return direction == 4 && var6 && !var8 && !var9 ? true : direction == 5 && var7 && !var8 && !var9;
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void animateTick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        int var6 = level.getData(x, y, z);
        if (var6 > 0) {
            double var7 = x.doubleValue() + 0.5 + (random.nextFloat() - 0.5) * 0.2;
            double var9 = y + 0.0625F;
            double var11 = z.doubleValue() + 0.5 + (random.nextFloat() - 0.5) * 0.2;
            float var13 = var6 / 15.0F;
            float var14 = var13 * 0.6F + 0.4F;
            if (var6 == 0) {
                var14 = 0.0F;
            }

            float var15 = var13 * var13 * 0.7F - 0.5F;
            float var16 = var13 * var13 * 0.6F - 0.7F;
            if (var15 < 0.0F) {
                var15 = 0.0F;
            }

            if (var16 < 0.0F) {
                var16 = 0.0F;
            }

            level.addParticle("reddust", var7, var9, var11, var14, var15, var16);
        }
    }
}
