package me.alphamode.mcbig.mixin.tiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Facing;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.FurnaceTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.TileEntityTile;
import net.minecraft.world.level.tile.entity.FurnaceTileEntity;
import net.minecraft.world.level.tile.entity.TileEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(FurnaceTile.class)
public abstract class FurnaceTileMixin extends TileEntityTile {
    @Shadow
    @Final
    private boolean isLit;

    @Shadow
    private static boolean updating;

    @Shadow
    private Random random;

    protected FurnaceTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public void onPlace(Level level, BigInteger x, int y, BigInteger z) {
        super.onPlace(level, x, y, z);
        this.recalcLockDir(level, x, y, z);
    }

    private void recalcLockDir(Level level, BigInteger x, int y, BigInteger z) {
        if (level.isClientSide) return;

        int n = level.getTile(x, y, z.subtract(BigInteger.ONE));
        int s = level.getTile(x, y, z.add(BigInteger.ONE));
        int w = level.getTile(x.subtract(BigInteger.ONE), y, z);
        int e = level.getTile(x.add(BigInteger.ONE), y, z);

        int lockDir = 3;
        if (Tile.solid[n] && !Tile.solid[s]) lockDir = 3;
        if (Tile.solid[s] && !Tile.solid[n]) lockDir = 2;
        if (Tile.solid[w] && !Tile.solid[e]) lockDir = 5;
        if (Tile.solid[e] && !Tile.solid[w]) lockDir = 4;

        level.setData(x, y, z, lockDir);
    }

    @Override
    public int getTexture(LevelSource level, BigInteger x, int y, BigInteger z, int face) {
        if (face == 1) return this.tex + 17;
        if (face == 0) return this.tex + 17;

        int lockDir = level.getData(x, y, z);
        if (face != lockDir) {
            return this.tex;
        } else {
            return this.isLit ? this.tex + 16 : this.tex - 1;
        }
    }

    @Override
    public void animateTick(Level level, BigInteger xt, int yt, BigInteger zt, Random random) {
        if (!this.isLit) return;

        int dir = level.getData(xt, yt, zt);

        float x = xt.floatValue() + 0.5F;
        float y = yt + 0.0F + random.nextFloat() * 6.0F / 16.0F;
        float z = zt.floatValue() + 0.5F;
        float r = 0.52F;
        float ss = random.nextFloat() * 0.6F - 0.3F;

        if (dir == 4) {
            level.addParticle("smoke", x - r, y, z + ss, 0.0, 0.0, 0.0);
            level.addParticle("flame", x - r, y, z + ss, 0.0, 0.0, 0.0);
        } else if (dir == 5) {
            level.addParticle("smoke", x + r, y, z + ss, 0.0, 0.0, 0.0);
            level.addParticle("flame", x + r, y, z + ss, 0.0, 0.0, 0.0);
        } else if (dir == 2) {
            level.addParticle("smoke", x + ss, y, z - r, 0.0, 0.0, 0.0);
            level.addParticle("flame", x + ss, y, z - r, 0.0, 0.0, 0.0);
        } else if (dir == 3) {
            level.addParticle("smoke", x + ss, y, z + r, 0.0, 0.0, 0.0);
            level.addParticle("flame", x + ss, y, z + r, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public boolean use(Level level, BigInteger x, int y, BigInteger z, Player player) {
        if (level.isClientSide)
            return true;

        FurnaceTileEntity furnace = (FurnaceTileEntity) level.getTileEntity(x, y, z);
        if (furnace != null)
            player.openFurnace(furnace);

        return true;
    }

    private static void setLit(boolean lit, Level level, BigInteger x, int y, BigInteger z) {
        int data = level.getData(x, y, z);
        TileEntity te = level.getTileEntity(x, y, z);

        updating = true;
        if (lit) level.setTile(x, y, z, Tile.furnace_lit.id);
        else level.setTile(x, y, z, Tile.furnace.id);
        updating = false;

        level.setData(x, y, z, data);

        if (te != null) {
            te.clearRemoved();
            level.setTileEntity(x, y, z, te);
        }
    }

    @Override
    public TileEntity newTileEntity() {
        return new FurnaceTileEntity();
    }

    @Override
    public void setPlacedBy(Level level, BigInteger x, int y, BigInteger z, Mob entity) {
        int dir = Mth.floor(entity.yRot * 4.0F / 360.0F + 0.5) & 3;

        if (dir == 0) level.setData(x, y, z, 2);
        if (dir == 1) level.setData(x, y, z, 5);
        if (dir == 2) level.setData(x, y, z, 3);
        if (dir == 3) level.setData(x, y, z, 4);
    }

    @Override
    public void onRemove(Level level, BigInteger x, int y, BigInteger z) {
        if (!updating) {
            FurnaceTileEntity container = (FurnaceTileEntity) level.getTileEntity(x, y, z);
            if (container != null) {
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
                            itemEntity.xd = (float)this.random.nextGaussian() * pow;
                            itemEntity.yd = (float)this.random.nextGaussian() * pow + 0.2F;
                            itemEntity.zd = (float)this.random.nextGaussian() * pow;
                            level.addEntity(itemEntity);
                        }
                    }
                }
            }
        }

        super.onRemove(level, x, y, z);
    }
}
