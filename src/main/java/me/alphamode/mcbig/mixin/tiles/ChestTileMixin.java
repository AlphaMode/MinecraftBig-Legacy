package me.alphamode.mcbig.mixin.tiles;

import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.ItemInstance;
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

    @Override
    public void onRemove(Level level, BigInteger x, int y, BigInteger z) {
        Container container = (ChestTileEntity) level.getTileEntity(x, y, z);

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
