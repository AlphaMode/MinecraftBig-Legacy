package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.extensions.BigFeatureExtension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.BushFeature;
import net.minecraft.world.level.tile.Bush;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(BushFeature.class)
public class BushFeatureMixin implements BigFeatureExtension {
    @Shadow
    private int tile;

    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        int var6 = 0;

        while (((var6 = level.getTile(x, y, z)) == 0 || var6 == Tile.LEAVES.id) && y > 0) {
            y--;
        }

        for (int var7 = 0; var7 < 4; var7++) {
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
