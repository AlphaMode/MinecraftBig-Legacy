package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.tiles.BigRailTileExtension;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.Minecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.tile.DetectorRailTile;
import net.minecraft.world.level.tile.RailTile;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

@Mixin(DetectorRailTile.class)
public abstract class DetectorRailTileMixin extends RailTile implements BigRailTileExtension {
    protected DetectorRailTileMixin(int id, int tex, boolean isStraight) {
        super(id, tex, isStraight);
    }

    @Override
    public void entityInside(Level level, BigInteger x, int y, BigInteger z, Entity entity) {
        if (!level.isClientSide) {
            int data = level.getData(x, y, z);
            if ((data & 8) == 0) {
                this.checkPressed(level, x, y, z, data);
            }
        }
    }

    @Override
    public void tick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        if (!level.isClientSide) {
            int data = level.getData(x, y, z);
            if ((data & 8) != 0) {
                this.checkPressed(level, x, y, z, data);
            }
        }
    }

    @Override
    public boolean getSignal(LevelSource levelReader, BigInteger x, int y, BigInteger z, int direction) {
        return (levelReader.getData(x, y, z) & 8) != 0;
    }

    @Override
    public boolean getDirectSignal(Level level, BigInteger x, int y, BigInteger z, int direction) {
        return (level.getData(x, y, z) & 8) == 0 ? false : direction == 1;
    }

    private void checkPressed(Level level, BigInteger x, int y, BigInteger z, int meta) {
        boolean wasPressed = (meta & 8) != 0;
        boolean shouldBePressed = false;

        float b = 2 / 16.0f;
        List<Minecart> entities = level.getEntitiesOfClass(Minecart.class, AABB.newTemp(x.doubleValue() + b, y, z.doubleValue() + b, x.add(BigInteger.ONE).doubleValue() - b, y + 0.25, z.add(BigInteger.ONE).doubleValue() - b));
        if (entities.size() > 0) {
            shouldBePressed = true;
        }

        if (shouldBePressed && !wasPressed) {
            level.setData(x, y, z, meta | 8);
            level.updateNeighborsAt(x, y, z, this.id);
            level.updateNeighborsAt(x, y - 1, z, this.id);
            level.setTilesDirty(x, y, z, x, y, z);
        }

        if (!shouldBePressed && wasPressed) {
            level.setData(x, y, z, meta & 7);
            level.updateNeighborsAt(x, y, z, this.id);
            level.updateNeighborsAt(x, y - 1, z, this.id);
            level.setTilesDirty(x, y, z, x, y, z);
        }

        if (shouldBePressed) {
            level.addToTickNextTick(x, y, z, this.id, this.getTickDelay());
        }
    }
}
