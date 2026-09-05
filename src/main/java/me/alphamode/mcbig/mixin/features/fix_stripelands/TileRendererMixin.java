package me.alphamode.mcbig.mixin.features.fix_stripelands;

import dev.kikugie.fletching_table.mixin.MixinEnvironment;
import me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.renderer.TileRenderer;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;

@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
@Mixin(TileRenderer.class)
public class TileRendererMixin implements BigTileRendererExtension {
    @Shadow
    private int downFlip;

    @Shadow
    private int upFlip;

    @Shadow
    private int eastFlip;

    @Shadow
    private int westFlip;

    @Shadow
    private int southFlip;

    @Shadow
    private int northFlip;

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

    //? >=1.0.0-beta.8.0.r {
    /*@Shadow
    private int tc1;
    @Shadow
    private int tc2;
    @Shadow
    private int tc3;
    @Shadow
    private int tc4;
    *///? }

    @Override
    public void renderFaceDown(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        Tesselator t = Tesselator.instance;
        if (this.fixedTexture >= 0) tex = this.fixedTexture;

        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u00 = ((double) xt + tile.xx0 * 16.0) / 256.0;
        double u11 = ((double) xt + tile.xx1 * 16.0 - 0.01) / 256.0;
        double v00 = ((double) yt + tile.zz0 * 16.0) / 256.0;
        double v11 = ((double) yt + tile.zz1 * 16.0 - 0.01) / 256.0;
        if (tile.xx0 < 0.0 || tile.xx1 > 1.0) {
            u00 = ((float) xt + 0.0F) / 256.0F;
            u11 = ((float) xt + 15.99F) / 256.0F;
        }

        if (tile.zz0 < 0.0 || tile.zz1 > 1.0) {
            v00 = ((float) yt + 0.0F) / 256.0F;
            v11 = ((float) yt + 15.99F) / 256.0F;
        }

        double u01 = u11, u10 = u00, v01 = v00, v10 = v11;
        if (this.downFlip == FLIP_CCW) {
            u00 = ((double) xt + tile.zz0 * 16.0) / 256.0;
            v00 = ((double) (yt + 16) - tile.xx1 * 16.0) / 256.0;
            u11 = ((double) xt + tile.zz1 * 16.0) / 256.0;
            v11 = ((double) (yt + 16) - tile.xx0 * 16.0) / 256.0;
            v01 = v00;
            v10 = v11;
            u01 = u00;
            u10 = u11;
            v00 = v11;
            v11 = v00;
        } else if (this.downFlip == FLIP_CW) {
            // reshape
            u00 = ((double) (xt + 16) - tile.zz1 * 16.0) / 256.0;
            v00 = ((double) yt + tile.xx0 * 16.0) / 256.0;
            u11 = ((double) (xt + 16) - tile.zz0 * 16.0) / 256.0;
            v11 = ((double) yt + tile.xx1 * 16.0) / 256.0;

            // rotate
            u01 = u11;
            u10 = u00;
            u00 = u11;
            u11 = u00;
            v01 = v11;
            v10 = v00;
        } else if (this.downFlip == FLIP_180) {
            u00 = ((double) (xt + 16) - tile.xx0 * 16.0) / 256.0;
            u11 = ((double) (xt + 16) - tile.xx1 * 16.0 - 0.01) / 256.0;
            v00 = ((double) (yt + 16) - tile.zz0 * 16.0) / 256.0;
            v11 = ((double) (yt + 16) - tile.zz1 * 16.0 - 0.01) / 256.0;
            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
        }

        BigDecimal x0 = x.add(new BigDecimal(tile.xx0));
        BigDecimal x1 = x.add(new BigDecimal(tile.xx1));
        double y1 = y + tile.yy0;
        BigDecimal z0 = z.add(new BigDecimal(tile.zz0));
        BigDecimal z1 = z.add(new BigDecimal(tile.zz1));
        if (this.blen) {
            t.color(this.c1r, this.c1g, this.c1b);
            t.vertexUV(x0, y1, z1, u10, v10);
            t.color(this.c2r, this.c2g, this.c2b);
            t.vertexUV(x0, y1, z0, u00, v00);
            t.color(this.c3r, this.c3g, this.c3b);
            t.vertexUV(x1, y1, z0, u01, v01);
            t.color(this.c4r, this.c4g, this.c4b);
            t.vertexUV(x1, y1, z1, u11, v11);
        } else {
            t.vertexUV(x0, y1, z1, u10, v10);
            t.vertexUV(x0, y1, z0, u00, v00);
            t.vertexUV(x1, y1, z0, u01, v01);
            t.vertexUV(x1, y1, z1, u11, v11);
        }
    }

    @Override
    public void renderFaceUp(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        Tesselator t = Tesselator.instance;
        if (this.fixedTexture >= 0) tex = this.fixedTexture;

        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u00 = ((double) xt + tile.xx0 * 16.0) / 256.0;
        double u11 = ((double) xt + tile.xx1 * 16.0 - 0.01) / 256.0;
        double v00 = ((double) yt + tile.zz0 * 16.0) / 256.0;
        double v11 = ((double) yt + tile.zz1 * 16.0 - 0.01) / 256.0;
        if (tile.xx0 < 0.0 || tile.xx1 > 1.0) {
            u00 = ((float) xt + 0.0F) / 256.0F;
            u11 = ((float) xt + 15.99F) / 256.0F;
        }

        if (tile.zz0 < 0.0 || tile.zz1 > 1.0) {
            v00 = ((float) yt + 0.0F) / 256.0F;
            v11 = ((float) yt + 15.99F) / 256.0F;
        }

        double u01 = u11, u10 = u00, v01 = v00, v10 = v11;
        if (this.upFlip == FLIP_CW) {
            u00 = ((double) xt + tile.zz0 * 16.0) / 256.0;
            v00 = ((double) (yt + 16) - tile.xx1 * 16.0) / 256.0;
            u11 = ((double) xt + tile.zz1 * 16.0) / 256.0;
            v11 = ((double) (yt + 16) - tile.xx0 * 16.0) / 256.0;

            v01 = v00;
            v10 = v11;
            u01 = u00;
            u10 = u11;
            v00 = v11;
            v11 = v00;
        } else if (this.upFlip == FLIP_CCW) {
            u00 = ((double) (xt + 16) - tile.zz1 * 16.0) / 256.0;
            v00 = ((double) yt + tile.xx0 * 16.0) / 256.0;
            u11 = ((double) (xt + 16) - tile.zz0 * 16.0) / 256.0;
            v11 = ((double) yt + tile.xx1 * 16.0) / 256.0;

            u01 = u11;
            u10 = u00;
            u00 = u11;
            u11 = u00;
            v01 = v11;
            v10 = v00;
        } else if (this.upFlip == FLIP_180) {
            u00 = ((double) (xt + 16) - tile.xx0 * 16.0) / 256.0;
            u11 = ((double) (xt + 16) - tile.xx1 * 16.0 - 0.01) / 256.0;
            v00 = ((double) (yt + 16) - tile.zz0 * 16.0) / 256.0;
            v11 = ((double) (yt + 16) - tile.zz1 * 16.0 - 0.01) / 256.0;

            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
        }

        BigDecimal x0 = x.add(new BigDecimal(tile.xx0));
        BigDecimal x1 = x.add(new BigDecimal(tile.xx1));
        double y0 = y + tile.yy1;
        BigDecimal z0 = z.add(new BigDecimal(tile.zz0));
        BigDecimal z1 = z.add(new BigDecimal(tile.zz1));
        if (this.blen) {
            t.color(this.c1r, this.c1g, this.c1b);
            t.vertexUV(x1, y0, z1, u11, v11);
            t.color(this.c2r, this.c2g, this.c2b);
            t.vertexUV(x1, y0, z0, u01, v01);
            t.color(this.c3r, this.c3g, this.c3b);
            t.vertexUV(x0, y0, z0, u00, v00);
            t.color(this.c4r, this.c4g, this.c4b);
            t.vertexUV(x0, y0, z1, u10, v10);
        } else {
            t.vertexUV(x1, y0, z1, u11, v11);
            t.vertexUV(x1, y0, z0, u01, v01);
            t.vertexUV(x0, y0, z0, u00, v00);
            t.vertexUV(x0, y0, z1, u10, v10);
        }
    }

    @Override
    public void renderNorth(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        Tesselator t = Tesselator.instance;
        if (this.fixedTexture >= 0) tex = this.fixedTexture;

        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u00 = ((double) xt + tile.xx0 * 16.0) / 256.0;
        double u11 = ((double) xt + tile.xx1 * 16.0 - 0.01) / 256.0;
        double v00 = ((double) (yt + 16) - tile.yy1 * 16.0) / 256.0;
        double v11 = ((double) (yt + 16) - tile.yy0 * 16.0 - 0.01) / 256.0;
        if (this.xFlipTexture) {
            double tmp = u00;
            u00 = u11;
            u11 = tmp;
        }

        if (tile.xx0 < 0.0 || tile.xx1 > 1.0) {
            u00 = ((float) xt + 0.0F) / 256.0F;
            u11 = ((float) xt + 15.99F) / 256.0F;
        }

        if (tile.yy0 < 0.0 || tile.yy1 > 1.0) {
            v00 = ((float) yt + 0.0F) / 256.0F;
            v11 = ((float) yt + 15.99F) / 256.0F;
        }

        double u01 = u11, u10 = u00, v01 = v00, v10 = v11;
        if (this.northFlip == FLIP_CCW) {
            u00 = ((double) xt + tile.yy0 * 16.0) / 256.0;
            v00 = ((double) (yt + 16) - tile.xx0 * 16.0) / 256.0;
            u11 = ((double) xt + tile.yy1 * 16.0) / 256.0;
            v11 = ((double) (yt + 16) - tile.xx1 * 16.0) / 256.0;

            v01 = v00;
            v10 = v11;
            u01 = u00;
            u10 = u11;
            v00 = v11;
            v11 = v00;
        } else if (this.northFlip == FLIP_CW) {
            u00 = ((double) (xt + 16) - tile.yy1 * 16.0) / 256.0;
            v00 = ((double) yt + tile.xx1 * 16.0) / 256.0;
            u11 = ((double) (xt + 16) - tile.yy0 * 16.0) / 256.0;
            v11 = ((double) yt + tile.xx0 * 16.0) / 256.0;

            u01 = u11;
            u10 = u00;
            u00 = u11;
            u11 = u00;
            v01 = v11;
            v10 = v00;
        } else if (this.northFlip == FLIP_180) {
            u00 = ((double) (xt + 16) - tile.xx0 * 16.0) / 256.0;
            u11 = ((double) (xt + 16) - tile.xx1 * 16.0 - 0.01) / 256.0;
            v00 = ((double) yt + tile.yy1 * 16.0) / 256.0;
            v11 = ((double) yt + tile.yy0 * 16.0 - 0.01) / 256.0;
            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
        }

        BigDecimal x0 = x.add(new BigDecimal(tile.xx0));
        BigDecimal x1 = x.add(new BigDecimal(tile.xx1));
        double y0 = y + tile.yy0;
        double y1 = y + tile.yy1;
        BigDecimal z0 = z.add(new BigDecimal(tile.zz0));
        if (this.blen) {
            t.color(this.c1r, this.c1g, this.c1b);
            t.vertexUV(x0, y1, z0, u01, v01);
            t.color(this.c2r, this.c2g, this.c2b);
            t.vertexUV(x1, y1, z0, u00, v00);
            t.color(this.c3r, this.c3g, this.c3b);
            t.vertexUV(x1, y0, z0, u10, v10);
            t.color(this.c4r, this.c4g, this.c4b);
            t.vertexUV(x0, y0, z0, u11, v11);
        } else {
            t.vertexUV(x0, y1, z0, u01, v01);
            t.vertexUV(x1, y1, z0, u00, v00);
            t.vertexUV(x1, y0, z0, u10, v10);
            t.vertexUV(x0, y0, z0, u11, v11);
        }
    }

    @Override
    public void renderSouth(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        Tesselator t = Tesselator.instance;
        if (this.fixedTexture >= 0) tex = this.fixedTexture;

        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u00 = ((double) xt + tile.xx0 * 16.0) / 256.0;
        double u11 = ((double) xt + tile.xx1 * 16.0 - 0.01) / 256.0;
        double v00 = ((double) (yt + 16) - tile.yy1 * 16.0) / 256.0;
        double v11 = ((double) (yt + 16) - tile.yy0 * 16.0 - 0.01) / 256.0;
        if (this.xFlipTexture) {
            double tmp = u00;
            u00 = u11;
            u11 = tmp;
        }

        if (tile.xx0 < 0.0 || tile.xx1 > 1.0) {
            u00 = (double) (((float) xt + 0.0F) / 256.0F);
            u11 = (double) (((float) xt + 15.99F) / 256.0F);
        }

        if (tile.yy0 < 0.0 || tile.yy1 > 1.0) {
            v00 = (double) (((float) yt + 0.0F) / 256.0F);
            v11 = (double) (((float) yt + 15.99F) / 256.0F);
        }

        double u01 = u11, u10 = u00, v01 = v00, v10 = v11;
        if (this.southFlip == FLIP_CW) {
            u00 = ((double) xt + tile.yy0 * 16.0) / 256.0;
            v11 = ((double) (yt + 16) - tile.xx0 * 16.0) / 256.0;
            u11 = ((double) xt + tile.yy1 * 16.0) / 256.0;
            v00 = ((double) (yt + 16) - tile.xx1 * 16.0) / 256.0;

            v01 = v00;
            v10 = v11;
            u01 = u00;
            u10 = u11;
            v00 = v11;
            v11 = v00;
        } else if (this.southFlip == FLIP_CCW) {
            u00 = ((double) (xt + 16) - tile.yy1 * 16.0) / 256.0;
            v00 = ((double) yt + tile.xx0 * 16.0) / 256.0;
            u11 = ((double) (xt + 16) - tile.yy0 * 16.0) / 256.0;
            v11 = ((double) yt + tile.xx1 * 16.0) / 256.0;

            u01 = u11;
            u10 = u00;
            u00 = u11;
            u11 = u00;
            v01 = v11;
            v10 = v00;
        } else if (this.southFlip == FLIP_180) {
            u00 = ((double) (xt + 16) - tile.xx0 * 16.0) / 256.0;
            u11 = ((double) (xt + 16) - tile.xx1 * 16.0 - 0.01) / 256.0;
            v00 = ((double) yt + tile.yy1 * 16.0) / 256.0;
            v11 = ((double) yt + tile.yy0 * 16.0 - 0.01) / 256.0;

            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
        }

        BigDecimal x0 = x.add(new BigDecimal(tile.xx0));
        BigDecimal x1 = x.add(new BigDecimal(tile.xx1));
        double y0 = y + tile.yy0;
        double y1 = y + tile.yy1;
        BigDecimal z0 = z.add(new BigDecimal(tile.zz1));
        if (this.blen) {
            t.color(this.c1r, this.c1g, this.c1b);
            t.vertexUV(x0, y1, z0, u00, v00);
            t.color(this.c2r, this.c2g, this.c2b);
            t.vertexUV(x0, y0, z0, u10, v10);
            t.color(this.c3r, this.c3g, this.c3b);
            t.vertexUV(x1, y0, z0, u11, v11);
            t.color(this.c4r, this.c4g, this.c4b);
            t.vertexUV(x1, y1, z0, u01, v01);
        } else {
            t.vertexUV(x0, y1, z0, u00, v00);
            t.vertexUV(x0, y0, z0, u10, v10);
            t.vertexUV(x1, y0, z0, u11, v11);
            t.vertexUV(x1, y1, z0, u01, v01);
        }
    }

    @Override
    public void renderWest(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        Tesselator t = Tesselator.instance;
        if (this.fixedTexture >= 0) tex = this.fixedTexture;

        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u00 = ((double) xt + tile.zz0 * 16.0) / 256.0;
        double u11 = ((double) xt + tile.zz1 * 16.0 - 0.01) / 256.0;
        double v00 = ((double) (yt + 16) - tile.yy1 * 16.0) / 256.0;
        double v11 = ((double) (yt + 16) - tile.yy0 * 16.0 - 0.01) / 256.0;
        if (this.xFlipTexture) {
            double tmp = u00;
            u00 = u11;
            u11 = tmp;
        }

        if (tile.zz0 < 0.0 || tile.zz1 > 1.0) {
            u00 = ((float) xt + 0.0F) / 256.0F;
            u11 = ((float) xt + 15.99F) / 256.0F;
        }

        if (tile.yy0 < 0.0 || tile.yy1 > 1.0) {
            v00 = ((float) yt + 0.0F) / 256.0F;
            v11 = ((float) yt + 15.99F) / 256.0F;
        }

        double u01 = u11, u10 = u00, v01 = v00, v10 = v11;
        if (this.westFlip == FLIP_CW) {
            u00 = ((double) xt + tile.yy0 * 16.0) / 256.0;
            v00 = ((double) (yt + 16) - tile.zz1 * 16.0) / 256.0;
            u11 = ((double) xt + tile.yy1 * 16.0) / 256.0;
            v11 = ((double) (yt + 16) - tile.zz0 * 16.0) / 256.0;

            v01 = v00;
            v10 = v11;
            u01 = u00;
            u10 = u11;
            v00 = v11;
            v11 = v00;
        } else if (this.westFlip == FLIP_CCW) {
            u00 = ((double) (xt + 16) - tile.yy1 * 16.0) / 256.0;
            v00 = ((double) yt + tile.zz0 * 16.0) / 256.0;
            u11 = ((double) (xt + 16) - tile.yy0 * 16.0) / 256.0;
            v11 = ((double) yt + tile.zz1 * 16.0) / 256.0;

            u01 = u11;
            u10 = u00;
            u00 = u11;
            u11 = u00;
            v01 = v11;
            v10 = v00;
        } else if (this.westFlip == FLIP_180) {
            u00 = ((double) (xt + 16) - tile.zz0 * 16.0) / 256.0;
            u11 = ((double) (xt + 16) - tile.zz1 * 16.0 - 0.01) / 256.0;
            v00 = ((double) yt + tile.yy1 * 16.0) / 256.0;
            v11 = ((double) yt + tile.yy0 * 16.0 - 0.01) / 256.0;

            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
        }

        BigDecimal x0 = x.add(new BigDecimal(tile.xx0));
        double y0 = y + tile.yy0;
        double y1 = y + tile.yy1;
        BigDecimal z0 = z.add(new BigDecimal(tile.zz0));
        BigDecimal z1 = z.add(new BigDecimal(tile.zz1));
        if (this.blen) {
            t.color(this.c1r, this.c1g, this.c1b);
            t.vertexUV(x0, y1, z1, u01, v01);
            t.color(this.c2r, this.c2g, this.c2b);
            t.vertexUV(x0, y1, z0, u00, v00);
            t.color(this.c3r, this.c3g, this.c3b);
            t.vertexUV(x0, y0, z0, u10, v10);
            t.color(this.c4r, this.c4g, this.c4b);
            t.vertexUV(x0, y0, z1, u11, v11);
        } else {
            t.vertexUV(x0, y1, z1, u01, v01);
            t.vertexUV(x0, y1, z0, u00, v00);
            t.vertexUV(x0, y0, z0, u10, v10);
            t.vertexUV(x0, y0, z1, u11, v11);
        }
    }

    @Override
    public void renderEast(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        Tesselator t = Tesselator.instance;

        if (this.fixedTexture >= 0) tex = this.fixedTexture;
        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u00 = ((double) xt + tile.zz0 * 16.0) / 256.0;
        double u11 = ((double) xt + tile.zz1 * 16.0 - 0.01) / 256.0;
        double v00 = ((double) (yt + 16) - tile.yy1 * 16.0) / 256.0;
        double v11 = ((double) (yt + 16) - tile.yy0 * 16.0 - 0.01) / 256.0;
        if (this.xFlipTexture) {
            double tmp = u00;
            u00 = u11;
            u11 = tmp;
        }

        if (tile.zz0 < 0.0 || tile.zz1 > 1.0) {
            u00 = ((float) xt + 0.0F) / 256.0F;
            u11 = ((float) xt + 15.99F) / 256.0F;
        }

        if (tile.yy0 < 0.0 || tile.yy1 > 1.0) {
            v00 = ((float) yt + 0.0F) / 256.0F;
            v11 = ((float) yt + 15.99F) / 256.0F;
        }

        double u01 = u11;
        double u10 = u00;
        double v01 = v00;
        double v10 = v11;
        if (this.eastFlip == FLIP_CCW) {
            u00 = ((double) xt + tile.yy0 * 16.0) / 256.0;
            v00 = ((double) (yt + 16) - tile.zz0 * 16.0) / 256.0;
            u11 = ((double) xt + tile.yy1 * 16.0) / 256.0;
            v11 = ((double) (yt + 16) - tile.zz1 * 16.0) / 256.0;

            v01 = v00;
            v10 = v11;
            u01 = u00;
            u10 = u11;
            v00 = v11;
            v11 = v00;
        } else if (this.eastFlip == FLIP_CW) {
            u00 = ((double) (xt + 16) - tile.yy1 * 16.0) / 256.0;
            v00 = ((double) yt + tile.zz1 * 16.0) / 256.0;
            u11 = ((double) (xt + 16) - tile.yy0 * 16.0) / 256.0;
            v11 = ((double) yt + tile.zz0 * 16.0) / 256.0;

            u01 = u11;
            u10 = u00;
            u00 = u11;
            u11 = u00;
            v01 = v11;
            v10 = v00;
        } else if (this.eastFlip == FLIP_180) {
            u00 = ((double) (xt + 16) - tile.zz0 * 16.0) / 256.0;
            u11 = ((double) (xt + 16) - tile.zz1 * 16.0 - 0.01) / 256.0;
            v00 = ((double) yt + tile.yy1 * 16.0) / 256.0;
            v11 = ((double) yt + tile.yy0 * 16.0 - 0.01) / 256.0;

            u01 = u11;
            u10 = u00;
            v01 = v00;
            v10 = v11;
        }

        BigDecimal x0 = x.add(new BigDecimal(tile.xx1));
        double y0 = y + tile.yy0;
        double y1 = y + tile.yy1;
        BigDecimal z0 = z.add(new BigDecimal(tile.zz0));
        BigDecimal z1 = z.add(new BigDecimal(tile.zz1));
        if (this.blen) {
            t.color(this.c1r, this.c1g, this.c1b);
            //? >=1.0.0-beta.8.0.r
            //t.tex2(this.tc1);
            t.vertexUV(x0, y0, z1, u10, v10);
            t.color(this.c2r, this.c2g, this.c2b);
            //? >=1.0.0-beta.8.0.r
            //t.tex2(this.tc2);
            t.vertexUV(x0, y0, z0, u11, v11);
            t.color(this.c3r, this.c3g, this.c3b);
            //? >=1.0.0-beta.8.0.r
            //t.tex2(this.tc3);
            t.vertexUV(x0, y1, z0, u01, v01);
            t.color(this.c4r, this.c4g, this.c4b);
            //? >=1.0.0-beta.8.0.r
            //t.tex2(this.tc4);
            t.vertexUV(x0, y1, z1, u00, v00);
        } else {
            t.vertexUV(x0, y0, z1, u10, v10);
            t.vertexUV(x0, y0, z0, u11, v11);
            t.vertexUV(x0, y1, z0, u01, v01);
            t.vertexUV(x0, y1, z1, u00, v00);
        }
    }
}
