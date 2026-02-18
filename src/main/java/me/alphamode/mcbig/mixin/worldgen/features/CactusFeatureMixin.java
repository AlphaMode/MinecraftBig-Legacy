package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.extensions.BigFeatureExtension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.CactusFeature;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;
import java.util.Random;

@Mixin(CactusFeature.class)
public class CactusFeatureMixin implements BigFeatureExtension {
    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        for (int i = 0; i < 10; i++) {
            BigInteger xt = x.add(BigInteger.valueOf(random.nextInt(8) - random.nextInt(8)));
            int yt = y + random.nextInt(4) - random.nextInt(4);
            BigInteger zt = z.add(BigInteger.valueOf(random.nextInt(8) - random.nextInt(8)));
            if (level.isEmptyTile(xt, yt, zt)) {
                int maxHeight = 1 + random.nextInt(random.nextInt(3) + 1);

                for (int height = 0; height < maxHeight; height++) {
                    if (Tile.CACTUS.canPlace(level, xt, yt + height, zt)) {
                        level.setTileNoUpdate(xt, yt + height, zt, Tile.CACTUS.id);
                    }
                }
            }
        }

        return true;
    }
}
