//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.mixin.worldgen.newbiome;

import me.alphamode.mcbig.math.BigMath;
import net.minecraft.util.IntCache;
import net.minecraft.world.level.newbiome.layer.FuzzyZoomLayer;
import net.minecraft.world.level.newbiome.layer.Layer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(FuzzyZoomLayer.class)
public abstract class FuzzyZoomLayerMixin extends Layer {
    @Shadow
    protected abstract int random(int a, int b);

    @Shadow
    protected abstract int random(int a, int b, int c, int d);

    public FuzzyZoomLayerMixin(long seedMixup) {
        super(seedMixup);
    }

    @Override
    public int[] getArea(BigInteger xo, BigInteger yo, int w, int h) {
        var px = xo.shiftRight(1);
        var py = yo.shiftRight(1);
        int pw = (w >> 1) + 3;
        int ph = (h >> 1) + 3;
        int[] p = this.parent.getArea(px, py, pw, ph);

        int[] tmp = IntCache.allocate(pw * 2 * ph * 2);
        int ww = pw << 1;
        for (int y = 0; y < ph - 1; y++) {
            int ry = y << 1;
            int pp = ry * ww;
            int ul = p[0 + (y + 0) * pw];
            int dl = p[0 + (y + 1) * pw];
            for (int x = 0; x < pw - 1; x++) {
                initRandom(x + px.longValue() << 1, y + py.longValue() << 1);
                int ur = p[x + 1 + (y + 0) * pw];
                int dr = p[x + 1 + (y + 1) * pw];
                tmp[pp] = ul;
                tmp[pp++ + ww] = random(ul, dl);
                tmp[pp] = random(ul, ur);
                tmp[pp++ + ww] = random(ul, ur, dl, dr);
                ul = ur;
                dl = dr;
            }
        }
        int[] result = IntCache.allocate(w * h);
        for (int y = 0; y < h; y++) {
            System.arraycopy(tmp, (y + (BigMath.fastAnd(yo, 1))) * (pw << 1) + (BigMath.fastAnd(xo, 1)), result, y * w, w);
        }

        return result;
    }
}
*///? }