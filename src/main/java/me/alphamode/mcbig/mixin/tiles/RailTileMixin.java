package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import me.alphamode.mcbig.extensions.tiles.BigRailTileExtension;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.RailTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(RailTile.class)
public abstract class RailTileMixin extends Tile implements BigTileExtension {
    @Shadow
    @Final
    private boolean isStraight;

    protected RailTileMixin(int id, Material material) {
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
    public HitResult clip(Level level, BigInteger x, int y, BigInteger z, Vec3 vec1, Vec3 vec2) {
        this.updateShape(level, x, y, z);
        return super.clip(level, x, y, z, vec1, vec2);
    }

    @Override
    public void updateShape(LevelSource source, BigInteger x, int y, BigInteger z) {
        int data = source.getData(x, y, z);
        if (data >= 2 && data <= 5) {
            this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
        } else {
            this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        }
    }

    @Override
    public boolean mayPlace(Level level, BigInteger x, int y, BigInteger z) {
        return level.isSolidBlockingTile(x, y - 1, z);
    }

    @Override
    public void onPlace(Level level, BigInteger x, int y, BigInteger z) {
        if (!level.isClientSide) {
            this.updateDir(level, x, y, z, true);
        }
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int tile) {
        if (!level.isClientSide) {
            int var6 = level.getData(x, y, z);
            int var7 = var6;
            if (this.isStraight) {
                var7 = var6 & 7;
            }

            boolean var8 = false;
            if (!level.isSolidBlockingTile(x, y - 1, z)) {
                var8 = true;
            }

            if (var7 == 2 && !level.isSolidBlockingTile(x.add(BigInteger.ONE), y, z)) {
                var8 = true;
            }

            if (var7 == 3 && !level.isSolidBlockingTile(x.subtract(BigInteger.ONE), y, z)) {
                var8 = true;
            }

            if (var7 == 4 && !level.isSolidBlockingTile(x, y, z.subtract(BigInteger.ONE))) {
                var8 = true;
            }

            if (var7 == 5 && !level.isSolidBlockingTile(x, y, z.add(BigInteger.ONE))) {
                var8 = true;
            }

            if (var8) {
                this.dropResources(level, x, y, z, level.getData(x, y, z));
                level.setTile(x, y, z, 0);
            } else if (this.id == Tile.POWERED_RAIL.id) {
                boolean var9 = level.hasNeighborSignal(x, y, z) || level.hasNeighborSignal(x, y + 1, z);
                var9 = var9 || this.findPoweredRailSignal(level, x, y, z, var6, true, 0) || this.findPoweredRailSignal(level, x, y, z, var6, false, 0);
                boolean var10 = false;
                if (var9 && (var6 & 8) == 0) {
                    level.setData(x, y, z, var7 | 8);
                    var10 = true;
                } else if (!var9 && (var6 & 8) != 0) {
                    level.setData(x, y, z, var7);
                    var10 = true;
                }

                if (var10) {
                    level.updateNeighborsAt(x, y - 1, z, this.id);
                    if (var7 == 2 || var7 == 3 || var7 == 4 || var7 == 5) {
                        level.updateNeighborsAt(x, y + 1, z, this.id);
                    }
                }
            } else if (tile > 0 && Tile.tiles[tile].isSignalSource() && !this.isStraight && new BigRailTileExtension.BigRailState(level, x, y, z).countPotentialConnections() == 3) {
                this.updateDir(level, x, y, z, false);
            }
        }
    }

    private void updateDir(Level level, BigInteger x, int y, BigInteger z, boolean forceUpdate) {
        if (!level.isClientSide) {
            new BigRailTileExtension.BigRailState(level, x, y, z).place(level.hasNeighborSignal(x, y, z), forceUpdate);
        }
    }

    /**
     * @param data the rail shape meta data
     */
    private boolean findPoweredRailSignal(Level level, BigInteger x, int y, BigInteger z, int data, boolean hasSignal, int power) {
        if (power >= 8) {
            return false;
        } else {
            int var8 = data & 7;
            boolean var9 = true;
            switch (var8) {
                case 0:
                    if (hasSignal) {
                        z = z.add(BigInteger.ONE);
                    } else {
                        z = z.subtract(BigInteger.ONE);
                    }
                    break;
                case 1:
                    if (hasSignal) {
                        x = x.subtract(BigInteger.ONE);
                    } else {
                        x = x.add(BigInteger.ONE);
                    }
                    break;
                case 2:
                    if (hasSignal) {
                        x = x.subtract(BigInteger.ONE);
                    } else {
                        x = x.add(BigInteger.ONE);
                        y++;
                        var9 = false;
                    }

                    var8 = 1;
                    break;
                case 3:
                    if (hasSignal) {
                        x = x.subtract(BigInteger.ONE);
                        y++;
                        var9 = false;
                    } else {
                        x = x.add(BigInteger.ONE);
                    }

                    var8 = 1;
                    break;
                case 4:
                    if (hasSignal) {
                        z = z.add(BigInteger.ONE);
                    } else {
                        z = z.subtract(BigInteger.ONE);
                        y++;
                        var9 = false;
                    }

                    var8 = 0;
                    break;
                case 5:
                    if (hasSignal) {
                        z = z.add(BigInteger.ONE);
                        y++;
                        var9 = false;
                    } else {
                        z = z.subtract(BigInteger.ONE);
                    }

                    var8 = 0;
            }

            return this.isSameRailWithPower(level, x, y, z, hasSignal, power, var8)
                    ? true
                    : var9 && this.isSameRailWithPower(level, x, y - 1, z, hasSignal, power, var8);
        }
    }

    private boolean isSameRailWithPower(Level level, BigInteger x, int y, BigInteger z, boolean hasSignal, int power, int railShape) {
        int t = level.getTile(x, y, z);
        if (t == Tile.POWERED_RAIL.id) {
            int d = level.getData(x, y, z);
            int var10 = d & 7;
            if (railShape == 1 && (var10 == 0 || var10 == 4 || var10 == 5)) {
                return false;
            }

            if (railShape == 0 && (var10 == 1 || var10 == 2 || var10 == 3)) {
                return false;
            }

            if ((d & 8) != 0) {
                if (!level.hasNeighborSignal(x, y, z) && !level.hasNeighborSignal(x, y + 1, z)) {
                    return this.findPoweredRailSignal(level, x, y, z, d, hasSignal, power + 1);
                }

                return true;
            }
        }

        return false;
    }
}
