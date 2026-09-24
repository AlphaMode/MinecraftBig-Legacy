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
public abstract class LiquidTileMixin extends Tile implements BigTileExtension, BigLiquidTileExtension {
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
        Material m = level.getMaterial(x, y, z);
        if (m == this.material) return false;
        // beta 1.8 swaps these checks
        //? >=1.0.0-beta.8.0.r {
        /*if (face == Facing.UP) return true;
        if (m == Material.ice) return false;
        *///? } else {
        if (m == Material.ice) return false;
        if (face == Facing.UP) return true;
        //? }

        return super.isSolid(level, x, y, z, face);
    }

    @Override
    public boolean shouldRenderFace(LevelSource level, BigInteger x, int y, BigInteger z, int face) {
        Material m = level.getMaterial(x, y, z);
        if (m == this.material) return false;
        //? >=1.0.0-beta.8.0.r {
        /*if (face == Facing.UP) return true;
        if (m == Material.ice) return false;
        *///? } else {
        if (m == Material.ice) return false;
        if (face == Facing.UP) return true;
        //? }
        return super.shouldRenderFace(level, x, y, z, face);
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
        Vec3 flow = Vec3.newTemp(0.0, 0.0, 0.0);
        int mid = this.getRenderedDepth(level, x, y, z);

        for(int d = 0; d < 4; ++d) {
            BigInteger xt = x;
            BigInteger zt = z;
            if (d == 0) {
                xt = x.subtract(BigInteger.ONE);
            }

            if (d == 1) {
                zt = z.subtract(BigInteger.ONE);
            }

            if (d == 2) {
                xt = xt.add(BigInteger.ONE);
            }

            if (d == 3) {
                zt = zt.add(BigInteger.ONE);
            }

            int t = this.getRenderedDepth(level, xt, y, zt);
            if (t < 0) {
                if (!level.getMaterial(xt, y, zt).blocksMotion()) {
                    t = this.getRenderedDepth(level, xt, y - 1, zt);
                    if (t >= 0) {
                        int dir = t - (mid - 8);
                        flow = flow.add((double)((xt.subtract(x)).doubleValue() * dir), (double)((y - y) * dir), (double)((zt.subtract(z)).doubleValue() * dir));
                    }
                }
            } else if (t >= 0) {
                int dir = t - mid;
                flow = flow.add((double)((xt.subtract(x)).doubleValue() * dir), (double)((y - y) * dir), (double)((zt.subtract(z)).doubleValue() * dir));
            }
        }

        if (level.getData(x, y, z) >= 8) {
            boolean ok = false;
            if (ok || this.isSolid(level, x, y, z.subtract(BigInteger.ONE), Facing.NORTH)) ok = true;
            if (ok || this.isSolid(level, x, y, z.add(BigInteger.ONE), Facing.SOUTH)) ok = true;
            if (ok || this.isSolid(level, x.subtract(BigInteger.ONE), y, z, Facing.WEST)) ok = true;
            if (ok || this.isSolid(level, x.add(BigInteger.ONE), y, z, Facing.EAST)) ok = true;
            if (ok || this.isSolid(level, x, y + 1, z.subtract(BigInteger.ONE), Facing.NORTH)) ok = true;
            if (ok || this.isSolid(level, x, y + 1, z.add(BigInteger.ONE), Facing.SOUTH)) ok = true;
            if (ok || this.isSolid(level, x.subtract(BigInteger.ONE), y + 1, z, Facing.WEST)) ok = true;
            if (ok || this.isSolid(level, x.add(BigInteger.ONE), y + 1, z, Facing.EAST)) ok = true;
            if (ok) flow = flow.normalize().add(0.0, -6.0, 0.0);
        }

        return flow.normalize();
    }

    @Override
    public void handleEntityInside(Level level, BigInteger x, int y, BigInteger z, Entity entity, Vec3 delta) {
        Vec3 flow = getFlow(level, x, y, z);
        delta.x += flow.x;
        delta.y += flow.y;
        delta.z += flow.z;
    }

    //? >=1.0.0-beta.8.0.r {
    /*@Override
    public int getLightColor(LevelSource level, BigInteger x, int y, BigInteger z) {
        int a = level.getLightColor(x, y, z, 0);
        int b = level.getLightColor(x, y + 1, z, 0);

        int aa = a & 0xFF;
        int ba = b & 0xFF;
        int ab = a >> 16 & 0xFF;
        int bb = b >> 16 & 0xFF;

        return (aa > ba ? aa : ba) | (ab > bb ? ab : bb) << 16;
    }
    *///? }

    @Override
    public float getBrightness(LevelSource level, BigInteger x, int y, BigInteger z) {
        float a = level.getBrightness(x, y, z);
        float b = level.getBrightness(x, y + 1, z);
        return a > b ? a : b;
    }

    @Override
    public void animateTick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        //? >=1.0.0-beta.8.0.r {
        /*if (this.material == Material.water) {
            if (random.nextInt(10) == 0) {
                int d = level.getData(x, y, z);
                if (d <= 0 || d >= 8) {
                    level.addParticle("suspended", x.doubleValue() + random.nextFloat(), y + random.nextFloat(), z.doubleValue() + random.nextFloat(), 0.0, 0.0, 0.0);
                }
            }

            for (int var21 = 0; var21 < 0; var21++) {
                int dir = random.nextInt(4);
                BigInteger xt = x;
                BigInteger zt = z;

                if (dir == 0) xt = xt.subtract(BigInteger.ONE);
                if (dir == 1) xt = xt.add(BigInteger.ONE);
                if (dir == 2) zt = zt.subtract(BigInteger.ONE);
                if (dir == 3) zt = zt.add(BigInteger.ONE);

                if (level.getMaterial(xt, y, zt) == Material.air
                        && (level.getMaterial(xt, y - 1, zt).blocksMotion() || level.getMaterial(xt, y - 1, zt).isLiquid())) {
                    float r = 1 / 16.0f;
                    double xx = x.doubleValue() + random.nextFloat();
                    double yy = y + random.nextFloat();
                    double zz = z.doubleValue() + random.nextFloat();
                    if (dir == 0) xx = x.doubleValue() - r;
                    if (dir == 1) xx = x.doubleValue() + 1 + r;
                    if (dir == 2) zz = z.doubleValue() - r;
                    if (dir == 3) zz = z.doubleValue() + 1 + r;

                    double xd = 0;
                    double zd = 0;

                    if (dir == 0) xd = -r;
                    if (dir == 1) xd = r;
                    if (dir == 2) zd = -r;
                    if (dir == 3) zd = r;

                    level.addParticle("splash", xx, yy, zz, xd, 0.0, zd);
                }
            }
        }
        *///? }

        if (this.material == Material.water && random.nextInt(64) == 0) {
            int d = level.getData(x, y, z);
            if (d > 0 && d < 8) {
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

        if (this.material == Material.lava && level.getMaterial(x, y + 1, z) == Material.air && !level.isSolidRenderTile(x, y + 1, z) && random.nextInt(100) == 0) {
            double xx = (double)(x.doubleValue() + random.nextFloat());
            double yy = (double)y + this.yy1;
            double zz = (double)(z.doubleValue() + random.nextFloat());
            level.addParticle("lava", xx, yy, zz, 0.0, 0.0, 0.0);
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
        if (level.getTile(x, y, z) != this.id) return;
        if (this.material == Material.lava) {
            boolean water = false;
            if (water || level.getMaterial(x, y, z.subtract(BigInteger.ONE)) == Material.water) water = true;
            if (water || level.getMaterial(x, y, z.add(BigInteger.ONE)) == Material.water) water = true;
            if (water || level.getMaterial(x.subtract(BigInteger.ONE), y, z) == Material.water) water = true;
            if (water || level.getMaterial(x.add(BigInteger.ONE), y, z) == Material.water) water = true;
            if (water || level.getMaterial(x, y + 1, z) == Material.water) water = true;

            if (water) {
                int data = level.getData(x, y, z);
                if (data == 0) {
                    level.setTile(x, y, z, Tile.obsidian.id);
                } else if (data <= 4) {
                    level.setTile(x, y, z, Tile.cobblestone.id);
                }

                this.fizz(level, x, y, z);
            }
        }
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
