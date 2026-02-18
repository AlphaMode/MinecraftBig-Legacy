package me.alphamode.mcbig.mixin.tiles;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Bush;
import net.minecraft.world.level.tile.MushroomTile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;
import java.util.Random;

@Mixin(MushroomTile.class)
public abstract class MushroomTileMixin extends Bush {
    protected MushroomTileMixin(int id, int texture) {
        super(id, texture);
    }

    @Override
    public void tick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        if (random.nextInt(100) == 0) {
            BigInteger xt = x.add(BigInteger.valueOf(random.nextInt(3) - 1));
            int yt = y + random.nextInt(2) - random.nextInt(2);
            BigInteger zt = z.add(BigInteger.valueOf(random.nextInt(3) - 1));
            if (level.isEmptyTile(xt, yt, zt) && this.canPlace(level, xt, yt, zt)) {
                x = x.add(BigInteger.valueOf(random.nextInt(3) - 1));
                z = z.add(BigInteger.valueOf(random.nextInt(3) - 1));
                if (level.isEmptyTile(xt, yt, zt) && this.canPlace(level, xt, yt, zt)) {
                    level.setTile(xt, yt, zt, this.id);
                }
            }
        }
    }

    @Override
    public boolean canPlace(Level level, BigInteger x, int y, BigInteger z) {
        return y >= 0 && y < 128 ? level.getRawBrightness(x, y, z) < 13 && this.mayPlaceOn(level.getTile(x, y - 1, z)) : false;
    }
}
