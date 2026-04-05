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
            BigInteger x2 = x.add(BigInteger.valueOf(random.nextInt(8) - random.nextInt(8)));
            int y2 = y + random.nextInt(4) - random.nextInt(4);
            BigInteger z2 = z.add(BigInteger.valueOf(random.nextInt(8) - random.nextInt(8)));
            if (level.isEmptyTile(x2, y2, z2)) {
                int h = 1 + random.nextInt(random.nextInt(3) + 1);

                for (int yy = 0; yy < h; yy++) {
                    if (Tile.cactus.canPlace(level, x2, y2 + yy, z2)) {
                        level.setTileNoUpdate(x2, y2 + yy, z2, Tile.cactus.id);
                    }
                }
            }
        }

        return true;
    }
}
