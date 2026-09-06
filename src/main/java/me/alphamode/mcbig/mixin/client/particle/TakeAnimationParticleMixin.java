package me.alphamode.mcbig.mixin.client.particle;

import me.alphamode.mcbig.client.renderer.entity.EntityRenderDispatcherData;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.math.BigMath;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TakeAnimationParticle;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;
import java.math.BigInteger;

@Mixin(TakeAnimationParticle.class)
public abstract class TakeAnimationParticleMixin extends Particle {
    @Shadow
    private int life;

    @Shadow
    private int lifeTime;

    @Shadow
    private Entity item;

    @Shadow
    private Entity target;

    @Shadow
    private float yOffs;

    public TakeAnimationParticleMixin(Level level, double x, double y, double z, double xa, double ya, double za) {
        super(level, x, y, z, xa, ya, za);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void render(Tesselator t, float a, float xa, float ya, float za, float xa2, float za2) {
        float time = (this.life + a) / this.lifeTime;
        time *= time;

        boolean isTargetBig = this.target.isBigMovementEnabled();

        BigDecimal xx;
        BigDecimal zz;

        if (isTargetBig) {
            BigDecimal ab = new BigDecimal(a);
            BigEntityExtension targetb = (BigEntityExtension) this.target;
            BigDecimal xt = targetb.getXOld().add((targetb.getX().subtract(targetb.getXOld())).multiply(ab));
            BigDecimal zt = targetb.getZOld().add((targetb.getZ().subtract(targetb.getZOld())).multiply(ab));

            BigDecimal timeb = new BigDecimal(time);
            BigDecimal xo;
            BigDecimal zo;
            if (this.item.isBigMovementEnabled()) {
                xo = ((BigEntityExtension) this.item).getX();
                zo = ((BigEntityExtension) this.item).getZ();
            } else {
                xo = new BigDecimal(this.item.x);
                zo = new BigDecimal(this.item.z);
            }
            xx = xo.add((xt.subtract(xo)).multiply(timeb));
            zz = zo.add((zt.subtract(zo)).multiply(timeb));
        } else {
            double xt = this.target.xOld + (this.target.x - this.target.xOld) * a;
            double zt = this.target.zOld + (this.target.z - this.target.zOld) * a;

            double xo = this.item.x;
            double zo = this.item.z;

            xx = new BigDecimal(xo + (xt - xo) * time);
            zz = new BigDecimal(zo + (zt - zo) * time);
        }

        double yo = this.item.y;

        double yt = this.target.yOld + (this.target.y - this.target.yOld) * a + this.yOffs;
        double yy = yo + (yt - yo) * time;

        float xr, zr;

        BigInteger xTile = BigMath.floor(xx);
        int yTile = Mth.floor(yy + this.heightOffset / 2.0F);
        BigInteger zTile = BigMath.floor(zz);

        float br = this.level.getBrightness(xTile, yTile, zTile);
        GL11.glColor4f(br, br, br, 1.0F);

        xr = xx.subtract(EntityRenderDispatcherData.xOff).floatValue();
        yy -= yOff;
        zr = zz.subtract(EntityRenderDispatcherData.zOff).floatValue();

        EntityRenderDispatcher.INSTANCE.render(this.item, (float) xr, (float) yy, (float) zr, this.item.yRot, a);
    }
}
