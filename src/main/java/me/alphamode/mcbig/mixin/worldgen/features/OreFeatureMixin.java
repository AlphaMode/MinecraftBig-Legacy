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
        float var6 = random.nextFloat() * (float) Math.PI;
        BigDecimal var7 = new BigDecimal(x.add(BigConstants.EIGHT), MathContext.DECIMAL64).add(BigDecimal.valueOf(Mth.sin(var6) * this.count / 8.0F));
        BigDecimal var9 = new BigDecimal(x.add(BigConstants.EIGHT), MathContext.DECIMAL64).subtract(BigDecimal.valueOf(Mth.sin(var6) * this.count / 8.0F));
        BigDecimal var11 = new BigDecimal(z.add(BigConstants.EIGHT), MathContext.DECIMAL64).add(BigDecimal.valueOf(Mth.cos(var6) * this.count / 8.0F));
        BigDecimal var13 = new BigDecimal(z.add(BigConstants.EIGHT), MathContext.DECIMAL64).subtract(BigDecimal.valueOf(Mth.cos(var6) * this.count / 8.0F));
        double var15 = y + random.nextInt(3) + 2;
        double var17 = y + random.nextInt(3) + 2;

        for (int i = 0; i <= this.count; i++) {
            BigDecimal bigI = new BigDecimal(i, MathContext.DECIMAL64);
            BigDecimal var20 = var7.add((var9.subtract(var7, MathContext.DECIMAL64)).multiply(bigI, MathContext.DECIMAL64).divide(this.bigCount, MathContext.DECIMAL64), MathContext.DECIMAL64);
            double var22 = var15 + (var17 - var15) * i / this.count;
            BigDecimal var24 = var11.add((var13.subtract(var11, MathContext.DECIMAL64)).multiply(bigI, MathContext.DECIMAL64).divide(this.bigCount, MathContext.DECIMAL64), MathContext.DECIMAL64);
            double var26 = random.nextDouble() * this.count / 16.0;
            double var28 = (Mth.sin(i * (float) Math.PI / this.count) + 1.0F) * var26 + 1.0;
            double var30 = (Mth.sin(i * (float) Math.PI / this.count) + 1.0F) * var26 + 1.0;
            BigInteger x0 = BigMath.floor(var20.subtract(BigDecimal.valueOf(var28 / 2.0)));
            int y0 = Mth.floor(var22 - var30 / 2.0);
            BigInteger z0 = BigMath.floor(var24.subtract(BigDecimal.valueOf(var28 / 2.0)));
            BigInteger x1 = BigMath.floor(var20.add(BigDecimal.valueOf(var28 / 2.0)));
            int y1 = Mth.floor(var22 + var30 / 2.0);
            BigInteger z1 = BigMath.floor(var24.add(BigDecimal.valueOf(var28 / 2.0)));

            for (BigInteger xt = x0; xt.compareTo(x1) <= 0; xt = xt.add(BigInteger.ONE)) {
                double var39 = (new BigDecimal(xt, MathContext.DECIMAL64).add(BigConstants.POINT_FIVE, MathContext.DECIMAL64).subtract(var20)).divide(BigDecimal.valueOf(var28 / 2.0), MathContext.DECIMAL64).doubleValue();
                if (var39 * var39 < 1.0) {
                    for (int yt = y0; yt <= y1; yt++) {
                        double var42 = (yt + 0.5 - var22) / (var30 / 2.0);
                        if (var39 * var39 + var42 * var42 < 1.0) {
                            for (BigInteger zt = z0; zt.compareTo(z1) <= 0; zt = zt.add(BigInteger.ONE)) {
                                double var45 = (new BigDecimal(zt, MathContext.DECIMAL64).add(BigConstants.POINT_FIVE).subtract(var24, MathContext.DECIMAL64)).divide(BigDecimal.valueOf(var28 / 2.0), MathContext.DECIMAL64).doubleValue();
                                if (var39 * var39 + var42 * var42 + var45 * var45 < 1.0 && level.getTile(xt, yt, zt) == Tile.STONE.id) {
                                    level.setTileNoUpdate(xt, yt, zt, this.tile);
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
