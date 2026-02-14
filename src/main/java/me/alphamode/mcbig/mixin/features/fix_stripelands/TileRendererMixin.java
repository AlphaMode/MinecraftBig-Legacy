package me.alphamode.mcbig.mixin.features.fix_stripelands;

import me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.renderer.TileRenderer;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;

@Mixin(TileRenderer.class)
public class TileRendererMixin implements BigTileRendererExtension {
    @Shadow
    private int f_35670914;

    @Shadow
    private int f_28464555;

    @Shadow
    private int f_54847291;

    @Shadow
    private int f_41835850;

    @Shadow
    private int f_78756416;

    @Shadow
    private int f_50036552;

    @Shadow
    private int fixedTexture;

    @Shadow
    private boolean xFlipTexture;

    @Shadow
    private boolean blen;

    @Shadow
    private float c1r;

    @Shadow
    private float c1g;

    @Shadow
    private float c1b;

    @Shadow
    private float c2r;

    @Shadow
    private float c2g;

    @Shadow
    private float c2b;

    @Shadow
    private float c3r;

    @Shadow
    private float c3g;

    @Shadow
    private float c3b;

    @Shadow
    private float c4r;

    @Shadow
    private float c4g;

    @Shadow
    private float c4b;

    @Override
    public void renderFaceDown(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        Tesselator t = Tesselator.instance;
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }

        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u0 = ((double) xt + tile.xx0 * 16.0) / 256.0;
        double u1 = ((double) xt + tile.xx1 * 16.0 - 0.01) / 256.0;
        double v0 = ((double) yt + tile.zz0 * 16.0) / 256.0;
        double v1 = ((double) yt + tile.zz1 * 16.0 - 0.01) / 256.0;
        if (tile.xx0 < 0.0 || tile.xx1 > 1.0) {
            u0 = ((float) xt + 0.0F) / 256.0F;
            u1 = ((float) xt + 15.99F) / 256.0F;
        }

        if (tile.zz0 < 0.0 || tile.zz1 > 1.0) {
            v0 = ((float) yt + 0.0F) / 256.0F;
            v1 = ((float) yt + 15.99F) / 256.0F;
        }

        double var20 = u1;
        double var22 = u0;
        double var24 = v0;
        double var26 = v1;
        if (this.f_35670914 == 2) {
            u0 = ((double) xt + tile.zz0 * 16.0) / 256.0;
            v0 = ((double) (yt + 16) - tile.xx1 * 16.0) / 256.0;
            u1 = ((double) xt + tile.zz1 * 16.0) / 256.0;
            v1 = ((double) (yt + 16) - tile.xx0 * 16.0) / 256.0;
            var24 = v0;
            var26 = v1;
            var20 = u0;
            var22 = u1;
            v0 = v1;
            v1 = v0;
        } else if (this.f_35670914 == 1) {
            u0 = ((double) (xt + 16) - tile.zz1 * 16.0) / 256.0;
            v0 = ((double) yt + tile.xx0 * 16.0) / 256.0;
            u1 = ((double) (xt + 16) - tile.zz0 * 16.0) / 256.0;
            v1 = ((double) yt + tile.xx1 * 16.0) / 256.0;
            var20 = u1;
            var22 = u0;
            u0 = u1;
            u1 = u0;
            var24 = v1;
            var26 = v0;
        } else if (this.f_35670914 == 3) {
            u0 = ((double) (xt + 16) - tile.xx0 * 16.0) / 256.0;
            u1 = ((double) (xt + 16) - tile.xx1 * 16.0 - 0.01) / 256.0;
            v0 = ((double) (yt + 16) - tile.zz0 * 16.0) / 256.0;
            v1 = ((double) (yt + 16) - tile.zz1 * 16.0 - 0.01) / 256.0;
            var20 = u1;
            var22 = u0;
            var24 = v0;
            var26 = v1;
        }

        BigDecimal x0 = x.add(new BigDecimal(tile.xx0));
        BigDecimal x1 = x.add(new BigDecimal(tile.xx1));
        double y1 = y + tile.yy0;
        BigDecimal z0 = z.add(new BigDecimal(tile.zz0));
        BigDecimal z1 = z.add(new BigDecimal(tile.zz1));
        if (this.blen) {
            t.color(this.c1r, this.c1g, this.c1b);
            t.vertexUV(x0, y1, z1, var22, var26);
            t.color(this.c2r, this.c2g, this.c2b);
            t.vertexUV(x0, y1, z0, u0, v0);
            t.color(this.c3r, this.c3g, this.c3b);
            t.vertexUV(x1, y1, z0, var20, var24);
            t.color(this.c4r, this.c4g, this.c4b);
            t.vertexUV(x1, y1, z1, u1, v1);
        } else {
            t.vertexUV(x0, y1, z1, var22, var26);
            t.vertexUV(x0, y1, z0, u0, v0);
            t.vertexUV(x1, y1, z0, var20, var24);
            t.vertexUV(x1, y1, z1, u1, v1);
        }
    }

    @Override
    public void renderFaceUp(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        Tesselator t = Tesselator.instance;
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }

        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u0 = ((double) xt + tile.xx0 * 16.0) / 256.0;
        double u1 = ((double) xt + tile.xx1 * 16.0 - 0.01) / 256.0;
        double v0 = ((double) yt + tile.zz0 * 16.0) / 256.0;
        double v1 = ((double) yt + tile.zz1 * 16.0 - 0.01) / 256.0;
        if (tile.xx0 < 0.0 || tile.xx1 > 1.0) {
            u0 = ((float) xt + 0.0F) / 256.0F;
            u1 = ((float) xt + 15.99F) / 256.0F;
        }

        if (tile.zz0 < 0.0 || tile.zz1 > 1.0) {
            v0 = ((float) yt + 0.0F) / 256.0F;
            v1 = ((float) yt + 15.99F) / 256.0F;
        }

        double var20 = u1;
        double var22 = u0;
        double var24 = v0;
        double var26 = v1;
        if (this.f_28464555 == 1) {
            u0 = ((double) xt + tile.zz0 * 16.0) / 256.0;
            v0 = ((double) (yt + 16) - tile.xx1 * 16.0) / 256.0;
            u1 = ((double) xt + tile.zz1 * 16.0) / 256.0;
            v1 = ((double) (yt + 16) - tile.xx0 * 16.0) / 256.0;
            var24 = v0;
            var26 = v1;
            var20 = u0;
            var22 = u1;
            v0 = v1;
            v1 = v0;
        } else if (this.f_28464555 == 2) {
            u0 = ((double) (xt + 16) - tile.zz1 * 16.0) / 256.0;
            v0 = ((double) yt + tile.xx0 * 16.0) / 256.0;
            u1 = ((double) (xt + 16) - tile.zz0 * 16.0) / 256.0;
            v1 = ((double) yt + tile.xx1 * 16.0) / 256.0;
            var20 = u1;
            var22 = u0;
            u0 = u1;
            u1 = u0;
            var24 = v1;
            var26 = v0;
        } else if (this.f_28464555 == 3) {
            u0 = ((double) (xt + 16) - tile.xx0 * 16.0) / 256.0;
            u1 = ((double) (xt + 16) - tile.xx1 * 16.0 - 0.01) / 256.0;
            v0 = ((double) (yt + 16) - tile.zz0 * 16.0) / 256.0;
            v1 = ((double) (yt + 16) - tile.zz1 * 16.0 - 0.01) / 256.0;
            var20 = u1;
            var22 = u0;
            var24 = v0;
            var26 = v1;
        }

        BigDecimal x0 = x.add(new BigDecimal(tile.xx0));
        BigDecimal x1 = x.add(new BigDecimal(tile.xx1));
        double y0 = y + tile.yy1;
        BigDecimal z0 = z.add(new BigDecimal(tile.zz0));
        BigDecimal z1 = z.add(new BigDecimal(tile.zz1));
        if (this.blen) {
            t.color(this.c1r, this.c1g, this.c1b);
            t.vertexUV(x1, y0, z1, u1, v1);
            t.color(this.c2r, this.c2g, this.c2b);
            t.vertexUV(x1, y0, z0, var20, var24);
            t.color(this.c3r, this.c3g, this.c3b);
            t.vertexUV(x0, y0, z0, u0, v0);
            t.color(this.c4r, this.c4g, this.c4b);
            t.vertexUV(x0, y0, z1, var22, var26);
        } else {
            t.vertexUV(x1, y0, z1, u1, v1);
            t.vertexUV(x1, y0, z0, var20, var24);
            t.vertexUV(x0, y0, z0, u0, v0);
            t.vertexUV(x0, y0, z1, var22, var26);
        }
    }

    @Override
    public void renderNorth(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        Tesselator t = Tesselator.instance;
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }

        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u0 = ((double) xt + tile.xx0 * 16.0) / 256.0;
        double u1 = ((double) xt + tile.xx1 * 16.0 - 0.01) / 256.0;
        double v0 = ((double) (yt + 16) - tile.yy1 * 16.0) / 256.0;
        double v1 = ((double) (yt + 16) - tile.yy0 * 16.0 - 0.01) / 256.0;
        if (this.xFlipTexture) {
            double var20 = u0;
            u0 = u1;
            u1 = var20;
        }

        if (tile.xx0 < 0.0 || tile.xx1 > 1.0) {
            u0 = ((float) xt + 0.0F) / 256.0F;
            u1 = ((float) xt + 15.99F) / 256.0F;
        }

        if (tile.yy0 < 0.0 || tile.yy1 > 1.0) {
            v0 = ((float) yt + 0.0F) / 256.0F;
            v1 = ((float) yt + 15.99F) / 256.0F;
        }

        double var42 = u1;
        double var22 = u0;
        double var24 = v0;
        double var26 = v1;
        if (this.f_50036552 == 2) {
            u0 = ((double) xt + tile.yy0 * 16.0) / 256.0;
            v0 = ((double) (yt + 16) - tile.xx0 * 16.0) / 256.0;
            u1 = ((double) xt + tile.yy1 * 16.0) / 256.0;
            v1 = ((double) (yt + 16) - tile.xx1 * 16.0) / 256.0;
            var24 = v0;
            var26 = v1;
            var42 = u0;
            var22 = u1;
            v0 = v1;
            v1 = v0;
        } else if (this.f_50036552 == 1) {
            u0 = ((double) (xt + 16) - tile.yy1 * 16.0) / 256.0;
            v0 = ((double) yt + tile.xx1 * 16.0) / 256.0;
            u1 = ((double) (xt + 16) - tile.yy0 * 16.0) / 256.0;
            v1 = ((double) yt + tile.xx0 * 16.0) / 256.0;
            var42 = u1;
            var22 = u0;
            u0 = u1;
            u1 = u0;
            var24 = v1;
            var26 = v0;
        } else if (this.f_50036552 == 3) {
            u0 = ((double) (xt + 16) - tile.xx0 * 16.0) / 256.0;
            u1 = ((double) (xt + 16) - tile.xx1 * 16.0 - 0.01) / 256.0;
            v0 = ((double) yt + tile.yy1 * 16.0) / 256.0;
            v1 = ((double) yt + tile.yy0 * 16.0 - 0.01) / 256.0;
            var42 = u1;
            var22 = u0;
            var24 = v0;
            var26 = v1;
        }

        BigDecimal x0 = x.add(new BigDecimal(tile.xx0));
        BigDecimal x1 = x.add(new BigDecimal(tile.xx1));
        double y0 = y + tile.yy0;
        double y1 = y + tile.yy1;
        BigDecimal z0 = z.add(new BigDecimal(tile.zz0));
        if (this.blen) {
            t.color(this.c1r, this.c1g, this.c1b);
            t.vertexUV(x0, y1, z0, var42, var24);
            t.color(this.c2r, this.c2g, this.c2b);
            t.vertexUV(x1, y1, z0, u0, v0);
            t.color(this.c3r, this.c3g, this.c3b);
            t.vertexUV(x1, y0, z0, var22, var26);
            t.color(this.c4r, this.c4g, this.c4b);
            t.vertexUV(x0, y0, z0, u1, v1);
        } else {
            t.vertexUV(x0, y1, z0, var42, var24);
            t.vertexUV(x1, y1, z0, u0, v0);
            t.vertexUV(x1, y0, z0, var22, var26);
            t.vertexUV(x0, y0, z0, u1, v1);
        }
    }

    @Override
    public void renderSouth(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        Tesselator t = Tesselator.instance;
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }

        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u0 = ((double) xt + tile.xx0 * 16.0) / 256.0;
        double u1 = ((double) xt + tile.xx1 * 16.0 - 0.01) / 256.0;
        double v0 = ((double) (yt + 16) - tile.yy1 * 16.0) / 256.0;
        double v1 = ((double) (yt + 16) - tile.yy0 * 16.0 - 0.01) / 256.0;
        if (this.xFlipTexture) {
            double tmp = u0;
            u0 = u1;
            u1 = tmp;
        }

        if (tile.xx0 < 0.0 || tile.xx1 > 1.0) {
            u0 = (double) (((float) xt + 0.0F) / 256.0F);
            u1 = (double) (((float) xt + 15.99F) / 256.0F);
        }

        if (tile.yy0 < 0.0 || tile.yy1 > 1.0) {
            v0 = (double) (((float) yt + 0.0F) / 256.0F);
            v1 = (double) (((float) yt + 15.99F) / 256.0F);
        }

        double var42 = u1;
        double var22 = u0;
        double var24 = v0;
        double var26 = v1;
        if (this.f_78756416 == 1) {
            u0 = ((double) xt + tile.yy0 * 16.0) / 256.0;
            v1 = ((double) (yt + 16) - tile.xx0 * 16.0) / 256.0;
            u1 = ((double) xt + tile.yy1 * 16.0) / 256.0;
            v0 = ((double) (yt + 16) - tile.xx1 * 16.0) / 256.0;
            var24 = v0;
            var26 = v1;
            var42 = u0;
            var22 = u1;
            v0 = v1;
            v1 = v0;
        } else if (this.f_78756416 == 2) {
            u0 = ((double) (xt + 16) - tile.yy1 * 16.0) / 256.0;
            v0 = ((double) yt + tile.xx0 * 16.0) / 256.0;
            u1 = ((double) (xt + 16) - tile.yy0 * 16.0) / 256.0;
            v1 = ((double) yt + tile.xx1 * 16.0) / 256.0;
            var42 = u1;
            var22 = u0;
            u0 = u1;
            u1 = u0;
            var24 = v1;
            var26 = v0;
        } else if (this.f_78756416 == 3) {
            u0 = ((double) (xt + 16) - tile.xx0 * 16.0) / 256.0;
            u1 = ((double) (xt + 16) - tile.xx1 * 16.0 - 0.01) / 256.0;
            v0 = ((double) yt + tile.yy1 * 16.0) / 256.0;
            v1 = ((double) yt + tile.yy0 * 16.0 - 0.01) / 256.0;
            var42 = u1;
            var22 = u0;
            var24 = v0;
            var26 = v1;
        }

        BigDecimal x0 = x.add(new BigDecimal(tile.xx0));
        BigDecimal x1 = x.add(new BigDecimal(tile.xx1));
        double y0 = y + tile.yy0;
        double y1 = y + tile.yy1;
        BigDecimal z0 = z.add(new BigDecimal(tile.zz1));
        if (this.blen) {
            t.color(this.c1r, this.c1g, this.c1b);
            t.vertexUV(x0, y1, z0, u0, v0);
            t.color(this.c2r, this.c2g, this.c2b);
            t.vertexUV(x0, y0, z0, var22, var26);
            t.color(this.c3r, this.c3g, this.c3b);
            t.vertexUV(x1, y0, z0, u1, v1);
            t.color(this.c4r, this.c4g, this.c4b);
            t.vertexUV(x1, y1, z0, var42, var24);
        } else {
            t.vertexUV(x0, y1, z0, u0, v0);
            t.vertexUV(x0, y0, z0, var22, var26);
            t.vertexUV(x1, y0, z0, u1, v1);
            t.vertexUV(x1, y1, z0, var42, var24);
        }
    }

    @Override
    public void renderWest(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        Tesselator t = Tesselator.instance;
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }

        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u0 = ((double) xt + tile.zz0 * 16.0) / 256.0;
        double u1 = ((double) xt + tile.zz1 * 16.0 - 0.01) / 256.0;
        double v0 = ((double) (yt + 16) - tile.yy1 * 16.0) / 256.0;
        double v1 = ((double) (yt + 16) - tile.yy0 * 16.0 - 0.01) / 256.0;
        if (this.xFlipTexture) {
            double tmp = u0;
            u0 = u1;
            u1 = tmp;
        }

        if (tile.zz0 < 0.0 || tile.zz1 > 1.0) {
            u0 = ((float) xt + 0.0F) / 256.0F;
            u1 = ((float) xt + 15.99F) / 256.0F;
        }

        if (tile.yy0 < 0.0 || tile.yy1 > 1.0) {
            v0 = ((float) yt + 0.0F) / 256.0F;
            v1 = ((float) yt + 15.99F) / 256.0F;
        }

        double var42 = u1;
        double var22 = u0;
        double var24 = v0;
        double var26 = v1;
        if (this.f_41835850 == 1) {
            u0 = ((double) xt + tile.yy0 * 16.0) / 256.0;
            v0 = ((double) (yt + 16) - tile.zz1 * 16.0) / 256.0;
            u1 = ((double) xt + tile.yy1 * 16.0) / 256.0;
            v1 = ((double) (yt + 16) - tile.zz0 * 16.0) / 256.0;
            var24 = v0;
            var26 = v1;
            var42 = u0;
            var22 = u1;
            v0 = v1;
            v1 = v0;
        } else if (this.f_41835850 == 2) {
            u0 = ((double) (xt + 16) - tile.yy1 * 16.0) / 256.0;
            v0 = ((double) yt + tile.zz0 * 16.0) / 256.0;
            u1 = ((double) (xt + 16) - tile.yy0 * 16.0) / 256.0;
            v1 = ((double) yt + tile.zz1 * 16.0) / 256.0;
            var42 = u1;
            var22 = u0;
            u0 = u1;
            u1 = u0;
            var24 = v1;
            var26 = v0;
        } else if (this.f_41835850 == 3) {
            u0 = ((double) (xt + 16) - tile.zz0 * 16.0) / 256.0;
            u1 = ((double) (xt + 16) - tile.zz1 * 16.0 - 0.01) / 256.0;
            v0 = ((double) yt + tile.yy1 * 16.0) / 256.0;
            v1 = ((double) yt + tile.yy0 * 16.0 - 0.01) / 256.0;
            var42 = u1;
            var22 = u0;
            var24 = v0;
            var26 = v1;
        }

        BigDecimal x0 = x.add(new BigDecimal(tile.xx0));
        double y0 = y + tile.yy0;
        double y1 = y + tile.yy1;
        BigDecimal z0 = z.add(new BigDecimal(tile.zz0));
        BigDecimal z1 = z.add(new BigDecimal(tile.zz1));
        if (this.blen) {
            t.color(this.c1r, this.c1g, this.c1b);
            t.vertexUV(x0, y1, z1, var42, var24);
            t.color(this.c2r, this.c2g, this.c2b);
            t.vertexUV(x0, y1, z0, u0, v0);
            t.color(this.c3r, this.c3g, this.c3b);
            t.vertexUV(x0, y0, z0, var22, var26);
            t.color(this.c4r, this.c4g, this.c4b);
            t.vertexUV(x0, y0, z1, u1, v1);
        } else {
            t.vertexUV(x0, y1, z1, var42, var24);
            t.vertexUV(x0, y1, z0, u0, v0);
            t.vertexUV(x0, y0, z0, var22, var26);
            t.vertexUV(x0, y0, z1, u1, v1);
        }
    }

    @Override
    public void renderEast(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        Tesselator t = Tesselator.instance;
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }

        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u0 = ((double) xt + tile.zz0 * 16.0) / 256.0;
        double u1 = ((double) xt + tile.zz1 * 16.0 - 0.01) / 256.0;
        double v0 = ((double) (yt + 16) - tile.yy1 * 16.0) / 256.0;
        double v1 = ((double) (yt + 16) - tile.yy0 * 16.0 - 0.01) / 256.0;
        if (this.xFlipTexture) {
            double tmp = u0;
            u0 = u1;
            u1 = tmp;
        }

        if (tile.zz0 < 0.0 || tile.zz1 > 1.0) {
            u0 = ((float) xt + 0.0F) / 256.0F;
            u1 = ((float) xt + 15.99F) / 256.0F;
        }

        if (tile.yy0 < 0.0 || tile.yy1 > 1.0) {
            v0 = ((float) yt + 0.0F) / 256.0F;
            v1 = ((float) yt + 15.99F) / 256.0F;
        }

        double var42 = u1;
        double var22 = u0;
        double var24 = v0;
        double var26 = v1;
        if (this.f_54847291 == 2) {
            u0 = ((double) xt + tile.yy0 * 16.0) / 256.0;
            v0 = ((double) (yt + 16) - tile.zz0 * 16.0) / 256.0;
            u1 = ((double) xt + tile.yy1 * 16.0) / 256.0;
            v1 = ((double) (yt + 16) - tile.zz1 * 16.0) / 256.0;
            var24 = v0;
            var26 = v1;
            var42 = u0;
            var22 = u1;
            v0 = v1;
            v1 = v0;
        } else if (this.f_54847291 == 1) {
            u0 = ((double) (xt + 16) - tile.yy1 * 16.0) / 256.0;
            v0 = ((double) yt + tile.zz1 * 16.0) / 256.0;
            u1 = ((double) (xt + 16) - tile.yy0 * 16.0) / 256.0;
            v1 = ((double) yt + tile.zz0 * 16.0) / 256.0;
            var42 = u1;
            var22 = u0;
            u0 = u1;
            u1 = u0;
            var24 = v1;
            var26 = v0;
        } else if (this.f_54847291 == 3) {
            u0 = ((double) (xt + 16) - tile.zz0 * 16.0) / 256.0;
            u1 = ((double) (xt + 16) - tile.zz1 * 16.0 - 0.01) / 256.0;
            v0 = ((double) yt + tile.yy1 * 16.0) / 256.0;
            v1 = ((double) yt + tile.yy0 * 16.0 - 0.01) / 256.0;
            var42 = u1;
            var22 = u0;
            var24 = v0;
            var26 = v1;
        }

        BigDecimal x0 = x.add(new BigDecimal(tile.xx1));
        double y0 = y + tile.yy0;
        double y1 = y + tile.yy1;
        BigDecimal z0 = z.add(new BigDecimal(tile.zz0));
        BigDecimal z1 = z.add(new BigDecimal(tile.zz1));
        if (this.blen) {
            t.color(this.c1r, this.c1g, this.c1b);
            t.vertexUV(x0, y0, z1, var22, var26);
            t.color(this.c2r, this.c2g, this.c2b);
            t.vertexUV(x0, y0, z0, u1, v1);
            t.color(this.c3r, this.c3g, this.c3b);
            t.vertexUV(x0, y1, z0, var42, var24);
            t.color(this.c4r, this.c4g, this.c4b);
            t.vertexUV(x0, y1, z1, u0, v0);
        } else {
            t.vertexUV(x0, y0, z1, var22, var26);
            t.vertexUV(x0, y0, z0, u1, v1);
            t.vertexUV(x0, y1, z0, var42, var24);
            t.vertexUV(x0, y1, z1, u0, v0);
        }
    }
}
