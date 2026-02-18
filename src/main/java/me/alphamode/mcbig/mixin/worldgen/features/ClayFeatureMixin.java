package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.extensions.BigFeatureExtension;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.math.BigMath;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ClayFeature;
import net.minecraft.world.level.material.Material;
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

@Mixin(ClayFeature.class)
public class ClayFeatureMixin implements BigFeatureExtension {
    @Shadow
    private int radius;

    @Shadow
    private int tile;

    private BigDecimal bigRadius;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void makeBigRadius(int par1, CallbackInfo ci) {
        this.bigRadius = BigDecimal.valueOf(this.radius);
    }

    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        if (level.getMaterial(x, y, z) != Material.WATER) {
            return false;
        } else {
            float var6 = random.nextFloat() * (float) Math.PI;
            BigDecimal var7 = new BigDecimal(x.add(BigConstants.EIGHT)).add(BigDecimal.valueOf(Mth.sin(var6) * this.radius / 8.0F));
            BigDecimal var9 = new BigDecimal(x.add(BigConstants.EIGHT)).subtract(BigDecimal.valueOf(Mth.sin(var6) * this.radius / 8.0F));
            BigDecimal var11 = new BigDecimal(z.add(BigConstants.EIGHT)).add(BigDecimal.valueOf(Mth.cos(var6) * this.radius / 8.0F));
            BigDecimal var13 = new BigDecimal(z.add(BigConstants.EIGHT)).subtract(BigDecimal.valueOf(Mth.cos(var6) * this.radius / 8.0F));
            double var15 = y + random.nextInt(3) + 2;
            double var17 = y + random.nextInt(3) + 2;

            for (int i = 0; i <= this.radius; i++) {
                BigDecimal bigI = BigDecimal.valueOf(i);
                BigDecimal var20 = var7.add((var9.subtract(var7)).multiply(bigI).divide(this.bigRadius, MathContext.DECIMAL64));
                double var22 = var15 + (var17 - var15) * i / this.radius;
                BigDecimal var24 = var11.add((var13.subtract(var11)).multiply(bigI).divide(this.bigRadius, MathContext.DECIMAL64));
                double var26 = random.nextDouble() * this.radius / 16.0;
                double var28 = (Mth.sin(i * (float) Math.PI / this.radius) + 1.0F) * var26 + 1.0;
                double var30 = (Mth.sin(i * (float) Math.PI / this.radius) + 1.0F) * var26 + 1.0;
                BigInteger x0 = BigMath.floor(var20.subtract(BigDecimal.valueOf(var28 / 2.0)));
                BigInteger x1 = BigMath.floor(var20.add(BigDecimal.valueOf(var28 / 2.0)));
                int y0 = Mth.floor(var22 - var30 / 2.0);
                int y1 = Mth.floor(var22 + var30 / 2.0);
                BigInteger z0 = BigMath.floor(var24.subtract(BigDecimal.valueOf(var28 / 2.0)));
                BigInteger z1 = BigMath.floor(var24.add(BigDecimal.valueOf(var28 / 2.0)));

                for (BigInteger xt = x0; xt.compareTo(x1) <= 0; xt = xt.add(BigInteger.ONE)) {
                    for (int yt = y0; yt <= y1; yt++) {
                        for (BigInteger zt = z0; zt.compareTo(z1) <= 0; zt = zt.add(BigInteger.ONE)) {
                            double var41 = (new BigDecimal(xt).add(BigConstants.POINT_FIVE).subtract(var20)).divide(BigDecimal.valueOf(var28 / 2.0), MathContext.DECIMAL64).doubleValue();
                            double var43 = (yt + 0.5 - var22) / (var30 / 2.0);
                            double var45 = (new BigDecimal(zt).add(BigConstants.POINT_FIVE).subtract(var24)).divide(BigDecimal.valueOf(var28 / 2.0), MathContext.DECIMAL64).doubleValue();
                            if (var41 * var41 + var43 * var43 + var45 * var45 < 1.0) {
                                int var47 = level.getTile(xt, yt, zt);
                                if (var47 == Tile.SAND.id) {
                                    level.setTileNoUpdate(xt, yt, zt, this.tile);
                                }
                            }
                        }
                    }
                }
            }

            return true;
        }
    }
}
