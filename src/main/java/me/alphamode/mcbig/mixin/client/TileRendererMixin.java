package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.extensions.BigTileRendererExtension;
import me.alphamode.mcbig.extensions.tiles.BigRedStoneDustTileExtension;
import me.alphamode.mcbig.level.tile.LiquidUtil;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.math.BigMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockShapes;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.renderer.TileRenderer;
import net.minecraft.util.Facing;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.*;
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

    @Shadow
    public abstract boolean tesselateInWorld(Tile tile, int x, int y, int z);

    @Shadow
    public abstract boolean tesselateWaterInWorld(Tile level, int x, int y, int z);

    @Shadow
    public abstract void tesselateTorch(Tile tile, double x, double y, double z, double xxa, double zza);

    @Shadow
    private boolean xFlipTexture;

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
        } else if (shape == BlockShapes.REEDS) {
            return this.tesselateCrossInWorld(tile, x, y, z);
        } else if (shape == BlockShapes.CROP) {
            return this.tesselateRowInWorld(tile, x, y, z);
        } else if (shape == BlockShapes.TORCH) {
            return this.tesselateTorchInWorld(tile, x, y, z);
        } else if (shape == BlockShapes.FIRE) {
            return this.tesselateFireInWorld(tile, x, y, z);
        } else if (shape == BlockShapes.REDSTONE) {
            return this.tesselateDustInWorld(tile, x, y, z);
        } else if (shape == BlockShapes.LADDER) {
            return this.tesselateLadderInWorld(tile, x, y, z);
        } else if (shape == BlockShapes.DOOR) {
            return this.tesselateDoorInWorld(tile, x, y, z);
        } else if (shape == BlockShapes.RAILS) {
            return this.tesselateRailInWorld((RailTile)tile, x, y, z);
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
    public boolean tesselateBlockInWorldWithAmbienceOcclusion(Tile tile, final BigInteger x, int y, final BigInteger z, float f, float g, float h) {
        BigDecimal bigX = new BigDecimal(x);
        BigDecimal bigZ = new BigDecimal(z);
        this.blen = true;
        boolean changed = false;
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
        final BigInteger xPlusOne = x.add(BigInteger.ONE);
        final BigInteger xMinusOne = x.subtract(BigInteger.ONE);
        final BigInteger zPlusOne = z.add(BigInteger.ONE);
        final BigInteger zMinusOne = z.subtract(BigInteger.ONE);

        this.ll000 = tile.getBrightness(this.level, x, y, z);
        this.llx00 = tile.getBrightness(this.level, xMinusOne, y, z);
        this.ll0y0 = tile.getBrightness(this.level, x, y - 1, z);
        this.ll00z = tile.getBrightness(this.level, x, y, zMinusOne);
        this.llX00 = tile.getBrightness(this.level, xPlusOne, y, z);
        this.ll0Y0 = tile.getBrightness(this.level, x, y + 1, z);
        this.ll00Z = tile.getBrightness(this.level, x, y, zPlusOne);
        this.f_13329347 = Tile.translucent[this.level.getTile(xPlusOne, y + 1, z)];
        this.f_80261192 = Tile.translucent[this.level.getTile(xPlusOne, y - 1, z)];
        this.f_36351595 = Tile.translucent[this.level.getTile(xPlusOne, y, zPlusOne)];
        this.f_85822976 = Tile.translucent[this.level.getTile(xPlusOne, y, zMinusOne)];
        this.f_83934836 = Tile.translucent[this.level.getTile(xMinusOne, y + 1, z)];
        this.f_14266051 = Tile.translucent[this.level.getTile(xMinusOne, y - 1, z)];
        this.f_69224315 = Tile.translucent[this.level.getTile(xMinusOne, y, zMinusOne)];
        this.f_71082245 = Tile.translucent[this.level.getTile(xMinusOne, y, zPlusOne)];
        this.f_33293353 = Tile.translucent[this.level.getTile(x, y + 1, zPlusOne)];
        this.f_65471437 = Tile.translucent[this.level.getTile(x, y + 1, zMinusOne)];
        this.f_53281161 = Tile.translucent[this.level.getTile(x, y - 1, zPlusOne)];
        this.f_75488651 = Tile.translucent[this.level.getTile(x, y - 1, zMinusOne)];
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
                this.llxyz = tile.getBrightness(this.level, xMinusOne, --y, z);
                this.llxy0 = tile.getBrightness(this.level, x, y, zMinusOne);
                this.llxyZ = tile.getBrightness(this.level, x, y, zPlusOne);
                this.ll0yz = tile.getBrightness(this.level, xPlusOne, y, z);
                if (!this.f_75488651 && !this.f_14266051) {
                    this.ll0yZ = this.llxyz;
                } else {
                    this.ll0yZ = tile.getBrightness(this.level, xMinusOne, y, zMinusOne);
                }

                if (!this.f_53281161 && !this.f_14266051) {
                    this.llXyz = this.llxyz;
                } else {
                    this.llXyz = tile.getBrightness(this.level, xMinusOne, y, zPlusOne);
                }

                if (!this.f_75488651 && !this.f_80261192) {
                    this.llXy0 = this.ll0yz;
                } else {
                    this.llXy0 = tile.getBrightness(this.level, xPlusOne, y, zMinusOne);
                }

                if (!this.f_53281161 && !this.f_80261192) {
                    this.llXyZ = this.ll0yz;
                } else {
                    this.llXyZ = tile.getBrightness(this.level, xPlusOne, y, zPlusOne);
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
                this.renderFaceDown(tile, bigX, y, bigZ, tile.getTexture(this.level, x, y, z, Facing.DOWN));
            } else {
                renderFaceDown(tile, x.doubleValue(), y, z.doubleValue(), tile.getTexture(this.level, x, y, z, Facing.DOWN));
            }
            changed = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x, y + 1, z, Facing.UP)) {
            if (this.blsmooth <= 0) {
                var12 = this.ll0Y0;
                var11 = this.ll0Y0;
                var10 = this.ll0Y0;
                var9 = this.ll0Y0;
            } else {
                y++;
                this.llxY0 = tile.getBrightness(this.level, xMinusOne, y, z);
                this.llXY0 = tile.getBrightness(this.level, xPlusOne, y, z);
                this.ll0Yz = tile.getBrightness(this.level, x, y, zMinusOne);
                this.ll0YZ = tile.getBrightness(this.level, x, y, zPlusOne);
                if (!this.f_65471437 && !this.f_83934836) {
                    this.llxYz = this.llxY0;
                } else {
                    this.llxYz = tile.getBrightness(this.level, xMinusOne, y, zMinusOne);
                }

                if (!this.f_65471437 && !this.f_13329347) {
                    this.llXYz = this.llXY0;
                } else {
                    this.llXYz = tile.getBrightness(this.level, xPlusOne, y, zMinusOne);
                }

                if (!this.f_33293353 && !this.f_83934836) {
                    this.llxYZ = this.llxY0;
                } else {
                    this.llxYZ = tile.getBrightness(this.level, xMinusOne, y, zPlusOne);
                }

                if (!this.f_33293353 && !this.f_13329347) {
                    this.llXYZ = this.llXY0;
                } else {
                    this.llXYZ = tile.getBrightness(this.level, xPlusOne, y, zPlusOne);
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
                this.renderFaceUp(tile, bigX, y, bigZ, tile.getTexture(this.level, x, y, z, Facing.UP));
            } else {
                renderFaceUp(tile, x.doubleValue(), y, z.doubleValue(), tile.getTexture(this.level, x, y, z, Facing.UP));
            }
            changed = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x, y, zMinusOne, Facing.NORTH)) {
            if (this.blsmooth <= 0) {
                var12 = this.ll00z;
                var11 = this.ll00z;
                var10 = this.ll00z;
                var9 = this.ll00z;
            } else {
                this.llx0z = tile.getBrightness(this.level, xMinusOne, y, zMinusOne);
                this.llxy0 = tile.getBrightness(this.level, x, y - 1, zMinusOne);
                this.ll0Yz = tile.getBrightness(this.level, x, y + 1, zMinusOne);
                this.llX0z = tile.getBrightness(this.level, xPlusOne, y, zMinusOne);
                if (!this.f_69224315 && !this.f_75488651) {
                    this.ll0yZ = this.llx0z;
                } else {
                    this.ll0yZ = tile.getBrightness(this.level, xMinusOne, y - 1, zMinusOne);
                }

                if (!this.f_69224315 && !this.f_65471437) {
                    this.llxYz = this.llx0z;
                } else {
                    this.llxYz = tile.getBrightness(this.level, xMinusOne, y + 1, zMinusOne);
                }

                if (!this.f_85822976 && !this.f_75488651) {
                    this.llXy0 = this.llX0z;
                } else {
                    this.llXy0 = tile.getBrightness(this.level, xPlusOne, y - 1, zMinusOne);
                }

                if (!this.f_85822976 && !this.f_65471437) {
                    this.llXYz = this.llX0z;
                } else {
                    this.llXYz = tile.getBrightness(this.level, xPlusOne, y + 1, zMinusOne);
                }

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
            int tex = tile.getTexture(this.level, x, y, z, Facing.NORTH);
            if (FIX_STRIPELANDS) {
                this.renderNorth(tile, bigX, y, bigZ, tex);
            } else {
                renderNorth(tile, x.doubleValue(), y, z.doubleValue(), tex);
            }
            if (fancy && tex == 3 && this.fixedTexture < 0) {
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
                    this.renderNorth(tile, bigX, y, bigZ, 38);
                } else {
                    renderNorth(tile, x.doubleValue(), y, z.doubleValue(), 38);
                }
            }

            changed = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x, y, zPlusOne, Facing.SOUTH)) {
            if (this.blsmooth <= 0) {
                var12 = this.ll00Z;
                var11 = this.ll00Z;
                var10 = this.ll00Z;
                var9 = this.ll00Z;
            } else {
                this.llx0Z = tile.getBrightness(this.level, xMinusOne, y, zPlusOne);
                this.llX0Z = tile.getBrightness(this.level, xPlusOne, y, zPlusOne);
                this.llxyZ = tile.getBrightness(this.level, x, y - 1, zPlusOne);
                this.ll0YZ = tile.getBrightness(this.level, x, y + 1, zPlusOne);
                if (!this.f_71082245 && !this.f_53281161) {
                    this.llXyz = this.llx0Z;
                } else {
                    this.llXyz = tile.getBrightness(this.level, xMinusOne, y - 1, zPlusOne);
                }

                if (!this.f_71082245 && !this.f_33293353) {
                    this.llxYZ = this.llx0Z;
                } else {
                    this.llxYZ = tile.getBrightness(this.level, xMinusOne, y + 1, zPlusOne);
                }

                if (!this.f_36351595 && !this.f_53281161) {
                    this.llXyZ = this.llX0Z;
                } else {
                    this.llXyZ = tile.getBrightness(this.level, xPlusOne, y - 1, zPlusOne);
                }

                if (!this.f_36351595 && !this.f_33293353) {
                    this.llXYZ = this.llX0Z;
                } else {
                    this.llXYZ = tile.getBrightness(this.level, xPlusOne, y + 1, zPlusOne);
                }

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
                this.renderSouth(tile, bigX, y, bigZ, tile.getTexture(this.level, x, y, z, Facing.SOUTH));
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
                    this.renderSouth(tile, bigX, y, bigZ, 38);
                } else {
                    renderSouth(tile, x.doubleValue(), y, z.doubleValue(), 38);
                }
            }

            changed = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, xMinusOne, y, z, Facing.WEST)) {
            if (this.blsmooth <= 0) {
                var12 = this.llx00;
                var11 = this.llx00;
                var10 = this.llx00;
                var9 = this.llx00;
            } else {
                this.llxyz = tile.getBrightness(this.level, xMinusOne, y - 1, z);
                this.llx0z = tile.getBrightness(this.level, xMinusOne, y, zMinusOne);
                this.llx0Z = tile.getBrightness(this.level, xMinusOne, y, zPlusOne);
                this.llxY0 = tile.getBrightness(this.level, xMinusOne, y + 1, z);
                if (!this.f_69224315 && !this.f_14266051) {
                    this.ll0yZ = this.llx0z;
                } else {
                    this.ll0yZ = tile.getBrightness(this.level, xMinusOne, y - 1, zMinusOne);
                }

                if (!this.f_71082245 && !this.f_14266051) {
                    this.llXyz = this.llx0Z;
                } else {
                    this.llXyz = tile.getBrightness(this.level, xMinusOne, y - 1, zPlusOne);
                }

                if (!this.f_69224315 && !this.f_83934836) {
                    this.llxYz = this.llx0z;
                } else {
                    this.llxYz = tile.getBrightness(this.level, xMinusOne, y + 1, zMinusOne);
                }

                if (!this.f_71082245 && !this.f_83934836) {
                    this.llxYZ = this.llx0Z;
                } else {
                    this.llxYZ = tile.getBrightness(this.level, xMinusOne, y + 1, zPlusOne);
                }

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
                this.renderWest(tile, bigX, y, bigZ, var51);
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
                    this.renderWest(tile, bigX, y, bigZ, 38);
                } else {
                    renderWest(tile, x.doubleValue(), y, z.doubleValue(), 38);
                }
            }

            changed = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, xPlusOne, y, z, Facing.EAST)) {
            if (this.blsmooth <= 0) {
                var12 = this.llX00;
                var11 = this.llX00;
                var10 = this.llX00;
                var9 = this.llX00;
            } else {
                this.ll0yz = tile.getBrightness(this.level, xPlusOne, y - 1, z);
                this.llX0z = tile.getBrightness(this.level, xPlusOne, y, zMinusOne);
                this.llX0Z = tile.getBrightness(this.level, xPlusOne, y, zPlusOne);
                this.llXY0 = tile.getBrightness(this.level, xPlusOne, y + 1, z);
                if (!this.f_80261192 && !this.f_85822976) {
                    this.llXy0 = this.llX0z;
                } else {
                    this.llXy0 = tile.getBrightness(this.level, xPlusOne, y - 1, zMinusOne);
                }

                if (!this.f_80261192 && !this.f_36351595) {
                    this.llXyZ = this.llX0Z;
                } else {
                    this.llXyZ = tile.getBrightness(this.level, xPlusOne, y - 1, zPlusOne);
                }

                if (!this.f_13329347 && !this.f_85822976) {
                    this.llXYz = this.llX0z;
                } else {
                    this.llXYz = tile.getBrightness(this.level, xPlusOne, y + 1, zMinusOne);
                }

                if (!this.f_13329347 && !this.f_36351595) {
                    this.llXYZ = this.llX0Z;
                } else {
                    this.llXYZ = tile.getBrightness(this.level, xPlusOne, y + 1, zPlusOne);
                }

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
                this.renderEast(tile, bigX, y, bigZ, var52);
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
                    this.renderEast(tile, bigX, y, bigZ, 38);
                } else {
                    renderEast(tile, x.doubleValue(), y, z.doubleValue(), 38);
                }
            }

            changed = true;
        }

        this.blen = false;
        return changed;
    }

    @Override
    public boolean tesselateBlockInWorld(Tile tile, BigInteger x, int y, BigInteger z, float r, float g, float b) {
        final BigDecimal bigX = new BigDecimal(x);
        final BigDecimal bigZ = new BigDecimal(z);
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
                this.renderFaceDown(tile, bigX, y, bigZ, tile.getTexture(this.level, x, y, z, Facing.DOWN));
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
                this.renderFaceUp(tile, bigX, y, bigZ, tile.getTexture(this.level, x, y, z, Facing.UP));
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
                this.renderNorth(tile, bigX, y, bigZ, texture);
            } else {
                renderNorth(tile, x.doubleValue(), y, z.doubleValue(), texture);
            }
            if (fancy && texture == 3 && this.fixedTexture < 0) {
                t.color(r2 * br * r, g2 * br * g, b2 * br * b);
                if (FIX_STRIPELANDS) {
                    this.renderNorth(tile, bigX, y, bigZ, 38);
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
                this.renderSouth(tile, bigX, y, bigZ, texture);
            } else {
                renderSouth(tile, x.doubleValue(), y, z.doubleValue(), texture);
            }
            if (fancy && texture == 3 && this.fixedTexture < 0) {
                t.color(r2 * br * r, g2 * br * g, b2 * br * b);
                if (FIX_STRIPELANDS) {
                    this.renderSouth(tile, bigX, y, bigZ, 38);
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
                this.renderWest(tile, bigX, y, bigZ, texture);
            } else {
                renderWest(tile, x.doubleValue(), y, z.doubleValue(), texture);
            }
            if (fancy && texture == 3 && this.fixedTexture < 0) {
                t.color(r3 * br * r, g3 * br * g, b3 * br * b);
                if (FIX_STRIPELANDS) {
                    this.renderWest(tile, bigX, y, bigZ, 38);
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
                this.renderEast(tile, bigX, y, bigZ, texture);
            } else {
                renderEast(tile, x.doubleValue(), y, z.doubleValue(), texture);
            }
            if (fancy && texture == 3 && this.fixedTexture < 0) {
                t.color(r3 * var33 * r, g3 * var33 * g, b3 * var33 * b);
                if (FIX_STRIPELANDS) {
                    this.renderEast(tile, bigX, y, bigZ, 38);
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
        boolean changed = false;
        final float c10 = 0.5F;
        final float c11 = 1.0F;
        final float c2 = 0.8F;
        final float c3 = 0.6F;
        final float r10 = c10 * r;
        final float r11 = c11 * r;
        final float r2 = c2 * r;
        final float r3 = c3 * r;
        final float g10 = c10 * g;
        final float g11 = c11 * g;
        final float g2 = c2 * g;
        final float g3 = c3 * g;
        final float b10 = c10 * b;
        final float b11 = c11 * b;
        final float b2 = c2 * b;
        final float b3 = c3 * b;
        final float epsilon = 0.0625F;
        final float centerBrightness = tt.getBrightness(this.level, x, y, z);
        if (this.noCulling || tt.shouldRenderFace(this.level, x, y - 1, z, Facing.DOWN)) {
            float br = tt.getBrightness(this.level, x, y - 1, z);
            t.color(r10 * br, g10 * br, b10 * br);
            if (FIX_STRIPELANDS) {
                this.renderFaceDown(tt, bigX, y, bigZ, tt.getTexture(this.level, x, y, z, Facing.DOWN));
            } else {
                this.renderFaceDown(tt, x.doubleValue(), y, z.doubleValue(), tt.getTexture(this.level, x, y, z, Facing.DOWN));
            }

            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y + 1, z, Facing.UP)) {
            float br = tt.getBrightness(this.level, x, y + 1, z);
            if (tt.yy1 != 1.0 && !tt.material.isLiquid()) {
                br = centerBrightness;
            }

            t.color(r11 * br, g11 * br, b11 * br);
            if (FIX_STRIPELANDS) {
                this.renderFaceUp(tt, bigX, y, bigZ, tt.getTexture(this.level, x, y, z, Facing.UP));
            } else {
                this.renderFaceUp(tt, x.doubleValue(), y, z.doubleValue(), tt.getTexture(this.level, x, y, z, Facing.UP));
            }
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y, z.subtract(BigInteger.ONE), Facing.NORTH)) {
            float br = tt.getBrightness(this.level, x, y, z.subtract(BigInteger.ONE));
            if (tt.zz0 > 0.0) {
                br = centerBrightness;
            }

            t.color(r2 * br, g2 * br, b2 * br);

            if (FIX_STRIPELANDS) {
                t.addOffset(BigDecimal.ZERO, 0.0F, BigConstants.EPSILON);
                this.renderNorth(tt, bigX, y, bigZ, tt.getTexture(this.level, x, y, z, Facing.NORTH));
                t.addOffset(BigDecimal.ZERO, 0.0F, BigConstants.NEGATIVE_EPSILON);
            } else {
                t.addOffset(0.0F, 0.0F, epsilon);
                this.renderNorth(tt, x.doubleValue(), y, z.doubleValue(), tt.getTexture(this.level, x, y, z, Facing.NORTH));
                t.addOffset(0.0F, 0.0F, -epsilon);
            }

            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y, z.add(BigInteger.ONE), Facing.SOUTH)) {
            float br = tt.getBrightness(this.level, x, y, z.add(BigInteger.ONE));
            if (tt.zz1 < 1.0) {
                br = centerBrightness;
            }

            t.color(r2 * br, g2 * br, b2 * br);

            if (FIX_STRIPELANDS) {
                t.addOffset(BigDecimal.ZERO, 0.0F, BigConstants.NEGATIVE_EPSILON);
                this.renderSouth(tt, bigX, y, bigZ, tt.getTexture(this.level, x, y, z, Facing.SOUTH));
                t.addOffset(BigDecimal.ZERO, 0.0F, BigConstants.EPSILON);
            } else {
                t.addOffset(0.0F, 0.0F, -epsilon);
                this.renderSouth(tt, x.doubleValue(), y, z.doubleValue(), tt.getTexture(this.level, x, y, z, Facing.SOUTH));
                t.addOffset(0.0F, 0.0F, epsilon);
            }
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x.subtract(BigInteger.ONE), y, z, Facing.WEST)) {
            float br = tt.getBrightness(this.level, x.subtract(BigInteger.ONE), y, z);
            if (tt.xx0 > 0.0) {
                br = centerBrightness;
            }

            t.color(r3 * br, g3 * br, b3 * br);

            if (FIX_STRIPELANDS) {
                t.addOffset(BigConstants.EPSILON, 0.0F, BigDecimal.ZERO);
                this.renderWest(tt, bigX, y, bigZ, tt.getTexture(this.level, x, y, z, Facing.WEST));
                t.addOffset(BigConstants.NEGATIVE_EPSILON, 0.0F, BigDecimal.ZERO);
            } else {
                t.addOffset(epsilon, 0.0F, 0.0F);
                this.renderWest(tt, x.doubleValue(), y, z.doubleValue(), tt.getTexture(this.level, x, y, z, Facing.WEST));
                t.addOffset(-epsilon, 0.0F, 0.0F);
            }

            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x.add(BigInteger.ONE), y, z, Facing.EAST)) {
            float br = tt.getBrightness(this.level, x.add(BigInteger.ONE), y, z);
            if (tt.xx1 < 1.0) {
                br = centerBrightness;
            }

            t.color(r3 * br, g3 * br, b3 * br);

            if (FIX_STRIPELANDS) {
                t.addOffset(BigConstants.NEGATIVE_EPSILON, 0.0F, BigDecimal.ZERO);
                this.renderEast(tt, bigX, y, bigZ, tt.getTexture(this.level, x, y, z, Facing.EAST));
                t.addOffset(BigConstants.EPSILON, 0.0F, BigDecimal.ZERO);
            } else {
                t.addOffset(-epsilon, 0.0F, 0.0F);
                this.renderEast(tt, x.doubleValue(), y, z.doubleValue(), tt.getTexture(this.level, x, y, z, Facing.EAST));
                t.addOffset(epsilon, 0.0F, 0.0F);
            }

            changed = true;
        }

        return changed;
    }

    public boolean tesselateLadderInWorld(Tile tile, BigInteger x, int y, BigInteger z) {
        Tesselator t = Tesselator.instance;
        int tex = tile.getTexture(0);
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }

        float br = tile.getBrightness(this.level, x, y, z);
        t.color(br, br, br);
        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u0 = xt / 256.0F;
        double u1 = (xt + 15.99F) / 256.0F;
        double v0 = yt / 256.0F;
        double v1 = (yt + 15.99F) / 256.0F;
        int face = this.level.getData(x, y, z);
        float o = 0.0F;
        float r = 0.05F;

        BigDecimal bigR = BigMath.decimal(r);
        BigDecimal xD = new BigDecimal(x);
        BigDecimal zD = new BigDecimal(z);
        BigDecimal xPlusR = BigMath.addD(xD, bigR);
        BigDecimal zPlusR = BigMath.addD(zD, bigR);
        BigDecimal xPlusOneD = new BigDecimal(x.add(BigInteger.ONE));
        BigDecimal zPlusOneD = new BigDecimal(z.add(BigInteger.ONE));
        BigDecimal xPlusOneMinusR = BigMath.addD(xPlusOneD, bigR);
        BigDecimal zPlusOneMinusR = BigMath.addD(zPlusOneD, bigR);

        if (face == 5) {
            t.vertexUV(xPlusR, y + 1 + o, zPlusOneD, u0, v0);
            t.vertexUV(xPlusR, y + 0 - o, zPlusOneD, u0, v1);
            t.vertexUV(xPlusR, y + 0 - o, zD, u1, v1);
            t.vertexUV(xPlusR, y + 1 + o, zD, u1, v0);
        }

        if (face == 4) {
            t.vertexUV(xPlusOneMinusR, y + 0 - o, zPlusOneD, u1, v1);
            t.vertexUV(xPlusOneMinusR, y + 1 + o, zPlusOneD, u1, v0);
            t.vertexUV(xPlusOneMinusR, y + 1 + o, zD, u0, v0);
            t.vertexUV(xPlusOneMinusR, y + 0 - o, zD, u0, v1);
        }

        if (face == 3) {
            t.vertexUV(xPlusOneD, y + 0 - o, zPlusR, u1, v1);
            t.vertexUV(xPlusOneD, y + 1 + o, zPlusR, u1, v0);
            t.vertexUV(xD, y + 1 + o, zPlusR, u0, v0);
            t.vertexUV(xD, y + 0 - o, zPlusR, u0, v1);
        }

        if (face == 2) {
            t.vertexUV(xPlusOneD, y + 1 + o, zPlusOneMinusR, u0, v0);
            t.vertexUV(xPlusOneD, y + 0 - o, zPlusOneMinusR, u0, v1);
            t.vertexUV(xD, y + 0 - o, zPlusOneMinusR, u1, v1);
            t.vertexUV(xD, y + 1 + o, zPlusOneMinusR, u1, v0);
        }

        return true;
    }

    public boolean tesselateCrossInWorld(Tile tile, BigInteger x, int y, BigInteger z) {
        Tesselator t = Tesselator.instance;
        float br = tile.getBrightness(this.level, x, y, z);
        int col = tile.getFoliageColor(this.level, x, y, z);
        float r = (col >> 16 & 0xFF) / 255.0F;
        float g = (col >> 8 & 0xFF) / 255.0F;
        float b = (col & 0xFF) / 255.0F;
        if (GameRenderer.anaglyph3d) {
            float var11 = (r * 30.0F + g * 59.0F + b * 11.0F) / 100.0F;
            float var12 = (r * 30.0F + g * 70.0F) / 100.0F;
            float var13 = (r * 30.0F + b * 70.0F) / 100.0F;
            r = var11;
            g = var12;
            b = var13;
        }

        t.color(br * r, br * g, br * b);
        BigDecimal rX = new BigDecimal(x);
        double rY = y;
        BigDecimal rZ = new BigDecimal(z);
        if (tile == Tile.TALL_GRASS) {
            // Random offset based on position
            long hash = x.longValue() * 3129871 ^ z.longValue() * 116129781L ^ y;
            hash = hash * hash * 42317861L + hash * 11L;
            rX = rX.add(BigDecimal.valueOf(((float) (hash >> 16 & 15L) / 15.0F - 0.5) * 0.5));
            rY += ((float)(hash >> 20 & 15L) / 15.0F - 1.0) * 0.2;
            rZ = rZ.add(BigDecimal.valueOf(((float)(hash >> 24 & 15L) / 15.0F - 0.5) * 0.5));
        }

        this.tesselateCrossTexture(tile, this.level.getData(x, y, z), rX, rY, rZ);
        return true;
    }

    public boolean tesselateRowInWorld(Tile tile, BigInteger x, int y, BigInteger z) {
        Tesselator t = Tesselator.instance;
        float br = tile.getBrightness(this.level, x, y, z);
        t.color(br, br, br);
        this.tesselateRowTexture(tile, this.level.getData(x, y, z), new BigDecimal(x), y - 0.0625F, new BigDecimal(z));
        return true;
    }

    public boolean tesselateTorchInWorld(Tile tile, BigInteger x, int y, BigInteger z) {
        int dir = this.level.getData(x, y, z);
        Tesselator t = Tesselator.instance;
        float br = tile.getBrightness(this.level, x, y, z);
        if (Tile.lightEmission[tile.id] > 0) {
            br = 1.0F;
        }

        t.color(br, br, br);
        double r = 0.4F;
        double r2 = 0.5 - r;
        BigDecimal bigX = new BigDecimal(x);
        BigDecimal bigZ = new BigDecimal(z);
        BigDecimal bigR2 = BigDecimal.valueOf(r2);
        BigDecimal rX0 = bigX.subtract(bigR2);
        BigDecimal rX1 = bigX.add(bigR2);
        BigDecimal rZ0 = bigZ.subtract(bigR2);
        BigDecimal rZ1 = bigZ.add(bigR2);
        double h = 0.2F;
        if (dir == 1) {
            this.tesselateTorch(tile, rX0, y + h, bigZ, -r, 0.0);
        } else if (dir == 2) {
            this.tesselateTorch(tile, rX1, y + h, bigZ, r, 0.0);
        } else if (dir == 3) {
            this.tesselateTorch(tile, bigX, y + h, rZ0, 0.0, -r);
        } else if (dir == 4) {
            this.tesselateTorch(tile, bigX, y + h, rZ1, 0.0, r);
        } else {
            this.tesselateTorch(tile, bigX, y, bigZ, 0.0, 0.0);
        }

        return true;
    }

    public void tesselateTorch(Tile tile, BigDecimal x, double y, BigDecimal z, double xxa, double zza) {
        Tesselator t = Tesselator.instance;
        int tex = tile.getTexture(Facing.DOWN);
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }

        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        float u0 = xt / 256.0F;
        float u1 = (xt + 15.99F) / 256.0F;
        float v0 = yt / 256.0F;
        float v1 = (yt + 15.99F) / 256.0F;
        double uc0 = u0 + 0.02734375;
        double vc0 = v0 + 0.0234375;
        double uc1 = u0 + 0.03515625;
        double vc1 = v0 + 0.03125;

        final BigDecimal x0 = x;
        final BigDecimal x1 = x.add(BigDecimal.ONE);
        final BigDecimal z0 = z;
        final BigDecimal z1 = z.add(BigDecimal.ONE);

        x = x.add(BigConstants.POINT_FIVE);
        z = z.add(BigConstants.POINT_FIVE);

        double r = 0.0625;
        double h = 0.625;

        BigDecimal tx00 = BigMath.addD(x, xxa * (1.0 - h) - r);
        BigDecimal tx01 = BigMath.addD(x, xxa * (1.0 - h) + r);

        BigDecimal tz00 = BigMath.addD(z, zza * (1.0 - h) - r);
        BigDecimal tz01 = BigMath.addD(z, zza * (1.0 - h) + r);

        // Top
        t.vertexUV(tx00, y + h, tz00, uc0, vc0); // t.vertexUV(x + xxa * (1.0 - h) - r, y + h, z + zza * (1.0 - h) - r, uc0, vc0);
        t.vertexUV(tx00, y + h, tz01, uc0, vc1); // t.vertexUV(x + xxa * (1.0 - h) - r, y + h, z + zza * (1.0 - h) + r, uc0, vc1);
        t.vertexUV(tx01, y + h, tz01, uc1, vc1); // t.vertexUV(x + xxa * (1.0 - h) + r, y + h, z + zza * (1.0 - h) + r, uc1, vc1);
        t.vertexUV(tx01, y + h, tz00, uc1, vc0); // t.vertexUV(x + xxa * (1.0 - h) + r, y + h, z + zza * (1.0 - h) - r, uc1, vc0);

        BigDecimal bigR = BigMath.decimal(r);
        BigDecimal bigXXA = BigMath.decimal(xxa);
        BigDecimal bigZZA = BigMath.decimal(zza);

        BigDecimal tx10 = BigMath.subD(x, bigR);
        BigDecimal tx11 = BigMath.addD(BigMath.subD(x, r), xxa);

        BigDecimal tz10 = BigMath.addD(z0, bigZZA);
        BigDecimal tz11 = BigMath.addD(z1, bigZZA);

        // West (-x)
        t.vertexUV(tx10, y + 1.0, z0, u0, v0);   // t.vertexUV(x - r, y + 1.0, z0, u0, v0);
        t.vertexUV(tx11, y + 0.0, tz10, u0, v1); // t.vertexUV(x - r + xxa, y + 0.0, z0 + zza, u0, v1);
        t.vertexUV(tx11, y + 0.0, tz11, u1, v1); // t.vertexUV(x - r + xxa, y + 0.0, z1 + zza, u1, v1);
        t.vertexUV(tx10, y + 1.0, z1, u1, v0);   // t.vertexUV(x - r, y + 1.0, z1, u1, v0);

        BigDecimal tx20 = BigMath.addD(x, bigR);
        BigDecimal tx21 = BigMath.addD(x, xxa + r);

        // East (+x)
        t.vertexUV(tx20, y + 1.0, z1, u0, v0);   // t.vertexUV(x + r, y + 1.0, z1, u0, v0);
        t.vertexUV(tx21, y + 0.0, tz11, u0, v1); // t.vertexUV(x + xxa + r, y + 0.0, z1 + zza, u0, v1);
        t.vertexUV(tx21, y + 0.0, tz10, u1, v1); // t.vertexUV(x + xxa + r, y + 0.0, z0 + zza, u1, v1);
        t.vertexUV(tx20, y + 1.0, z0, u1, v0);   // t.vertexUV(x + r, y + 1.0, z0, u1, v0);

        BigDecimal tx30 = BigMath.addD(x0, bigXXA);
        BigDecimal tx31 = BigMath.addD(x1, bigXXA);

        BigDecimal tz30 = BigMath.addD(z, bigR);
        BigDecimal tz31 = BigMath.addD(z, zza + r);

        // South (+z)
        t.vertexUV(x0, y + 1.0, tz30, u0, v0);   // t.vertexUV(x0, y + 1.0, z + r, u0, v0);
        t.vertexUV(tx30, y + 0.0, tz31, u0, v1); // t.vertexUV(x0 + xxa, y + 0.0, z + r + zza, u0, v1);
        t.vertexUV(tx31, y + 0.0, tz31, u1, v1); // t.vertexUV(x1 + xxa, y + 0.0, z + r + zza, u1, v1);
        t.vertexUV(x1, y + 1.0, tz30, u1, v0);   // t.vertexUV(x1, y + 1.0, z + r, u1, v0);

        BigDecimal tz40 = BigMath.subD(z, bigR);
        BigDecimal tz41 = new BigDecimal(z.doubleValue() - r + zza);//BigMath.subD(z, zza + r);

        // North (-z)
        t.vertexUV(x1, y + 1.0, tz40, u0, v0);   // t.vertexUV(x1, y + 1.0, z - r, u0, v0);
        t.vertexUV(tx31, y + 0.0, tz41, u0, v1); // t.vertexUV(x1 + xxa, y + 0.0, z - r + zza, u0, v1);
        t.vertexUV(tx30, y + 0.0, tz41, u1, v1); // t.vertexUV(x0 + xxa, y + 0.0, z - r + zza, u1, v1);
        t.vertexUV(x0, y + 1.0, tz40, u1, v0);   // t.vertexUV(x0, y + 1.0, z - r, u1, v0);
    }

    public boolean tesselateFireInWorld(Tile tile, final BigInteger x, int y, final BigInteger z) {
        Tesselator t = Tesselator.instance;
        int tex = tile.getTexture(0);
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }

        float br = tile.getBrightness(this.level, x, y, z);
        t.color(br, br, br);
        int tx = (tex & 15) << 4;
        int yt = tex & 240;
        double u0 = tx / 256.0F;
        double u1 = (tx + 15.99F) / 256.0F;
        double v0 = yt / 256.0F;
        double v1 = (yt + 15.99F) / 256.0F;
        float h = 1.4F;

        final BigInteger xPlusOne = x.add(BigInteger.ONE);
        final BigInteger xMinusOne = x.subtract(BigInteger.ONE);
        final BigInteger zPlusOne = z.add(BigInteger.ONE);
        final BigInteger zMinusOne = z.subtract(BigInteger.ONE);

        final BigDecimal xD = new BigDecimal(x);
        final BigDecimal zD = new BigDecimal(z);

        final BigDecimal xPlusOneD = new BigDecimal(xPlusOne);
        final BigDecimal zPlusOneD = new BigDecimal(zPlusOne);


        if (!this.level.isSolidBlockingTile(x, y - 1, z) && !Tile.FIRE.canBurn(this.level, x, y - 1, z)) {
            float r = 0.2F;
            float yo = 0.0625F;
            BigDecimal bigR = BigMath.decimal(r);
            BigDecimal xPlusR = BigMath.addD(xD, bigR);
            BigDecimal xPlusOneMinusR = BigMath.subD(xPlusOneD, bigR);
            BigDecimal zPlusR = BigMath.addD(zD, bigR);
            BigDecimal zPlusOneMinusR = BigMath.subD(zPlusOneD, bigR);
            if ((x.add(BigInteger.valueOf(y)).add(z).and(BigInteger.ONE).intValue()) == 1) {
                u0 = tx / 256.0F;
                u1 = (tx + 15.99F) / 256.0F;
                v0 = (yt + 16) / 256.0F;
                v1 = (yt + 15.99F + 16.0F) / 256.0F;
            }

            if ((x.divide(BigInteger.TWO).add(BigInteger.valueOf(y / 2)).add(z.divide(BigInteger.TWO)).and(BigInteger.ONE).intValue()) == 1) {
                double tmp = u1;
                u1 = u0;
                u0 = tmp;
            }

            if (Tile.FIRE.canBurn(this.level, xMinusOne, y, z)) {
                t.vertexUV(xPlusR, y + h + yo, zPlusOneD, u1, v0);
                t.vertexUV(xD, y + 0 + yo, zPlusOneD, u1, v1);
                t.vertexUV(xD, y + 0 + yo, zD, u0, v1);
                t.vertexUV(xPlusR, y + h + yo, zD, u0, v0);
                t.vertexUV(xPlusR, y + h + yo, zD, u0, v0);
                t.vertexUV(xD, y + 0 + yo, zD, u0, v1);
                t.vertexUV(xD, y + 0 + yo, zPlusOneD, u1, v1);
                t.vertexUV(xPlusR, y + h + yo, zPlusOneD, u1, v0);
            }

            if (Tile.FIRE.canBurn(this.level, xPlusOne, y, z)) {
                t.vertexUV(xPlusOneMinusR, y + h + yo, zD, u0, v0);
                t.vertexUV(xPlusOneD, y + 0 + yo, zD, u0, v1);
                t.vertexUV(xPlusOneD, y + 0 + yo, zPlusOneD, u1, v1);
                t.vertexUV(xPlusOneMinusR, y + h + yo, zPlusOneD, u1, v0);
                t.vertexUV(xPlusOneMinusR, y + h + yo, zPlusOneD, u1, v0);
                t.vertexUV(xPlusOneD, y + 0 + yo, zPlusOneD, u1, v1);
                t.vertexUV(xPlusOneD, y + 0 + yo, zD, u0, v1);
                t.vertexUV(xPlusOneMinusR, y + h + yo, zD, u0, v0);
            }

            if (Tile.FIRE.canBurn(this.level, x, y, zMinusOne)) {
                t.vertexUV(xD, y + h + yo, zPlusR, u1, v0);
                t.vertexUV(xD, y + 0 + yo, zD, u1, v1);
                t.vertexUV(xPlusOneD, y + 0 + yo, zD, u0, v1);
                t.vertexUV(xPlusOneD, y + h + yo, zPlusR, u0, v0);
                t.vertexUV(xPlusOneD, y + h + yo, zPlusR, u0, v0);
                t.vertexUV(xPlusOneD, y + 0 + yo, zD, u0, v1);
                t.vertexUV(xD, y + 0 + yo, zD, u1, v1);
                t.vertexUV(xD, y + h + yo, zPlusR, u1, v0);
            }

            if (Tile.FIRE.canBurn(this.level, x, y, zPlusOne)) {
                t.vertexUV(xPlusOneD, y + h + yo, zPlusOneMinusR, u0, v0);
                t.vertexUV(xPlusOneD, y + 0 + yo, zPlusOneD, u0, v1);
                t.vertexUV(xD, y + 0 + yo, zPlusOneD, u1, v1);
                t.vertexUV(xD, y + h + yo, zPlusOneMinusR, u1, v0);
                t.vertexUV(xD, y + h + yo, zPlusOneMinusR, u1, v0);
                t.vertexUV(xD, y + 0 + yo, zPlusOneD, u1, v1);
                t.vertexUV(xPlusOneD, y + 0 + yo, zPlusOneD, u0, v1);
                t.vertexUV(xPlusOneD, y + h + yo, zPlusOneMinusR, u0, v0);
            }

            if (Tile.FIRE.canBurn(this.level, x, y + 1, z)) {
                BigDecimal x0 = BigMath.addD(xD, BigDecimal.ONE);
                BigDecimal x1 = xD;
                BigDecimal z0 = BigMath.addD(zD, BigDecimal.ONE);
                BigDecimal z1 = zD;
                BigDecimal x0_ = xD;
                BigDecimal x1_ = BigMath.addD(xD, BigDecimal.ONE);
                BigDecimal z0_ = zD;
                BigDecimal z1_ = BigMath.addD(z, BigDecimal.ONE);
                u0 = tx / 256.0F;
                u1 = (tx + 15.99F) / 256.0F;
                v0 = yt / 256.0F;
                v1 = (yt + 15.99F) / 256.0F;
                y++;
                h = -0.2F;
                if ((x.add(BigInteger.valueOf(y)).add(z).and(BigInteger.ONE).intValue()) == 0) {
                    t.vertexUV(x0_, y + h, zD, u1, v0);
                    t.vertexUV(x0, y + 0, zD, u1, v1);
                    t.vertexUV(x0, y + 0, zPlusOneD, u0, v1);
                    t.vertexUV(x0_, y + h, zPlusOneD, u0, v0);
                    u0 = tx / 256.0F;
                    u1 = (tx + 15.99F) / 256.0F;
                    v0 = (yt + 16) / 256.0F;
                    v1 = (yt + 15.99F + 16.0F) / 256.0F;
                    t.vertexUV(x1_, y + h, zPlusOneD, u1, v0);
                    t.vertexUV(x1, y + 0, zPlusOneD, u1, v1);
                    t.vertexUV(x1, y + 0, zD, u0, v1);
                    t.vertexUV(x1_, y + h, zD, u0, v0);
                } else {
                    t.vertexUV(xD, y + h, z1_, u1, v0);
                    t.vertexUV(xD, y + 0, z1, u1, v1);
                    t.vertexUV(xPlusOneD, y + 0, z1, u0, v1);
                    t.vertexUV(xPlusOneD, y + h, z1_, u0, v0);
                    u0 = tx / 256.0F;
                    u1 = (tx + 15.99F) / 256.0F;
                    v0 = (yt + 16) / 256.0F;
                    v1 = (yt + 15.99F + 16.0F) / 256.0F;
                    t.vertexUV(xPlusOneD, y + h, z0_, u1, v0);
                    t.vertexUV(xPlusOneD, y + 0, z0, u1, v1);
                    t.vertexUV(xD, y + 0, z0, u0, v1);
                    t.vertexUV(xD, y + h, z0_, u0, v0);
                }
            }
        } else {
            final BigDecimal xPlusHalf = BigMath.addD(xD, BigConstants.POINT_FIVE);
            final BigDecimal zPlusHalf = BigMath.addD(zD, BigConstants.POINT_FIVE);
            BigDecimal x0 = BigMath.addD(xPlusHalf, BigConstants.POINT_TWO);
            BigDecimal x1 = BigMath.subD(xPlusHalf, BigConstants.POINT_TWO);
            BigDecimal z0 = BigMath.addD(zPlusHalf, BigConstants.POINT_TWO);
            BigDecimal z1 = BigMath.subD(zPlusHalf, BigConstants.POINT_TWO);
            BigDecimal x0_ = BigMath.subD(xPlusHalf, BigConstants.POINT_THREE);
            BigDecimal x1_ = BigMath.addD(xPlusHalf, BigConstants.POINT_THREE);
            BigDecimal z0_ = BigMath.subD(zPlusHalf, BigConstants.POINT_THREE);
            BigDecimal z1_ = BigMath.addD(zPlusHalf, BigConstants.POINT_THREE);
            t.vertexUV(x0_, y + h, zPlusOneD, u1, v0);
            t.vertexUV(x0, y + 0, zPlusOneD, u1, v1);
            t.vertexUV(x0, y + 0, zD, u0, v1);
            t.vertexUV(x0_, y + h, zD, u0, v0);
            t.vertexUV(x1_, y + h, zD, u1, v0);
            t.vertexUV(x1, y + 0, zD, u1, v1);
            t.vertexUV(x1, y + 0, zPlusOneD, u0, v1);
            t.vertexUV(x1_, y + h, zPlusOneD, u0, v0);
            u0 = tx / 256.0F;
            u1 = (tx + 15.99F) / 256.0F;
            v0 = (yt + 16) / 256.0F;
            v1 = (yt + 15.99F + 16.0F) / 256.0F;
            t.vertexUV(xPlusOneD, y + h, z1_, u1, v0);
            t.vertexUV(xPlusOneD, y + 0, z1, u1, v1);
            t.vertexUV(xD, y + 0, z1, u0, v1);
            t.vertexUV(xD, y + h, z1_, u0, v0);
            t.vertexUV(xD, y + h, z0_, u1, v0);
            t.vertexUV(xD, y + 0, z0, u1, v1);
            t.vertexUV(xPlusOneD, y + 0, z0, u0, v1);
            t.vertexUV(xPlusOneD, y + h, z0_, u0, v0);
            x0 = xD;
            x1 = BigMath.addD(x, BigDecimal.ONE);
            z0 = zD;
            z1 = BigMath.addD(z, BigDecimal.ONE);
            x0_ = BigMath.subD(xPlusHalf, BigConstants.POINT_FOUR);
            x1_ = BigMath.addD(xPlusHalf, BigConstants.POINT_FOUR);
            z0_ = BigMath.subD(zPlusHalf, BigConstants.POINT_FOUR);
            z1_ = BigMath.addD(zPlusHalf, BigConstants.POINT_FOUR);
            t.vertexUV(x0_, y + h, zD, u0, v0);
            t.vertexUV(x0, y + 0, zD, u0, v1);
            t.vertexUV(x0, y + 0, zPlusOneD, u1, v1);
            t.vertexUV(x0_, y + h, zPlusOneD, u1, v0);
            t.vertexUV(x1_, y + h, zPlusOneD, u0, v0);
            t.vertexUV(x1, y + 0, zPlusOneD, u0, v1);
            t.vertexUV(x1, y + 0, zD, u1, v1);
            t.vertexUV(x1_, y + h, zD, u1, v0);
            u0 = tx / 256.0F;
            u1 = (tx + 15.99F) / 256.0F;
            v0 = yt / 256.0F;
            v1 = (yt + 15.99F) / 256.0F;
            t.vertexUV(xD, y + h, z1_, u0, v0);
            t.vertexUV(xD, y + 0, z1, u0, v1);
            t.vertexUV(xPlusOneD, y + 0, z1, u1, v1);
            t.vertexUV(xPlusOneD, y + h, z1_, u1, v0);
            t.vertexUV(xPlusOneD, y + h, z0_, u0, v0);
            t.vertexUV(xPlusOneD, y + 0, z0, u0, v1);
            t.vertexUV(xD, y + 0, z0, u1, v1);
            t.vertexUV(xD, y + h, z0_, u1, v0);
        }

        return true;
    }

    public boolean tesselateDustInWorld(Tile tile, BigInteger x, int y, BigInteger z) {
        Tesselator t = Tesselator.instance;
        int power = this.level.getData(x, y, z);
        int tex = tile.getTexture(1, power);
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }

        float br = tile.getBrightness(this.level, x, y, z);
        float var9 = power / 15.0F;
        float var10 = var9 * 0.6F + 0.4F;
        if (power == 0) {
            var10 = 0.3F;
        }

        float var11 = var9 * var9 * 0.7F - 0.5F;
        float var12 = var9 * var9 * 0.6F - 0.7F;
        if (var11 < 0.0F) {
            var11 = 0.0F;
        }

        if (var12 < 0.0F) {
            var12 = 0.0F;
        }

        t.color(br * var10, br * var11, br * var12);
        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u0 = xt / 256.0F;
        double u1 = (xt + 15.99F) / 256.0F;
        double v0 = yt / 256.0F;
        double v1 = (yt + 15.99F) / 256.0F;

        BigInteger xPlusOne = x.add(BigInteger.ONE);
        BigInteger xMinusOne = x.subtract(BigInteger.ONE);
        BigInteger zPlusOne = z.add(BigInteger.ONE);
        BigInteger zMinusOne = z.subtract(BigInteger.ONE);

        BigDecimal xD = new BigDecimal(x);
        BigDecimal zD = new BigDecimal(z);

        boolean w = BigRedStoneDustTileExtension.isPowerSourceAt(this.level, xMinusOne, y, z, 1)
                || !this.level.isSolidBlockingTile(xMinusOne, y, z) && BigRedStoneDustTileExtension.isPowerSourceAt(this.level, xMinusOne, y - 1, z, -1);
        boolean e = BigRedStoneDustTileExtension.isPowerSourceAt(this.level, xPlusOne, y, z, 3)
                || !this.level.isSolidBlockingTile(xPlusOne, y, z) && BigRedStoneDustTileExtension.isPowerSourceAt(this.level, xPlusOne, y - 1, z, -1);
        boolean n = BigRedStoneDustTileExtension.isPowerSourceAt(this.level, x, y, zMinusOne, 2)
                || !this.level.isSolidBlockingTile(x, y, zMinusOne) && BigRedStoneDustTileExtension.isPowerSourceAt(this.level, x, y - 1, zMinusOne, -1);
        boolean s = BigRedStoneDustTileExtension.isPowerSourceAt(this.level, x, y, zPlusOne, 0)
                || !this.level.isSolidBlockingTile(x, y, zPlusOne) && BigRedStoneDustTileExtension.isPowerSourceAt(this.level, x, y - 1, zPlusOne, -1);
        if (!this.level.isSolidBlockingTile(x, y + 1, z)) {
            if (this.level.isSolidBlockingTile(xMinusOne, y, z) && BigRedStoneDustTileExtension.isPowerSourceAt(this.level, xMinusOne, y + 1, z, -1)) {
                w = true;
            }

            if (this.level.isSolidBlockingTile(xPlusOne, y, z) && BigRedStoneDustTileExtension.isPowerSourceAt(this.level, xPlusOne, y + 1, z, -1)) {
                e = true;
            }

            if (this.level.isSolidBlockingTile(x, y, zMinusOne) && BigRedStoneDustTileExtension.isPowerSourceAt(this.level, x, y + 1, zMinusOne, -1)) {
                n = true;
            }

            if (this.level.isSolidBlockingTile(x, y, zPlusOne) && BigRedStoneDustTileExtension.isPowerSourceAt(this.level, x, y + 1, zPlusOne, -1)) {
                s = true;
            }
        }

        float d = 0.3125F;
        BigDecimal dB = BigMath.decimal(d);
        BigDecimal xPlusOneD = new BigDecimal(xPlusOne);
        BigDecimal zPlusOneD = new BigDecimal(zPlusOne);
        BigDecimal x0 = xD;
        BigDecimal x1 = xPlusOneD;
        BigDecimal z0 = zD;
        BigDecimal z1 = zPlusOneD;
        float r = 0.03125F;
        float o = r / 2;
        BigDecimal oB = BigMath.decimal(o);
        BigDecimal xPlusO = BigMath.addD(xD, oB);
        BigDecimal xPlusOneMinusO = BigMath.subD(xPlusOneD, oB);
        BigDecimal zPlusO = BigMath.addD(zD, oB);
        BigDecimal zPlusOneMinusO = BigMath.subD(zPlusOneD, oB);
        int pic = 0;
        if ((w || e) && !n && !s) {
            pic = 1;
        }

        if ((n || s) && !e && !w) {
            pic = 2;
        }

        if (pic != 0) {
            u0 = (xt + 16) / 256.0F;
            u1 = (xt + 16 + 15.99F) / 256.0F;
            v0 = yt / 256.0F;
            v1 = (yt + 15.99F) / 256.0F;
        }

        if (pic == 0) {
            if (e || n || s || w) {
                if (!w) {
                    x0 = BigMath.addD(x0, dB);
                }

                if (!w) {
                    u0 += d / 16.0F;
                }

                if (!e) {
                    x1 = BigMath.subD(x1, dB);
                }

                if (!e) {
                    u1 -= d / 16.0F;
                }

                if (!n) {
                    z0 = BigMath.addD(z0, dB);
                }

                if (!n) {
                    v0 += d / 16.0F;
                }

                if (!s) {
                    z1 = BigMath.subD(z1, dB);
                }

                if (!s) {
                    v1 -= d / 16.0F;
                }
            }

            t.vertexUV(x1, y + 0.015625F, z1, u1, v1);
            t.vertexUV(x1, y + 0.015625F, z0, u1, v0);
            t.vertexUV(x0, y + 0.015625F, z0, u0, v0);
            t.vertexUV(x0, y + 0.015625F, z1, u0, v1);
            t.color(br, br, br);
            t.vertexUV(x1, y + 0.015625F, z1, u1, v1 + 0.0625);
            t.vertexUV(x1, y + 0.015625F, z0, u1, v0 + 0.0625);
            t.vertexUV(x0, y + 0.015625F, z0, u0, v0 + 0.0625);
            t.vertexUV(x0, y + 0.015625F, z1, u0, v1 + 0.0625);
        } else if (pic == 1) {
            t.vertexUV(x1, y + 0.015625F, z1, u1, v1);
            t.vertexUV(x1, y + 0.015625F, z0, u1, v0);
            t.vertexUV(x0, y + 0.015625F, z0, u0, v0);
            t.vertexUV(x0, y + 0.015625F, z1, u0, v1);
            t.color(br, br, br);
            t.vertexUV(x1, y + 0.015625F, z1, u1, v1 + 0.0625);
            t.vertexUV(x1, y + 0.015625F, z0, u1, v0 + 0.0625);
            t.vertexUV(x0, y + 0.015625F, z0, u0, v0 + 0.0625);
            t.vertexUV(x0, y + 0.015625F, z1, u0, v1 + 0.0625);
        } else if (pic == 2) {
            t.vertexUV(x1, y + 0.015625F, z1, u1, v1);
            t.vertexUV(x1, y + 0.015625F, z0, u0, v1);
            t.vertexUV(x0, y + 0.015625F, z0, u0, v0);
            t.vertexUV(x0, y + 0.015625F, z1, u1, v0);
            t.color(br, br, br);
            t.vertexUV(x1, y + 0.015625F, z1, u1, v1 + 0.0625);
            t.vertexUV(x1, y + 0.015625F, z0, u0, v1 + 0.0625);
            t.vertexUV(x0, y + 0.015625F, z0, u0, v0 + 0.0625);
            t.vertexUV(x0, y + 0.015625F, z1, u1, v0 + 0.0625);
        }

        if (!this.level.isSolidBlockingTile(x, y + 1, z)) {
            u0 = (xt + 16) / 256.0F;
            u1 = (xt + 16 + 15.99F) / 256.0F;
            v0 = yt / 256.0F;
            v1 = (yt + 15.99F) / 256.0F;
            if (this.level.isSolidBlockingTile(xMinusOne, y, z) && this.level.getTile(xMinusOne, y + 1, z) == Tile.REDSTONE.id) {
                t.color(br * var10, br * var11, br * var12);
                t.vertexUV(xPlusO, y + 1 + 0.021875F, zPlusOneD, u1, v0);
                t.vertexUV(xPlusO, y + 0, zPlusOneD, u0, v0);
                t.vertexUV(xPlusO, y + 0, zD, u0, v1);
                t.vertexUV(xPlusO, y + 1 + 0.021875F, zD, u1, v1);
                t.color(br, br, br);
                t.vertexUV(xPlusO, y + 1 + 0.021875F, zPlusOneD, u1, v0 + 0.0625);
                t.vertexUV(xPlusO, y + 0, zPlusOneD, u0, v0 + 0.0625);
                t.vertexUV(xPlusO, y + 0, zD, u0, v1 + 0.0625);
                t.vertexUV(xPlusO, y + 1 + 0.021875F, zD, u1, v1 + 0.0625);
            }

            if (this.level.isSolidBlockingTile(xPlusOne, y, z) && this.level.getTile(xPlusOne, y + 1, z) == Tile.REDSTONE.id) {
                t.color(br * var10, br * var11, br * var12);
                t.vertexUV(xPlusOneMinusO, y + 0, zPlusOneD, u0, v1);
                t.vertexUV(xPlusOneMinusO, y + 1 + 0.021875F, zPlusOneD, u1, v1);
                t.vertexUV(xPlusOneMinusO, y + 1 + 0.021875F, zD, u1, v0);
                t.vertexUV(xPlusOneMinusO, y + 0, zD, u0, v0);
                t.color(br, br, br);
                t.vertexUV(xPlusOneMinusO, y + 0, zPlusOneD, u0, v1 + 0.0625);
                t.vertexUV(xPlusOneMinusO, y + 1 + 0.021875F, zPlusOneD, u1, v1 + 0.0625);
                t.vertexUV(xPlusOneMinusO, y + 1 + 0.021875F, zD, u1, v0 + 0.0625);
                t.vertexUV(xPlusOneMinusO, y + 0, zD, u0, v0 + 0.0625);
            }

            if (this.level.isSolidBlockingTile(x, y, zMinusOne) && this.level.getTile(x, y + 1, zMinusOne) == Tile.REDSTONE.id) {
                t.color(br * var10, br * var11, br * var12);
                t.vertexUV(xPlusOneD, y + 0, zPlusO, u0, v1);
                t.vertexUV(xPlusOneD, y + 1 + 0.021875F, zPlusO, u1, v1);
                t.vertexUV(xD, y + 1 + 0.021875F, zPlusO, u1, v0);
                t.vertexUV(xD, y + 0, zPlusO, u0, v0);
                t.color(br, br, br);
                t.vertexUV(xPlusOneD, y + 0, zPlusO, u0, v1 + 0.0625);
                t.vertexUV(xPlusOneD, y + 1 + 0.021875F, zPlusO, u1, v1 + 0.0625);
                t.vertexUV(xD, y + 1 + 0.021875F, zPlusO, u1, v0 + 0.0625);
                t.vertexUV(xD, y + 0, zPlusO, u0, v0 + 0.0625);
            }

            if (this.level.isSolidBlockingTile(x, y, zPlusOne) && this.level.getTile(x, y + 1, zPlusOne) == Tile.REDSTONE.id) {
                t.color(br * var10, br * var11, br * var12);
                t.vertexUV(xPlusOneD, y + 1 + 0.021875F, zPlusOneMinusO, u1, v0);
                t.vertexUV(xPlusOneD, y + 0, zPlusOneMinusO, u0, v0);
                t.vertexUV(xD, y + 0, zPlusOneMinusO, u0, v1);
                t.vertexUV(xD, y + 1 + 0.021875F, zPlusOneMinusO, u1, v1);
                t.color(br, br, br);
                t.vertexUV(xPlusOneD, y + 1 + 0.021875F, zPlusOneMinusO, u1, v0 + 0.0625);
                t.vertexUV(xPlusOneD, y + 0, zPlusOneMinusO, u0, v0 + 0.0625);
                t.vertexUV(xD, y + 0, zPlusOneMinusO, u0, v1 + 0.0625);
                t.vertexUV(xD, y + 1 + 0.021875F, zPlusOneMinusO, u1, v1 + 0.0625);
            }
        }

        return true;
    }

    public boolean tesselateRailInWorld(RailTile rail, final BigInteger x, int y, final BigInteger z) {
        Tesselator t = Tesselator.instance;
        int data = this.level.getData(x, y, z);
        int tex = rail.getTexture(0, data);
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }

        if (rail.isStraight()) {
            data &= 7;
        }

        float br = rail.getBrightness(this.level, x, y, z);
        t.color(br, br, br);
        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u0 = xt / 256.0F;
        double u1 = (xt + 15.99F) / 256.0F;
        double v0 = yt / 256.0F;
        double v1 = (yt + 15.99F) / 256.0F;
        float r = 0.0625F;
        BigDecimal xD = new BigDecimal(x);
        BigDecimal zD = new BigDecimal(z);
        BigDecimal xPlusOneD = new BigDecimal(x.add(BigInteger.ONE));
        BigDecimal zPlusOneD = new BigDecimal(z.add(BigInteger.ONE));
        BigDecimal x0 = xPlusOneD;
        BigDecimal x1 = xPlusOneD;
        BigDecimal x2 = xD;
        BigDecimal x3 = xD;
        BigDecimal z0 = zD;
        BigDecimal z1 = zPlusOneD;
        BigDecimal z2 = zPlusOneD;
        BigDecimal z3 = zD;
        float y0 = y + r;
        float y1 = y + r;
        float y2 = y + r;
        float y3 = y + r;
        if (data == 1 || data == 2 || data == 3 || data == 7) {
            x0 = x3 = xPlusOneD;
            x1 = x2 = xD;
            z0 = z1 = zPlusOneD;
            z2 = z3 = zD;
        } else if (data == 8) {
            x0 = x1 = xD;
            x2 = x3 = xPlusOneD;
            z0 = z3 = zPlusOneD;
            z1 = z2 = zD;
        } else if (data == 9) {
            x0 = x3 = xD;
            x1 = x2 = xPlusOneD;
            z0 = z1 = zD;
            z2 = z3 = zPlusOneD;
        }

        if (data == 2 || data == 4) {
            y0++;
            y3++;
        } else if (data == 3 || data == 5) {
            y1++;
            y2++;
        }

        t.vertexUV(x0, y0, z0, u1, v0);
        t.vertexUV(x1, y1, z1, u1, v1);
        t.vertexUV(x2, y2, z2, u0, v1);
        t.vertexUV(x3, y3, z3, u0, v0);
        t.vertexUV(x3, y3, z3, u0, v0);
        t.vertexUV(x2, y2, z2, u0, v1);
        t.vertexUV(x1, y1, z1, u1, v1);
        t.vertexUV(x0, y0, z0, u1, v0);
        return true;
    }

    private static BigDecimal CROSS_CONSTANT = BigDecimal.valueOf(0.45F);

    public void tesselateCrossTexture(Tile tile, int data, BigDecimal x, double y, BigDecimal z) {
        Tesselator t = Tesselator.instance;
        int tex = tile.getTexture(0, data);
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }

        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u0 = xt / 256.0F;
        double u1 = (xt + 15.99F) / 256.0F;
        double v0 = yt / 256.0F;
        double v1 = (yt + 15.99F) / 256.0F;
        BigDecimal x0 = x.add(BigConstants.POINT_FIVE).subtract(CROSS_CONSTANT);
        BigDecimal x1 = x.add(BigConstants.POINT_FIVE).add(CROSS_CONSTANT);
        BigDecimal z0 = z.add(BigConstants.POINT_FIVE).subtract(CROSS_CONSTANT);
        BigDecimal z1 = z.add(BigConstants.POINT_FIVE).add(CROSS_CONSTANT);
        t.vertexUV(x0, y + 1.0, z0, u0, v0);
        t.vertexUV(x0, y + 0.0, z0, u0, v1);
        t.vertexUV(x1, y + 0.0, z1, u1, v1);
        t.vertexUV(x1, y + 1.0, z1, u1, v0);
        t.vertexUV(x1, y + 1.0, z1, u0, v0);
        t.vertexUV(x1, y + 0.0, z1, u0, v1);
        t.vertexUV(x0, y + 0.0, z0, u1, v1);
        t.vertexUV(x0, y + 1.0, z0, u1, v0);
        t.vertexUV(x0, y + 1.0, z1, u0, v0);
        t.vertexUV(x0, y + 0.0, z1, u0, v1);
        t.vertexUV(x1, y + 0.0, z0, u1, v1);
        t.vertexUV(x1, y + 1.0, z0, u1, v0);
        t.vertexUV(x1, y + 1.0, z0, u0, v0);
        t.vertexUV(x1, y + 0.0, z0, u0, v1);
        t.vertexUV(x0, y + 0.0, z1, u1, v1);
        t.vertexUV(x0, y + 1.0, z1, u1, v0);
    }

    private static final BigDecimal ROW_CONSTANT = BigDecimal.valueOf(0.25);

    public void tesselateRowTexture(Tile tile, int data, BigDecimal x, double y, BigDecimal z) {
        Tesselator t = Tesselator.instance;
        int tex = tile.getTexture(0, data);
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }

        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u0 = xt / 256.0F;
        double u1 = (xt + 15.99F) / 256.0F;
        double v0 = yt / 256.0F;
        double v1 = (yt + 15.99F) / 256.0F;
        BigDecimal x0 = x.add(BigConstants.POINT_FIVE).subtract(ROW_CONSTANT);
        BigDecimal x1 = x.add(BigConstants.POINT_FIVE).add(ROW_CONSTANT);
        BigDecimal z0 = z;
        BigDecimal z1 = z.add(BigDecimal.ONE);
        t.vertexUV(x0, y + 1.0, z0, u0, v0);
        t.vertexUV(x0, y + 0.0, z0, u0, v1);
        t.vertexUV(x0, y + 0.0, z1, u1, v1);
        t.vertexUV(x0, y + 1.0, z1, u1, v0);
        t.vertexUV(x0, y + 1.0, z1, u0, v0);
        t.vertexUV(x0, y + 0.0, z1, u0, v1);
        t.vertexUV(x0, y + 0.0, z0, u1, v1);
        t.vertexUV(x0, y + 1.0, z0, u1, v0);
        t.vertexUV(x1, y + 1.0, z1, u0, v0);
        t.vertexUV(x1, y + 0.0, z1, u0, v1);
        t.vertexUV(x1, y + 0.0, z0, u1, v1);
        t.vertexUV(x1, y + 1.0, z0, u1, v0);
        t.vertexUV(x1, y + 1.0, z0, u0, v0);
        t.vertexUV(x1, y + 0.0, z0, u0, v1);
        t.vertexUV(x1, y + 0.0, z1, u1, v1);
        t.vertexUV(x1, y + 1.0, z1, u1, v0);
        x0 = x;
        x1 = x.add(BigDecimal.ONE);
        z0 = z.add(BigConstants.POINT_FIVE).subtract(ROW_CONSTANT);
        z1 = z.add(BigConstants.POINT_FIVE).add(ROW_CONSTANT);
        t.vertexUV(x0, y + 1.0, z0, u0, v0);
        t.vertexUV(x0, y + 0.0, z0, u0, v1);
        t.vertexUV(x1, y + 0.0, z0, u1, v1);
        t.vertexUV(x1, y + 1.0, z0, u1, v0);
        t.vertexUV(x1, y + 1.0, z0, u0, v0);
        t.vertexUV(x1, y + 0.0, z0, u0, v1);
        t.vertexUV(x0, y + 0.0, z0, u1, v1);
        t.vertexUV(x0, y + 1.0, z0, u1, v0);
        t.vertexUV(x1, y + 1.0, z1, u0, v0);
        t.vertexUV(x1, y + 0.0, z1, u0, v1);
        t.vertexUV(x0, y + 0.0, z1, u1, v1);
        t.vertexUV(x0, y + 1.0, z1, u1, v0);
        t.vertexUV(x0, y + 1.0, z1, u0, v0);
        t.vertexUV(x0, y + 0.0, z1, u0, v1);
        t.vertexUV(x1, y + 0.0, z1, u1, v1);
        t.vertexUV(x1, y + 1.0, z1, u1, v0);
    }

    @Override
    public boolean tesselateWaterInWorld(Tile tt, final BigInteger x, int y, final BigInteger z) {
        Tesselator t = Tesselator.instance;
        int col = tt.getFoliageColor(this.level, x, y, z);
        float r = (float) (col >> 16 & 0xFF) / 255.0F;
        float g = (float) (col >> 8 & 0xFF) / 255.0F;
        float b = (float) (col & 0xFF) / 255.0F;
        boolean up = tt.shouldRenderFace(this.level, x, y + 1, z, Facing.UP);
        boolean down = tt.shouldRenderFace(this.level, x, y - 1, z, Facing.DOWN);

        BigInteger xPlusOne = x.add(BigInteger.ONE);
        BigInteger xMinusOne = x.subtract(BigInteger.ONE);
        BigInteger zPlusOne = z.add(BigInteger.ONE);
        BigInteger zMinusOne = z.subtract(BigInteger.ONE);

        boolean[] dirs = new boolean[]{
                tt.shouldRenderFace(this.level, x, y, zMinusOne, Facing.NORTH),
                tt.shouldRenderFace(this.level, x, y, zPlusOne, Facing.SOUTH),
                tt.shouldRenderFace(this.level, xMinusOne, y, z, Facing.WEST),
                tt.shouldRenderFace(this.level, xPlusOne, y, z, Facing.EAST)
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
            float h1 = getWaterHeight(x, y, zPlusOne, m);
            float h2 = getWaterHeight(xPlusOne, y, zPlusOne, m);
            float h3 = getWaterHeight(xPlusOne, y, z, m);
            if (this.noCulling || up) {
                changed = true;
                int tex = tt.getTexture(Facing.UP, data);
                float angle = (float) LiquidUtil.getSlopeAngle(this.level, x, y, z, m);
                if (angle > -999.0F) {
                    tex = tt.getTexture(Facing.NORTH, data);
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
                    BigDecimal bigX = new BigDecimal(x);
                    BigDecimal bigXPlusOne = new BigDecimal(xPlusOne);
                    BigDecimal bigZ = new BigDecimal(z);
                    BigDecimal bigZPlusOne = new BigDecimal(zPlusOne);
                    t.vertexUV(bigX, (float) y + h0, bigZ, uc - (double) c - (double) s, vc - (double) c + (double) s);
                    t.vertexUV(bigX, (float) y + h1, bigZPlusOne, uc - (double) c + (double) s, vc + (double) c + (double) s);
                    t.vertexUV(bigXPlusOne, (float) y + h2, bigZPlusOne, uc + (double) c + (double) s, vc + (double) c - (double) s);
                    t.vertexUV(bigXPlusOne, (float) y + h3, bigZ, uc + (double) c - (double) s, vc - (double) c - (double) s);
                } else {
                    t.vertexUV(x.doubleValue(), (float) y + h0, z.doubleValue(), uc - (double) c - (double) s, vc - (double) c + (double) s);
                    t.vertexUV(x.doubleValue(), (float) y + h1, z.add(BigInteger.ONE).doubleValue(), uc - (double) c + (double) s, vc + (double) c + (double) s);
                    t.vertexUV(x.add(BigInteger.ONE).doubleValue(), (float) y + h2, z.add(BigInteger.ONE).doubleValue(), uc + (double) c + (double) s, vc + (double) c - (double) s);
                    t.vertexUV(x.add(BigInteger.ONE).doubleValue(), (float) y + h3, z.doubleValue(), uc + (double) c - (double) s, vc - (double) c - (double) s);
                }
            }

            if (this.noCulling || down) {
                float br = tt.getBrightness(this.level, x, y - 1, z);
                t.color(c10 * br, c10 * br, c10 * br);
                if (FIX_STRIPELANDS) {
                    this.renderFaceDown(tt, new BigDecimal(x), y, new BigDecimal(z), tt.getTexture(Facing.DOWN));
                } else {
                    renderFaceDown(tt, x.doubleValue(), y, z.doubleValue(), tt.getTexture(Facing.DOWN));
                }
                changed = true;
            }

            for (int face = 0; face < 4; ++face) {
                if (FIX_STRIPELANDS) {
                    changed |= renderBigLiquidFace(
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
                    changed |= renderLiquidFace(
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
            final Tesselator t,
            final BigInteger x, final int y, final BigInteger z,
            final Tile tt, final int data,
            final boolean[] dirs,
            final float c2, final float c3, final float c11,
            final float r, final float g, final float b,
            final float h0, final float h1, final float h2, final float h3,
            final int face
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

    public boolean tesselateDoorInWorld(Tile tt, final BigInteger x, int y, final BigInteger z) {
        Tesselator t = Tesselator.instance;
        DoorTile dt = (DoorTile) tt;
        boolean changed = false;
        float c10 = 0.5F;
        float c11 = 1.0F;
        float c2 = 0.8F;
        float c3 = 0.6F;
        float centerBrightness = tt.getBrightness(this.level, x, y, z);
        float br = tt.getBrightness(this.level, x, y - 1, z);
        BigDecimal xD = new BigDecimal(x);
        BigDecimal zD = new BigDecimal(z);
        if (dt.yy0 > 0.0) {
            br = centerBrightness;
        }

        if (Tile.lightEmission[tt.id] > 0) {
            br = 1.0F;
        }

        t.color(c10 * br, c10 * br, c10 * br);
        if (FIX_STRIPELANDS) {
            this.renderFaceDown(tt, xD, y, zD, tt.getTexture(this.level, x, y, z, Facing.DOWN));
        } else {
            this.renderFaceDown(tt, x.doubleValue(), y, z.doubleValue(), tt.getTexture(this.level, x, y, z, Facing.DOWN));
        }

        changed = true;
        br = tt.getBrightness(this.level, x, y + 1, z);
        if (dt.yy1 < 1.0) {
            br = centerBrightness;
        }

        if (Tile.lightEmission[tt.id] > 0) {
            br = 1.0F;
        }

        t.color(c11 * br, c11 * br, c11 * br);
        if (FIX_STRIPELANDS) {
            this.renderFaceUp(tt, xD, y, zD, tt.getTexture(this.level, x, y, z, Facing.UP));
        } else {
            this.renderFaceUp(tt, x.doubleValue(), y, z.doubleValue(), tt.getTexture(this.level, x, y, z, Facing.UP));
        }

        changed = true;
        br = tt.getBrightness(this.level, x, y, z.subtract(BigInteger.ONE));
        if (dt.zz0 > 0.0) {
            br = centerBrightness;
        }

        if (Tile.lightEmission[tt.id] > 0) {
            br = 1.0F;
        }

        t.color(c2 * br, c2 * br, c2 * br);
        int tex = tt.getTexture(this.level, x, y, z, Facing.NORTH);
        if (tex < 0) {
            this.xFlipTexture = true;
            tex = -tex;
        }

        if (FIX_STRIPELANDS) {
            this.renderNorth(tt, xD, y, zD, tex);
        } else {
            this.renderNorth(tt, x.doubleValue(), y, z.doubleValue(), tex);
        }
        changed = true;
        this.xFlipTexture = false;
        br = tt.getBrightness(this.level, x, y, z.add(BigInteger.ONE));
        if (dt.zz1 < 1.0) {
            br = centerBrightness;
        }

        if (Tile.lightEmission[tt.id] > 0) {
            br = 1.0F;
        }

        t.color(c2 * br, c2 * br, c2 * br);
        tex = tt.getTexture(this.level, x, y, z, Facing.SOUTH);
        if (tex < 0) {
            this.xFlipTexture = true;
            tex = -tex;
        }

        if (FIX_STRIPELANDS) {
            this.renderSouth(tt, xD, y, zD, tex);
        } else {
            this.renderSouth(tt, x.doubleValue(), y, z.doubleValue(), tex);
        }
        changed = true;
        this.xFlipTexture = false;
        br = tt.getBrightness(this.level, x.subtract(BigInteger.ONE), y, z);
        if (dt.xx0 > 0.0) {
            br = centerBrightness;
        }

        if (Tile.lightEmission[tt.id] > 0) {
            br = 1.0F;
        }

        t.color(c3 * br, c3 * br, c3 * br);
        tex = tt.getTexture(this.level, x, y, z, Facing.WEST);
        if (tex < 0) {
            this.xFlipTexture = true;
            tex = -tex;
        }

        if (FIX_STRIPELANDS) {
            this.renderWest(tt, xD, y, zD, tex);
        } else {
            this.renderWest(tt, x.doubleValue(), y, z.doubleValue(), tex);
        }
        changed = true;
        this.xFlipTexture = false;
        br = tt.getBrightness(this.level, x.add(BigInteger.ONE), y, z);
        if (dt.xx1 < 1.0) {
            br = centerBrightness;
        }

        if (Tile.lightEmission[tt.id] > 0) {
            br = 1.0F;
        }

        t.color(c3 * br, c3 * br, c3 * br);
        tex = tt.getTexture(this.level, x, y, z, Facing.EAST);
        if (tex < 0) {
            this.xFlipTexture = true;
            tex = -tex;
        }

        if (FIX_STRIPELANDS) {
            this.renderEast(tt, xD, y, zD, tex);
        } else {
            this.renderEast(tt, x.doubleValue(), y, z.doubleValue(), tex);
        }
        changed = true;
        this.xFlipTexture = false;
        return changed;
    }
}
