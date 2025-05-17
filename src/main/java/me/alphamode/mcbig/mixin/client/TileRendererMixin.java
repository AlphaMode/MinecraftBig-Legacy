package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.extensions.BigTileRendererExtension;
import me.alphamode.mcbig.level.tile.LiquidUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockShapes;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.renderer.TileRenderer;
import net.minecraft.util.Facing;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.LiquidTile;
import net.minecraft.world.level.tile.RailTile;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;
import java.math.BigInteger;

@Mixin(TileRenderer.class)
public abstract class TileRendererMixin implements BigTileRendererExtension {
    @Shadow private LevelSource level;

    @Shadow private boolean blen;

    @Shadow private float ll000;

    @Shadow private float llx00;

    @Shadow private float ll0y0;

    @Shadow private float ll00z;

    @Shadow private float llX00;

    @Shadow private float ll0Y0;

    @Shadow private float ll00Z;

    @Shadow private boolean field_70;

    @Shadow private boolean field_78;

    @Shadow private boolean field_74;

    @Shadow private boolean field_76;

    @Shadow private boolean field_71;

    @Shadow private boolean field_79;

    @Shadow private boolean field_73;

    @Shadow private boolean field_75;

    @Shadow private boolean field_72;

    @Shadow private boolean field_69;

    @Shadow private boolean field_80;

    @Shadow private boolean field_77;

    @Shadow private int fixedTexture;

    @Shadow private boolean noCulling;

    @Shadow private int blsmooth;

    @Shadow private float llxyz;

    @Shadow private float llxy0;

    @Shadow private float llxyZ;

    @Shadow private float ll0yz;

    @Shadow private float ll0yZ;

    @Shadow private float llXyz;

    @Shadow private float llXy0;

    @Shadow private float llXyZ;

    @Shadow private float c1r;

    @Shadow private float c2r;

    @Shadow private float c3r;

    @Shadow private float c4r;

    @Shadow private float c1g;

    @Shadow private float c2g;

    @Shadow private float c3g;

    @Shadow private float c4g;

    @Shadow private float c1b;

    @Shadow private float c2b;

    @Shadow private float c3b;

    @Shadow private float c4b;

    @Shadow private float llX0Z;

    @Shadow private float llXYZ;

    @Shadow private float llXY0;

    @Shadow private float llXYz;

    @Shadow private float llX0z;

    @Shadow public abstract void renderEast(Tile tile, double x, double y, double z, int texture);

    @Shadow public static boolean fancy;

    @Shadow public abstract void renderWest(Tile tile, double x, double y, double z, int texture);

    @Shadow private float llx0Z;

    @Shadow private float llxY0;

    @Shadow private float llxYZ;

    @Shadow private float llxYz;

    @Shadow private float llx0z;

    @Shadow public abstract void renderSouth(Tile tile, double x, double y, double z, int texture);

    @Shadow private float ll0YZ;

    @Shadow public abstract void renderNorth(Tile tile, double x, double y, double z, int texture);

    @Shadow private float ll0Yz;

    @Shadow private int field_91;

    @Shadow private int field_90;

    @Override
    public void renderFaceDown(Tile tile, BigDecimal x, double y, BigDecimal z, int tex) {
        Tesselator t = Tesselator.instance;
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }

        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u0 = ((double)xt + tile.xx0 * 16.0) / 256.0;
        double u1 = ((double)xt + tile.xx1 * 16.0 - 0.01) / 256.0;
        double v0 = ((double)yt + tile.zz0 * 16.0) / 256.0;
        double v1 = ((double)yt + tile.zz1 * 16.0 - 0.01) / 256.0;
        if (tile.xx0 < 0.0 || tile.xx1 > 1.0) {
            u0 = ((float)xt + 0.0F) / 256.0F;
            u1 = ((float)xt + 15.99F) / 256.0F;
        }

        if (tile.zz0 < 0.0 || tile.zz1 > 1.0) {
            v0 = ((float)yt + 0.0F) / 256.0F;
            v1 = ((float)yt + 15.99F) / 256.0F;
        }

        double var20 = u1;
        double var22 = u0;
        double var24 = v0;
        double var26 = v1;
        if (this.field_91 == 2) {
            u0 = ((double)xt + tile.zz0 * 16.0) / 256.0;
            v0 = ((double)(yt + 16) - tile.xx1 * 16.0) / 256.0;
            u1 = ((double)xt + tile.zz1 * 16.0) / 256.0;
            v1 = ((double)(yt + 16) - tile.xx0 * 16.0) / 256.0;
            var24 = v0;
            var26 = v1;
            var20 = u0;
            var22 = u1;
            v0 = v1;
            v1 = v0;
        } else if (this.field_91 == 1) {
            u0 = ((double)(xt + 16) - tile.zz1 * 16.0) / 256.0;
            v0 = ((double)yt + tile.xx0 * 16.0) / 256.0;
            u1 = ((double)(xt + 16) - tile.zz0 * 16.0) / 256.0;
            v1 = ((double)yt + tile.xx1 * 16.0) / 256.0;
            var20 = u1;
            var22 = u0;
            u0 = u1;
            u1 = u0;
            var24 = v1;
            var26 = v0;
        } else if (this.field_91 == 3) {
            u0 = ((double)(xt + 16) - tile.xx0 * 16.0) / 256.0;
            u1 = ((double)(xt + 16) - tile.xx1 * 16.0 - 0.01) / 256.0;
            v0 = ((double)(yt + 16) - tile.zz0 * 16.0) / 256.0;
            v1 = ((double)(yt + 16) - tile.zz1 * 16.0 - 0.01) / 256.0;
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
        double u0 = ((double)xt + tile.xx0 * 16.0) / 256.0;
        double u1 = ((double)xt + tile.xx1 * 16.0 - 0.01) / 256.0;
        double v0 = ((double)yt + tile.zz0 * 16.0) / 256.0;
        double v1 = ((double)yt + tile.zz1 * 16.0 - 0.01) / 256.0;
        if (tile.xx0 < 0.0 || tile.xx1 > 1.0) {
            u0 = ((float)xt + 0.0F) / 256.0F;
            u1 = ((float)xt + 15.99F) / 256.0F;
        }

        if (tile.zz0 < 0.0 || tile.zz1 > 1.0) {
            v0 = ((float)yt + 0.0F) / 256.0F;
            v1 = ((float)yt + 15.99F) / 256.0F;
        }

        double var20 = u1;
        double var22 = u0;
        double var24 = v0;
        double var26 = v1;
        if (this.field_90 == 1) {
            u0 = ((double)xt + tile.zz0 * 16.0) / 256.0;
            v0 = ((double)(yt + 16) - tile.xx1 * 16.0) / 256.0;
            u1 = ((double)xt + tile.zz1 * 16.0) / 256.0;
            v1 = ((double)(yt + 16) - tile.xx0 * 16.0) / 256.0;
            var24 = v0;
            var26 = v1;
            var20 = u0;
            var22 = u1;
            v0 = v1;
            v1 = v0;
        } else if (this.field_90 == 2) {
            u0 = ((double)(xt + 16) - tile.zz1 * 16.0) / 256.0;
            v0 = ((double)yt + tile.xx0 * 16.0) / 256.0;
            u1 = ((double)(xt + 16) - tile.zz0 * 16.0) / 256.0;
            v1 = ((double)yt + tile.xx1 * 16.0) / 256.0;
            var20 = u1;
            var22 = u0;
            u0 = u1;
            u1 = u0;
            var24 = v1;
            var26 = v0;
        } else if (this.field_90 == 3) {
            u0 = ((double)(xt + 16) - tile.xx0 * 16.0) / 256.0;
            u1 = ((double)(xt + 16) - tile.xx1 * 16.0 - 0.01) / 256.0;
            v0 = ((double)(yt + 16) - tile.zz0 * 16.0) / 256.0;
            v1 = ((double)(yt + 16) - tile.zz1 * 16.0 - 0.01) / 256.0;
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
    public boolean tesselateInWorld(Tile tile, BigInteger x, int y, BigInteger z) {
        int shape = tile.getRenderShape();
        tile.updateShape(this.level, x, y, z);
        if (shape == 0) {
            return tesselateBlockInWorld(tile, x, y, z);
        } else if (shape == BlockShapes.LIQUID) {
            return tesselateWaterInWorld(tile, x, y, z);
//        } else if (shape == BlockShapes.CACTUS) {
//            return this.tesselateCactusInWorld(tile, x, y, z);
//        } else if (shape == BlockShapes.REEDS) {
//            return this.tesselateCrossInWorld(tile, x, y, z);
//        } else if (shape == BlockShapes.CROP) {
//            return this.tesselateRowInWorld(tile, x, y, z);
//        } else if (shape == BlockShapes.TORCH) {
//            return this.tesselateTorchInWorld(tile, x, y, z);
//        } else if (shape == BlockShapes.FIRE) {
//            return this.tesselateFireInWorld(tile, x, y, z);
//        } else if (shape == BlockShapes.REDSTONE) {
//            return this.tesselateDustInWorld(tile, x, y, z);
//        } else if (shape == BlockShapes.LADDER) {
//            return this.tesselateLadderInWorld(tile, x, y, z);
//        } else if (shape == BlockShapes.DOOR) {
//            return this.tesselateDoorInWorld(tile, x, y, z);
//        } else if (shape == BlockShapes.RAILS) {
//            return this.tesselateRailInWorld((RailTile)tile, x, y, z);
//        } else if (shape == BlockShapes.STAIRS) {
//            return this.tesselateStairsInWorld(tile, x, y, z);
//        } else if (shape == BlockShapes.FENCE) {
//            return this.tesselateFenceInWorld(tile, x, y, z);
//        } else if (shape == BlockShapes.LEVER) {
//            return this.tesselateLeverInWorld(tile, x, y, z);
//        } else if (shape == BlockShapes.BED) {
//            return this.tesselateBedInWorld(tile, x, y, z);
//        } else if (shape == BlockShapes.REPEATER) {
//            return this.tesselateRepeaterInWorld(tile, x, y, z);
//        } else if (shape == BlockShapes.PISTON) {
//            return this.tesselatePistonInWorld(tile, x, y, z, false);
//        } else {
//            return shape == BlockShapes.PISTON_HEAD ? this.tesselateHeadPistonInWorld(tile, x, y, z, true) : false;
        }
        return tesselateBlockInWorld(tile, x, y, z);
    }

    @Override
    public boolean tesselateBlockInWorld(Tile tile, BigInteger x, int y, BigInteger z) {
        int color = tile.getFoliageColor(this.level, x, y, z);
        float r = (float)(color >> 16 & 0xFF) / 255.0F;
        float g = (float)(color >> 8 & 0xFF) / 255.0F;
        float b = (float)(color & 0xFF) / 255.0F;
        if (GameRenderer.anaglyph3d) {
            float var9 = (r * 30.0F + g * 59.0F + b * 11.0F) / 100.0F;
            float var10 = (r * 30.0F + g * 70.0F) / 100.0F;
            float var11 = (r * 30.0F + b * 70.0F) / 100.0F;
            r = var9;
            g = var10;
            b = var11;
        }

        return Minecraft.useAmbientOcclusion()
                ? this.tesselateBlockInWorldWithAmbienceOcclusion(tile, x, y, z, r, g, b)
                : this.tesselateBlockInWorld(tile, x, y, z, r, g, b);
    }

    @Override
    public boolean tesselateBlockInWorldWithAmbienceOcclusion(Tile tile, BigInteger x, int y, BigInteger z, float f, float g, float h) {
        this.blen = true;
        boolean var8 = false;
        float var9 = this.ll000;
        float var10 = this.ll000;
        float var11 = this.ll000;
        float var12 = this.ll000;
        boolean var13 = true;
        boolean var14 = true;
        boolean var15 = true;
        boolean var16 = true;
        boolean var17 = true;
        boolean var18 = true;
        this.ll000 = tile.getBrightness(this.level, x, y, z);
        this.llx00 = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y, z);
        this.ll0y0 = tile.getBrightness(this.level, x, y - 1, z);
        this.ll00z = tile.getBrightness(this.level, x, y, z.subtract(BigInteger.ONE));
        this.llX00 = tile.getBrightness(this.level, x.add(BigInteger.ONE), y, z);
        this.ll0Y0 = tile.getBrightness(this.level, x, y + 1, z);
        this.ll00Z = tile.getBrightness(this.level, x, y, z.add(BigInteger.ONE));
        this.field_70 = Tile.translucent[this.level.getTile(x.add(BigInteger.ONE), y + 1, z)];
        this.field_78 = Tile.translucent[this.level.getTile(x.add(BigInteger.ONE), y - 1, z)];
        this.field_74 = Tile.translucent[this.level.getTile(x.add(BigInteger.ONE), y, z.add(BigInteger.ONE))];
        this.field_76 = Tile.translucent[this.level.getTile(x.add(BigInteger.ONE), y, z.subtract(BigInteger.ONE))];
        this.field_71 = Tile.translucent[this.level.getTile(x.subtract(BigInteger.ONE), y + 1, z)];
        this.field_79 = Tile.translucent[this.level.getTile(x.subtract(BigInteger.ONE), y - 1, z)];
        this.field_73 = Tile.translucent[this.level.getTile(x.subtract(BigInteger.ONE), y, z.subtract(BigInteger.ONE))];
        this.field_75 = Tile.translucent[this.level.getTile(x.subtract(BigInteger.ONE), y, z.add(BigInteger.ONE))];
        this.field_72 = Tile.translucent[this.level.getTile(x, y + 1, z.add(BigInteger.ONE))];
        this.field_69 = Tile.translucent[this.level.getTile(x, y + 1, z.subtract(BigInteger.ONE))];
        this.field_80 = Tile.translucent[this.level.getTile(x, y - 1, z.add(BigInteger.ONE))];
        this.field_77 = Tile.translucent[this.level.getTile(x, y - 1, z.subtract(BigInteger.ONE))];
        if (tile.tex == 3) {
            var18 = false;
            var17 = false;
            var16 = false;
            var15 = false;
            var13 = false;
        }

        if (this.fixedTexture >= 0) {
            var18 = false;
            var17 = false;
            var16 = false;
            var15 = false;
            var13 = false;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x, y - 1, z, Facing.DOWN)) {
            if (this.blsmooth <= 0) {
                var12 = this.ll0y0;
                var11 = this.ll0y0;
                var10 = this.ll0y0;
                var9 = this.ll0y0;
            } else {
                this.llxyz = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), --y, z);
                this.llxy0 = tile.getBrightness(this.level, x, y, z.subtract(BigInteger.ONE));
                this.llxyZ = tile.getBrightness(this.level, x, y, z.add(BigInteger.ONE));
                this.ll0yz = tile.getBrightness(this.level, x.add(BigInteger.ONE), y, z);
                if (!this.field_77 && !this.field_79) {
                    this.ll0yZ = this.llxyz;
                } else {
                    this.ll0yZ = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y, z.subtract(BigInteger.ONE));
                }

                if (!this.field_80 && !this.field_79) {
                    this.llXyz = this.llxyz;
                } else {
                    this.llXyz = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y, z.add(BigInteger.ONE));
                }

                if (!this.field_77 && !this.field_78) {
                    this.llXy0 = this.ll0yz;
                } else {
                    this.llXy0 = tile.getBrightness(this.level, x.add(BigInteger.ONE), y, z.subtract(BigInteger.ONE));
                }

                if (!this.field_80 && !this.field_78) {
                    this.llXyZ = this.ll0yz;
                } else {
                    this.llXyZ = tile.getBrightness(this.level, x.add(BigInteger.ONE), y, z.add(BigInteger.ONE));
                }

                ++y;
                var9 = (this.llXyz + this.llxyz + this.llxyZ + this.ll0y0) / 4.0F;
                var12 = (this.llxyZ + this.ll0y0 + this.llXyZ + this.ll0yz) / 4.0F;
                var11 = (this.ll0y0 + this.llxy0 + this.ll0yz + this.llXy0) / 4.0F;
                var10 = (this.llxyz + this.ll0yZ + this.ll0y0 + this.llxy0) / 4.0F;
            }

            this.c1r = this.c2r = this.c3r = this.c4r = (var13 ? f : 1.0F) * 0.5F;
            this.c1g = this.c2g = this.c3g = this.c4g = (var13 ? g : 1.0F) * 0.5F;
            this.c1b = this.c2b = this.c3b = this.c4b = (var13 ? h : 1.0F) * 0.5F;
            this.c1r *= var9;
            this.c1g *= var9;
            this.c1b *= var9;
            this.c2r *= var10;
            this.c2g *= var10;
            this.c2b *= var10;
            this.c3r *= var11;
            this.c3g *= var11;
            this.c3b *= var11;
            this.c4r *= var12;
            this.c4g *= var12;
            this.c4b *= var12;
            renderFaceDown(tile, new BigDecimal(x), y, new BigDecimal(z), tile.getTexture(this.level, x, y, z, Facing.DOWN));
            var8 = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x, y + 1, z, Facing.UP)) {
            if (this.blsmooth <= 0) {
                var12 = this.ll0Y0;
                var11 = this.ll0Y0;
                var10 = this.ll0Y0;
                var9 = this.ll0Y0;
            } else {
                this.llxY0 = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), ++y, z);
                this.llXY0 = tile.getBrightness(this.level, x.add(BigInteger.ONE), y, z);
                this.ll0Yz = tile.getBrightness(this.level, x, y, z.subtract(BigInteger.ONE));
                this.ll0YZ = tile.getBrightness(this.level, x, y, z.add(BigInteger.ONE));
                if (!this.field_69 && !this.field_71) {
                    this.llxYz = this.llxY0;
                } else {
                    this.llxYz = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y, z.subtract(BigInteger.ONE));
                }

                if (!this.field_69 && !this.field_70) {
                    this.llXYz = this.llXY0;
                } else {
                    this.llXYz = tile.getBrightness(this.level, x.add(BigInteger.ONE), y, z.subtract(BigInteger.ONE));
                }

                if (!this.field_72 && !this.field_71) {
                    this.llxYZ = this.llxY0;
                } else {
                    this.llxYZ = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y, z.add(BigInteger.ONE));
                }

                if (!this.field_72 && !this.field_70) {
                    this.llXYZ = this.llXY0;
                } else {
                    this.llXYZ = tile.getBrightness(this.level, x.add(BigInteger.ONE), y, z.add(BigInteger.ONE));
                }

                --y;
                var12 = (this.llxYZ + this.llxY0 + this.ll0YZ + this.ll0Y0) / 4.0F;
                var9 = (this.ll0YZ + this.ll0Y0 + this.llXYZ + this.llXY0) / 4.0F;
                var10 = (this.ll0Y0 + this.ll0Yz + this.llXY0 + this.llXYz) / 4.0F;
                var11 = (this.llxY0 + this.llxYz + this.ll0Y0 + this.ll0Yz) / 4.0F;
            }

            this.c1r = this.c2r = this.c3r = this.c4r = var14 ? f : 1.0F;
            this.c1g = this.c2g = this.c3g = this.c4g = var14 ? g : 1.0F;
            this.c1b = this.c2b = this.c3b = this.c4b = var14 ? h : 1.0F;
            this.c1r *= var9;
            this.c1g *= var9;
            this.c1b *= var9;
            this.c2r *= var10;
            this.c2g *= var10;
            this.c2b *= var10;
            this.c3r *= var11;
            this.c3g *= var11;
            this.c3b *= var11;
            this.c4r *= var12;
            this.c4g *= var12;
            this.c4b *= var12;
            renderFaceUp(tile, new BigDecimal(x), y, new BigDecimal(z), tile.getTexture(this.level, x, y, z, Facing.UP));
            var8 = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x, y, z.subtract(BigInteger.ONE), Facing.NORTH)) {
            if (this.blsmooth <= 0) {
                var12 = this.ll00z;
                var11 = this.ll00z;
                var10 = this.ll00z;
                var9 = this.ll00z;
            } else {
                this.llx0z = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y, z = z.subtract(BigInteger.ONE));
                this.llxy0 = tile.getBrightness(this.level, x, y - 1, z);
                this.ll0Yz = tile.getBrightness(this.level, x, y + 1, z);
                this.llX0z = tile.getBrightness(this.level, x.add(BigInteger.ONE), y, z);
                if (!this.field_73 && !this.field_77) {
                    this.ll0yZ = this.llx0z;
                } else {
                    this.ll0yZ = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y - 1, z);
                }

                if (!this.field_73 && !this.field_69) {
                    this.llxYz = this.llx0z;
                } else {
                    this.llxYz = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y + 1, z);
                }

                if (!this.field_76 && !this.field_77) {
                    this.llXy0 = this.llX0z;
                } else {
                    this.llXy0 = tile.getBrightness(this.level, x.add(BigInteger.ONE), y - 1, z);
                }

                if (!this.field_76 && !this.field_69) {
                    this.llXYz = this.llX0z;
                } else {
                    this.llXYz = tile.getBrightness(this.level, x.add(BigInteger.ONE), y + 1, z);
                }

                z = z.add(BigInteger.ONE);
                var9 = (this.llx0z + this.llxYz + this.ll00z + this.ll0Yz) / 4.0F;
                var10 = (this.ll00z + this.ll0Yz + this.llX0z + this.llXYz) / 4.0F;
                var11 = (this.llxy0 + this.ll00z + this.llXy0 + this.llX0z) / 4.0F;
                var12 = (this.ll0yZ + this.llx0z + this.llxy0 + this.ll00z) / 4.0F;
            }

            this.c1r = this.c2r = this.c3r = this.c4r = (var15 ? f : 1.0F) * 0.8F;
            this.c1g = this.c2g = this.c3g = this.c4g = (var15 ? g : 1.0F) * 0.8F;
            this.c1b = this.c2b = this.c3b = this.c4b = (var15 ? h : 1.0F) * 0.8F;
            this.c1r *= var9;
            this.c1g *= var9;
            this.c1b *= var9;
            this.c2r *= var10;
            this.c2g *= var10;
            this.c2b *= var10;
            this.c3r *= var11;
            this.c3g *= var11;
            this.c3b *= var11;
            this.c4r *= var12;
            this.c4g *= var12;
            this.c4b *= var12;
            int var19 = tile.getTexture(this.level, x, y, z, Facing.NORTH);
            this.renderNorth(tile, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), var19);
            if (fancy && var19 == 3 && this.fixedTexture < 0) {
                this.c1r *= f;
                this.c2r *= f;
                this.c3r *= f;
                this.c4r *= f;
                this.c1g *= g;
                this.c2g *= g;
                this.c3g *= g;
                this.c4g *= g;
                this.c1b *= h;
                this.c2b *= h;
                this.c3b *= h;
                this.c4b *= h;
                renderNorth(tile, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), 38);
            }

            var8 = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x, y, z.add(BigInteger.ONE), Facing.SOUTH)) {
            if (this.blsmooth <= 0) {
                var12 = this.ll00Z;
                var11 = this.ll00Z;
                var10 = this.ll00Z;
                var9 = this.ll00Z;
            } else {
                this.llx0Z = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y, z = z.add(BigInteger.ONE));
                this.llX0Z = tile.getBrightness(this.level, x.add(BigInteger.ONE), y, z);
                this.llxyZ = tile.getBrightness(this.level, x, y - 1, z);
                this.ll0YZ = tile.getBrightness(this.level, x, y + 1, z);
                if (!this.field_75 && !this.field_80) {
                    this.llXyz = this.llx0Z;
                } else {
                    this.llXyz = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y - 1, z);
                }

                if (!this.field_75 && !this.field_72) {
                    this.llxYZ = this.llx0Z;
                } else {
                    this.llxYZ = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y + 1, z);
                }

                if (!this.field_74 && !this.field_80) {
                    this.llXyZ = this.llX0Z;
                } else {
                    this.llXyZ = tile.getBrightness(this.level, x.add(BigInteger.ONE), y - 1, z);
                }

                if (!this.field_74 && !this.field_72) {
                    this.llXYZ = this.llX0Z;
                } else {
                    this.llXYZ = tile.getBrightness(this.level, x.add(BigInteger.ONE), y + 1, z);
                }

                z = z.subtract(BigInteger.ONE);
                var9 = (this.llx0Z + this.llxYZ + this.ll00Z + this.ll0YZ) / 4.0F;
                var12 = (this.ll00Z + this.ll0YZ + this.llX0Z + this.llXYZ) / 4.0F;
                var11 = (this.llxyZ + this.ll00Z + this.llXyZ + this.llX0Z) / 4.0F;
                var10 = (this.llXyz + this.llx0Z + this.llxyZ + this.ll00Z) / 4.0F;
            }

            this.c1r = this.c2r = this.c3r = this.c4r = (var16 ? f : 1.0F) * 0.8F;
            this.c1g = this.c2g = this.c3g = this.c4g = (var16 ? g : 1.0F) * 0.8F;
            this.c1b = this.c2b = this.c3b = this.c4b = (var16 ? h : 1.0F) * 0.8F;
            this.c1r *= var9;
            this.c1g *= var9;
            this.c1b *= var9;
            this.c2r *= var10;
            this.c2g *= var10;
            this.c2b *= var10;
            this.c3r *= var11;
            this.c3g *= var11;
            this.c3b *= var11;
            this.c4r *= var12;
            this.c4g *= var12;
            this.c4b *= var12;
            int var50 = tile.getTexture(this.level, x, y, z, Facing.SOUTH);
            renderSouth(tile, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), tile.getTexture(this.level, x, y, z, Facing.SOUTH));
            if (fancy && var50 == 3 && this.fixedTexture < 0) {
                this.c1r *= f;
                this.c2r *= f;
                this.c3r *= f;
                this.c4r *= f;
                this.c1g *= g;
                this.c2g *= g;
                this.c3g *= g;
                this.c4g *= g;
                this.c1b *= h;
                this.c2b *= h;
                this.c3b *= h;
                this.c4b *= h;
                renderSouth(tile, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), 38);
            }

            var8 = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x.subtract(BigInteger.ONE), y, z, Facing.WEST)) {
            if (this.blsmooth <= 0) {
                var12 = this.llx00;
                var11 = this.llx00;
                var10 = this.llx00;
                var9 = this.llx00;
            } else {
                this.llxyz = tile.getBrightness(this.level, x = x.subtract(BigInteger.ONE), y - 1, z);
                this.llx0z = tile.getBrightness(this.level, x, y, z.subtract(BigInteger.ONE));
                this.llx0Z = tile.getBrightness(this.level, x, y, z.add(BigInteger.ONE));
                this.llxY0 = tile.getBrightness(this.level, x, y + 1, z);
                if (!this.field_73 && !this.field_79) {
                    this.ll0yZ = this.llx0z;
                } else {
                    this.ll0yZ = tile.getBrightness(this.level, x, y - 1, z.subtract(BigInteger.ONE));
                }

                if (!this.field_75 && !this.field_79) {
                    this.llXyz = this.llx0Z;
                } else {
                    this.llXyz = tile.getBrightness(this.level, x, y - 1, z.add(BigInteger.ONE));
                }

                if (!this.field_73 && !this.field_71) {
                    this.llxYz = this.llx0z;
                } else {
                    this.llxYz = tile.getBrightness(this.level, x, y + 1, z.subtract(BigInteger.ONE));
                }

                if (!this.field_75 && !this.field_71) {
                    this.llxYZ = this.llx0Z;
                } else {
                    this.llxYZ = tile.getBrightness(this.level, x, y + 1, z.add(BigInteger.ONE));
                }

                x = x.add(BigInteger.ONE);
                var12 = (this.llxyz + this.llXyz + this.llx00 + this.llx0Z) / 4.0F;
                var9 = (this.llx00 + this.llx0Z + this.llxY0 + this.llxYZ) / 4.0F;
                var10 = (this.llx0z + this.llx00 + this.llxYz + this.llxY0) / 4.0F;
                var11 = (this.ll0yZ + this.llxyz + this.llx0z + this.llx00) / 4.0F;
            }

            this.c1r = this.c2r = this.c3r = this.c4r = (var17 ? f : 1.0F) * 0.6F;
            this.c1g = this.c2g = this.c3g = this.c4g = (var17 ? g : 1.0F) * 0.6F;
            this.c1b = this.c2b = this.c3b = this.c4b = (var17 ? h : 1.0F) * 0.6F;
            this.c1r *= var9;
            this.c1g *= var9;
            this.c1b *= var9;
            this.c2r *= var10;
            this.c2g *= var10;
            this.c2b *= var10;
            this.c3r *= var11;
            this.c3g *= var11;
            this.c3b *= var11;
            this.c4r *= var12;
            this.c4g *= var12;
            this.c4b *= var12;
            int var51 = tile.getTexture(this.level, x, y, z, Facing.WEST);
            this.renderWest(tile, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), var51);
            if (fancy && var51 == 3 && this.fixedTexture < 0) {
                this.c1r *= f;
                this.c2r *= f;
                this.c3r *= f;
                this.c4r *= f;
                this.c1g *= g;
                this.c2g *= g;
                this.c3g *= g;
                this.c4g *= g;
                this.c1b *= h;
                this.c2b *= h;
                this.c3b *= h;
                this.c4b *= h;
                this.renderWest(tile, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), 38);
            }

            var8 = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x.add(BigInteger.ONE), y, z, Facing.EAST)) {
            if (this.blsmooth <= 0) {
                var12 = this.llX00;
                var11 = this.llX00;
                var10 = this.llX00;
                var9 = this.llX00;
            } else {
                this.ll0yz = tile.getBrightness(this.level, x = x.add(BigInteger.ONE), y - 1, z);
                this.llX0z = tile.getBrightness(this.level, x, y, z.subtract(BigInteger.ONE));
                this.llX0Z = tile.getBrightness(this.level, x, y, z.add(BigInteger.ONE));
                this.llXY0 = tile.getBrightness(this.level, x, y + 1, z);
                if (!this.field_78 && !this.field_76) {
                    this.llXy0 = this.llX0z;
                } else {
                    this.llXy0 = tile.getBrightness(this.level, x, y - 1, z.subtract(BigInteger.ONE));
                }

                if (!this.field_78 && !this.field_74) {
                    this.llXyZ = this.llX0Z;
                } else {
                    this.llXyZ = tile.getBrightness(this.level, x, y - 1, z.add(BigInteger.ONE));
                }

                if (!this.field_70 && !this.field_76) {
                    this.llXYz = this.llX0z;
                } else {
                    this.llXYz = tile.getBrightness(this.level, x, y + 1, z.subtract(BigInteger.ONE));
                }

                if (!this.field_70 && !this.field_74) {
                    this.llXYZ = this.llX0Z;
                } else {
                    this.llXYZ = tile.getBrightness(this.level, x, y + 1, z.add(BigInteger.ONE));
                }

                x = x.subtract(BigInteger.ONE);
                var9 = (this.ll0yz + this.llXyZ + this.llX00 + this.llX0Z) / 4.0F;
                var12 = (this.llX00 + this.llX0Z + this.llXY0 + this.llXYZ) / 4.0F;
                var11 = (this.llX0z + this.llX00 + this.llXYz + this.llXY0) / 4.0F;
                var10 = (this.llXy0 + this.ll0yz + this.llX0z + this.llX00) / 4.0F;
            }

            this.c1r = this.c2r = this.c3r = this.c4r = (var18 ? f : 1.0F) * 0.6F;
            this.c1g = this.c2g = this.c3g = this.c4g = (var18 ? g : 1.0F) * 0.6F;
            this.c1b = this.c2b = this.c3b = this.c4b = (var18 ? h : 1.0F) * 0.6F;
            this.c1r *= var9;
            this.c1g *= var9;
            this.c1b *= var9;
            this.c2r *= var10;
            this.c2g *= var10;
            this.c2b *= var10;
            this.c3r *= var11;
            this.c3g *= var11;
            this.c3b *= var11;
            this.c4r *= var12;
            this.c4g *= var12;
            this.c4b *= var12;
            int var52 = tile.getTexture(this.level, x, y, z, Facing.EAST);
            this.renderEast(tile, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), var52);
            if (fancy && var52 == 3 && this.fixedTexture < 0) {
                this.c1r *= f;
                this.c2r *= f;
                this.c3r *= f;
                this.c4r *= f;
                this.c1g *= g;
                this.c2g *= g;
                this.c3g *= g;
                this.c4g *= g;
                this.c1b *= h;
                this.c2b *= h;
                this.c3b *= h;
                this.c4b *= h;
                this.renderEast(tile, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), 38);
            }

            var8 = true;
        }

        this.blen = false;
        return var8;
    }

    @Override
    public boolean tesselateBlockInWorld(Tile tile, BigInteger x, int y, BigInteger z, float r, float g, float b) {
        this.blen = false;
        Tesselator t = Tesselator.instance;
        boolean changed = false;
        float c10 = 0.5F;
        float c11 = 1.0F;
        float c2 = 0.8F;
        float c3 = 0.6F;
        float r11 = c11 * r;
        float g11 = c11 * g;
        float b11 = c11 * b;
        float r10 = c10;
        float r2 = c2;
        float r3 = c3;
        float g10 = c10;
        float g2 = c2;
        float g3 = c3;
        float b10 = c10;
        float b2 = c2;
        float b3 = c3;
        if (tile != Tile.GRASS) {
            r10 = c10 * r;
            r2 = c2 * r;
            r3 = c3 * r;
            g10 = c10 * g;
            g2 = c2 * g;
            g3 = c3 * g;
            b10 = c10 * b;
            b2 = c2 * b;
            b3 = c3 * b;
        }

        float centerBrightness = tile.getBrightness(this.level, x, y, z);
        if (this.noCulling || tile.shouldRenderFace(this.level, x, y - 1, z, Facing.DOWN)) {
            float br = tile.getBrightness(this.level, x, y - 1, z);
            t.color(r10 * br, g10 * br, b10 * br);
            renderFaceDown(tile, new BigDecimal(x), (double)y, new BigDecimal(z), tile.getTexture(this.level, x, y, z, Facing.DOWN));
            changed = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x, y + 1, z, Facing.UP)) {
            float br = tile.getBrightness(this.level, x, y + 1, z);
            if (tile.yy1 != 1.0 && !tile.material.isLiquid()) {
                br = centerBrightness;
            }

            t.color(r11 * br, g11 * br, b11 * br);
            renderFaceUp(tile, new BigDecimal(x), y, new BigDecimal(z), tile.getTexture(this.level, x, y, z, Facing.UP));
            changed = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x, y, z.subtract(BigInteger.ONE), Facing.NORTH)) {
            float br = tile.getBrightness(this.level, x, y, z.subtract(BigInteger.ONE));
            if (tile.zz0 > 0.0) {
                br = centerBrightness;
            }

            t.color(r2 * br, g2 * br, b2 * br);
            int texture = tile.getTexture(this.level, x, y, z, Facing.NORTH);
            this.renderNorth(tile, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), texture);
            if (fancy && texture == 3 && this.fixedTexture < 0) {
                t.color(r2 * br * r, g2 * br * g, b2 * br * b);
                this.renderNorth(tile, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), 38);
            }

            changed = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x, y, z.add(BigInteger.ONE), Facing.SOUTH)) {
            float br = tile.getBrightness(this.level, x, y, z.add(BigInteger.ONE));
            if (tile.zz1 < 1.0) {
                br = centerBrightness;
            }

            t.color(r2 * br, g2 * br, b2 * br);
            int texture = tile.getTexture(this.level, x, y, z, Facing.SOUTH);
            this.renderSouth(tile, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), texture);
            if (fancy && texture == 3 && this.fixedTexture < 0) {
                t.color(r2 * br * r, g2 * br * g, b2 * br * b);
                this.renderSouth(tile, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), 38);
            }

            changed = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x.subtract(BigInteger.ONE), y, z, Facing.WEST)) {
            float br = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y, z);
            if (tile.xx0 > 0.0) {
                br = centerBrightness;
            }

            t.color(r3 * br, g3 * br, b3 * br);
            int var35 = tile.getTexture(this.level, x, y, z, Facing.WEST);
            this.renderWest(tile, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), var35);
            if (fancy && var35 == 3 && this.fixedTexture < 0) {
                t.color(r3 * br * r, g3 * br * g, b3 * br * b);
                this.renderWest(tile, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), 38);
            }

            changed = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x.add(BigInteger.ONE), y, z, Facing.EAST)) {
            float var33 = tile.getBrightness(this.level, x.add(BigInteger.ONE), y, z);
            if (tile.xx1 < 1.0) {
                var33 = centerBrightness;
            }

            t.color(r3 * var33, g3 * var33, b3 * var33);
            int var36 = tile.getTexture(this.level, x, y, z, Facing.EAST);
            this.renderEast(tile, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), var36);
            if (fancy && var36 == 3 && this.fixedTexture < 0) {
                t.color(r3 * var33 * r, g3 * var33 * g, b3 * var33 * b);
                this.renderEast(tile, (double)x.doubleValue(), (double)y, (double)z.doubleValue(), 38);
            }

            changed = true;
        }

        return changed;
    }

    @Override
    public boolean tesselateWaterInWorld(Tile tt, BigInteger x, int y, BigInteger z) {
        Tesselator t = Tesselator.instance;
        int col = tt.getFoliageColor(this.level, x, y, z);
        float var7 = (float)(col >> 16 & 0xFF) / 255.0F;
        float var8 = (float)(col >> 8 & 0xFF) / 255.0F;
        float var9 = (float)(col & 0xFF) / 255.0F;
        boolean up = tt.shouldRenderFace(this.level, x, y + 1, z, Facing.UP);
        boolean down = tt.shouldRenderFace(this.level, x, y - 1, z, Facing.DOWN);
        boolean[] dirs = new boolean[]{
                tt.shouldRenderFace(this.level, x, y, z.subtract(BigInteger.ONE), Facing.NORTH),
                tt.shouldRenderFace(this.level, x, y, z.add(BigInteger.ONE), Facing.SOUTH),
                tt.shouldRenderFace(this.level, x.subtract(BigInteger.ONE), y, z, Facing.WEST),
                tt.shouldRenderFace(this.level, x.add(BigInteger.ONE), y, z, Facing.EAST)
        };
        if (!up && !down && !dirs[0] && !dirs[1] && !dirs[2] && !dirs[3]) {
            return false;
        } else {
            boolean changed = false;
            float c10 = 0.5F;
            float c11 = 1.0F;
            float c2 = 0.8F;
            float c3 = 0.6F;
            double yo0 = 0.0;
            double yo1 = 1.0;
            Material m = tt.material;
            int data = this.level.getData(x, y, z);
            float h0 = getWaterHeight(x, y, z, m);
            float h1 = getWaterHeight(x, y, z.add(BigInteger.ONE), m);
            float h2 = getWaterHeight(x.add(BigInteger.ONE), y, z.add(BigInteger.ONE), m);
            float h3 = getWaterHeight(x.add(BigInteger.ONE), y, z, m);
            if (this.noCulling || up) {
                changed = true;
                int tex = tt.getTexture(Facing.UP, data);
                float angle = (float) LiquidUtil.getSlopeAngle(this.level, x, y, z, m);
                if (angle > -999.0F) {
                    tex = tt.getTexture(2, data);
                }

                int xt = (tex & 15) << 4;
                int yt = tex & 240;
                double uc = ((double)xt + 8.0) / 256.0;
                double vc = ((double)yt + 8.0) / 256.0;
                if (angle < -999.0F) {
                    angle = 0.0F;
                } else {
                    uc = (double)((float)(xt + 16) / 256.0F);
                    vc = (double)((float)(yt + 16) / 256.0F);
                }

                float s = Mth.sin(angle) * 8.0F / 256.0F;
                float c = Mth.cos(angle) * 8.0F / 256.0F;
                float br = tt.getBrightness(this.level, x, y, z);
                t.color(c11 * br * var7, c11 * br * var8, c11 * br * var9);
                t.vertexUV((double)(x.doubleValue()), (double)((float)y + h0), (double)(z).doubleValue(), uc - (double)c - (double)s, vc - (double)c + (double)s);
                t.vertexUV((double)(x.doubleValue()), (double)((float)y + h1), (double)(z.add(BigInteger.ONE)).doubleValue(), uc - (double)c + (double)s, vc + (double)c + (double)s);
                t.vertexUV((double)(x.add(BigInteger.ONE)).doubleValue(), (double)((float)y + h2), (double)(z.add(BigInteger.ONE)).doubleValue(), uc + (double)c + (double)s, vc + (double)c - (double)s);
                t.vertexUV((double)(x.add(BigInteger.ONE)).doubleValue(), (double)((float)y + h3), (double)(z).doubleValue(), uc + (double)c - (double)s, vc - (double)c - (double)s);
            }

            if (this.noCulling || down) {
                float br = tt.getBrightness(this.level, x, y - 1, z);
                t.color(c10 * br, c10 * br, c10 * br);
                renderFaceDown(tt, new BigDecimal(x), y, new BigDecimal(z), tt.getTexture(Facing.DOWN));
                changed = true;
            }

            for(int face = 0; face < 4; ++face) {
                BigInteger xt = x;
                BigInteger zt = z;
                if (face == 0) {
                    zt = z.subtract(BigInteger.ONE);
                }

                if (face == 1) {
                    zt = zt.add(BigInteger.ONE);
                }

                if (face == 2) {
                    xt = x.subtract(BigInteger.ONE);
                }

                if (face == 3) {
                    xt = xt.add(BigInteger.ONE);
                }

                int texx = tt.getTexture(face + 2, data);
                int xTex = (texx & 15) << 4;
                int yTex = texx & 240;
                if (this.noCulling || dirs[face]) {
                    float hh0;
                    double x1;
                    double z1;
                    float hh1;
                    double x0;
                    double z0;
                    if (face == 0) {
                        hh0 = h0;
                        hh1 = h3;
                        x0 = x.doubleValue();
                        x1 = (x.add(BigInteger.ONE)).doubleValue();
                        z0 = z.doubleValue();
                        z1 = z.doubleValue();
                    } else if (face == 1) {
                        hh0 = h2;
                        hh1 = h1;
                        x0 = (x.add(BigInteger.ONE)).doubleValue();
                        x1 = x.doubleValue();
                        z0 = (z.add(BigInteger.ONE)).doubleValue();
                        z1 = (z.add(BigInteger.ONE)).doubleValue();
                    } else if (face == 2) {
                        hh0 = h1;
                        hh1 = h0;
                        x0 = x.doubleValue();
                        x1 = x.doubleValue();
                        z0 = (z.add(BigInteger.ONE)).doubleValue();
                        z1 = z.doubleValue();
                    } else {
                        hh0 = h3;
                        hh1 = h2;
                        x0 = (x.add(BigInteger.ONE)).doubleValue();
                        x1 = (x.add(BigInteger.ONE)).doubleValue();
                        z0 = z.doubleValue();
                        z1 = (z.add(BigInteger.ONE)).doubleValue();
                    }

                    changed = true;
                    double u0 = (double)((float)(xTex + 0) / 256.0F);
                    double u1 = ((double)(xTex + 16) - 0.01) / 256.0;
                    double v01 = (double)(((float)yTex + (1.0F - hh0) * 16.0F) / 256.0F);
                    double v02 = (double)(((float)yTex + (1.0F - hh1) * 16.0F) / 256.0F);
                    double v1 = ((double)(yTex + 16) - 0.01) / 256.0;
                    float br = tt.getBrightness(this.level, xt, y, zt);
                    if (face < 2) {
                        br *= c2;
                    } else {
                        br *= c3;
                    }

                    t.color(c11 * br * var7, c11 * br * var8, c11 * br * var9);
                    t.vertexUV((double)x0, (double)((float)y + hh0), (double)z0, u0, v01);
                    t.vertexUV((double)x1, (double)((float)y + hh1), (double)z1, u1, v02);
                    t.vertexUV((double)x1, (double)(y + 0), (double)z1, u1, v1);
                    t.vertexUV((double)x0, (double)(y + 0), (double)z0, u0, v1);
                }
            }

            tt.yy0 = yo0;
            tt.yy1 = yo1;
            return changed;
        }
    }

    private float getWaterHeight(BigInteger x, int y, BigInteger z, Material m) {
        int count = 0;
        float h = 0.0F;

        for(int i = 0; i < 4; ++i) {
            BigInteger xx = x.subtract(BigInteger.valueOf((i & 1)));
            BigInteger zz = z.subtract(BigInteger.valueOf((i >> 1 & 1)));
            if (this.level.getMaterial(xx, y + 1, zz) == m) {
                return 1.0F;
            }

            Material tm = this.level.getMaterial(xx, y, zz);
            if (tm == m) {
                int d = this.level.getData(xx, y, zz);
                if (d >= 8 || d == 0) {
                    h += LiquidTile.getHeight(d) * 10.0F;
                    count += 10;
                }

                h += LiquidTile.getHeight(d);
                ++count;
            } else if (!tm.isSolid()) {
                ++h;
                ++count;
            }
        }

        return 1.0F - h / (float)count;
    }
}
