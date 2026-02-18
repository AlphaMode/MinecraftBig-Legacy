package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.tiles.BigPortalTileExtension;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.minecraft.util.Facing;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.HalfTransparentTile;
import net.minecraft.world.level.tile.PortalTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;
import java.util.Random;

@Mixin(PortalTile.class)
public abstract class PortalTileMixin extends HalfTransparentTile implements BigPortalTileExtension {
    protected PortalTileMixin(int id, int tex, Material material, boolean allowSame) {
        super(id, tex, material, allowSame);
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
    public void updateShape(LevelSource source, BigInteger x, int y, BigInteger z) {
        if (source.getTile(x.subtract(BigInteger.ONE), y, z) != this.id && source.getTile(x.add(BigInteger.ONE), y, z) != this.id) {
            float width = 0.125F;
            float heightOff = 0.5F;
            this.setShape(0.5F - width, 0.0F, 0.5F - heightOff, 0.5F + width, 1.0F, 0.5F + heightOff);
        } else {
            float heightOff = 0.5F;
            float width = 0.125F;
            this.setShape(0.5F - heightOff, 0.0F, 0.5F - width, 0.5F + heightOff, 1.0F, 0.5F + width);
        }

    }

    @Override
    public boolean isPortal(Level level, BigInteger x, int y, BigInteger z) {
        BigInteger xOff = BigInteger.ZERO;
        BigInteger zOff = BigInteger.ZERO;
        if (level.getTile(x.subtract(BigInteger.ONE), y, z) == Tile.OBSIDIAN.id || level.getTile(x.add(BigInteger.ONE), y, z) == Tile.OBSIDIAN.id) {
            xOff = BigInteger.ONE;
        }

        if (level.getTile(x, y, z.subtract(BigInteger.ONE)) == Tile.OBSIDIAN.id || level.getTile(x, y, z.add(BigInteger.ONE)) == Tile.OBSIDIAN.id) {
            zOff = BigInteger.ONE;
        }

        if (xOff.equals(zOff)) {
            return false;
        } else {
            if (level.getTile(x.subtract(xOff), y, z.subtract(zOff)) == 0) {
                x = x.subtract(xOff);
                z = z.subtract(zOff);
            }

            for (int wO = -1; wO <= 2; wO++) {
                BigInteger bigWO = BigInteger.valueOf(wO);
                for (int hO = -1; hO <= 3; hO++) {
                    boolean var9 = wO == -1 || wO == 2 || hO == -1 || hO == 3;
                    if (wO != -1 && wO != 2 || hO != -1 && hO != 3) {
                        int tile = level.getTile(x.add(xOff.multiply(bigWO)), y + hO, z.add(zOff.multiply(bigWO)));
                        if (var9) {
                            if (tile != Tile.OBSIDIAN.id) {
                                return false;
                            }
                        } else if (tile != 0 && tile != Tile.FIRE.id) {
                            return false;
                        }
                    }
                }
            }

            level.noNeighborUpdate = true;

            for (int wO = 0; wO < 2; wO++) {
                BigInteger bigWO = BigInteger.valueOf(wO);
                for (int hO = 0; hO < 3; hO++) {
                    level.setTile(x.add(xOff.multiply(bigWO)), y + hO, z.add(zOff.multiply(bigWO)), Tile.PORTAL.id);
                }
            }

            level.noNeighborUpdate = false;
            return true;
        }
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int tile) {
        BigInteger xOff = BigInteger.ZERO;
        BigInteger zOff = BigInteger.ONE;
        if (level.getTile(x.subtract(BigInteger.ONE), y, z) == this.id || level.getTile(x.add(BigInteger.ONE), y, z) == this.id) {
            xOff = BigInteger.ONE;
            zOff = BigInteger.ZERO;
        }

        int yt = y;
        while (level.getTile(x, yt - 1, z) == this.id) {
            --yt;
        }

        if (level.getTile(x, yt - 1, z) != Tile.OBSIDIAN.id) {
            level.setTile(x, y, z, 0);
        } else {
            int yOff = 1;
            while (yOff < 4 && level.getTile(x, yt + yOff, z) == this.id) {
                ++yOff;
            }

            if (yOff == 3 && level.getTile(x, yt + yOff, z) == Tile.OBSIDIAN.id) {
                boolean sideSame = level.getTile(x.subtract(BigInteger.ONE), y, z) == this.id || level.getTile(x.add(BigInteger.ONE), y, z) == this.id;
                boolean endSame = level.getTile(x, y, z.subtract(BigInteger.ONE)) == this.id || level.getTile(x, y, z.add(BigInteger.ONE)) == this.id;
                if (sideSame && endSame) {
                    level.setTile(x, y, z, 0);
                } else if ((level.getTile(x.add(xOff), y, z.add(zOff)) != Tile.OBSIDIAN.id || level.getTile(x.subtract(xOff), y, z.subtract(zOff)) != this.id) && (level.getTile(x.subtract(xOff), y, z.subtract(zOff)) != Tile.OBSIDIAN.id || level.getTile(x.add(xOff), y, z.add(zOff)) != this.id)) {
                    level.setTile(x, y, z, 0);
                }
            } else {
                level.setTile(x, y, z, 0);
            }
        }
    }

    @Override
    public boolean shouldRenderFace(LevelSource level, BigInteger x, int y, BigInteger z, int face) {
        if (level.getTile(x, y, z) == this.id) {
            return false;
        } else {
            boolean nX = level.getTile(x.subtract(BigInteger.ONE), y, z) == this.id && level.getTile(x.subtract(BigInteger.TWO), y, z) != this.id;
            boolean pX = level.getTile(x.add(BigInteger.ONE), y, z) == this.id && level.getTile(x.add(BigInteger.TWO), y, z) != this.id;
            boolean nZ = level.getTile(x, y, z.subtract(BigInteger.ONE)) == this.id && level.getTile(x, y, z.subtract(BigInteger.TWO)) != this.id;
            boolean pZ = level.getTile(x, y, z.add(BigInteger.ONE)) == this.id && level.getTile(x, y, z.add(BigInteger.TWO)) != this.id;
            boolean var10 = nX || pX;
            boolean var11 = nZ || pZ;
            if (var10 && face == Facing.WEST) {
                return true;
            } else if (var10 && face == Facing.EAST) {
                return true;
            } else if (var11 && face == Facing.NORTH) {
                return true;
            } else {
                return var11 && face == Facing.SOUTH;
            }
        }
    }

    @Override
    public void entityInside(Level level, BigInteger x, int y, BigInteger z, Entity entity) {
        if (entity.riding == null && entity.rider == null) {
            entity.handleInsidePortal();
        }
    }

    @Override
    public void animateTick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        if (random.nextInt(100) == 0) {
            level.playSound((double) x.doubleValue() + (double) 0.5F, (double) y + (double) 0.5F, (double) z.doubleValue() + (double) 0.5F, "portal.portal", 1.0F, random.nextFloat() * 0.4F + 0.8F);
        }

        for (int var6 = 0; var6 < 4; ++var6) {
            double var7 = (double) ((float) x.floatValue() + random.nextFloat());
            double var9 = (double) ((float) y + random.nextFloat());
            double var11 = (double) ((float) z.floatValue() + random.nextFloat());
            double var13 = (double) 0.0F;
            double var15 = (double) 0.0F;
            double var17 = (double) 0.0F;
            int var19 = random.nextInt(2) * 2 - 1;
            var13 = ((double) random.nextFloat() - (double) 0.5F) * (double) 0.5F;
            var15 = ((double) random.nextFloat() - (double) 0.5F) * (double) 0.5F;
            var17 = ((double) random.nextFloat() - (double) 0.5F) * (double) 0.5F;
            if (level.getTile(x.subtract(BigInteger.ONE), y, z) != this.id && level.getTile(x.add(BigInteger.ONE), y, z) != this.id) {
                var7 = (double) x.doubleValue() + (double) 0.5F + (double) 0.25F * (double) var19;
                var13 = (double) (random.nextFloat() * 2.0F * (float) var19);
            } else {
                var11 = (double) z.doubleValue() + (double) 0.5F + (double) 0.25F * (double) var19;
                var17 = (double) (random.nextFloat() * 2.0F * (float) var19);
            }

            level.addParticle("portal", var7, var9, var11, var13, var15, var17);
        }

    }
}
