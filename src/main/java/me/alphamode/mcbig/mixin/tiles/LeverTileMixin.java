package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.minecraft.util.Facing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.LeverTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(LeverTile.class)
public abstract class LeverTileMixin extends Tile implements BigTileExtension {
    protected LeverTileMixin(int id, Material material) {
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
    public boolean canPlace(Level level, BigInteger x, int y, BigInteger z, int face) {
        if (face == Facing.UP && level.isSolidBlockingTile(x, y - 1, z)) {
            return true;
        } else if (face == Facing.NORTH && level.isSolidBlockingTile(x, y, z.add(BigInteger.ONE))) {
            return true;
        } else if (face == Facing.SOUTH && level.isSolidBlockingTile(x, y, z.subtract(BigInteger.ONE))) {
            return true;
        } else {
            return face == Facing.WEST && level.isSolidBlockingTile(x.add(BigInteger.ONE), y, z) ? true : face == 5 && level.isSolidBlockingTile(x.subtract(BigInteger.ONE), y, z);
        }
    }

    @Override
    public boolean mayPlace(Level level, BigInteger x, int y, BigInteger z) {
        if (level.isSolidBlockingTile(x.subtract(BigInteger.ONE), y, z)) {
            return true;
        } else if (level.isSolidBlockingTile(x.add(BigInteger.ONE), y, z)) {
            return true;
        } else if (level.isSolidBlockingTile(x, y, z.subtract(BigInteger.ONE))) {
            return true;
        } else {
            return level.isSolidBlockingTile(x, y, z.add(BigInteger.ONE)) ? true : level.isSolidBlockingTile(x, y - 1, z);
        }
    }

    @Override
    public void setPlacedOnFace(Level level, BigInteger x, int y, BigInteger z, int facing) {
        int data = level.getData(x, y, z);
        int flipped = data & 8;
        data &= 7;
        data = -1;
        if (facing == 1 && level.isSolidBlockingTile(x, y - 1, z)) {
            data = 5 + level.random.nextInt(2);
        }

        if (facing == 2 && level.isSolidBlockingTile(x, y, z.add(BigInteger.ONE))) {
            data = 4;
        }

        if (facing == 3 && level.isSolidBlockingTile(x, y, z.subtract(BigInteger.ONE))) {
            data = 3;
        }

        if (facing == 4 && level.isSolidBlockingTile(x.add(BigInteger.ONE), y, z)) {
            data = 2;
        }

        if (facing == 5 && level.isSolidBlockingTile(x.subtract(BigInteger.ONE), y, z)) {
            data = 1;
        }

        if (data == -1) {
            this.dropResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
        } else {
            level.setData(x, y, z, data + flipped);
        }
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int tile) {
        if (this.checkCanSurvive(level, x, y, z)) {
            int data = level.getData(x, y, z) & 7;
            boolean canSupport = false;
            if (!level.isSolidBlockingTile(x.subtract(BigInteger.ONE), y, z) && data == 1) {
                canSupport = true;
            }

            if (!level.isSolidBlockingTile(x.add(BigInteger.ONE), y, z) && data == 2) {
                canSupport = true;
            }

            if (!level.isSolidBlockingTile(x, y, z.subtract(BigInteger.ONE)) && data == 3) {
                canSupport = true;
            }

            if (!level.isSolidBlockingTile(x, y, z.add(BigInteger.ONE)) && data == 4) {
                canSupport = true;
            }

            if (!level.isSolidBlockingTile(x, y - 1, z) && data == 5) {
                canSupport = true;
            }

            if (!level.isSolidBlockingTile(x, y - 1, z) && data == 6) {
                canSupport = true;
            }

            if (canSupport) {
                this.dropResources(level, x, y, z, level.getData(x, y, z));
                level.setTile(x, y, z, 0);
            }
        }
    }

    private boolean checkCanSurvive(Level level, BigInteger x, int y, BigInteger z) {
        if (!this.mayPlace(level, x, y, z)) {
            this.dropResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void updateShape(LevelSource source, BigInteger x, int y, BigInteger z) {
        int data = source.getData(x, y, z) & 7;
        float r = 0.1875F;
        if (data == 1) {
            this.setShape(0.0F, 0.2F, 0.5F - r, r * 2.0F, 0.8F, 0.5F + r);
        } else if (data == 2) {
            this.setShape(1.0F - r * 2.0F, 0.2F, 0.5F - r, 1.0F, 0.8F, 0.5F + r);
        } else if (data == 3) {
            this.setShape(0.5F - r, 0.2F, 0.0F, 0.5F + r, 0.8F, r * 2.0F);
        } else if (data == 4) {
            this.setShape(0.5F - r, 0.2F, 1.0F - r * 2.0F, 0.5F + r, 0.8F, 1.0F);
        } else {
            r = 0.25F;
            this.setShape(0.5F - r, 0.0F, 0.5F - r, 0.5F + r, 0.6F, 0.5F + r);
        }
    }

    @Override
    public void attack(Level level, BigInteger x, int y, BigInteger z, Player player) {
        this.use(level, x, y, z, player);
    }

    @Override
    public boolean use(Level level, BigInteger x, int y, BigInteger z, Player player) {
        if (level.isClientSide) {
            return true;
        } else {
            int data = level.getData(x, y, z);
            int dir = data & 7;
            int flipped = 8 - (data & 8);
            level.setData(x, y, z, dir + flipped);
            level.setTilesDirty(x, y, z, x, y, z);
            level.playSound(x.doubleValue() + 0.5, y + 0.5, z.doubleValue() + 0.5, "random.click", 0.3F, flipped > 0 ? 0.6F : 0.5F);
            level.updateNeighborsAt(x, y, z, this.id);
            if (dir == 1) {
                level.updateNeighborsAt(x.subtract(BigInteger.ONE), y, z, this.id);
            } else if (dir == 2) {
                level.updateNeighborsAt(x.add(BigInteger.ONE), y, z, this.id);
            } else if (dir == 3) {
                level.updateNeighborsAt(x, y, z.subtract(BigInteger.ONE), this.id);
            } else if (dir == 4) {
                level.updateNeighborsAt(x, y, z.add(BigInteger.ONE), this.id);
            } else {
                level.updateNeighborsAt(x, y - 1, z, this.id);
            }

            return true;
        }
    }

    @Override
    public void onRemove(Level level, BigInteger x, int y, BigInteger z) {
        int data = level.getData(x, y, z);
        if ((data & 8) > 0) {
            level.updateNeighborsAt(x, y, z, this.id);
            int var6 = data & 7;
            if (var6 == 1) {
                level.updateNeighborsAt(x.subtract(BigInteger.ONE), y, z, this.id);
            } else if (var6 == 2) {
                level.updateNeighborsAt(x.add(BigInteger.ONE), y, z, this.id);
            } else if (var6 == 3) {
                level.updateNeighborsAt(x, y, z.subtract(BigInteger.ONE), this.id);
            } else if (var6 == 4) {
                level.updateNeighborsAt(x, y, z.add(BigInteger.ONE), this.id);
            } else {
                level.updateNeighborsAt(x, y - 1, z, this.id);
            }
        }

        super.onRemove(level, x, y, z);
    }

    @Override
    public boolean getSignal(LevelSource levelReader, BigInteger x, int y, BigInteger z, int direction) {
        return (levelReader.getData(x, y, z) & 8) > 0;
    }

    @Override
    public boolean getDirectSignal(Level level, BigInteger x, int y, BigInteger z, int direction) {
        int data = level.getData(x, y, z);
        if ((data & 8) == 0) {
            return false;
        } else {
            int dir = data & 7;
            if (dir == 6 && direction == 1) {
                return true;
            } else if (dir == 5 && direction == 1) {
                return true;
            } else if (dir == 4 && direction == 2) {
                return true;
            } else if (dir == 3 && direction == 3) {
                return true;
            } else {
                return dir == 2 && direction == 4 ? true : dir == 1 && direction == 5;
            }
        }
    }
}
