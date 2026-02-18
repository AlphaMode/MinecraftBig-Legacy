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
            BigInteger xt = x.add(BigInteger.valueOf(random.nextInt(4) - random.nextInt(4)));
            int yt = y;
            BigInteger zt = z.add(BigInteger.valueOf(random.nextInt(4) - random.nextInt(4)));
            if (level.isEmptyTile(xt, y, zt)
                    && (
                    level.getMaterial(xt.subtract(BigInteger.ONE), y - 1, zt) == Material.WATER
                            || level.getMaterial(xt.add(BigInteger.ONE), y - 1, zt) == Material.WATER
                            || level.getMaterial(xt, y - 1, zt.subtract(BigInteger.ONE)) == Material.WATER
                            || level.getMaterial(xt, y - 1, zt.add(BigInteger.ONE)) == Material.WATER
            )) {
                int var10 = 2 + random.nextInt(random.nextInt(3) + 1);

                for (int var11 = 0; var11 < var10; var11++) {
                    if (Tile.REEDS.canPlace(level, xt, yt + var11, zt)) {
                        level.setTileNoUpdate(xt, yt + var11, zt, Tile.REEDS.id);
                    }
                }
            }
        }

        return true;
    }
}
