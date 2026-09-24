//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.mixin.worldgen.newbiome;

import net.minecraft.util.IntCache;
import net.minecraft.world.level.newbiome.layer.IslandLayer;
import net.minecraft.world.level.newbiome.layer.Layer;
import org.spongepowered.asm.mixin.Mixin;import java.math.BigInteger;

@Mixin(IslandLayer.class)
public abstract class IslandLayerMixin extends Layer {
    public IslandLayerMixin(long seedMixup) {
        super(seedMixup);
    }

    @Override
    public int[] getArea(BigInteger xo, BigInteger yo, int w, int h) {
        int[] result = IntCache.allocate(w * h);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                initRandom(xo.longValue() + x, yo.longValue() + y);
                result[x + y * w] = nextRandom(10) == 0 ? 1 : 0;
            }
        }

        // if (0, 0) is located here, place an island
        int _xo = xo.intValue();
        int _yo = yo.intValue();
        if (_xo > -w && _xo <= 0 && _yo > -h && _yo <= 0) {
            result[-_xo + -_yo * w] = 1;
        }

        return result;
    }
}
*///? }