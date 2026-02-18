package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.extensions.BigFeatureExtension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.FlowerFeature;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(FlowerFeature.class)
public class FlowerFeatureMixin implements BigFeatureExtension {
    @Shadow
    private int tile;

    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        for (int i = 0; i < 64; i++) {
            BigInteger xt = x.add(BigInteger.valueOf(random.nextInt(8) - random.nextInt(8)));
            int yt = y + random.nextInt(4) - random.nextInt(4);
            BigInteger zt = z.add(BigInteger.valueOf(random.nextInt(8) - random.nextInt(8)));
            if (level.isEmptyTile(xt, yt, zt) && Tile.tiles[this.tile].canPlace(level, xt, yt, zt)) {
                level.setTileNoUpdate(xt, yt, zt, this.tile);
            }
        }

        return true;
    }
}
