package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemInstance;
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
public class LeafTileMixin extends TransparentTile implements BigTileExtension {
    private static int REQUIRED_WOOD_RANGE = 4;
    @Shadow
    private int[] checkBuffer;

    protected LeafTileMixin(int id, int tex, Material material, boolean allowSame) {
        super(id, tex, material, allowSame);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public int getFoliageColor(LevelSource level, BigInteger x, int y, BigInteger z) {
        int data = level.getData(x, y, z);
        if ((data & 1) == 1) {
            return FoliageColor.getEvergreenColor();
        } else if ((data & 2) == 2) {
            return FoliageColor.getBirchColor();
        } else {
            //? >=1.0.0-beta.8.0.r {
            /*double temperature = level.getBiomeSource().getTemperature(x, z);
            double downfall = level.getBiomeSource().getDownfall(x, z);
            *///? } else {
            level.getBiomeSource().getBiomeBlock(x, z, 1, 1);
            double temperature = level.getBiomeSource().temperatures[0];
            double downfall = level.getBiomeSource().downfalls[0];
            //? }
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
                        if (tile == Tile.leaves.id) {
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
            //? >= 1.0.0-beta.8.0.r {
            /*if ((data & 8) != 0 && (data & 4) == 0) {
            *///? } else {
            if ((data & 8) != 0) {
            //? }
                int r = REQUIRED_WOOD_RANGE;
                final int r2 = r + 1;
                BigInteger r2B = BigInteger.valueOf(r2);
                byte W = 32;
                int WW = W * W;
                int WO = W / 2;
                if (this.checkBuffer == null) {
                    this.checkBuffer = new int[W * W * W];
                }

                if (level.hasChunksAt(x.subtract(r2B), y - r2, z.subtract(r2B), x.add(r2B), y + r2, z.add(r2B))) {
                    for (int xo = -r; xo <= r; xo++) {
                        BigInteger xoB = BigInteger.valueOf(xo);
                        for (int yo = -r; yo <= r; yo++) {
                            for (int zo = -r; zo <= r; zo++) {
                                int tile = level.getTile(x.add(xoB), y + yo, z.add(BigInteger.valueOf(zo)));
                                if (tile == Tile.treeTrunk.id) {
                                    this.checkBuffer[(xo + WO) * WW + (yo + WO) * W + zo + WO] = 0;
                                } else if (tile == Tile.leaves.id) {
                                    this.checkBuffer[(xo + WO) * WW + (yo + WO) * W + zo + WO] = -2;
                                } else {
                                    this.checkBuffer[(xo + WO) * WW + (yo + WO) * W + zo + WO] = -1;
                                }
                            }
                        }
                    }

                    for (int i = 1; i <= REQUIRED_WOOD_RANGE; i++) {
                        for (int xo = -r; xo <= r; xo++)
                        for (int yo = -r; yo <= r; yo++)
                        for (int zo = -r; zo <= r; zo++) {
                            if (this.checkBuffer[(xo + WO) * WW + (yo + WO) * W + zo + WO] == i - 1) {
                                if (this.checkBuffer[(xo + WO - 1) * WW + (yo + WO) * W + zo + WO] == -2) {
                                    this.checkBuffer[(xo + WO - 1) * WW + (yo + WO) * W + zo + WO] = i;
                                }
                                if (this.checkBuffer[(xo + WO + 1) * WW + (yo + WO) * W + zo + WO] == -2) {
                                    this.checkBuffer[(xo + WO + 1) * WW + (yo + WO) * W + zo + WO] = i;
                                }
                                if (this.checkBuffer[(xo + WO) * WW + (yo + WO - 1) * W + zo + WO] == -2) {
                                    this.checkBuffer[(xo + WO) * WW + (yo + WO - 1) * W + zo + WO] = i;
                                }
                                if (this.checkBuffer[(xo + WO) * WW + (yo + WO + 1) * W + zo + WO] == -2) {
                                    this.checkBuffer[(xo + WO) * WW + (yo + WO + 1) * W + zo + WO] = i;
                                }
                                if (this.checkBuffer[(xo + WO) * WW + (yo + WO) * W + (zo + WO - 1)] == -2) {
                                    this.checkBuffer[(xo + WO) * WW + (yo + WO) * W + (zo + WO - 1)] = i;
                                }
                                if (this.checkBuffer[(xo + WO) * WW + (yo + WO) * W + zo + WO + 1] == -2) {
                                    this.checkBuffer[(xo + WO) * WW + (yo + WO) * W + zo + WO + 1] = i;
                                }
                            }
                        }
                    }
                }

                int mid = this.checkBuffer[WO * WW + WO * W + WO];
                if (mid >= 0) {
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
        if (!level.isClientSide && player.getSelectedItem() != null && player.getSelectedItem().id == Item.shears.id) {
            player.awardStat(Stats.blockMined[this.id], 1);
            this.popResource(level, x, y, z, new ItemInstance(Tile.leaves.id, 1, meta & 3));
        } else {
            super.playerDestroy(level, player, x, y, z, meta);
        }
    }

    @Override
    public void stepOn(Level level, BigInteger x, int y, BigInteger z, Entity entity) {
        super.stepOn(level, x, y, z, entity);
    }
}

