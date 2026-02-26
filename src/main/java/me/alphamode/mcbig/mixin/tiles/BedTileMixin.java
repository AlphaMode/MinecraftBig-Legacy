package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import me.alphamode.mcbig.extensions.tiles.BigBedTileExtension;
import net.minecraft.util.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.BedTile;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(BedTile.class)
public abstract class BedTileMixin extends Tile implements BigTileExtension {
    @Shadow
    public static boolean isHeadPiece(int meta) {
        return false;
    };

    @Shadow
    public static int getBedOrientation(int meta) {
        return 0;
    }

    @Shadow
    @Final
    public static int[][] HEAD_DIRECTION_OFFSETS;

    @Shadow
    public static boolean isBedOccupied(int meta) {
        return false;
    }

    @Shadow
    protected abstract void setDefaultShape();

    protected BedTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public boolean use(Level level, BigInteger x, int y, BigInteger z, Player player) {
        if (level.isClientSide) {
            return true;
        } else {
            int var6 = level.getData(x, y, z);
            if (!isHeadPiece(var6)) {
                int var7 = getBedOrientation(var6);
                x = x.add(BigInteger.valueOf(HEAD_DIRECTION_OFFSETS[var7][0]));
                z = z.add(BigInteger.valueOf(HEAD_DIRECTION_OFFSETS[var7][1]));
                if (level.getTile(x, y, z) != this.id) {
                    return true;
                }

                var6 = level.getData(x, y, z);
            }

            if (!level.dimension.mayRespawn()) {
                double var18 = x.doubleValue() + 0.5;
                double var20 = y + 0.5;
                double var11 = z.doubleValue() + 0.5;
                level.setTile(x, y, z, 0);
                int var13 = getBedOrientation(var6);
                x = x.add(BigInteger.valueOf(HEAD_DIRECTION_OFFSETS[var13][0]));
                z = z.add(BigInteger.valueOf(HEAD_DIRECTION_OFFSETS[var13][1]));
                if (level.getTile(x, y, z) == this.id) {
                    level.setTile(x, y, z, 0);
                    var18 = (var18 + x.doubleValue() + 0.5) / 2.0;
                    var20 = (var20 + y + 0.5) / 2.0;
                    var11 = (var11 + z.doubleValue() + 0.5) / 2.0;
                }

                level.explode(null, x.doubleValue() + 0.5F, y + 0.5F, z.doubleValue() + 0.5F, 5.0F, true);
                return true;
            } else {
                if (isBedOccupied(var6)) {
                    Player var16 = null;

                    for (Player p : level.players) {
                        if (p.isSleeping()) {
                            Vec3i pos = p.sleepingPos;
                            if (pos.x == x.intValue() && pos.y == y && pos.z == z.intValue()) {
                                var16 = p;
                            }
                        }
                    }

                    if (var16 != null) {
                        player.displayClientMessage("tile.bed.occupied");
                        return true;
                    }

                    BigBedTileExtension.setOccupied(level, x, y, z, false);
                }

                Player.BedSleepingProblem var17 = player.startSleepInBed(x.intValue(), y, z.intValue());
                if (var17 == Player.BedSleepingProblem.OK) {
                    BigBedTileExtension.setOccupied(level, x, y, z, true);
                    return true;
                } else {
                    if (var17 == Player.BedSleepingProblem.NOT_POSSIBLE_NOW) {
                        player.displayClientMessage("tile.bed.noSleep");
                    }

                    return true;
                }
            }
        }
    }

    @Override
    public void updateShape(LevelSource source, BigInteger x, int y, BigInteger z) {
        this.setDefaultShape();
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int tile) {
        int var6 = level.getData(x, y, z);
        int var7 = getBedOrientation(var6);
        if (isHeadPiece(var6)) {
            if (level.getTile(x.subtract(BigInteger.valueOf(HEAD_DIRECTION_OFFSETS[var7][0])), y, z.subtract(BigInteger.valueOf(HEAD_DIRECTION_OFFSETS[var7][1]))) != this.id) {
                level.setTile(x, y, z, 0);
            }
        } else if (level.getTile(x.add(BigInteger.valueOf(HEAD_DIRECTION_OFFSETS[var7][0])), y, z.add(BigInteger.valueOf(HEAD_DIRECTION_OFFSETS[var7][1]))) != this.id) {
            level.setTile(x, y, z, 0);
            if (!level.isClientSide) {
                this.dropResources(level, x, y, z, var6);
            }
        }
    }

    @Override
    public void dropResources(Level level, BigInteger x, int y, BigInteger z, int meta, float dropChance) {
        if (!isHeadPiece(meta)) {
            super.dropResources(level, x, y, z, meta, dropChance);
        }
    }
}
