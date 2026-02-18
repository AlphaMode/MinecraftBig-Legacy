package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.extensions.BigFeatureExtension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.PumpkinFeature;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;
import java.util.Random;

@Mixin(PumpkinFeature.class)
public class PumpkinFeatureMixin implements BigFeatureExtension {
    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        for (int i = 0; i < 64; i++) {
            BigInteger xt = x.add(BigInteger.valueOf(random.nextInt(8) - random.nextInt(8)));
            int yt = y + random.nextInt(4) - random.nextInt(4);
            BigInteger zt = z.add(BigInteger.valueOf(random.nextInt(8) - random.nextInt(8)));
            if (level.isEmptyTile(xt, yt, zt) && level.getTile(xt, yt - 1, zt) == Tile.GRASS.id && Tile.PUMPKIN.mayPlace(level, xt, yt, zt)) {
                level.setTileAndDataNoUpdate(xt, yt, zt, Tile.PUMPKIN.id, random.nextInt(4));
            }
        }

        return true;
    }
}
