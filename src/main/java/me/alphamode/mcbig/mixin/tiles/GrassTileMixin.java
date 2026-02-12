package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import net.minecraft.util.Facing;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.GrassTile;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;
import java.util.Random;

@Mixin(GrassTile.class)
public class GrassTileMixin implements BigTileExtension {
    @Override
    public int getTexture(LevelSource level, BigInteger x, int y, BigInteger z, int side) {
        if (side == Facing.UP) {
            return 0;
        } else if (side == Facing.DOWN) {
            return 2;
        } else {
            Material var6 = level.getMaterial(x, y + 1, z);
            return var6 != Material.TOP_SNOW && var6 != Material.SNOW ? 3 : 68;
        }
    }

    @Override
    public int getFoliageColor(LevelSource level, BigInteger x, int y, BigInteger z) {
        level.getBiomeSource().getBiomeBlock(x.intValue(), z.intValue(), 1, 1);
        double temp = level.getBiomeSource().temperatures[0];
        double downfall = level.getBiomeSource().downfalls[0];
        return GrassColor.get(temp, downfall);
    }

    @Override
    public void tick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        if (!level.isClientSide) {
            if (level.getLightLevel(x, y + 1, z) < 4 && Tile.lightBlock[level.getTile(x, y + 1, z)] > 2) {
                if (random.nextInt(4) != 0) {
                    return;
                }

                level.setTile(x, y, z, Tile.DIRT.id);
            } else if (level.getLightLevel(x, y + 1, z) >= 9) {
                BigInteger xt = x.add(BigInteger.valueOf(random.nextInt(3) - 1));
                int yt = y + random.nextInt(5) - 3;
                BigInteger zt = z.add(BigInteger.valueOf(random.nextInt(3) - 1));
                int tt = level.getTile(xt, yt + 1, zt);
                if (level.getTile(xt, yt, zt) == Tile.DIRT.id && level.getLightLevel(xt, yt + 1, zt) >= 4 && Tile.lightBlock[tt] <= 2) {
                    level.setTile(xt, yt, zt, Tile.GRASS.id);
                }
            }
        }
    }
}
