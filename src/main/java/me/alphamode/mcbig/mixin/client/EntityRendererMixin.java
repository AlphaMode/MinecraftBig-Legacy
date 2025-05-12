package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.math.BigMath;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.renderer.Textures;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Shadow protected abstract Level getLevel();

    @Shadow protected EntityRenderDispatcher entityRenderDispatcher;

    @Shadow protected float shadowRadius;

    private void renderTileShadow(Tile tile, double x, double y, double z, BigInteger xt, int yt, BigInteger zt, float pow, float r, double xo, double yo, double zo) {
        Tesselator var19 = Tesselator.instance;
        if (tile.isCubeShaped()) {
            double var20 = ((double)pow - (y - ((double)yt + yo)) / 2.0) * 0.5 * (double)this.getLevel().getBrightness(xt, yt, zt);
            if (!(var20 < 0.0)) {
                if (var20 > 1.0) {
                    var20 = 1.0;
                }

                var19.color(1.0F, 1.0F, 1.0F, (float)var20);
                double var22 = (double)xt.doubleValue() + tile.xx0 + xo;
                double var24 = (double)xt.doubleValue() + tile.xx1 + xo;
                double var26 = (double)yt + tile.yy0 + yo + 0.015625;
                double var28 = (double)zt.doubleValue() + tile.zz0 + zo;
                double var30 = (double)zt.doubleValue() + tile.zz1 + zo;
                float var32 = (float)((x - var22) / 2.0 / (double)r + 0.5);
                float var33 = (float)((x - var24) / 2.0 / (double)r + 0.5);
                float var34 = (float)((z - var28) / 2.0 / (double)r + 0.5);
                float var35 = (float)((z - var30) / 2.0 / (double)r + 0.5);
                var19.vertexUV(var22, var26, var28, (double)var32, (double)var34);
                var19.vertexUV(var22, var26, var30, (double)var32, (double)var35);
                var19.vertexUV(var24, var26, var30, (double)var33, (double)var35);
                var19.vertexUV(var24, var26, var28, (double)var33, (double)var34);
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void renderShadow(Entity e, double x, double y, double z, float pow, float a) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Textures var10 = this.entityRenderDispatcher.textures;
        var10.bind(var10.loadTexture("%clamp%/misc/shadow.png"));
        Level var11 = this.getLevel();
        GL11.glDepthMask(false);
        float var12 = this.shadowRadius;
        double var13 = e.xOld + (e.x - e.xOld) * (double)a;
        double var15 = e.yOld + (e.y - e.yOld) * (double)a + (double)e.getShadowHeightOffs();
        double var17 = e.zOld + (e.z - e.zOld) * (double)a;
        BigInteger var19 = BigMath.floor(var13 - (double)var12);
        BigInteger var20 = BigMath.floor(var13 + (double)var12);
        int var21 = Mth.floor(var15 - (double)var12);
        int var22 = Mth.floor(var15);
        BigInteger var23 = BigMath.floor(var17 - (double)var12);
        BigInteger var24 = BigMath.floor(var17 + (double)var12);
        double var25 = x - var13;
        double var27 = y - var15;
        double var29 = z - var17;
        Tesselator var31 = Tesselator.instance;
        var31.begin();

        for(BigInteger var32 = var19; var32.compareTo(var20) <= 0; var32 = var32.add(BigInteger.ONE)) {
            for(int var33 = var21; var33 <= var22; ++var33) {
                for(BigInteger var34 = var23; var34.compareTo(var24) <= 0; var34 = var34.add(BigInteger.ONE)) {
                    int var35 = var11.getTile(var32, var33 - 1, var34);
                    if (var35 > 0 && var11.getLightLevel(var32, var33, var34) > 3) {
                        this.renderTileShadow(
                                Tile.tiles[var35], x, y + (double)e.getShadowHeightOffs(), z, var32, var33, var34, pow, var12, var25, var27 + (double)e.getShadowHeightOffs(), var29
                        );
                    }
                }
            }
        }

        var31.end();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
    }
}
