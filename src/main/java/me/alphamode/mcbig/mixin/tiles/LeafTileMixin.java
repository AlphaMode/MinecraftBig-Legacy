package me.alphamode.mcbig.mixin.tiles;

import net.minecraft.stats.Stats;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.LeafTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.TransparentTile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(LeafTile.class)
public class LeafTileMixin extends TransparentTile {
    @Shadow
    private int[] checkBuffer;

    protected LeafTileMixin(int id, int tex, Material material, boolean allowSame) {
        super(id, tex, material, allowSame);
    }

    @Override
    public int getFoliageColor(LevelSource level, BigInteger x, int y, BigInteger z) {
        int data = level.getData(x, y, z);
        if ((data & 1) == 1) {
            return FoliageColor.getEvergreenColor();
        } else if ((data & 2) == 2) {
            return FoliageColor.getBirchColor();
        } else {
            level.getBiomeSource().getBiomeBlock(x, z, 1, 1);
            double temperature = level.getBiomeSource().temperatures[0];
            double downfall = level.getBiomeSource().downfalls[0];
            return FoliageColor.get(temperature, downfall);
        }
    }

    @Override
    public void onRemove(Level level, BigInteger x, int y, BigInteger z) {
        int r = 1;
        int range = r + 1;
        BigInteger bigRange = BigInteger.valueOf(range);
        if (level.hasChunksAt(x.subtract(bigRange), y - range, z.subtract(bigRange), x.add(bigRange), y + range, z.add(bigRange))) {
            for (int xOff = -r; xOff <= r; xOff++) {
                BigInteger bigXOff = BigInteger.valueOf(xOff);
                for (int zOff = -r; zOff <= r; zOff++) {
                    BigInteger bigZOff = BigInteger.valueOf(zOff);
                    for (int yOff = -r; yOff <= r; yOff++) {
                        BigInteger bigX = x.add(bigXOff);
                        BigInteger bigZ = z.add(bigZOff);
                        int tile = level.getTile(bigX, y + yOff, bigZ);
                        if (tile == Tile.LEAVES.id) {
                            int data = level.getData(bigX, y + yOff, bigZ);
                            level.setDataNoUpdate(bigX, y + yOff, bigZ, data | 8);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void tick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        if (!level.isClientSide) {
            int data = level.getData(x, y, z);
            if ((data & 8) != 0) {
                int r = 4;
                final int range = r + 1;
                BigInteger bigRange = BigInteger.valueOf(range);
                byte radius = 32;
                int var10 = radius * radius;
                int var11 = radius / 2;
                if (this.checkBuffer == null) {
                    this.checkBuffer = new int[radius * radius * radius];
                }

                if (level.hasChunksAt(x.subtract(bigRange), y - range, z.subtract(bigRange), x.add(bigRange), y + range, z.add(bigRange))) {
                    for (int xOff = -r; xOff <= r; xOff++) {
                        BigInteger bigXOff = BigInteger.valueOf(xOff);
                        for (int zOff = -r; zOff <= r; zOff++) {
                            BigInteger bigZOff = BigInteger.valueOf(zOff);
                            for (int yOff = -r; yOff <= r; yOff++) {
                                int tile = level.getTile(x.add(bigXOff), y + yOff, z.add(bigZOff));
                                if (tile == Tile.LOG.id) {
                                    this.checkBuffer[(xOff + var11) * var10 + (yOff + var11) * radius + zOff + var11] = 0;
                                } else if (tile == Tile.LEAVES.id) {
                                    this.checkBuffer[(xOff + var11) * var10 + (yOff + var11) * radius + zOff + var11] = -2;
                                } else {
                                    this.checkBuffer[(xOff + var11) * var10 + (yOff + var11) * radius + zOff + var11] = -1;
                                }
                            }
                        }
                    }

                    for (int var16 = 1; var16 <= 4; var16++) {
                        for (int var18 = -r; var18 <= r; var18++) {
                            for (int var19 = -r; var19 <= r; var19++) {
                                for (int var20 = -r; var20 <= r; var20++) {
                                    if (this.checkBuffer[(var18 + var11) * var10 + (var19 + var11) * radius + var20 + var11] == var16 - 1) {
                                        if (this.checkBuffer[(var18 + var11 - 1) * var10 + (var19 + var11) * radius + var20 + var11] == -2) {
                                            this.checkBuffer[(var18 + var11 - 1) * var10 + (var19 + var11) * radius + var20 + var11] = var16;
                                        }

                                        if (this.checkBuffer[(var18 + var11 + 1) * var10 + (var19 + var11) * radius + var20 + var11] == -2) {
                                            this.checkBuffer[(var18 + var11 + 1) * var10 + (var19 + var11) * radius + var20 + var11] = var16;
                                        }

                                        if (this.checkBuffer[(var18 + var11) * var10 + (var19 + var11 - 1) * radius + var20 + var11] == -2) {
                                            this.checkBuffer[(var18 + var11) * var10 + (var19 + var11 - 1) * radius + var20 + var11] = var16;
                                        }

                                        if (this.checkBuffer[(var18 + var11) * var10 + (var19 + var11 + 1) * radius + var20 + var11] == -2) {
                                            this.checkBuffer[(var18 + var11) * var10 + (var19 + var11 + 1) * radius + var20 + var11] = var16;
                                        }

                                        if (this.checkBuffer[(var18 + var11) * var10 + (var19 + var11) * radius + (var20 + var11 - 1)] == -2) {
                                            this.checkBuffer[(var18 + var11) * var10 + (var19 + var11) * radius + (var20 + var11 - 1)] = var16;
                                        }

                                        if (this.checkBuffer[(var18 + var11) * var10 + (var19 + var11) * radius + var20 + var11 + 1] == -2) {
                                            this.checkBuffer[(var18 + var11) * var10 + (var19 + var11) * radius + var20 + var11 + 1] = var16;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                int var17 = this.checkBuffer[var11 * var10 + var11 * radius + var11];
                if (var17 >= 0) {
                    level.setDataNoUpdate(x, y, z, data & -9);
                } else {
                    this.die(level, x, y, z);
                }
            }
        }
    }

    private void die(Level level, BigInteger x, int y, BigInteger z) {
        this.dropResources(level, x, y, z, level.getData(x, y, z));
        level.setTile(x, y, z, 0);
    }

    @Override
    public void playerDestroy(Level level, Player player, BigInteger x, int y, BigInteger z, int meta) {
        if (!level.isClientSide && player.getSelectedItem() != null && player.getSelectedItem().id == Item.SHEARS.id) {
            player.awardStat(Stats.STAT_MINE_BLOCK[this.id], 1);
            this.popResource(level, x, y, z, new ItemInstance(Tile.LEAVES.id, 1, meta & 3));
        } else {
            super.playerDestroy(level, player, x, y, z, meta);
        }
    }

    @Override
    public void stepOn(Level level, BigInteger x, int y, BigInteger z, Entity entity) {
        super.stepOn(level, x, y, z, entity);
    }
}

