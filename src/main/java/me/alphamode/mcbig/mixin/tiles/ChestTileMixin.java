package me.alphamode.mcbig.mixin.tiles;

import net.minecraft.util.Facing;
import net.minecraft.util.Mth;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.ChestTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.TileEntityTile;
import net.minecraft.world.level.tile.entity.ChestTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(ChestTile.class)
public abstract class ChestTileMixin extends TileEntityTile {
    @Shadow
    private Random random;

    protected ChestTileMixin(int i, Material material) {
        super(i, material);
    }

    //? >=1.0.0-beta.8.0.r {
    /*@Override
    public void onPlace(Level level, BigInteger x, int y, BigInteger z) {
        super.onPlace(level, x, y, z);
        recalcLockDir(level, x, y, z);

        var xmo = x.subtract(BigInteger.ONE);
        var xpo = x.add(BigInteger.ONE);
        var zmo = z.subtract(BigInteger.ONE);
        var zpo = z.add(BigInteger.ONE);

        int n = level.getTile(x, y, zmo); // face = 2
        int s = level.getTile(x, y, zpo); // face = 3
        int w = level.getTile(xmo, y, z); // face = 4
        int e = level.getTile(xpo, y, z); // face = 5
        if (n == this.id) recalcLockDir(level, x, y, zmo);
        if (s == this.id) recalcLockDir(level, x, y, zpo);
        if (w == this.id) recalcLockDir(level, xmo, y, z);
        if (e == this.id) recalcLockDir(level, xpo, y, z);
    }

    @Override
    public void setPlacedBy(Level level, BigInteger x, int y, BigInteger z, Mob entity) {
        var xmo = x.subtract(BigInteger.ONE);
        var xpo = x.add(BigInteger.ONE);
        var zmo = z.subtract(BigInteger.ONE);
        var zpo = z.add(BigInteger.ONE);

        int n = level.getTile(x, y, zmo); // face = 2
        int s = level.getTile(x, y, zpo); // face = 3
        int w = level.getTile(xmo, y, z); // face = 4
        int e = level.getTile(xpo, y, z); // face = 5

        int facing = 0;
        int dir = Mth.floor(entity.yRot * 4.0F / 360.0F + 0.5) & 3;

        if (dir == 0) facing = Facing.NORTH;
        if (dir == 1) facing = Facing.EAST;
        if (dir == 2) facing = Facing.SOUTH;
        if (dir == 3) facing = Facing.WEST;

        if (n != this.id && s != this.id && w != this.id && e != this.id) {
            level.setData(x, y, z, facing);
        } else {
            if ((n == this.id || s == this.id) && (facing == 4 || facing == 5)) {
                if (n == this.id) level.setData(x, y, zmo, facing);
                else level.setData(x, y, zpo, facing);

                level.setData(x, y, z, facing);
            }

            if ((w == this.id || e == this.id) && (facing == 2 || facing == 3)) {
                if (w == this.id) level.setData(xmo, y, z, facing);
                else level.setData(xpo, y, z, facing);
                level.setData(x, y, z, facing);
            }
        }
    }

    public void recalcLockDir(Level level, BigInteger x, int y, BigInteger z) {
        if (level.isClientSide)
            return;

        var xmo = x.subtract(BigInteger.ONE);
        var xpo = x.add(BigInteger.ONE);
        var zmo = z.subtract(BigInteger.ONE);
        var zpo = z.add(BigInteger.ONE);

        int n = level.getTile(x, y, zmo); // face = 2
        int s = level.getTile(x, y, zpo); // face = 3
        int w = level.getTile(xmo, y, z); // face = 4
        int e = level.getTile(xpo, y, z); // face = 5

        int lockDir = 4;
        if (n == this.id || s == this.id) {
            int w2 = level.getTile(xmo, y, n == this.id ? zmo : zpo);
            int e2 = level.getTile(xpo, y, n == this.id ? zmo : zpo);

            lockDir = 5;

            int otherDir = -1;
            if (n == this.id) otherDir = level.getData(x, y, zmo);
            else otherDir = level.getData(x, y, zpo);

            if (otherDir == 4) {
                lockDir = 4;
            }

            if ((Tile.solid[w] || Tile.solid[w2]) && !Tile.solid[e] && !Tile.solid[e2]) lockDir = 5;
            if ((Tile.solid[e] || Tile.solid[e2]) && !Tile.solid[w] && !Tile.solid[w2]) lockDir = 4;
        } else if (w != this.id && e != this.id) {
            lockDir = 3;
            if (Tile.solid[n] && !Tile.solid[s]) lockDir = Facing.SOUTH;
            if (Tile.solid[s] && !Tile.solid[n]) lockDir = Facing.NORTH;
            if (Tile.solid[w] && !Tile.solid[e]) lockDir = Facing.EAST;
            if (Tile.solid[e] && !Tile.solid[w]) lockDir = Facing.WEST;
        } else {
            int n2 = level.getTile(w == this.id ? xmo : xpo, y, zmo);
            int s2 = level.getTile(w == this.id ? xmo : xpo, y, zpo);

            lockDir = 3;
            int otherDir = -1;
            if (w == this.id) otherDir = level.getData(xmo, y, z);
            else otherDir = level.getData(xpo, y, z);

            if (otherDir == 2) lockDir = 2;

            if ((Tile.solid[n] || Tile.solid[n2]) && !Tile.solid[s] && !Tile.solid[s2]) lockDir = 3;
            if ((Tile.solid[s] || Tile.solid[s2]) && !Tile.solid[n] && !Tile.solid[n2]) lockDir = 2;
        }

        level.setData(x, y, z, lockDir);
    }
    *///? }

    @Override
    public int getTexture(LevelSource level, BigInteger x, int y, BigInteger z, int face) {
        if (face == 1) return this.tex - 1;
        if (face == 0) return this.tex - 1;

        int n = level.getTile(x, y, z.subtract(BigInteger.ONE));
        int s = level.getTile(x, y, z.add(BigInteger.ONE));
        int w = level.getTile(x.subtract(BigInteger.ONE), y, z);
        int e = level.getTile(x.add(BigInteger.ONE), y, z);
        if (n != this.id && s != this.id) {
            if (w != this.id && e != this.id) {
                int lockDir = 3;
                if (Tile.solid[n] && !Tile.solid[s]) {
                    lockDir = 3;
                }

                if (Tile.solid[s] && !Tile.solid[n]) {
                    lockDir = 2;
                }

                if (Tile.solid[w] && !Tile.solid[e]) {
                    lockDir = 5;
                }

                if (Tile.solid[e] && !Tile.solid[w]) {
                    lockDir = 4;
                }

                return face == lockDir ? this.tex + 1 : this.tex;
            } else if (face != 4 && face != 5) {
                int offs = 0;
                if (w == this.id) {
                    offs = -1;
                }

                int n2 = level.getTile(w == this.id ? x.subtract(BigInteger.ONE) : x.add(BigInteger.ONE), y, z.subtract(BigInteger.ONE));
                int s2 = level.getTile(w == this.id ? x.subtract(BigInteger.ONE) : x.add(BigInteger.ONE), y, z.add(BigInteger.ONE));
                if (face == 3) {
                    offs = -1 - offs;
                }

                int lockDir = 3;
                if ((Tile.solid[n] || Tile.solid[n2]) && !Tile.solid[s] && !Tile.solid[s2]) {
                    lockDir = 3;
                }

                if ((Tile.solid[s] || Tile.solid[s2]) && !Tile.solid[n] && !Tile.solid[n2]) {
                    lockDir = 2;
                }

                return (face == lockDir ? this.tex + 16 : this.tex + 32) + offs;
            } else {
                return this.tex;
            }
        } else if (face != 2 && face != 3) {
            int offs = 0;
            if (n == this.id) {
                offs = -1;
            }

            int w2 = level.getTile(x.subtract(BigInteger.ONE), y, n == this.id ? z.subtract(BigInteger.ONE) : z.add(BigInteger.ONE));
            int e2 = level.getTile(x.add(BigInteger.ONE), y, n == this.id ? z.subtract(BigInteger.ONE) : z.add(BigInteger.ONE));
            if (face == 4) offs = -1 - offs;

            int lockDir = 5;
            if ((Tile.solid[w] || Tile.solid[w2]) && !Tile.solid[e] && !Tile.solid[e2]) lockDir = 5;
            if ((Tile.solid[e] || Tile.solid[e2]) && !Tile.solid[w] && !Tile.solid[w2]) lockDir = 4;

            return (face == lockDir ? this.tex + 16 : this.tex + 32) + offs;
        } else {
            return this.tex;
        }
    }

    @Override
    public boolean mayPlace(Level level, BigInteger x, int y, BigInteger z) {
        int chestCount = 0;
        BigInteger xMinusOne = x.subtract(BigInteger.ONE);
        BigInteger xPlusOne = x.add(BigInteger.ONE);
        BigInteger zMinusOne = z.subtract(BigInteger.ONE);
        BigInteger zPlusOne = z.add(BigInteger.ONE);
        if (level.getTile(xMinusOne, y, z) == this.id) {
            chestCount++;
        }

        if (level.getTile(xPlusOne, y, z) == this.id) {
            chestCount++;
        }

        if (level.getTile(x, y, zMinusOne) == this.id) {
            chestCount++;
        }

        if (level.getTile(x, y, zPlusOne) == this.id) {
            chestCount++;
        }

        if (chestCount > 1) {
            return false;
        } else if (isFullChest(level, xMinusOne, y, z)) {
            return false;
        } else if (isFullChest(level, xPlusOne, y, z)) {
            return false;
        } else {
            return isFullChest(level, x, y, zMinusOne) ? false : !isFullChest(level, x, y, zPlusOne);
        }
    }

    private boolean isFullChest(Level level, BigInteger x, int y, BigInteger z) {
        if (level.getTile(x, y, z) != this.id) return false;
        if (level.getTile(x.subtract(BigInteger.ONE), y, z) == this.id) return true;
        if (level.getTile(x.add(BigInteger.ONE), y, z) == this.id) return true;
        if (level.getTile(x, y, z.subtract(BigInteger.ONE)) == this.id) return true;
        if (level.getTile(x, y, z.add(BigInteger.ONE)) == this.id) return true;
        return false;
    }

    //? >=1.0.0-beta.8.0.r {
    /*@Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int type) {
        super.neighborChanged(level, x, y, z, type);
        ChestTileEntity chest = (ChestTileEntity)level.getTileEntity(x, y, z);
        if (chest != null) chest.clearCache();
    }
    *///? }

    @Override
    public void onRemove(Level level, BigInteger x, int y, BigInteger z) {
        Container container = (ChestTileEntity) level.getTileEntity(x, y, z);
        //? >=1.0.0-beta.8.0.r
        //if (container == null) return;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemInstance item = container.getItem(i);
            if (item != null) {
                float xo = this.random.nextFloat() * 0.8F + 0.1F;
                float yo = this.random.nextFloat() * 0.8F + 0.1F;
                float zo = this.random.nextFloat() * 0.8F + 0.1F;

                while (item.count > 0) {
                    int count = this.random.nextInt(21) + 10;
                    if (count > item.count) {
                        count = item.count;
                    }

                    item.count -= count;
                    ItemEntity itemEntity = new ItemEntity(level, x.doubleValue() + xo, y + yo, z.doubleValue() + zo, new ItemInstance(item.id, count, item.getAuxValue()));
                    float pow = 0.05F;
                    itemEntity.xd = (float) this.random.nextGaussian() * pow;
                    itemEntity.yd = (float) this.random.nextGaussian() * pow + 0.2F;
                    itemEntity.zd = (float) this.random.nextGaussian() * pow;
                    level.addEntity(itemEntity);
                }
            }
        }

        super.onRemove(level, x, y, z);
    }

    @Override
    public boolean use(Level level, BigInteger x, int y, BigInteger z, Player player) {
        Container container = (Container) level.getTileEntity(x, y, z);
        //? >=1.0.0-beta.8.0.r
        //if (container == null) return true;

        if (level.isSolidBlockingTile(x, y + 1, z)) return true;

        final BigInteger xMinusOne = x.subtract(BigInteger.ONE);
        final BigInteger xPlusOne = x.add(BigInteger.ONE);
        final BigInteger zMinusOne = z.subtract(BigInteger.ONE);
        final BigInteger zPlusOne = z.add(BigInteger.ONE);

        if (level.getTile(xMinusOne, y, z) == this.id && level.isSolidBlockingTile(xMinusOne, y + 1, z)) return true;
        if (level.getTile(xPlusOne, y, z) == this.id && level.isSolidBlockingTile(xPlusOne, y + 1, z)) return true;
        if (level.getTile(x, y, zMinusOne) == this.id && level.isSolidBlockingTile(x, y + 1, zMinusOne)) return true;
        if (level.getTile(x, y, zPlusOne) == this.id && level.isSolidBlockingTile(x, y + 1, zPlusOne)) return true;

        if (level.getTile(xMinusOne, y, z) == this.id) container = new CompoundContainer("Large chest", (ChestTileEntity) level.getTileEntity(xMinusOne, y, z), container);
        if (level.getTile(xPlusOne, y, z) == this.id) container = new CompoundContainer("Large chest", container, (ChestTileEntity) level.getTileEntity(xPlusOne, y, z));
        if (level.getTile(x, y, zMinusOne) == this.id) container = new CompoundContainer("Large chest", (ChestTileEntity) level.getTileEntity(x, y, zMinusOne), container);
        if (level.getTile(x, y, zPlusOne) == this.id) container = new CompoundContainer("Large chest", container, (ChestTileEntity) level.getTileEntity(x, y, zPlusOne));

        if (level.isClientSide) {
            return true;
        }

        player.openContainer(container);
        return true;
    }
}
