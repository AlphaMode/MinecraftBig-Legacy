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
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;
import java.math.BigInteger;

@Mixin(TileRenderer.class)
public abstract class TileRendererMixin implements BigTileRendererExtension, me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension {
    @Shadow
    private LevelSource level;

    @Shadow
    private boolean blen;

    @Shadow
    private float ll000;

    @Shadow
    private float llx00;

    @Shadow
    private float ll0y0;

    @Shadow
    private float ll00z;

    @Shadow
    private float llX00;

    @Shadow
    private float ll0Y0;

    @Shadow
    private float ll00Z;

    @Shadow
    private int fixedTexture;

    @Shadow
    private boolean noCulling;

    @Shadow
    private int blsmooth;

    @Shadow
    private float llxyz;

    @Shadow
    private float llxy0;

    @Shadow
    private float llxyZ;

    @Shadow
    private float ll0yz;

    @Shadow
    private float ll0yZ;

    @Shadow
    private float llXyz;

    @Shadow
    private float llXy0;

    @Shadow
    private float llXyZ;

    @Shadow
    private float c1r;

    @Shadow
    private float c2r;

    @Shadow
    private float c3r;

    @Shadow
    private float c4r;

    @Shadow
    private float c1g;

    @Shadow
    private float c2g;

    @Shadow
    private float c3g;

    @Shadow
    private float c4g;

    @Shadow
    private float c1b;

    @Shadow
    private float c2b;

    @Shadow
    private float c3b;

    @Shadow
    private float c4b;

    @Shadow
    private float llX0Z;

    @Shadow
    private float llXYZ;

    @Shadow
    private float llXY0;

    @Shadow
    private float llXYz;

    @Shadow
    private float llX0z;

    @Shadow
    public static boolean fancy;

    @Shadow
    private float llx0Z;

    @Shadow
    private float llxY0;

    @Shadow
    private float llxYZ;

    @Shadow
    private float llxYz;

    @Shadow
    private float llx0z;

    @Shadow
    private float ll0YZ;

    @Shadow
    private float ll0Yz;

    @Shadow
    private boolean f_13329347;

    @Shadow
    private boolean f_80261192;

    @Shadow
    private boolean f_36351595;

    @Shadow
    private boolean f_85822976;

    @Shadow
    private boolean f_83934836;

    @Shadow
    private boolean f_14266051;

    @Shadow
    private boolean f_69224315;

    @Shadow
    private boolean f_71082245;

    @Shadow
    private boolean f_33293353;

    @Shadow
    private boolean f_65471437;

    @Shadow
    private boolean f_53281161;

    @Shadow
    private boolean f_75488651;

    @Shadow
    public abstract void renderFaceDown(Tile tile, double x, double y, double z, int texture);

    @Shadow
    public abstract void renderFaceUp(Tile tile, double x, double y, double z, int texture);

    @Shadow
    public abstract void renderNorth(Tile tile, double x, double y, double z, int texture);

    @Shadow
    public abstract void renderSouth(Tile tile, double x, double y, double z, int texture);

    @Shadow
    public abstract void renderWest(Tile tile, double x, double y, double z, int texture);

    @Shadow
    public abstract void renderEast(Tile tile, double x, double y, double z, int texture);

    @Override
    public void tesselateInWorld(Tile tile, BigInteger x, int y, BigInteger z, int destroyProgress) {
        this.fixedTexture = destroyProgress;
        tesselateInWorld(tile, x, y, z);
        this.fixedTexture = -1;
    }

    @Override
    public boolean tesselateInWorld(Tile tile, BigInteger x, int y, BigInteger z) {
        int shape = tile.getRenderShape();
        tile.updateShape(this.level, x, y, z);
        if (shape == 0) {
            return tesselateBlockInWorld(tile, x, y, z);
        } else if (shape == BlockShapes.LIQUID) {
            return tesselateWaterInWorld(tile, x, y, z);
        } else if (shape == BlockShapes.CACTUS) {
            return this.tesselateCactusInWorld(tile, x, y, z);
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
        float r = (float) (color >> 16 & 0xFF) / 255.0F;
        float g = (float) (color >> 8 & 0xFF) / 255.0F;
        float b = (float) (color & 0xFF) / 255.0F;
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
        this.f_13329347 = Tile.translucent[this.level.getTile(x.add(BigInteger.ONE), y + 1, z)];
        this.f_80261192 = Tile.translucent[this.level.getTile(x.add(BigInteger.ONE), y - 1, z)];
        this.f_36351595 = Tile.translucent[this.level.getTile(x.add(BigInteger.ONE), y, z.add(BigInteger.ONE))];
        this.f_85822976 = Tile.translucent[this.level.getTile(x.add(BigInteger.ONE), y, z.subtract(BigInteger.ONE))];
        this.f_83934836 = Tile.translucent[this.level.getTile(x.subtract(BigInteger.ONE), y + 1, z)];
        this.f_14266051 = Tile.translucent[this.level.getTile(x.subtract(BigInteger.ONE), y - 1, z)];
        this.f_69224315 = Tile.translucent[this.level.getTile(x.subtract(BigInteger.ONE), y, z.subtract(BigInteger.ONE))];
        this.f_71082245 = Tile.translucent[this.level.getTile(x.subtract(BigInteger.ONE), y, z.add(BigInteger.ONE))];
        this.f_33293353 = Tile.translucent[this.level.getTile(x, y + 1, z.add(BigInteger.ONE))];
        this.f_65471437 = Tile.translucent[this.level.getTile(x, y + 1, z.subtract(BigInteger.ONE))];
        this.f_53281161 = Tile.translucent[this.level.getTile(x, y - 1, z.add(BigInteger.ONE))];
        this.f_75488651 = Tile.translucent[this.level.getTile(x, y - 1, z.subtract(BigInteger.ONE))];
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
                if (!this.f_75488651 && !this.f_14266051) {
                    this.ll0yZ = this.llxyz;
                } else {
                    this.ll0yZ = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y, z.subtract(BigInteger.ONE));
                }

                if (!this.f_53281161 && !this.f_14266051) {
                    this.llXyz = this.llxyz;
                } else {
                    this.llXyz = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y, z.add(BigInteger.ONE));
                }

                if (!this.f_75488651 && !this.f_80261192) {
                    this.llXy0 = this.ll0yz;
                } else {
                    this.llXy0 = tile.getBrightness(this.level, x.add(BigInteger.ONE), y, z.subtract(BigInteger.ONE));
                }

                if (!this.f_53281161 && !this.f_80261192) {
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
            if (FIX_STRIPELANDS) {
                ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderFaceDown(tile, new BigDecimal(x), y, new BigDecimal(z), tile.getTexture(this.level, x, y, z, Facing.DOWN));
            } else {
                renderFaceDown(tile, x.doubleValue(), y, z.doubleValue(), tile.getTexture(this.level, x, y, z, Facing.DOWN));
            }
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
                if (!this.f_65471437 && !this.f_83934836) {
                    this.llxYz = this.llxY0;
                } else {
                    this.llxYz = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y, z.subtract(BigInteger.ONE));
                }

                if (!this.f_65471437 && !this.f_13329347) {
                    this.llXYz = this.llXY0;
                } else {
                    this.llXYz = tile.getBrightness(this.level, x.add(BigInteger.ONE), y, z.subtract(BigInteger.ONE));
                }

                if (!this.f_33293353 && !this.f_83934836) {
                    this.llxYZ = this.llxY0;
                } else {
                    this.llxYZ = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y, z.add(BigInteger.ONE));
                }

                if (!this.f_33293353 && !this.f_13329347) {
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
            if (FIX_STRIPELANDS) {
                ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderFaceUp(tile, new BigDecimal(x), y, new BigDecimal(z), tile.getTexture(this.level, x, y, z, Facing.UP));
            } else {
                renderFaceUp(tile, x.doubleValue(), y, z.doubleValue(), tile.getTexture(this.level, x, y, z, Facing.UP));
            }
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
                if (!this.f_69224315 && !this.f_75488651) {
                    this.ll0yZ = this.llx0z;
                } else {
                    this.ll0yZ = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y - 1, z);
                }

                if (!this.f_69224315 && !this.f_65471437) {
                    this.llxYz = this.llx0z;
                } else {
                    this.llxYz = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y + 1, z);
                }

                if (!this.f_85822976 && !this.f_75488651) {
                    this.llXy0 = this.llX0z;
                } else {
                    this.llXy0 = tile.getBrightness(this.level, x.add(BigInteger.ONE), y - 1, z);
                }

                if (!this.f_85822976 && !this.f_65471437) {
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
            if (FIX_STRIPELANDS) {
                ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderNorth(tile, new BigDecimal(x), y, new BigDecimal(z), var19);
            } else {
                renderNorth(tile, x.doubleValue(), y, z.doubleValue(), tile.getTexture(this.level, x, y, z, var19));
            }
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
                if (FIX_STRIPELANDS) {
                    ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderNorth(tile, new BigDecimal(x), y, new BigDecimal(z), 38);
                } else {
                    renderNorth(tile, x.doubleValue(), y, z.doubleValue(), tile.getTexture(this.level, x, y, z, 38));
                }
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
                if (!this.f_71082245 && !this.f_53281161) {
                    this.llXyz = this.llx0Z;
                } else {
                    this.llXyz = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y - 1, z);
                }

                if (!this.f_71082245 && !this.f_33293353) {
                    this.llxYZ = this.llx0Z;
                } else {
                    this.llxYZ = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y + 1, z);
                }

                if (!this.f_36351595 && !this.f_53281161) {
                    this.llXyZ = this.llX0Z;
                } else {
                    this.llXyZ = tile.getBrightness(this.level, x.add(BigInteger.ONE), y - 1, z);
                }

                if (!this.f_36351595 && !this.f_33293353) {
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
            if (FIX_STRIPELANDS) {
                ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderSouth(tile, new BigDecimal(x), y, new BigDecimal(z), tile.getTexture(this.level, x, y, z, Facing.SOUTH));
            } else {
                renderSouth(tile, x.doubleValue(), y, z.doubleValue(), tile.getTexture(this.level, x, y, z, tile.getTexture(this.level, x, y, z, Facing.SOUTH)));
            }
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
                if (FIX_STRIPELANDS) {
                    ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderSouth(tile, new BigDecimal(x), y, new BigDecimal(z), 38);
                } else {
                    renderSouth(tile, x.doubleValue(), y, z.doubleValue(), 38);
                }
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
                if (!this.f_69224315 && !this.f_14266051) {
                    this.ll0yZ = this.llx0z;
                } else {
                    this.ll0yZ = tile.getBrightness(this.level, x, y - 1, z.subtract(BigInteger.ONE));
                }

                if (!this.f_71082245 && !this.f_14266051) {
                    this.llXyz = this.llx0Z;
                } else {
                    this.llXyz = tile.getBrightness(this.level, x, y - 1, z.add(BigInteger.ONE));
                }

                if (!this.f_69224315 && !this.f_83934836) {
                    this.llxYz = this.llx0z;
                } else {
                    this.llxYz = tile.getBrightness(this.level, x, y + 1, z.subtract(BigInteger.ONE));
                }

                if (!this.f_71082245 && !this.f_83934836) {
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
            if (FIX_STRIPELANDS) {
                ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderWest(tile, new BigDecimal(x), y, new BigDecimal(z), var51);
            } else {
                renderWest(tile, x.doubleValue(), y, z.doubleValue(), var51);
            }
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
                if (FIX_STRIPELANDS) {
                    ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderWest(tile, new BigDecimal(x), y, new BigDecimal(z), 38);
                } else {
                    renderWest(tile, x.doubleValue(), y, z.doubleValue(), 38);
                }
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
                if (!this.f_80261192 && !this.f_85822976) {
                    this.llXy0 = this.llX0z;
                } else {
                    this.llXy0 = tile.getBrightness(this.level, x, y - 1, z.subtract(BigInteger.ONE));
                }

                if (!this.f_80261192 && !this.f_36351595) {
                    this.llXyZ = this.llX0Z;
                } else {
                    this.llXyZ = tile.getBrightness(this.level, x, y - 1, z.add(BigInteger.ONE));
                }

                if (!this.f_13329347 && !this.f_85822976) {
                    this.llXYz = this.llX0z;
                } else {
                    this.llXYz = tile.getBrightness(this.level, x, y + 1, z.subtract(BigInteger.ONE));
                }

                if (!this.f_13329347 && !this.f_36351595) {
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
            if (FIX_STRIPELANDS) {
                ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderEast(tile, new BigDecimal(x), y, new BigDecimal(z), var52);
            } else {
                renderEast(tile, x.doubleValue(), y, z.doubleValue(), var52);
            }
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
                if (FIX_STRIPELANDS) {
                    ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderEast(tile, new BigDecimal(x), y, new BigDecimal(z), 38);
                } else {
                    renderEast(tile, x.doubleValue(), y, z.doubleValue(), 38);
                }
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
            if (FIX_STRIPELANDS) {
                ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderFaceDown(tile, new BigDecimal(x), y, new BigDecimal(z), tile.getTexture(this.level, x, y, z, Facing.DOWN));
            } else {
                renderFaceDown(tile, x.doubleValue(), y, z.doubleValue(), tile.getTexture(this.level, x, y, z, Facing.DOWN));
            }
            changed = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x, y + 1, z, Facing.UP)) {
            float br = tile.getBrightness(this.level, x, y + 1, z);
            if (tile.yy1 != 1.0 && !tile.material.isLiquid()) {
                br = centerBrightness;
            }

            t.color(r11 * br, g11 * br, b11 * br);
            if (FIX_STRIPELANDS) {
                ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderFaceUp(tile, new BigDecimal(x), y, new BigDecimal(z), tile.getTexture(this.level, x, y, z, Facing.UP));
            } else {
                renderFaceUp(tile, x.doubleValue(), y, z.doubleValue(), tile.getTexture(this.level, x, y, z, Facing.UP));
            }
            changed = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x, y, z.subtract(BigInteger.ONE), Facing.NORTH)) {
            float br = tile.getBrightness(this.level, x, y, z.subtract(BigInteger.ONE));
            if (tile.zz0 > 0.0) {
                br = centerBrightness;
            }

            t.color(r2 * br, g2 * br, b2 * br);
            int texture = tile.getTexture(this.level, x, y, z, Facing.NORTH);
            if (FIX_STRIPELANDS) {
                ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderNorth(tile, new BigDecimal(x), y, new BigDecimal(z), texture);
            } else {
                renderNorth(tile, x.doubleValue(), y, z.doubleValue(), texture);
            }
            if (fancy && texture == 3 && this.fixedTexture < 0) {
                t.color(r2 * br * r, g2 * br * g, b2 * br * b);
                if (FIX_STRIPELANDS) {
                    ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderNorth(tile, new BigDecimal(x), y, new BigDecimal(z), 38);
                } else {
                    renderNorth(tile, x.doubleValue(), y, z.doubleValue(), 38);
                }
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
            if (FIX_STRIPELANDS) {
                ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderSouth(tile, new BigDecimal(x), y, new BigDecimal(z), texture);
            } else {
                renderSouth(tile, x.doubleValue(), y, z.doubleValue(), texture);
            }
            if (fancy && texture == 3 && this.fixedTexture < 0) {
                t.color(r2 * br * r, g2 * br * g, b2 * br * b);
                if (FIX_STRIPELANDS) {
                    ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderSouth(tile, new BigDecimal(x), y, new BigDecimal(z), 38);
                } else {
                    renderSouth(tile, x.doubleValue(), y, z.doubleValue(), 38);
                }
            }

            changed = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x.subtract(BigInteger.ONE), y, z, Facing.WEST)) {
            float br = tile.getBrightness(this.level, x.subtract(BigInteger.ONE), y, z);
            if (tile.xx0 > 0.0) {
                br = centerBrightness;
            }

            t.color(r3 * br, g3 * br, b3 * br);
            int texture = tile.getTexture(this.level, x, y, z, Facing.WEST);
            if (FIX_STRIPELANDS) {
                ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderWest(tile, new BigDecimal(x), y, new BigDecimal(z), texture);
            } else {
                renderWest(tile, x.doubleValue(), y, z.doubleValue(), texture);
            }
            if (fancy && texture == 3 && this.fixedTexture < 0) {
                t.color(r3 * br * r, g3 * br * g, b3 * br * b);
                if (FIX_STRIPELANDS) {
                    ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderWest(tile, new BigDecimal(x), y, new BigDecimal(z), 38);
                } else {
                    renderWest(tile, x.doubleValue(), y, z.doubleValue(), 38);
                }
            }

            changed = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x.add(BigInteger.ONE), y, z, Facing.EAST)) {
            float var33 = tile.getBrightness(this.level, x.add(BigInteger.ONE), y, z);
            if (tile.xx1 < 1.0) {
                var33 = centerBrightness;
            }

            t.color(r3 * var33, g3 * var33, b3 * var33);
            int texture = tile.getTexture(this.level, x, y, z, Facing.EAST);
            if (FIX_STRIPELANDS) {
                ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderEast(tile, new BigDecimal(x), y, new BigDecimal(z), texture);
            } else {
                renderEast(tile, x.doubleValue(), y, z.doubleValue(), texture);
            }
            if (fancy && texture == 3 && this.fixedTexture < 0) {
                t.color(r3 * var33 * r, g3 * var33 * g, b3 * var33 * b);
                if (FIX_STRIPELANDS) {
                    ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderEast(tile, new BigDecimal(x), y, new BigDecimal(z), 38);
                } else {
                    renderEast(tile, x.doubleValue(), y, z.doubleValue(), 38);
                }
            }

            changed = true;
        }

        return changed;
    }

    public boolean tesselateCactusInWorld(Tile tt, BigInteger x, int y, BigInteger z) {
        int col = tt.getFoliageColor(this.level, x, y, z);
        float r = (col >> 16 & 0xFF) / 255.0F;
        float g = (col >> 8 & 0xFF) / 255.0F;
        float b = (col & 0xFF) / 255.0F;
        if (GameRenderer.anaglyph3d) {
            float cR = (r * 30.0F + g * 59.0F + b * 11.0F) / 100.0F;
            float cG = (r * 30.0F + g * 70.0F) / 100.0F;
            float cB = (r * 30.0F + b * 70.0F) / 100.0F;
            r = cR;
            g = cG;
            b = cB;
        }

        return this.tesselateCactusInWorld(tt, x, y, z, r, g, b);
    }

    public boolean tesselateCactusInWorld(Tile tt, final BigInteger x, int y, final BigInteger z, float r, float g, float b) {
        final BigDecimal bigX = new BigDecimal(x);
        final BigDecimal bigZ = new BigDecimal(z);
        Tesselator t = Tesselator.instance;
        boolean var9 = false;
        float var10 = 0.5F;
        float var11 = 1.0F;
        float var12 = 0.8F;
        float var13 = 0.6F;
        float var14 = var10 * r;
        float var15 = var11 * r;
        float var16 = var12 * r;
        float var17 = var13 * r;
        float var18 = var10 * g;
        float var19 = var11 * g;
        float var20 = var12 * g;
        float var21 = var13 * g;
        float var22 = var10 * b;
        float var23 = var11 * b;
        float var24 = var12 * b;
        float var25 = var13 * b;
        float var26 = 0.0625F;
        float var27 = tt.getBrightness(this.level, x, y, z);
        if (this.noCulling || tt.shouldRenderFace(this.level, x, y - 1, z, 0)) {
            float br = tt.getBrightness(this.level, x, y - 1, z);
            t.color(var14 * br, var18 * br, var22 * br);
            if (FIX_STRIPELANDS) {
                this.renderFaceDown(tt, bigX, y, bigZ, tt.getTexture(this.level, x, y, z, Facing.DOWN));
            } else {
                this.renderFaceDown(tt, x.doubleValue(), y, z.doubleValue(), tt.getTexture(this.level, x, y, z, Facing.DOWN));
            }

            var9 = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y + 1, z, Facing.UP)) {
            float br = tt.getBrightness(this.level, x, y + 1, z);
            if (tt.yy1 != 1.0 && !tt.material.isLiquid()) {
                br = var27;
            }

            t.color(var15 * br, var19 * br, var23 * br);
            if (FIX_STRIPELANDS) {
                this.renderFaceUp(tt, bigX, y, bigZ, tt.getTexture(this.level, x, y, z, Facing.UP));
            } else {
                this.renderFaceUp(tt, x.doubleValue(), y, z.doubleValue(), tt.getTexture(this.level, x, y, z, Facing.UP));
            }
            var9 = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y, z.subtract(BigInteger.ONE), Facing.NORTH)) {
            float var30 = tt.getBrightness(this.level, x, y, z.subtract(BigInteger.ONE));
            if (tt.zz0 > 0.0) {
                var30 = var27;
            }

            t.color(var16 * var30, var20 * var30, var24 * var30);
            t.addOffset(0.0F, 0.0F, var26);
            if (FIX_STRIPELANDS) {
                this.renderNorth(tt, bigX, y, bigZ, tt.getTexture(this.level, x, y, z, Facing.NORTH));
            } else {
                this.renderNorth(tt, x.doubleValue(), y, z.doubleValue(), tt.getTexture(this.level, x, y, z, Facing.NORTH));
            }
            t.addOffset(0.0F, 0.0F, -var26);
            var9 = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y, z.add(BigInteger.ONE), Facing.SOUTH)) {
            float br = tt.getBrightness(this.level, x, y, z.add(BigInteger.ONE));
            if (tt.zz1 < 1.0) {
                br = var27;
            }

            t.color(var16 * br, var20 * br, var24 * br);
            t.addOffset(0.0F, 0.0F, -var26);
            if (FIX_STRIPELANDS) {
                this.renderSouth(tt, bigX, y, bigZ, tt.getTexture(this.level, x, y, z, Facing.SOUTH));
            } else {
                this.renderSouth(tt, x.doubleValue(), y, z.doubleValue(), tt.getTexture(this.level, x, y, z, Facing.SOUTH));
            }
            t.addOffset(0.0F, 0.0F, var26);
            var9 = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x.subtract(BigInteger.ONE), y, z, Facing.WEST)) {
            float var32 = tt.getBrightness(this.level, x.subtract(BigInteger.ONE), y, z);
            if (tt.xx0 > 0.0) {
                var32 = var27;
            }

            t.color(var17 * var32, var21 * var32, var25 * var32);
            t.addOffset(var26, 0.0F, 0.0F);
            if (FIX_STRIPELANDS) {
                this.renderWest(tt, bigX, y, bigZ, tt.getTexture(this.level, x, y, z, Facing.WEST));
            } else {
                this.renderWest(tt, x.doubleValue(), y, z.doubleValue(), tt.getTexture(this.level, x, y, z, Facing.WEST));
            }
            t.addOffset(-var26, 0.0F, 0.0F);
            var9 = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x.add(BigInteger.ONE), y, z, Facing.EAST)) {
            float br = tt.getBrightness(this.level, x.add(BigInteger.ONE), y, z);
            if (tt.xx1 < 1.0) {
                br = var27;
            }

            t.color(var17 * br, var21 * br, var25 * br);
            t.addOffset(-var26, 0.0F, 0.0F);
            if (FIX_STRIPELANDS) {
                this.renderEast(tt, bigX, y, bigZ, tt.getTexture(this.level, x, y, z, Facing.EAST));
            } else {
                this.renderEast(tt, x.doubleValue(), y, z.doubleValue(), tt.getTexture(this.level, x, y, z, Facing.EAST));
            }
            t.addOffset(var26, 0.0F, 0.0F);
            var9 = true;
        }

        return var9;
    }

    @Override
    public boolean tesselateWaterInWorld(Tile tt, BigInteger x, int y, BigInteger z) {
        Tesselator t = Tesselator.instance;
        int col = tt.getFoliageColor(this.level, x, y, z);
        float r = (float) (col >> 16 & 0xFF) / 255.0F;
        float g = (float) (col >> 8 & 0xFF) / 255.0F;
        float b = (float) (col & 0xFF) / 255.0F;
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
                double uc = ((double) xt + 8.0) / 256.0;
                double vc = ((double) yt + 8.0) / 256.0;
                if (angle < -999.0F) {
                    angle = 0.0F;
                } else {
                    uc = (double) ((float) (xt + 16) / 256.0F);
                    vc = (double) ((float) (yt + 16) / 256.0F);
                }

                float s = Mth.sin(angle) * 8.0F / 256.0F;
                float c = Mth.cos(angle) * 8.0F / 256.0F;
                float br = tt.getBrightness(this.level, x, y, z);
                t.color(c11 * br * r, c11 * br * g, c11 * br * b);
                if (FIX_STRIPELANDS) {
                    t.vertexUV(new BigDecimal(x), (float) y + h0, new BigDecimal(z), uc - (double) c - (double) s, vc - (double) c + (double) s);
                    t.vertexUV(new BigDecimal(x), (float) y + h1, new BigDecimal(z.add(BigInteger.ONE)), uc - (double) c + (double) s, vc + (double) c + (double) s);
                    t.vertexUV(new BigDecimal(x.add(BigInteger.ONE)), (float) y + h2, new BigDecimal(z.add(BigInteger.ONE)), uc + (double) c + (double) s, vc + (double) c - (double) s);
                    t.vertexUV(new BigDecimal(x.add(BigInteger.ONE)), (float) y + h3, new BigDecimal(z), uc + (double) c - (double) s, vc - (double) c - (double) s);
                } else {
                    t.vertexUV((double) (x.doubleValue()), (double) ((float) y + h0), (double) (z).doubleValue(), uc - (double) c - (double) s, vc - (double) c + (double) s);
                    t.vertexUV((double) (x.doubleValue()), (double) ((float) y + h1), (double) (z.add(BigInteger.ONE)).doubleValue(), uc - (double) c + (double) s, vc + (double) c + (double) s);
                    t.vertexUV((double) (x.add(BigInteger.ONE)).doubleValue(), (double) ((float) y + h2), (double) (z.add(BigInteger.ONE)).doubleValue(), uc + (double) c + (double) s, vc + (double) c - (double) s);
                    t.vertexUV((double) (x.add(BigInteger.ONE)).doubleValue(), (double) ((float) y + h3), (double) (z).doubleValue(), uc + (double) c - (double) s, vc - (double) c - (double) s);
                }
            }

            if (this.noCulling || down) {
                float br = tt.getBrightness(this.level, x, y - 1, z);
                t.color(c10 * br, c10 * br, c10 * br);
                if (FIX_STRIPELANDS) {
                    ((me.alphamode.mcbig.extensions.features.fix_stripelands.BigTileRendererExtension) this).renderFaceDown(tt, new BigDecimal(x), y, new BigDecimal(z), tt.getTexture(Facing.DOWN));
                } else {
                    renderFaceDown(tt, x.doubleValue(), y, z.doubleValue(), tt.getTexture(Facing.DOWN));
                }
                changed = true;
            }

            for (int face = 0; face < 4; ++face) {
                if (FIX_STRIPELANDS) {
                    changed = renderBigLiquidFace(
                            t,
                            x, y, z,
                            new BigDecimal(x), new BigDecimal(z),
                            tt, data,
                            dirs,
                            c2, c3, c11,
                            r, g, b,
                            h0, h1, h2, h3,
                            face
                    );
                } else {
                    changed = renderLiquidFace(
                            t,
                            x, y, z,
                            tt, data,
                            dirs,
                            c2, c3, c11,
                            r, g, b,
                            h0, h1, h2, h3,
                            face
                    );
                }
            }

            tt.yy0 = yo0;
            tt.yy1 = yo1;
            return changed;
        }
    }

    private boolean renderBigLiquidFace(
            Tesselator t,
            final BigInteger x, final int y, final BigInteger z,
            final BigDecimal bigX, final BigDecimal bigZ,
            Tile tt, int data,
            boolean[] dirs,
            float c2, float c3, float c11,
            float r, float g, float b,
            float h0, float h1, float h2, float h3,
            int face
    ) {
        final BigDecimal bigXPOne = new BigDecimal(x.add(BigInteger.ONE));
        final BigDecimal bigZPOne = new BigDecimal(z.add(BigInteger.ONE));
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
            BigDecimal x1;
            BigDecimal z1;
            float hh1;
            BigDecimal x0;
            BigDecimal z0;
            if (face == 0) {
                hh0 = h0;
                hh1 = h3;
                x0 = bigX;
                x1 = bigXPOne;
                z0 = bigZ;
                z1 = bigZ;
            } else if (face == 1) {
                hh0 = h2;
                hh1 = h1;
                x0 = bigXPOne;
                x1 = bigX;
                z0 = bigZPOne;
                z1 = bigZPOne;
            } else if (face == 2) {
                hh0 = h1;
                hh1 = h0;
                x0 = bigX;
                x1 = bigX;
                z0 = bigZPOne;
                z1 = bigZ;
            } else {
                hh0 = h3;
                hh1 = h2;
                x0 = bigXPOne;
                x1 = bigXPOne;
                z0 = bigZ;
                z1 = bigZPOne;
            }

            double u0 = (double) ((float) (xTex + 0) / 256.0F);
            double u1 = ((double) (xTex + 16) - 0.01) / 256.0;
            double v01 = (double) (((float) yTex + (1.0F - hh0) * 16.0F) / 256.0F);
            double v02 = (double) (((float) yTex + (1.0F - hh1) * 16.0F) / 256.0F);
            double v1 = ((double) (yTex + 16) - 0.01) / 256.0;
            float br = tt.getBrightness(this.level, xt, y, zt);
            if (face < 2) {
                br *= c2;
            } else {
                br *= c3;
            }

            t.color(c11 * br * r, c11 * br * g, c11 * br * b);
            t.vertexUV(x0, (float) y + hh0, z0, u0, v01);
            t.vertexUV(x1, (float) y + hh1, z1, u1, v02);
            t.vertexUV(x1, y + 0, z1, u1, v1);
            t.vertexUV(x0, y + 0, z0, u0, v1);
            return true;
        }
        return false;
    }

    private boolean renderLiquidFace(
            Tesselator t,
            BigInteger x, int y, BigInteger z,
            Tile tt, int data,
            boolean[] dirs,
            float c2, float c3, float c11,
            float r, float g, float b,
            float h0, float h1, float h2, float h3,
            int face
    ) {
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

            double u0 = (double) ((float) (xTex + 0) / 256.0F);
            double u1 = ((double) (xTex + 16) - 0.01) / 256.0;
            double v01 = (double) (((float) yTex + (1.0F - hh0) * 16.0F) / 256.0F);
            double v02 = (double) (((float) yTex + (1.0F - hh1) * 16.0F) / 256.0F);
            double v1 = ((double) (yTex + 16) - 0.01) / 256.0;
            float br = tt.getBrightness(this.level, xt, y, zt);
            if (face < 2) {
                br *= c2;
            } else {
                br *= c3;
            }

            t.color(c11 * br * r, c11 * br * g, c11 * br * b);
            t.vertexUV((double) x0, (double) ((float) y + hh0), (double) z0, u0, v01);
            t.vertexUV((double) x1, (double) ((float) y + hh1), (double) z1, u1, v02);
            t.vertexUV((double) x1, (double) (y + 0), (double) z1, u1, v1);
            t.vertexUV((double) x0, (double) (y + 0), (double) z0, u0, v1);
            return true;
        }
        return false;
    }

    private float getWaterHeight(BigInteger x, int y, BigInteger z, Material m) {
        int count = 0;
        float h = 0.0F;

        for (int i = 0; i < 4; ++i) {
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

        return 1.0F - h / (float) count;
    }
}
