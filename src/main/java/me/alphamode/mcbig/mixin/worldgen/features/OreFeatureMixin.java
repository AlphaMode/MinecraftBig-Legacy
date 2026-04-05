package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.extensions.BigFeatureExtension;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.math.BigMath;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Random;

@Mixin(OreFeature.class)
public class OreFeatureMixin implements BigFeatureExtension {
    @Shadow
    private int count;

    @Shadow
    private int tile;

    private BigDecimal bigCount;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setBigCount(int tile, int count, CallbackInfo ci) {
        this.bigCount = new BigDecimal(this.count, MathContext.DECIMAL32);
    }

    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {

        float dir = random.nextFloat() * (float) Math.PI;

        BigDecimal x0 = new BigDecimal(x.add(BigConstants.EIGHT), MathContext.DECIMAL64).add(BigDecimal.valueOf(Mth.sin(dir) * this.count / 8));
        BigDecimal x1 = new BigDecimal(x.add(BigConstants.EIGHT), MathContext.DECIMAL64).subtract(BigDecimal.valueOf(Mth.sin(dir) * this.count / 8));
        BigDecimal z0 = new BigDecimal(z.add(BigConstants.EIGHT), MathContext.DECIMAL64).add(BigDecimal.valueOf(Mth.cos(dir) * this.count / 8));
        BigDecimal z1 = new BigDecimal(z.add(BigConstants.EIGHT), MathContext.DECIMAL64).subtract(BigDecimal.valueOf(Mth.cos(dir) * this.count / 8));

        double y0 = y + random.nextInt(3) + 2;
        double y1 = y + random.nextInt(3) + 2;

        for (int d = 0; d <= this.count; d++) {
            BigDecimal D = new BigDecimal(d, MathContext.DECIMAL64);
            BigDecimal xx = x0.add((x1.subtract(x0, MathContext.DECIMAL64)).multiply(D, MathContext.DECIMAL64).divide(this.bigCount, MathContext.DECIMAL64), MathContext.DECIMAL64);
            double yy = y0 + (y1 - y0) * d / this.count;
            BigDecimal zz = z0.add((z1.subtract(z0, MathContext.DECIMAL64)).multiply(D, MathContext.DECIMAL64).divide(this.bigCount, MathContext.DECIMAL64), MathContext.DECIMAL64);

            double ss = random.nextDouble() * this.count / 16;
            double r = (Mth.sin(d * (float) Math.PI / this.count) + 1.0F) * ss + 1;
            double hr = (Mth.sin(d * (float) Math.PI / this.count) + 1.0F) * ss + 1;

            BigInteger xt0 = BigMath.floor(xx.subtract(BigDecimal.valueOf(r / 2)));
            int yt0 = Mth.floor(yy - hr / 2.0);
            BigInteger zt0 = BigMath.floor(zz.subtract(BigDecimal.valueOf(r / 2)));

            BigInteger xt1 = BigMath.floor(xx.add(BigDecimal.valueOf(r / 2)));
            int yt1 = Mth.floor(yy + hr / 2.0);
            BigInteger zt1 = BigMath.floor(zz.add(BigDecimal.valueOf(r / 2)));

            for (BigInteger x2 = xt0; x2.compareTo(xt1) <= 0; x2 = x2.add(BigInteger.ONE)) {
                double xd = (new BigDecimal(x2, MathContext.DECIMAL64).add(BigConstants.POINT_FIVE, MathContext.DECIMAL64).subtract(xx)).divide(BigDecimal.valueOf(r / 2.0), MathContext.DECIMAL64).doubleValue();
                if (xd * xd < 1) {
                    for (int y2 = yt0; y2 <= yt1; y2++) {
                        double yd = (y2 + 0.5 - yy) / (hr / 2);
                        if (xd * xd + yd * yd < 1) {
                            for (BigInteger z2 = zt0; z2.compareTo(zt1) <= 0; z2 = z2.add(BigInteger.ONE)) {
                                double zd = (new BigDecimal(z2, MathContext.DECIMAL64).add(BigConstants.POINT_FIVE).subtract(zz, MathContext.DECIMAL64)).divide(BigDecimal.valueOf(r / 2.0), MathContext.DECIMAL64).doubleValue();
                                if (xd * xd + yd * yd + zd * zd < 1 && level.getTile(x2, y2, z2) == Tile.stone.id) {
                                    level.setTileNoUpdate(x2, y2, z2, this.tile);
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
