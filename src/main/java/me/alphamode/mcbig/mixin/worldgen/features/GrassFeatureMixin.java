package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.extensions.BigFeatureExtension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.GrassFeature;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(GrassFeature.class)
public class GrassFeatureMixin implements BigFeatureExtension {
    @Shadow
    private int tile;

    @Shadow
    private int meta;

    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        int tile = 0;

        while (((tile = level.getTile(x, y, z)) == 0 || tile == Tile.LEAVES.id) && y > 0) {
            y--;
        }

        for (int i = 0; i < 128; i++) {
            BigInteger xt = x.add(BigInteger.valueOf(random.nextInt(8) - random.nextInt(8)));
            int yt = y + random.nextInt(4) - random.nextInt(4);
            BigInteger zt = z.add(BigInteger.valueOf(random.nextInt(8) - random.nextInt(8)));
            if (level.isEmptyTile(xt, yt, zt) && Tile.tiles[this.tile].canPlace(level, xt, yt, zt)) {
                level.setTileAndDataNoUpdate(xt, yt, zt, this.tile, this.meta);
            }
        }

        return true;
    }
}
