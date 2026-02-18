package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.tiles.BigFireTileExtension;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.FireTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(FireTile.class)
public abstract class FireTileMixin extends Tile implements BigFireTileExtension {
    @Shadow
    private int[] burnOdds;

    @Shadow
    private int[] flameOdds;

    protected FireTileMixin(int id, Material material) {
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
    public void tick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        boolean var6 = level.getTile(x, y - 1, z) == Tile.NETHERRACK.id;
        if (!this.mayPlace(level, x, y, z)) {
            level.setTile(x, y, z, 0);
        }

        if (var6
                || !level.isRaining()
                || !level.isRainingAt(x, y, z)
                && !level.isRainingAt(x.subtract(BigInteger.ONE), y, z)
                && !level.isRainingAt(x.add(BigInteger.ONE), y, z)
                && !level.isRainingAt(x, y, z.subtract(BigInteger.ONE))
                && !level.isRainingAt(x, y, z.add(BigInteger.ONE))) {
            int data = level.getData(x, y, z);
            if (data < 15) {
                level.setDataNoUpdate(x, y, z, data + random.nextInt(3) / 2);
            }

            level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
            if (!var6 && !this.isValidFireLocation(level, x, y, z)) {
                if (!level.isSolidBlockingTile(x, y - 1, z) || data > 3) {
                    level.setTile(x, y, z, 0);
                }
            } else if (!var6 && !this.canBurn(level, x, y - 1, z) && data == 15 && random.nextInt(4) == 0) {
                level.setTile(x, y, z, 0);
            } else {
                this.checkBurn(level, x.add(BigInteger.ONE), y, z, 300, random, data);
                this.checkBurn(level, x.subtract(BigInteger.ONE), y, z, 300, random, data);
                this.checkBurn(level, x, y - 1, z, 250, random, data);
                this.checkBurn(level, x, y + 1, z, 250, random, data);
                this.checkBurn(level, x, y, z.subtract(BigInteger.ONE), 300, random, data);
                this.checkBurn(level, x, y, z.add(BigInteger.ONE), 300, random, data);

                for (BigInteger xt = x.subtract(BigInteger.ONE); xt.compareTo(x.add(BigInteger.ONE)) <= 0; xt = xt.add(BigInteger.ONE)) {
                    for (BigInteger zt = z.subtract(BigInteger.ONE); zt.compareTo(z.add(BigInteger.ONE)) <= 0; zt = zt.add(BigInteger.ONE)) {
                        for (int yt = y - 1; yt <= y + 4; yt++) {
                            if (!xt.equals(x) || yt != y || !zt.equals(z)) {
                                int var11 = 100;
                                if (yt > y + 1) {
                                    var11 += (yt - (y + 1)) * 100;
                                }

                                int var12 = this.getFireOdds(level, xt, yt, zt);
                                if (var12 > 0) {
                                    int var13 = (var12 + 40) / (data + 30);
                                    if (var13 > 0
                                            && random.nextInt(var11) <= var13
                                            && (!level.isRaining() || !level.isRainingAt(xt, yt, zt))
                                            && !level.isRainingAt(xt.subtract(BigInteger.ONE), yt, z)
                                            && !level.isRainingAt(xt.add(BigInteger.ONE), yt, zt)
                                            && !level.isRainingAt(xt, yt, zt.subtract(BigInteger.ONE))
                                            && !level.isRainingAt(xt, yt, zt.add(BigInteger.ONE))) {
                                        int var14 = data + random.nextInt(5) / 4;
                                        if (var14 > 15) {
                                            var14 = 15;
                                        }

                                        level.setTileAndData(xt, yt, zt, this.id, var14);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            level.setTile(x, y, z, 0);
        }
    }

    private void checkBurn(Level level, BigInteger x, int y, BigInteger z, int chance, Random random, int spreadChance) {
        int odds = this.burnOdds[level.getTile(x, y, z)];
        if (random.nextInt(chance) < odds) {
            boolean var9 = level.getTile(x, y, z) == Tile.TNT.id;
            if (random.nextInt(spreadChance + 10) < 5 && !level.isRainingAt(x, y, z)) {
                int data = spreadChance + random.nextInt(5) / 4;
                if (data > 15) {
                    data = 15;
                }

                level.setTileAndData(x, y, z, this.id, data);
            } else {
                level.setTile(x, y, z, 0);
            }

            if (var9) {
                Tile.TNT.destroy(level, x, y, z, 1);
            }
        }
    }

    private boolean isValidFireLocation(Level level, BigInteger x, int y, BigInteger z) {
        if (this.canBurn(level, x.add(BigInteger.ONE), y, z)) {
            return true;
        } else if (this.canBurn(level, x.subtract(BigInteger.ONE), y, z)) {
            return true;
        } else if (this.canBurn(level, x, y - 1, z)) {
            return true;
        } else if (this.canBurn(level, x, y + 1, z)) {
            return true;
        } else {
            return this.canBurn(level, x, y, z.subtract(BigInteger.ONE)) ? true : this.canBurn(level, x, y, z.add(BigInteger.ONE));
        }
    }

    private int getFireOdds(Level level, BigInteger x, int y, BigInteger z) {
        int odds = 0;
        if (!level.isEmptyTile(x, y, z)) {
            return 0;
        } else {
            odds = this.getFlammability(level, x.add(BigInteger.ONE), y, z, odds);
            odds = this.getFlammability(level, x.subtract(BigInteger.ONE), y, z, odds);
            odds = this.getFlammability(level, x, y - 1, z, odds);
            odds = this.getFlammability(level, x, y + 1, z, odds);
            odds = this.getFlammability(level, x, y, z.subtract(BigInteger.ONE), odds);
            return this.getFlammability(level, x, y, z.add(BigInteger.ONE), odds);
        }
    }

    @Override
    public boolean mayPick() {
        return false;
    }

    @Override
    public boolean canBurn(LevelSource level, BigInteger x, int y, BigInteger z) {
        return this.flameOdds[level.getTile(x, y, z)] > 0;
    }

    @Override
    public int getFlammability(Level level, BigInteger x, int y, BigInteger z, int lastOdds) {
        int odds = this.flameOdds[level.getTile(x, y, z)];
        return odds > lastOdds ? odds : lastOdds;
    }

    @Override
    public boolean mayPlace(Level level, BigInteger x, int y, BigInteger z) {
        return level.isSolidBlockingTile(x, y - 1, z) || this.isValidFireLocation(level, x, y, z);
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int tile) {
        if (!level.isSolidBlockingTile(x, y - 1, z) && !this.isValidFireLocation(level, x, y, z)) {
            level.setTile(x, y, z, 0);
        }
    }

    @Override
    public void onPlace(Level level, BigInteger x, int y, BigInteger z) {
        if (level.getTile(x, y - 1, z) != Tile.OBSIDIAN.id || !Tile.PORTAL.isPortal(level, x, y, z)) {
            if (!level.isSolidBlockingTile(x, y - 1, z) && !this.isValidFireLocation(level, x, y, z)) {
                level.setTile(x, y, z, 0);
            } else {
                level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
            }
        }
    }

    @Override
    public void animateTick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        if (random.nextInt(24) == 0) {
            level.playSound(x.doubleValue() + 0.5F, y + 0.5F, z.doubleValue() + 0.5F, "fire.fire", 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F);
        }

        if (!level.isSolidBlockingTile(x, y - 1, z) && !Tile.FIRE.canBurn(level, x, y - 1, z)) {
            if (Tile.FIRE.canBurn(level, x.subtract(BigInteger.ONE), y, z)) {
                for (int var10 = 0; var10 < 2; var10++) {
                    float var15 = x.floatValue() + random.nextFloat() * 0.1F;
                    float var20 = y + random.nextFloat();
                    float var25 = z.floatValue() + random.nextFloat();
                    level.addParticle("largesmoke", var15, var20, var25, 0.0, 0.0, 0.0);
                }
            }

            if (Tile.FIRE.canBurn(level, x.add(BigInteger.ONE), y, z)) {
                for (int var11 = 0; var11 < 2; var11++) {
                    float var16 = x.add(BigInteger.ONE).floatValue() - random.nextFloat() * 0.1F;
                    float var21 = y + random.nextFloat();
                    float var26 = z.floatValue() + random.nextFloat();
                    level.addParticle("largesmoke", var16, var21, var26, 0.0, 0.0, 0.0);
                }
            }

            if (Tile.FIRE.canBurn(level, x, y, z.subtract(BigInteger.ONE))) {
                for (int var12 = 0; var12 < 2; var12++) {
                    float var17 = x.floatValue() + random.nextFloat();
                    float var22 = y + random.nextFloat();
                    float var27 = z.floatValue() + random.nextFloat() * 0.1F;
                    level.addParticle("largesmoke", var17, var22, var27, 0.0, 0.0, 0.0);
                }
            }

            if (Tile.FIRE.canBurn(level, x, y, z.add(BigInteger.ONE))) {
                for (int var13 = 0; var13 < 2; var13++) {
                    float var18 = x.floatValue() + random.nextFloat();
                    float var23 = y + random.nextFloat();
                    float var28 = z.add(BigInteger.ONE).floatValue() - random.nextFloat() * 0.1F;
                    level.addParticle("largesmoke", var18, var23, var28, 0.0, 0.0, 0.0);
                }
            }

            if (Tile.FIRE.canBurn(level, x, y + 1, z)) {
                for (int var14 = 0; var14 < 2; var14++) {
                    float var19 = x.floatValue() + random.nextFloat();
                    float var24 = y + 1 - random.nextFloat() * 0.1F;
                    float var29 = z.floatValue() + random.nextFloat();
                    level.addParticle("largesmoke", var19, var24, var29, 0.0, 0.0, 0.0);
                }
            }
        } else {
            for (int var6 = 0; var6 < 3; var6++) {
                float var7 = x.floatValue() + random.nextFloat();
                float var8 = y + random.nextFloat() * 0.5F + 0.5F;
                float var9 = z.floatValue() + random.nextFloat();
                level.addParticle("largesmoke", var7, var8, var9, 0.0, 0.0, 0.0);
            }
        }
    }
}
