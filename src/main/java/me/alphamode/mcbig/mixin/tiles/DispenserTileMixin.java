package me.alphamode.mcbig.mixin.tiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Facing;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.DispenserTile;
import net.minecraft.world.level.tile.LevelEvent;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.TileEntityTile;
import net.minecraft.world.level.tile.entity.DispenserTileEntity;
import net.minecraft.world.level.tile.entity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(DispenserTile.class)
public abstract class DispenserTileMixin extends TileEntityTile {
    @Shadow
    private Random random;

    protected DispenserTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public void onPlace(Level level, BigInteger x, int y, BigInteger z) {
        super.onPlace(level, x, y, z);
        recalcLockDir(level, x, y, z);
    }

    private void recalcLockDir(Level level, BigInteger x, int y, BigInteger z) {
        if (level.isClientSide) {
            return;
        }

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
        if (face == Facing.UP) {
            return this.tex + 17;
        } else if (face == Facing.DOWN) {
            return this.tex + 17;
        } else {
            int data = level.getData(x, y, z);
            return face != data ? this.tex : this.tex + 1;
        }
    }

    @Override
    public boolean use(Level level, BigInteger x, int y, BigInteger z, Player player) {
        if (level.isClientSide) {
            return true;
        }

        DispenserTileEntity trap = (DispenserTileEntity) level.getTileEntity(x, y, z);
        //? >=1.0.0-beta.8.0.r
        //if (trap != null)
            player.openDispenser(trap);

        return true;

    }

    private void dispenseFrom(Level level, BigInteger x, int y, BigInteger z, Random random) {
        int data = level.getData(x, y, z);
        int xo = 0;
        int zo = 0;
        if (data == 3) {
            zo = 1;
        } else if (data == 2) {
            zo = -1;
        } else if (data == 5) {
            xo = 1;
        } else {
            xo = -1;
        }

        DispenserTileEntity trap = (DispenserTileEntity)level.getTileEntity(x, y, z);
        //? >=1.0.0-beta.8.0.r
        //if (trap != null) {
            ItemInstance item = trap.removeRandomItem();
            double xx = x.doubleValue() + xo * 0.6 + 0.5;
            double yy = y + 0.5;
            double zz = z.doubleValue() + zo * 0.6 + 0.5;
            if (item == null) {
                level.levelEvent(LevelEvent.SOUND_DISPENSER_FAIL, x, y, z, 0);
            } else {
                if (item.id == Item.arrow.id) {
                    Arrow arrow = new Arrow(level, xx, yy, zz);
                    arrow.shoot(xo, 0.1F, zo, 1.1F, 6.0F);
                    arrow.player = true;
                    level.addEntity(arrow);
                    level.levelEvent(LevelEvent.SOUND_DISPENSER_PROJECTILE_LAUNCH, x, y, z, 0);
                } else if (item.id == Item.egg.id) {
                    ThrownEgg egg = new ThrownEgg(level, xx, yy, zz);
                    egg.shoot(xo, 0.1F, zo, 1.1F, 6.0F);
                    level.addEntity(egg);
                    level.levelEvent(LevelEvent.SOUND_DISPENSER_PROJECTILE_LAUNCH, x, y, z, 0);
                } else if (item.id == Item.snowBall.id) {
                    Snowball snowball = new Snowball(level, xx, yy, zz);
                    snowball.shoot(xo, 0.1F, zo, 1.1F, 6.0F);
                    level.addEntity(snowball);
                    level.levelEvent(LevelEvent.SOUND_DISPENSER_PROJECTILE_LAUNCH, x, y, z, 0);
                } else {
                    ItemEntity itemEntity = new ItemEntity(level, xx, yy - 0.3, zz, item);
                    double p = random.nextDouble() * 0.1 + 0.2;
                    itemEntity.xd = xo * p;
                    itemEntity.yd = 0.2F;
                    itemEntity.zd = zo * p;
                    itemEntity.xd = itemEntity.xd + random.nextGaussian() * 0.0075F * 6.0;
                    itemEntity.yd = itemEntity.yd + random.nextGaussian() * 0.0075F * 6.0;
                    itemEntity.zd = itemEntity.zd + random.nextGaussian() * 0.0075F * 6.0;
                    level.addEntity(itemEntity);
                    level.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, x, y, z, 0);
                }

                level.levelEvent(LevelEvent.PARTICLES_SHOOT_SMOKE, x, y, z, xo + 1 + (zo + 1) * 3);
            }
        //? >=1.0.0-beta.8.0.r
        //}
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int type) {
        if (type > 0 && Tile.tiles[type].isSignalSource()) {
            boolean signal = level.hasNeighborSignal(x, y, z) || level.hasNeighborSignal(x, y + 1, z);
            if (signal) {
                level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
            }
        }
    }

    @Override
    public void tick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        if (level.hasNeighborSignal(x, y, z) || level.hasNeighborSignal(x, y + 1, z)) {
            this.dispenseFrom(level, x, y, z, random);
        }
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
        DispenserTileEntity trap = (DispenserTileEntity) level.getTileEntity(x, y, z);
        //? >=1.0.0-beta.8.0.r
        //if (trap != null) {
            for (int i = 0; i < trap.getContainerSize(); i++) {
                ItemInstance item = trap.getItem(i);
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
        //? >=1.0.0-beta.8.0.r
        //}

        super.onRemove(level, x, y, z);
    }
}
