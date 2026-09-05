package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.math.BigMath;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.renderer.Textures;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Shadow
    protected abstract Level getLevel();

    @Shadow
    protected EntityRenderDispatcher entityRenderDispatcher;

    @Shadow
    protected float shadowRadius;

    private void renderTileShadow(Tile tile, double x, double y, double z, BigInteger xt, int yt, BigInteger zt, float pow, float r, double xo, double yo, double zo) {
        Tesselator t = Tesselator.instance;
        if (!tile.isCubeShaped()) return;

        double a = ((double) pow - (y - ((double) yt + yo)) / 2.0) * 0.5 * (double) this.getLevel().getBrightness(xt, yt, zt);
        if (a < 0.0) return;
        if (a > 1.0) a = 1.0;
        t.color(1.0F, 1.0F, 1.0F, (float) a);

        double x0 = xt.doubleValue() + tile.xx0 + xo;
        double x1 = xt.doubleValue() + tile.xx1 + xo;
        double y0 = (double) yt + tile.yy0 + yo + 0.015625;
        double z0 = zt.doubleValue() + tile.zz0 + zo;
        double z1 = zt.doubleValue() + tile.zz1 + zo;

        float u0 = (float) ((x - x0) / 2.0 / (double) r + 0.5);
        float u1 = (float) ((x - x1) / 2.0 / (double) r + 0.5);
        float v0 = (float) ((z - z0) / 2.0 / (double) r + 0.5);
        float v1 = (float) ((z - z1) / 2.0 / (double) r + 0.5);

        t.vertexUV(x0, y0, z0, u0, v0);
        t.vertexUV(x0, y0, z1, u0, v1);
        t.vertexUV(x1, y0, z1, u1, v1);
        t.vertexUV(x1, y0, z0, u1, v0);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void renderShadow(Entity e, double x, double y, double z, float pow, float a) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Textures textures = this.entityRenderDispatcher.textures;
        textures.bind(textures.loadTexture("%clamp%/misc/shadow.png"));

        Level level = getLevel();

        GL11.glDepthMask(false);
        float r = this.shadowRadius;
        //? >=1.0.0-beta.8.0.r {
        /*if (e instanceof Mob mob) {
            r *= mob.getSizeScale();
        }
        *///? }

        double ex = e.xOld + (e.x - e.xOld) * (double) a;
        double ey = e.yOld + (e.y - e.yOld) * (double) a + (double) e.getShadowHeightOffs();
        double ez = e.zOld + (e.z - e.zOld) * (double) a;

        BigInteger x0 = BigMath.floor(ex - (double) r);
        BigInteger x1 = BigMath.floor(ex + (double) r);
        int y0 = Mth.floor(ey - (double) r);
        int y1 = Mth.floor(ey);
        BigInteger z0 = BigMath.floor(ez - (double) r);
        BigInteger z1 = BigMath.floor(ez + (double) r);

        double xo = x - ex;
        double yo = y - ey;
        double zo = z - ez;

        Tesselator tt = Tesselator.instance;
        tt.begin();
        for (BigInteger xt = x0; xt.compareTo(x1) <= 0; xt = xt.add(BigInteger.ONE)) {
            for (int yt = y0; yt <= y1; ++yt) {
                for (BigInteger zt = z0; zt.compareTo(z1) <= 0; zt = zt.add(BigInteger.ONE)) {
                    int t = level.getTile(xt, yt - 1, zt);
                    if (t > 0 && level.getLightLevel(xt, yt, zt) > 3) {
                        this.renderTileShadow(
                                Tile.tiles[t], x, y + (double) e.getShadowHeightOffs(), z, xt, yt, zt, pow, r, xo, yo + (double) e.getShadowHeightOffs(), zo
                        );
                    }
                }
            }
        }

        tt.end();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
    }
}
