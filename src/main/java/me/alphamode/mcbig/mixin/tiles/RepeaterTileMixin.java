package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import net.minecraft.util.Facing;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.RepeaterTile;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(RepeaterTile.class)
public abstract class RepeaterTileMixin extends Tile implements BigTileExtension {

    @Shadow
    @Final
    private boolean isOn;

    @Shadow
    @Final
    private static int[] DELAYS;

    @Shadow
    @Final
    public static double[] PARTICLE_OFFSETS;

    protected RepeaterTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public boolean mayPlace(Level level, BigInteger x, int y, BigInteger z) {
        return !level.isSolidBlockingTile(x, y - 1, z) ? false : super.mayPlace(level, x, y, z);
    }

    @Override
    public boolean canPlace(Level level, BigInteger x, int y, BigInteger z) {
        return !level.isSolidBlockingTile(x, y - 1, z) ? false : super.canPlace(level, x, y, z);
    }

    @Override
    public void tick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        int var6 = level.getData(x, y, z);
        boolean var7 = this.shouldTurnOn(level, x, y, z, var6);
        if (this.isOn && !var7) {
            level.setTileAndData(x, y, z, Tile.REPEATER_OFF.id, var6);
        } else if (!this.isOn) {
            level.setTileAndData(x, y, z, Tile.REPEATER_ON.id, var6);
            if (!var7) {
                int var8 = (var6 & 12) >> 2;
                level.addToTickNextTick(x, y, z, Tile.REPEATER_ON.id, DELAYS[var8] * 2);
            }
        }
    }

    @Override
    public boolean shouldRenderFace(LevelSource level, BigInteger x, int y, BigInteger z, int face) {
        return face != 0 && face != 1;
    }

    @Override
    public boolean getDirectSignal(Level level, BigInteger x, int y, BigInteger z, int direction) {
        return this.getSignal(level, x, y, z, direction);
    }

    @Override
    public boolean getSignal(LevelSource levelReader, BigInteger x, int y, BigInteger z, int facing) {
        if (!this.isOn) {
            return false;
        } else {
            int data = levelReader.getData(x, y, z) & 3;
            if (data == 0 && facing == Facing.SOUTH) {
                return true;
            } else if (data == 1 && facing == Facing.WEST) {
                return true;
            } else {
                return data == 2 && facing == Facing.NORTH ? true : data == 3 && facing == Facing.EAST;
            }
        }
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int tile) {
        if (!this.canPlace(level, x, y, z)) {
            this.dropResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
        } else {
            int var6 = level.getData(x, y, z);
            boolean var7 = this.shouldTurnOn(level, x, y, z, var6);
            int var8 = (var6 & 12) >> 2;
            if (this.isOn && !var7) {
                level.addToTickNextTick(x, y, z, this.id, DELAYS[var8] * 2);
            } else if (!this.isOn && var7) {
                level.addToTickNextTick(x, y, z, this.id, DELAYS[var8] * 2);
            }
        }
    }

    private boolean shouldTurnOn(Level level, BigInteger x, int y, BigInteger z, int data) {
        int delay = data & 3;
        switch (delay) {
            case 0:
                return level.getSignal(x, y, z.add(BigInteger.ONE), Facing.SOUTH) || level.getTile(x, y, z.add(BigInteger.ONE)) == Tile.REDSTONE.id && level.getData(x, y, z.add(BigInteger.ONE)) > 0;
            case 1:
                return level.getSignal(x.subtract(BigInteger.ONE), y, z, Facing.WEST) || level.getTile(x.subtract(BigInteger.ONE), y, z) == Tile.REDSTONE.id && level.getData(x.subtract(BigInteger.ONE), y, z) > 0;
            case 2:
                return level.getSignal(x, y, z.subtract(BigInteger.ONE), Facing.NORTH) || level.getTile(x, y, z.subtract(BigInteger.ONE)) == Tile.REDSTONE.id && level.getData(x, y, z.subtract(BigInteger.ONE)) > 0;
            case 3:
                return level.getSignal(x.add(BigInteger.ONE), y, z, Facing.EAST) || level.getTile(x.add(BigInteger.ONE), y, z) == Tile.REDSTONE.id && level.getData(x.add(BigInteger.ONE), y, z) > 0;
            default:
                return false;
        }
    }

    @Override
    public boolean use(Level level, int x, int y, int z, Player player) {
        int data = level.getData(x, y, z);
        int delay = (data & 12) >> 2;
        delay = delay + 1 << 2 & 12;
        level.setData(x, y, z, delay | data & 3);
        return true;
    }

    @Override
    public boolean isSignalSource() {
        return false;
    }

    @Override
    public void setPlacedBy(Level level, BigInteger x, int y, BigInteger z, Mob entity) {
        int var6 = ((Mth.floor(entity.yRot * 4.0F / 360.0F + 0.5) & 3) + 2) % 4;
        level.setData(x, y, z, var6);
        boolean var7 = this.shouldTurnOn(level, x, y, z, var6);
        if (var7) {
            level.addToTickNextTick(x, y, z, this.id, 1);
        }
    }

    @Override
    public void onPlace(Level level, BigInteger x, int y, BigInteger z) {
        level.updateNeighborsAt(x.add(BigInteger.ONE), y, z, this.id);
        level.updateNeighborsAt(x.subtract(BigInteger.ONE), y, z, this.id);
        level.updateNeighborsAt(x, y, z.add(BigInteger.ONE), this.id);
        level.updateNeighborsAt(x, y, z.subtract(BigInteger.ONE), this.id);
        level.updateNeighborsAt(x, y - 1, z, this.id);
        level.updateNeighborsAt(x, y + 1, z, this.id);
    }

    @Override
    public void animateTick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        if (this.isOn) {
            int var6 = level.getData(x, y, z);
            double var7 = x.doubleValue() + 0.5F + (random.nextFloat() - 0.5F) * 0.2;
            double var9 = y + 0.4F + (random.nextFloat() - 0.5F) * 0.2;
            double var11 = z.doubleValue() + 0.5F + (random.nextFloat() - 0.5F) * 0.2;
            double var13 = 0.0;
            double var15 = 0.0;
            if (random.nextInt(2) == 0) {
                switch (var6 & 3) {
                    case 0:
                        var15 = -0.3125;
                        break;
                    case 1:
                        var13 = 0.3125;
                        break;
                    case 2:
                        var15 = 0.3125;
                        break;
                    case 3:
                        var13 = -0.3125;
                }
            } else {
                int var17 = (var6 & 12) >> 2;
                switch (var6 & 3) {
                    case 0:
                        var15 = PARTICLE_OFFSETS[var17];
                        break;
                    case 1:
                        var13 = -PARTICLE_OFFSETS[var17];
                        break;
                    case 2:
                        var15 = -PARTICLE_OFFSETS[var17];
                        break;
                    case 3:
                        var13 = PARTICLE_OFFSETS[var17];
                }
            }

            level.addParticle("reddust", var7 + var13, var9, var11 + var15, 0.0, 0.0, 0.0);
        }
    }
}
