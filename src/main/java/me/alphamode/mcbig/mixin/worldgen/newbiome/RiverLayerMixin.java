//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.mixin.worldgen.newbiome;

import net.minecraft.util.IntCache;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.layer.Layer;
import net.minecraft.world.level.newbiome.layer.RiverLayer;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(RiverLayer.class)
public abstract class RiverLayerMixin extends Layer {
    public RiverLayerMixin(long seedMixup) {
        super(seedMixup);
    }

    @Override
    public int[] getArea(BigInteger xo, BigInteger yo, int w, int h) {
        var px = xo.subtract(BigInteger.ONE);
        var py = yo.subtract(BigInteger.ONE);
        int pw = w + 2;
        int ph = h + 2;
        int[] p = this.parent.getArea(px, py, pw, ph);
        int[] result = IntCache.allocate(w * h);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int l = p[x + 0 + (y + 1) * pw];
                int r = p[x + 2 + (y + 1) * pw];
                int u = p[x + 1 + (y + 0) * pw];
                int d = p[x + 1 + (y + 2) * pw];
                int c = p[x + 1 + (y + 1) * pw];
                if (c == 0 || l == 0 || r == 0 || u == 0 || d == 0) {
                    result[x + y * w] = Biome.river.id;
                } else if (c == l && c == u && c == r && c == d) {
                    result[x + y * w] = -1;
                } else {
                    result[x + y * w] = Biome.river.id;
                }
            }
        }

        return result;
    }
}
*///? }