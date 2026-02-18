package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigLiquidTileExtension;
import me.alphamode.mcbig.extensions.BigTileExtension;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Facing;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.LiquidTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;
import java.util.Random;

@Mixin(LiquidTile.class)
public abstract class LiquidTileMixin extends Tile implements BigLiquidTileExtension {
    protected LiquidTileMixin(int id, Material material) {
        super(id, material);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public int getFoliageColor(LevelSource level, BigInteger x, int y, BigInteger z) {
        return 16777215;
    }

    @Override
    public int getDepth(Level level, BigInteger x, int y, BigInteger z) {
        return level.getMaterial(x, y, z) != this.material ? -1 : level.getData(x, y, z);
    }

    protected int getRenderedDepth(LevelSource source, BigInteger x, int y, BigInteger z) {
        if (source.getMaterial(x, y, z) != this.material) {
            return -1;
        } else {
            int data = source.getData(x, y, z);
            if (data >= 8) {
                data = 0;
            }

            return data;
        }
    }

    @Override
    public boolean isSolid(LevelSource level, BigInteger x, int y, BigInteger z, int face) {
        Material mat = level.getMaterial(x, y, z);
        if (mat == this.material) {
            return false;
        } else if (mat == Material.ICE) {
            return false;
        } else {
            return face == Facing.UP ? true : super.isSolid(level, x, y, z, face);
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderFace(LevelSource level, BigInteger x, int y, BigInteger z, int face) {
        Material mat = level.getMaterial(x, y, z);
        if (mat == this.material) {
            return false;
        } else if (mat == Material.ICE) {
            return false;
        } else {
            return face == Facing.UP ? true : super.shouldRenderFace(level, x, y, z, face);
        }
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
    public Vec3 getFlow(LevelSource level, BigInteger x, int y, BigInteger z) {
        Vec3 var5 = Vec3.newTemp(0.0, 0.0, 0.0);
        int var6 = this.getRenderedDepth(level, x, y, z);

        for(int dir = 0; dir < 4; ++dir) {
            BigInteger var8 = x;
            BigInteger var10 = z;
            if (dir == 0) {
                var8 = x.subtract(BigInteger.ONE);
            }

            if (dir == 1) {
                var10 = z.subtract(BigInteger.ONE);
            }

            if (dir == 2) {
                var8 = var8.add(BigInteger.ONE);
            }

            if (dir == 3) {
                var10 = var10.add(BigInteger.ONE);
            }

            int var11 = this.getRenderedDepth(level, var8, y, var10);
            if (var11 < 0) {
                if (!level.getMaterial(var8, y, var10).blocksMotion()) {
                    var11 = this.getRenderedDepth(level, var8, y - 1, var10);
                    if (var11 >= 0) {
                        int var12 = var11 - (var6 - 8);
                        var5 = var5.add((double)((var8.subtract(x)).doubleValue() * var12), (double)((y - y) * var12), (double)((var10.subtract(z)).doubleValue() * var12));
                    }
                }
            } else if (var11 >= 0) {
                int var15 = var11 - var6;
                var5 = var5.add((double)((var8.subtract(x)).doubleValue() * var15), (double)((y - y) * var15), (double)((var10.subtract(z)).doubleValue() * var15));
            }
        }

        if (level.getData(x, y, z) >= 8) {
            boolean var13 = false;
            if (var13 || this.isSolid(level, x, y, z.subtract(BigInteger.ONE), Facing.NORTH)) {
                var13 = true;
            }

            if (var13 || this.isSolid(level, x, y, z.add(BigInteger.ONE), Facing.SOUTH)) {
                var13 = true;
            }

            if (var13 || this.isSolid(level, x.subtract(BigInteger.ONE), y, z, Facing.WEST)) {
                var13 = true;
            }

            if (var13 || this.isSolid(level, x.add(BigInteger.ONE), y, z, Facing.EAST)) {
                var13 = true;
            }

            if (var13 || this.isSolid(level, x, y + 1, z.subtract(BigInteger.ONE), Facing.NORTH)) {
                var13 = true;
            }

            if (var13 || this.isSolid(level, x, y + 1, z.add(BigInteger.ONE), Facing.SOUTH)) {
                var13 = true;
            }

            if (var13 || this.isSolid(level, x.subtract(BigInteger.ONE), y + 1, z, Facing.WEST)) {
                var13 = true;
            }

            if (var13 || this.isSolid(level, x.add(BigInteger.ONE), y + 1, z, Facing.EAST)) {
                var13 = true;
            }

            if (var13) {
                var5 = var5.normalize().add(0.0, -6.0, 0.0);
            }
        }

        return var5.normalize();
    }

    @Override
    public void handleEntityInside(Level level, BigInteger x, int y, BigInteger z, Entity entity, Vec3 delta) {
        Vec3 flow = getFlow(level, x, y, z);
        delta.x += flow.x;
        delta.y += flow.y;
        delta.z += flow.z;
    }

    @Override
    public float getBrightness(LevelSource level, BigInteger x, int y, BigInteger z) {
        float var5 = level.getBrightness(x, y, z);
        float var6 = level.getBrightness(x, y + 1, z);
        return var5 > var6 ? var5 : var6;
    }

    @Override
    public void animateTick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        if (this.material == Material.WATER && random.nextInt(64) == 0) {
            int var6 = level.getData(x, y, z);
            if (var6 > 0 && var6 < 8) {
                level.playSound(
                        (double)(x.doubleValue() + 0.5F),
                        (double)((float)y + 0.5F),
                        (double)(z.doubleValue() + 0.5F),
                        "liquid.water",
                        random.nextFloat() * 0.25F + 0.75F,
                        random.nextFloat() * 1.0F + 0.5F
                );
            }
        }

        if (this.material == Material.LAVA && level.getMaterial(x, y + 1, z) == Material.AIR && !level.isSolidTile(x, y + 1, z) && random.nextInt(100) == 0) {
            double var12 = (double)(x.doubleValue() + random.nextFloat());
            double var8 = (double)y + this.yy1;
            double var10 = (double)(z.doubleValue() + random.nextFloat());
            level.addParticle("lava", var12, var8, var10, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void onPlace(Level level, BigInteger x, int y, BigInteger z) {
        this.updateLiquid(level, x, y, z);
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int tile) {
        this.updateLiquid(level, x, y, z);
    }

    private void updateLiquid(Level level, BigInteger x, int y, BigInteger z) {
//        if (level.getTile(x, y, z) == this.id) {
//            if (this.material == Material.LAVA) {
//                boolean var5 = false;
//                if (var5 || level.getMaterial(x, y, z.subtract(BigInteger.ONE)) == Material.WATER) {
//                    var5 = true;
//                }
//
//                if (var5 || level.getMaterial(x, y, z.add(BigInteger.ONE)) == Material.WATER) {
//                    var5 = true;
//                }
//
//                if (var5 || level.getMaterial(x.subtract(BigInteger.ONE), y, z) == Material.WATER) {
//                    var5 = true;
//                }
//
//                if (var5 || level.getMaterial(x.add(BigInteger.ONE), y, z) == Material.WATER) {
//                    var5 = true;
//                }
//
//                if (var5 || level.getMaterial(x, y + 1, z) == Material.WATER) {
//                    var5 = true;
//                }
//
//                if (var5) {
//                    int var6 = level.getData(x, y, z);
//                    if (var6 == 0) {
//                        level.setTile(x, y, z, Tile.OBSIDIAN.id);
//                    } else if (var6 <= 4) {
//                        level.setTile(x, y, z, Tile.COBBLESTONE.id);
//                    }
//
//                    this.fizz(level, x, y, z);
//                }
//            }
//        }
    }

    @Override
    public void fizz(Level level, BigInteger x, int y, BigInteger z) {
        level.playSound(
                (double)(x.doubleValue() + 0.5F),
                (double)((float)y + 0.5F),
                (double)(z.doubleValue() + 0.5F),
                "random.fizz",
                0.5F,
                2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F
        );

        for(int var5 = 0; var5 < 8; ++var5) {
            level.addParticle("largesmoke", (double)x.doubleValue() + Math.random(), (double)y + 1.2, (double)z.doubleValue() + Math.random(), 0.0, 0.0, 0.0);
        }
    }
}
