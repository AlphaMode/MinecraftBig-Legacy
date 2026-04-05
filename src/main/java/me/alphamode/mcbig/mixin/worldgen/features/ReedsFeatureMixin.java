package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.extensions.BigFeatureExtension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ReedsFeature;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;
import java.util.Random;

@Mixin(ReedsFeature.class)
public class ReedsFeatureMixin implements BigFeatureExtension {
    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        for (int i = 0; i < 20; i++) {
            BigInteger x2 = x.add(BigInteger.valueOf(random.nextInt(4) - random.nextInt(4)));
            int y2 = y;
            BigInteger z2 = z.add(BigInteger.valueOf(random.nextInt(4) - random.nextInt(4)));
            if (level.isEmptyTile(x2, y, z2)
                    && (
                    level.getMaterial(x2.subtract(BigInteger.ONE), y - 1, z2) == Material.water
                            || level.getMaterial(x2.add(BigInteger.ONE), y - 1, z2) == Material.water
                            || level.getMaterial(x2, y - 1, z2.subtract(BigInteger.ONE)) == Material.water
                            || level.getMaterial(x2, y - 1, z2.add(BigInteger.ONE)) == Material.water
            )) {
                int h = 2 + random.nextInt(random.nextInt(3) + 1);

                for (int yy = 0; yy < h; yy++) {
                    if (Tile.reeds.canPlace(level, x2, y2 + yy, z2)) {
                        level.setTileNoUpdate(x2, y2 + yy, z2, Tile.reeds.id);
                    }
                }
            }
        }

        return true;
    }
}
