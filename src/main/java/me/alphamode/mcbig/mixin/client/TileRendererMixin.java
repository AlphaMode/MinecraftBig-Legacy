package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.Directions;
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
import net.minecraft.util.Direction;
import net.minecraft.util.Facing;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.*;
import net.minecraft.world.level.tile.meta.TileData;
import net.minecraft.world.level.tile.piston.PistonBaseTile;
import net.minecraft.world.level.tile.piston.PistonExtensionTile;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;
import java.math.BigInteger;

/// AO docs cc000, ll000
/// cc is for lightmap
/// ll is for ao
/// --000 means base xyz
/// lower case means negative so llxy0 means negative x and y
/// upper case means positive so llXy0 means positive x, negative y and base z
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
    private boolean llTransXY0;

    @Shadow
    private boolean llTransXy0;

    @Shadow
    private boolean llTransX0Z;

    @Shadow
    private boolean llTransX0z;

    @Shadow
    private boolean llTransxY0;

    @Shadow
    private boolean llTransxy0;

    @Shadow
    private boolean llTransx0z;

    @Shadow
    private boolean llTransx0Z;

    @Shadow
    private boolean llTrans0YZ;

    @Shadow
    private boolean llTrans0Yz;

    @Shadow
    private boolean llTrans0yZ;

    @Shadow
    private boolean llTrans0yz;

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
    public abstract void tesselateTorch(Tile tt, double x, double y, double z, double xxa, double zza);

    @Shadow
    private boolean xFlipTexture;

    @Shadow
    private int northFlip;

    @Shadow
    private int southFlip;

    @Shadow
    private int eastFlip;

    @Shadow
    private int westFlip;

    @Shadow
    private int upFlip;

    @Shadow
    private int downFlip;

    @Shadow
    public abstract void tesselateCrossTexture(Tile tile, int data, double x, double y, double z);

    @Shadow
    public abstract void tesselateRowTexture(Tile tile, int data, double x, double y, double z);

    //? >=1.0.0-beta.8.0.r {
    /*@Shadow
    private int ccxY0;

    @Shadow
    private int ccXY0;

    @Shadow
    private int cc0Yz;

    @Shadow
    private int cc0YZ;

    @Shadow
    private int ccxYz;

    @Shadow
    private int ccXYz;

    @Shadow
    private int ccxYZ;

    @Shadow
    private int ccXYZ;

    @Shadow
    private int ccx0z;

    @Shadow
    private int ccX0z;

    @Shadow
    private int ccx0Z;

    @Shadow
    private int ccX0Z;
    @Shadow
    private int ccxy0;
    @Shadow
    private int cc0yz;
    @Shadow
    private int cc0yZ;
    @Shadow
    private int ccXy0;
    @Shadow
    private int ccxyz;
    @Shadow
    private int ccxyZ;
    @Shadow
    private int ccXyz;
    @Shadow
    private int ccXyZ;

    @Shadow
    private int tc1;
    @Shadow
    private int tc4;
    @Shadow
    private int tc3;
    @Shadow
    private int tc2;

    @Shadow
    protected abstract int blend(int par1, int par2, int par3, int par4);

    @Shadow
    public abstract boolean tesselateStemInWorld(Tile tt, int x, int y, int z);
    *///? }

    @Shadow
    protected abstract void renderPistonArmUpDown(double x0, double x1, double y0, double y1, double z0, double z1, float br, double armLengthPixels);

    @Shadow
    protected abstract void renderPistonArmNorthSouth(double x0, double x1, double y0, double y1, double z0, double z1, float br, double armLengthPixels);

    @Shadow
    protected abstract void renderPistonArmEastWest(double x0, double x1, double y0, double y1, double z0, double z1, float br, double armLengthPixels);

    private static float tesselateColorInfo(Tesselator t, Tile tt, Level level, float r, float g, float b, BigInteger x, int y, BigInteger z) {
        float br = tt.getBrightness(level, x, y, z);
        t.color(r * br, g * br, b * br);
        return 0;
    }

    @Override
    public void tesselateInWorld(Tile tile, BigInteger x, int y, BigInteger z, int destroyProgress) {
        this.fixedTexture = destroyProgress;
        tesselateInWorld(tile, x, y, z);
        this.fixedTexture = -1;
    }

    @Override
    public boolean tesselateInWorld(Tile tt, BigInteger x, int y, BigInteger z) {
        int shape = tt.getRenderShape();
        tt.updateShape(this.level, x, y, z);
        if (shape == 0) {
            return tesselateBlockInWorld(tt, x, y, z);
        } else if (shape == BlockShapes.SHAPE_WATER) {
            return tesselateWaterInWorld(tt, x, y, z);
        } else if (shape == BlockShapes.SHAPE_CACTUS) {
            return this.tesselateCactusInWorld(tt, x, y, z);
        } else if (shape == BlockShapes.SHAPE_CROSS_TEXTURE) {
            return this.tesselateCrossInWorld(tt, x, y, z);
        //? >=1.0.0-beta.8.0.r {
        /*} else if (shape == BlockShapes.SHAPE_STEM) {
            return this.tesselateStemInWorld(tt, x.intValue(), y, z.intValue());
        *///? }
        } else if (shape == BlockShapes.SHAPE_ROWS) {
            return this.tesselateRowInWorld(tt, x, y, z);
        } else if (shape == BlockShapes.SHAPE_TORCH) {
            return this.tesselateTorchInWorld(tt, x, y, z);
        } else if (shape == BlockShapes.SHAPE_FIRE) {
            return this.tesselateFireInWorld(tt, x, y, z);
        } else if (shape == BlockShapes.SHAPE_RED_DUST) {
            return this.tesselateDustInWorld(tt, x, y, z);
        } else if (shape == BlockShapes.SHAPE_LADDER) {
            return this.tesselateLadderInWorld(tt, x, y, z);
        } else if (shape == BlockShapes.SHAPE_DOOR) {
            return this.tesselateDoorInWorld(tt, x, y, z);
        } else if (shape == BlockShapes.SHAPE_RAIL) {
            return this.tesselateRailInWorld((RailTile)tt, x, y, z);
        } else if (shape == BlockShapes.SHAPE_STAIRS) {
            return this.tesselateStairsInWorld(tt, x, y, z);
        } else if (shape == BlockShapes.SHAPE_FENCE) {
            return this.tesselateFenceInWorld(tt, x, y, z);
        } else if (shape == BlockShapes.SHAPE_LEVER) {
            return this.tesselateLeverInWorld(tt, x, y, z); // // TODO: use big decimal
        } else if (shape == BlockShapes.SHAPE_BED) {
            return this.tesselateBedInWorld(tt, x, y, z);
        } else if (shape == BlockShapes.SHAPE_REPEATER) {
            return this.tesselateRepeaterInWorld(tt, x, y, z);
        } else if (shape == BlockShapes.SHAPE_PISTON_BASE) {
            return this.tesselatePistonInWorld(tt, x, y, z, false);
        } else if (shape == BlockShapes.SHAPE_PISTON_EXTENSION) {
            return this.tesselatePistonExtensionInWorld(tt, x, y, z, true);
        }
        return false;
    }

    private static float getShade(Tile tt, LevelSource level, BigInteger x, int y, BigInteger z) {
        //? >=1.0.0-beta.8.0.r {
        /*return tt.getShadeBrightness(level, x, y, z);
        *///? } else {
        return tt.getBrightness(level, x, y, z);
        //? }
    }

    private boolean tesselateBedInWorld(Tile tt, int x, int y, int z) {
        Tesselator t = Tesselator.instance;
        int data = this.level.getData(x, y, z);
        int direction = BedTile.getBedOrientation(data);
        boolean isHead = BedTile.isHeadPiece(data);
        float c10 = 0.5F;
        float c11 = 1.0F;
        float c2 = 0.8F;
        float c3 = 0.6F;

        float r11 = c11;
        float g11 = c11;
        float b11 = c11;

        float r10 = c10;
        float r2 = c2;
        float r3 = c3;

        float g10 = c10;
        float g2 = c2;
        float g3 = c3;

        float b10 = c10;
        float b2 = c2;
        float b3 = c3;

        //? >=1.0.0-beta.8.0.r {
        /*int centerColor = tt.getLightColor(this.level, x, y, z);
        *///? } else {
        float centerBr = tt.getBrightness(this.level, x, y, z);
        //? }
        // render wooden underside
        {
            //? >=1.0.0-beta.8.0.r {
            /*t.tex2(centerColor);
            t.color(r10, g10, b10);
            *///? } else {
            t.color(r10 * centerBr, g10 * centerBr, b10 * centerBr);
            //? }
            int tex = tt.getTexture(this.level, x, y, z, Facing.DOWN);
            int xt = (tex & 15) << 4;
            int yt = tex & 240;
            double u0 = xt / 256.0F;
            double u1 = (xt + 16 - 0.01) / 256.0;
            double v0 = yt / 256.0F;
            double v1 = (yt + 16 - 0.01) / 256.0;
            double x0 = x + tt.xx0;
            double x1 = x + tt.xx1;
            double y0 = y + tt.yy0 + 3.0 / 16.0;
            double z0 = z + tt.zz0;
            double z1 = z + tt.zz1;
            t.vertexUV(x0, y0, z1, u0, v1);
            t.vertexUV(x0, y0, z0, u0, v0);
            t.vertexUV(x1, y0, z0, u1, v0);
            t.vertexUV(x1, y0, z1, u1, v1);
        }
        // render bed top
        //? >=1.0.0-beta.8.0.r {
        /*t.tex2(tt.getLightColor(this.level, x, y + 1, z));
        t.color(r11, g11, b11);
        *///? } else {
        float brightness = tt.getBrightness(this.level, x, y + 1, z);
        t.color(r11 * brightness, g11 * brightness, b11 * brightness);
        //? }
        int tex = tt.getTexture(this.level, x, y, z, Facing.UP);
        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u0 = xt / 256.0F;
        double u1 = (xt + 16 - 0.01) / 256.0;
        double v0 = yt / 256.0F;
        double v1 = (yt + 16 - 0.01) / 256.0;

        // Default is west
        double topLeftU = u0;
        double topRightU = u1;
        double topLeftV = v0;
        double topRightV = v0;
        double bottomLeftU = u0;
        double bottomRightU = u1;
        double bottomLeftV = v1;
        double bottomRightV = v1;

        if (direction == Directions.SOUTH) {
            // rotate 90 degrees clockwise
            topRightU = u0;
            topLeftV = v1;
            bottomLeftU = u1;
            bottomRightV = v0;
        } else if (direction == Directions.NORTH) {
            // rotate 90 degrees counter-clockwise
            topLeftU = u1;
            topRightV = v1;
            bottomRightU = u0;
            bottomLeftV = v0;
        } else if (direction == Directions.EAST) {
            // rotate 180 degrees
            topLeftU = u1;
            topRightV = v1;
            bottomRightU = u0;
            bottomLeftV = v0;
            topRightU = u0;
            topLeftV = v1;
            bottomLeftU = u1;
            bottomRightV = v0;
        }

        double x0 = x + tt.xx0;
        double x1 = x + tt.xx1;
        double y1 = y + tt.yy1;
        double z0 = z + tt.zz0;
        double z1 = z + tt.zz1;

        t.vertexUV(x1, y1, z1, bottomLeftU, bottomLeftV);
        t.vertexUV(x1, y1, z0, topLeftU, topLeftV);
        t.vertexUV(x0, y1, z0, topRightU, topRightV);
        t.vertexUV(x0, y1, z1, bottomRightU, bottomRightV);

        // determine which edge to skip (the one between foot and head piece)
        int skipEdge = Direction.DIRECTION_FACING[direction];
        if (isHead) {
            skipEdge = Direction.DIRECTION_FACING[Direction.DIRECTION_OPPOSITE[direction]];
        }
        // and which edge to x-flip
        int flipEdge = Facing.WEST;
        switch (direction) {
            case Directions.NORTH:
                break;
            case Directions.SOUTH:
                flipEdge = Facing.EAST;
                break;
            case Directions.EAST:
                flipEdge = Facing.NORTH;
                break;
            case Directions.WEST:
                flipEdge = Facing.SOUTH;
        }

        if (skipEdge != Facing.NORTH && (this.noCulling || tt.shouldRenderFace(this.level, x, y, z - 1, Facing.NORTH))) {
            //? >=1.0.0-beta.8.0.r {
            /*t.tex2(tt.zz0 > 0.0 ? centerColor : tt.getLightColor(this.level, x, y, z - 1));
            t.color(r2, g2, b2);
            *///? } else {
            float br = tt.getBrightness(this.level, x, y, z - 1);
            if (tt.zz0 > 0.0) br = centerBr;
            t.color(r2 * br, g2 * br, b2 * br);
            //? }
            this.xFlipTexture = flipEdge == Facing.NORTH;
            this.renderNorth(tt, x, y, z, tt.getTexture(this.level, x, y, z, Facing.NORTH));
        }

        if (skipEdge != Facing.SOUTH && (this.noCulling || tt.shouldRenderFace(this.level, x, y, z + 1, Facing.SOUTH))) {
            //? >=1.0.0-beta.8.0.r {
            /*t.tex2(tt.zz1 < 1.0 ? centerColor : tt.getLightColor(this.level, x, y, z + 1));
            t.color(r2, g2, b2);
            *///? } else {
            float br = tt.getBrightness(this.level, x, y, z + 1);
            if (tt.zz1 < 1.0) br = centerBr;
            t.color(r2 * br, g2 * br, b2 * br);
            //? }
            this.xFlipTexture = flipEdge == Facing.SOUTH;
            this.renderSouth(tt, x, y, z, tt.getTexture(this.level, x, y, z, Facing.SOUTH));
        }

        if (skipEdge != Facing.WEST && (this.noCulling || tt.shouldRenderFace(this.level, x - 1, y, z, Facing.WEST))) {
            //? >=1.0.0-beta.8.0.r {
            /*t.tex2(tt.zz0 > 0.0 ? centerColor : tt.getLightColor(this.level, x - 1, y, z));
            t.color(r3, g3, b3);
            *///? } else {
            float br = tt.getBrightness(this.level, x - 1, y, z);
            if (tt.zz0 > 0.0) br = centerBr;
            t.color(r3 * br, g3 * br, b3 * br);
            //? }
            this.xFlipTexture = flipEdge == Facing.WEST;
            this.renderWest(tt, x, y, z, tt.getTexture(this.level, x, y, z, Facing.WEST));
        }

        if (skipEdge != Facing.EAST && (this.noCulling || tt.shouldRenderFace(this.level, x + 1, y, z, Facing.EAST))) {
            //? >=1.0.0-beta.8.0.r {
            /*t.tex2(tt.zz1 < 1.0 ? centerColor : tt.getLightColor(this.level, x + 1, y, z));
            t.color(r3, g3, b3);
            *///? } else {
            float br = tt.getBrightness(this.level, x + 1, y, z);
            if (tt.zz1 < 1.0) br = centerBr;
            t.color(r3 * br, g3 * br, b3 * br);
            //? }
            this.xFlipTexture = flipEdge == Facing.EAST;
            this.renderEast(tt, x, y, z, tt.getTexture(this.level, x, y, z, Facing.EAST));
        }

        this.xFlipTexture = false;
        return true;
    }

    private boolean tesselateBedInWorld(Tile tt, final BigInteger x, int y, final BigInteger z) {
        Tesselator t = Tesselator.instance;
        int data = this.level.getData(x, y, z);
        int direction = BedTile.getBedOrientation(data);
        boolean isHead = BedTile.isHeadPiece(data);

        float c10 = 0.5F;
        float c11 = 1.0F;
        float c2 = 0.8F;
        float c3 = 0.6F;
        float centerBrightness = tt.getBrightness(this.level, x, y, z);
        // render wooden underside
        {
            t.color(c10 * centerBrightness, c10 * centerBrightness, c10 * centerBrightness);
            int tex = tt.getTexture(this.level, x, y, z, 0);
            int xt = (tex & 15) << 4;
            int yt = tex & 240;
            double u0 = xt / 256.0F;
            double u1 = (xt + 16 - 0.01) / 256.0;
            double v0 = yt / 256.0F;
            double v1 = (yt + 16 - 0.01) / 256.0;
            BigDecimal x0_ = BigMath.addD(x, tt.xx0);
            BigDecimal x1_ = BigMath.addD(x, tt.xx1);
            double y0_ = y + tt.yy0 + 0.1875;
            BigDecimal z0_ = BigMath.addD(z, tt.zz0);
            BigDecimal z1_ = BigMath.addD(z, tt.zz1);
            t.vertexUV(x0_, y0_, z1_, u0, v1);
            t.vertexUV(x0_, y0_, z0_, u0, v0);
            t.vertexUV(x1_, y0_, z0_, u1, v0);
            t.vertexUV(x1_, y0_, z1_, u1, v1);
        }
        double xx = FIX_STRIPELANDS ? BigMath.fastAnd(x, 15) : x.doubleValue();
        double zz = FIX_STRIPELANDS ? BigMath.fastAnd(z, 15) : z.doubleValue();
        double yy = FIX_STRIPELANDS ? y & 15 : y;

        // render bed top

        float brightness = tt.getBrightness(this.level, x, y + 1, z);
        t.color(c11 * brightness, c11 * brightness, c11 * brightness);

        int tex = tt.getTexture(this.level, x, y, z, 1);

        int xt = (tex & 15) << 4;
        int yt = tex & 240;

        double u0 = xt / 256.0F;
        double u1 = (xt + 16 - 0.01) / 256.0;
        double v0 = yt / 256.0F;
        double v1 = (yt + 16 - 0.01) / 256.0;

        // Default is west
        double topLeftU = u0;
        double topRightU = u1;
        double topLeftV = v0;
        double topRightV = v0;
        double bottomLeftU = u0;
        double bottomRightU = u1;
        double bottomLeftV = v1;
        double bottomRightV = v1;

        if (direction == 0) { // South
            // rotate 90 degrees clockwise
            topRightU = u0;
            topLeftV = v1;
            bottomLeftU = u1;
            bottomRightV = v0;
        } else if (direction == 2) { // North
            // rotate 90 degrees counter-clockwise
            topLeftU = u1;
            topRightV = v1;
            bottomRightU = u0;
            bottomLeftV = v0;
        } else if (direction == 3) { // East
            // rotate 180 degrees
            topLeftU = u1;
            topRightV = v1;
            bottomRightU = u0;
            bottomLeftV = v0;
            topRightU = u0;
            topLeftV = v1;
            bottomLeftU = u1;
            bottomRightV = v0;
        }

        double x0 = xx + tt.xx0;
        double x1 = xx + tt.xx1;
        double y0 = yy + tt.yy1;
        double z0 = zz + tt.zz0;
        double z1 = zz + tt.zz1;
        t.vertexUV(x1, y0, z1, bottomLeftU, bottomLeftV);
        t.vertexUV(x1, y0, z0, topLeftU, topLeftV);
        t.vertexUV(x0, y0, z0, topRightU, topRightV);
        t.vertexUV(x0, y0, z1, bottomRightU, bottomRightV);

        // determine which edge to skip (the one between foot and head piece)
        int skipEdge = Direction.DIRECTION_FACING[direction];
        if (isHead) {
            skipEdge = Direction.DIRECTION_FACING[Direction.DIRECTION_OPPOSITE[direction]];
        }

        int flipEdge = 4; // West
        switch (direction) {
            case 0: // South
                flipEdge = Facing.EAST;
                break;
            case 1: // West
                flipEdge = Facing.SOUTH;
            case 2:
                break;
            case 3: // East
                flipEdge = Facing.NORTH;
        }

        if (skipEdge != Facing.NORTH && (this.noCulling || tt.shouldRenderFace(this.level, x, y, z.subtract(BigInteger.ONE), Facing.NORTH))) {
            float br = tt.getBrightness(this.level, x, y, z.subtract(BigInteger.ONE));
            if (tt.zz0 > 0.0) {
                br = centerBrightness;
            }

            t.color(c2 * br, c2 * br, c2 * br);
            this.xFlipTexture = flipEdge == Facing.NORTH;
            this.renderNorth(tt, xx, yy, zz, tt.getTexture(this.level, x, y, z, Facing.NORTH));
        }

        if (skipEdge != Facing.SOUTH && (this.noCulling || tt.shouldRenderFace(this.level, x, y, z.add(BigInteger.ONE), Facing.SOUTH))) {
            float br = tt.getBrightness(this.level, x, y, z.add(BigInteger.ONE));
            if (tt.zz1 < 1.0) {
                br = centerBrightness;
            }

            t.color(c2 * br, c2 * br, c2 * br);
            this.xFlipTexture = flipEdge == Facing.SOUTH;
            this.renderSouth(tt, xx, yy, zz, tt.getTexture(this.level, x, y, z, Facing.SOUTH));
        }

        if (skipEdge != Facing.WEST && (this.noCulling || tt.shouldRenderFace(this.level, x.subtract(BigInteger.ONE), y, z, Facing.WEST))) {
            float br = tt.getBrightness(this.level, x.subtract(BigInteger.ONE), y, z);
            if (tt.xx0 > 0.0) {
                br = centerBrightness;
            }

            t.color(c3 * br, c3 * br, c3 * br);
            this.xFlipTexture = flipEdge == Facing.WEST;
            this.renderWest(tt, xx, yy, zz, tt.getTexture(this.level, x, y, z, Facing.WEST));
        }

        if (skipEdge != Facing.EAST && (this.noCulling || tt.shouldRenderFace(this.level, x.add(BigInteger.ONE), y, z, Facing.EAST))) {
            float br = tt.getBrightness(this.level, x.add(BigInteger.ONE), y, z);
            if (tt.xx1 < 1.0) {
                br = centerBrightness;
            }

            t.color(c3 * br, c3 * br, c3 * br);
            this.xFlipTexture = flipEdge == Facing.EAST;
            this.renderEast(tt, xx, yy, zz, tt.getTexture(this.level, x, y, z, Facing.EAST));
        }

        this.xFlipTexture = false;
        return true;
    }

    public boolean tesselateTorchInWorld(Tile tt, BigInteger x, int y, BigInteger z) {
        int dir = this.level.getData(x, y, z);
        Tesselator t = Tesselator.instance;
        float br = tt.getBrightness(this.level, x, y, z);
        if (Tile.lightEmission[tt.id] > 0) {
            br = 1.0F;
        }

        t.color(br, br, br);
        double r = 0.4F;
        double r2 = 0.5 - r;
        double h = 0.2F;

        double xx = FIX_STRIPELANDS ? BigMath.fastAnd(x, 15) : x.doubleValue();
        double zz = FIX_STRIPELANDS ? BigMath.fastAnd(z, 15) : z.doubleValue();
        double yy = FIX_STRIPELANDS ? y & 15 : y;

        if (dir == 1) {
            this.tesselateTorch(tt, xx - r2, yy + h, zz, -r, 0.0);
        } else if (dir == 2) {
            this.tesselateTorch(tt, xx + r2, yy + h, zz, r, 0.0);
        } else if (dir == 3) {
            this.tesselateTorch(tt, xx, yy + h, zz - r2, 0.0, -r);
        } else if (dir == 4) {
            this.tesselateTorch(tt, xx, yy + h, zz + r2, 0.0, r);
        } else {
            this.tesselateTorch(tt, xx, yy, zz, 0.0, 0.0);
        }

        return true;
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
    public boolean tesselateBlockInWorldWithAmbienceOcclusion(Tile tt, final BigInteger x, int y, final BigInteger z, float r, float g, float b) {
        double bigX = FIX_STRIPELANDS ? BigMath.fastAnd(x, 15) : x.doubleValue();
        double bigY = FIX_STRIPELANDS ? y & 15 : y;
        double bigZ = FIX_STRIPELANDS ? BigMath.fastAnd(z, 15) : z.doubleValue();
        this.blen = true;
        boolean changed = false;
        float ll1 = this.ll000;
        float ll2 = this.ll000;
        float ll3 = this.ll000;
        float ll4 = this.ll000;
        boolean tint0 = true;
        boolean tint1 = true;
        boolean tint2 = true;
        boolean tint3 = true;
        boolean tint4 = true;
        boolean tint5 = true;

        final BigInteger xPlusOne = x.add(BigInteger.ONE);
        final BigInteger xMinusOne = x.subtract(BigInteger.ONE);
        final BigInteger zPlusOne = z.add(BigInteger.ONE);
        final BigInteger zMinusOne = z.subtract(BigInteger.ONE);


        this.ll000 = getShade(tt, this.level, x, y, z);
        this.llx00 = getShade(tt, this.level, xMinusOne, y, z);
        this.ll0y0 = getShade(tt, this.level, x, y - 1, z);
        this.ll00z = getShade(tt, this.level, x, y, zMinusOne);
        this.llX00 = getShade(tt, this.level, xPlusOne, y, z);
        this.ll0Y0 = getShade(tt, this.level, x, y + 1, z);
        this.ll00Z = getShade(tt, this.level, x, y, zPlusOne);
        //? >=1.0.0-beta.8.0.r {
        /*int cx00 = tt.getLightColor(this.level, xMinusOne, y, z);
        int c0y0 = tt.getLightColor(this.level, x, y - 1, z);
        int c00z = tt.getLightColor(this.level, x, y, zMinusOne);
        int cX00 = tt.getLightColor(this.level, xPlusOne, y, z);
        int c0Y0 = tt.getLightColor(this.level, x, y + 1, z);
        int c00Z = tt.getLightColor(this.level, x, y, zPlusOne);
        Tesselator t = Tesselator.instance;
        t.tex2(0xf000f);
        *///? }
        this.llTransXY0 = Tile.translucent[this.level.getTile(xPlusOne, y + 1, z)];
        this.llTransXy0 = Tile.translucent[this.level.getTile(xPlusOne, y - 1, z)];
        this.llTransX0Z = Tile.translucent[this.level.getTile(xPlusOne, y, zPlusOne)];
        this.llTransX0z = Tile.translucent[this.level.getTile(xPlusOne, y, zMinusOne)];
        this.llTransxY0 = Tile.translucent[this.level.getTile(xMinusOne, y + 1, z)];
        this.llTransxy0 = Tile.translucent[this.level.getTile(xMinusOne, y - 1, z)];
        this.llTransx0z = Tile.translucent[this.level.getTile(xMinusOne, y, zMinusOne)];
        this.llTransx0Z = Tile.translucent[this.level.getTile(xMinusOne, y, zPlusOne)];
        this.llTrans0YZ = Tile.translucent[this.level.getTile(x, y + 1, zPlusOne)];
        this.llTrans0Yz = Tile.translucent[this.level.getTile(x, y + 1, zMinusOne)];
        this.llTrans0yZ = Tile.translucent[this.level.getTile(x, y - 1, zPlusOne)];
        this.llTrans0yz = Tile.translucent[this.level.getTile(x, y - 1, zMinusOne)];

        if (tt.tex == 3) tint0 = tint2 = tint3 = tint4 = tint5 = false;

        if (this.fixedTexture >= 0) {
            tint5 = false;
            tint4 = false;
            tint3 = false;
            tint2 = false;
            tint0 = false;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y - 1, z, Facing.DOWN)) {
            if (this.blsmooth <= 0) {
                ll4 = this.ll0y0;
                ll3 = this.ll0y0;
                ll2 = this.ll0y0;
                ll1 = this.ll0y0;
                //? >=1.0.0-beta.8.0.r
                //this.tc1 = this.tc2 = this.tc3 = this.tc4 = this.ccxy0;
            } else {
                y--;
                //? >=1.0.0-beta.8.0.r {
                /*this.ccxy0 = tt.getLightColor(this.level, xMinusOne, y, z);
                this.cc0yz = tt.getLightColor(this.level, x, y, zMinusOne);
                this.cc0yZ = tt.getLightColor(this.level, x, y, zPlusOne);
                this.ccXy0 = tt.getLightColor(this.level, xPlusOne, y, z);
                *///? }
                this.llxy0 = getShade(tt, this.level, xMinusOne, y, z);
                this.ll0yz = getShade(tt, this.level, x, y, zMinusOne);
                this.ll0yZ = getShade(tt, this.level, x, y, zPlusOne);
                this.llXy0 = getShade(tt, this.level, xPlusOne, y, z);

                if (!this.llTrans0yz && !this.llTransxy0) {
                    this.llxyz = this.llxy0;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxyz = this.ccxy0;
                } else {
                    this.llxyz = getShade(tt, this.level, xMinusOne, y, zMinusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxyz = tt.getLightColor(this.level, xMinusOne, y, zMinusOne);
                }

                if (!this.llTrans0yZ && !this.llTransxy0) {
                    this.llxyZ = this.llxy0;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxyZ = this.ccxy0;
                } else {
                    this.llxyZ = getShade(tt, this.level, xMinusOne, y, zPlusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxyZ = tt.getLightColor(this.level, xMinusOne, y, zPlusOne);
                }

                if (!this.llTrans0yz && !this.llTransXy0) {
                    this.llXyz = this.llXy0;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXyz = this.ccXy0;
                } else {
                    this.llXyz = getShade(tt, this.level, xPlusOne, y, zMinusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXyz = tt.getLightColor(this.level, xPlusOne, y, zMinusOne);
                }

                if (!this.llTrans0yZ && !this.llTransXy0) {
                    this.llXyZ = this.llXy0;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXyZ = this.ccXy0;
                } else {
                    this.llXyZ = getShade(tt, this.level, xPlusOne, y, zPlusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXyZ = tt.getLightColor(this.level, xPlusOne, y, zPlusOne);
                }

                y++;
                ll1 = (this.llxyZ + this.llxy0 + this.ll0yZ + this.ll0y0) / 4.0F;
                ll4 = (this.ll0yZ + this.ll0y0 + this.llXyZ + this.llXy0) / 4.0F;
                ll3 = (this.ll0y0 + this.ll0yz + this.llXy0 + this.llXyz) / 4.0F;
                ll2 = (this.llxy0 + this.llxyz + this.ll0y0 + this.ll0yz) / 4.0F;
                //? >=1.0.0-beta.8.0.r {
                /*this.tc1 = this.blend(this.ccxyZ, this.ccxy0, this.cc0yZ, c0y0);
                this.tc4 = this.blend(this.cc0yZ, this.ccXyZ, this.ccXy0, c0y0);
                this.tc3 = this.blend(this.cc0yz, this.ccXy0, this.ccXyz, c0y0);
                this.tc2 = this.blend(this.ccxy0, this.ccxyz, this.cc0yz, c0y0);
                *///? }
            }
            // TODO: unfuck this shit

            this.c1r = this.c2r = this.c3r = this.c4r = (tint0 ? r : 1.0F) * 0.5F;
            this.c1g = this.c2g = this.c3g = this.c4g = (tint0 ? g : 1.0F) * 0.5F;
            this.c1b = this.c2b = this.c3b = this.c4b = (tint0 ? b : 1.0F) * 0.5F;
            this.c1r *= ll1;
            this.c1g *= ll1;
            this.c1b *= ll1;
            this.c2r *= ll2;
            this.c2g *= ll2;
            this.c2b *= ll2;
            this.c3r *= ll3;
            this.c3g *= ll3;
            this.c3b *= ll3;
            this.c4r *= ll4;
            this.c4g *= ll4;
            this.c4b *= ll4;
            this.renderFaceDown(tt, bigX, bigY, bigZ, tt.getTexture(this.level, x, y, z, Facing.DOWN));
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y + 1, z, Facing.UP)) {
            if (this.blsmooth <= 0) {
                ll4 = this.ll0Y0;
                ll3 = this.ll0Y0;
                ll2 = this.ll0Y0;
                ll1 = this.ll0Y0;
                //? >=1.0.0-beta.8.0.r
                //this.tc1 = this.tc2 = this.tc3 = this.tc4 = c0Y0;
            } else {
                y++;
                //? >=1.0.0-beta.8.0.r {
                /*this.ccxY0 = tt.getLightColor(this.level, xMinusOne, y, z);
                this.ccXY0 = tt.getLightColor(this.level, xPlusOne, y, z);
                this.cc0Yz = tt.getLightColor(this.level, x, y, zMinusOne);
                this.cc0YZ = tt.getLightColor(this.level, x, y, zPlusOne);
                *///? }
                this.llxY0 = getShade(tt, this.level, xMinusOne, y, z);
                this.llXY0 = getShade(tt, this.level, xPlusOne, y, z);
                this.ll0Yz = getShade(tt, this.level, x, y, zMinusOne);
                this.ll0YZ = getShade(tt, this.level, x, y, zPlusOne);
                if (!this.llTrans0Yz && !this.llTransxY0) {
                    this.llxYz = this.llxY0;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxYz = this.ccxY0;
                } else {
                    this.llxYz = getShade(tt, this.level, xMinusOne, y, zMinusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxYz = tt.getLightColor(this.level, xMinusOne, y, zMinusOne);
                }

                if (!this.llTrans0Yz && !this.llTransXY0) {
                    this.llXYz = this.llXY0;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXYz = this.ccXY0;
                } else {
                    this.llXYz = getShade(tt, this.level, xPlusOne, y, zMinusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXYz = tt.getLightColor(this.level, xPlusOne, y, zMinusOne);
                }

                if (!this.llTrans0YZ && !this.llTransxY0) {
                    this.llxYZ = this.llxY0;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxYZ = this.ccxY0;
                } else {
                    this.llxYZ = getShade(tt, this.level, xMinusOne, y, zPlusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxYZ = tt.getLightColor(this.level, xMinusOne, y, zPlusOne);
                }

                if (!this.llTrans0YZ && !this.llTransXY0) {
                    this.llXYZ = this.llXY0;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXYZ = this.ccXY0;
                } else {
                    this.llXYZ = getShade(tt, this.level, xPlusOne, y, zPlusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXYZ = tt.getLightColor(this.level, xPlusOne, y, zPlusOne);
                }

                --y;
                ll4 = (this.llxYZ + this.llxY0 + this.ll0YZ + this.ll0Y0) / 4.0F;
                ll1 = (this.ll0YZ + this.ll0Y0 + this.llXYZ + this.llXY0) / 4.0F;
                ll2 = (this.ll0Y0 + this.ll0Yz + this.llXY0 + this.llXYz) / 4.0F;
                ll3 = (this.llxY0 + this.llxYz + this.ll0Y0 + this.ll0Yz) / 4.0F;
                //? >=1.0.0-beta.8.0.r {
                /*this.tc4 = this.blend(this.ccxYZ, this.ccxY0, this.cc0YZ, c0Y0);
                this.tc1 = this.blend(this.cc0YZ, this.ccXYZ, this.ccXY0, c0Y0);
                this.tc2 = this.blend(this.cc0Yz, this.ccXY0, this.ccXYz, c0Y0);
                this.tc3 = this.blend(this.ccxY0, this.ccxYz, this.cc0Yz, c0Y0);
                *///? }
            }

            this.c1r = this.c2r = this.c3r = this.c4r = tint1 ? r : 1.0F;
            this.c1g = this.c2g = this.c3g = this.c4g = tint1 ? g : 1.0F;
            this.c1b = this.c2b = this.c3b = this.c4b = tint1 ? b : 1.0F;
            this.c1r *= ll1;
            this.c1g *= ll1;
            this.c1b *= ll1;
            this.c2r *= ll2;
            this.c2g *= ll2;
            this.c2b *= ll2;
            this.c3r *= ll3;
            this.c3g *= ll3;
            this.c3b *= ll3;
            this.c4r *= ll4;
            this.c4g *= ll4;
            this.c4b *= ll4;
            this.renderFaceUp(tt, bigX, bigY, bigZ, tt.getTexture(this.level, x, y, z, Facing.UP));
            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y, zMinusOne, Facing.NORTH)) {
            if (this.blsmooth <= 0) {
                ll4 = this.ll00z;
                ll3 = this.ll00z;
                ll2 = this.ll00z;
                ll1 = this.ll00z;
                //? >=1.0.0-beta.8.0.r
                //this.tc1 = this.tc2 = this.tc3 = this.tc4 = c00z;
            } else {
                var _z = zMinusOne;
                this.llx0z = getShade(tt, this.level, xMinusOne, y, _z);
                this.ll0yz = getShade(tt, this.level, x, y - 1, _z);
                this.ll0Yz = getShade(tt, this.level, x, y + 1, _z);
                this.llX0z = getShade(tt, this.level, xPlusOne, y, _z);
                //? >=1.0.0-beta.8.0.r {
                /*this.ccx0z = tt.getLightColor(this.level, xMinusOne, y, _z);
                this.cc0yz = tt.getLightColor(this.level, x, y - 1, _z);
                this.cc0Yz = tt.getLightColor(this.level, x, y + 1, _z);
                this.ccX0z = tt.getLightColor(this.level, xPlusOne, y, _z);
                *///? }
                if (!this.llTransx0z && !this.llTrans0yz) {
                    this.llxyz = this.llx0z;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxyz = this.ccx0z;
                } else {
                    this.llxyz = getShade(tt, this.level, xMinusOne, y - 1, _z);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxyz = tt.getLightColor(this.level, xMinusOne, y - 1, _z);
                }

                if (!this.llTransx0z && !this.llTrans0Yz) {
                    this.llxYz = this.llx0z;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxYz = this.ccx0z;
                } else {
                    this.llxYz = getShade(tt, this.level, xMinusOne, y + 1, _z);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxYz = tt.getLightColor(this.level, xMinusOne, y + 1, _z);
                }

                if (!this.llTransX0z && !this.llTrans0yz) {
                    this.llXyz = this.llX0z;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXyz = this.ccX0z;
                } else {
                    this.llXyz = getShade(tt, this.level, xPlusOne, y - 1, _z);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXyz = tt.getLightColor(this.level, xPlusOne, y - 1, _z);
                }

                if (!this.llTransX0z && !this.llTrans0Yz) {
                    this.llXYz = this.llX0z;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXYz = this.ccX0z;
                } else {
                    this.llXYz = getShade(tt, this.level, xPlusOne, y + 1, _z);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXYz = tt.getLightColor(this.level, xPlusOne, y + 1, _z);
                }

                ll1 = (this.llx0z + this.llxYz + this.ll00z + this.ll0Yz) / 4.0F;
                ll2 = (this.ll00z + this.ll0Yz + this.llX0z + this.llXYz) / 4.0F;
                ll3 = (this.ll0yz + this.ll00z + this.llXyz + this.llX0z) / 4.0F;
                ll4 = (this.llxyz + this.llx0z + this.ll0yz + this.ll00z) / 4.0F;
                //? >=1.0.0-beta.8.0.r {
                /*this.tc1 = this.blend(this.ccx0z, this.ccxYz, this.cc0Yz, c00z);
                this.tc2 = this.blend(this.cc0Yz, this.ccX0z, this.ccXYz, c00z);
                this.tc3 = this.blend(this.cc0yz, this.ccXyz, this.ccX0z, c00z);
                this.tc4 = this.blend(this.ccxyz, this.ccx0z, this.cc0yz, c00z);
                *///? }
            }

            this.c1r = this.c2r = this.c3r = this.c4r = (tint2 ? r : 1.0F) * 0.8F;
            this.c1g = this.c2g = this.c3g = this.c4g = (tint2 ? g : 1.0F) * 0.8F;
            this.c1b = this.c2b = this.c3b = this.c4b = (tint2 ? b : 1.0F) * 0.8F;
            this.c1r *= ll1;
            this.c1g *= ll1;
            this.c1b *= ll1;
            this.c2r *= ll2;
            this.c2g *= ll2;
            this.c2b *= ll2;
            this.c3r *= ll3;
            this.c3g *= ll3;
            this.c3b *= ll3;
            this.c4r *= ll4;
            this.c4g *= ll4;
            this.c4b *= ll4;
            int tex = tt.getTexture(this.level, x, y, z, Facing.NORTH);
            this.renderNorth(tt, bigX, bigY, bigZ, tex);
            if (fancy && tex == 3 && this.fixedTexture < 0) {
                this.c1r *= r;
                this.c2r *= r;
                this.c3r *= r;
                this.c4r *= r;
                this.c1g *= g;
                this.c2g *= g;
                this.c3g *= g;
                this.c4g *= g;
                this.c1b *= b;
                this.c2b *= b;
                this.c3b *= b;
                this.c4b *= b;
                this.renderNorth(tt, bigX, bigY, bigZ, 38);
            }

            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y, zPlusOne, Facing.SOUTH)) {
            if (this.blsmooth <= 0) {
                ll4 = this.ll00Z;
                ll3 = this.ll00Z;
                ll2 = this.ll00Z;
                ll1 = this.ll00Z;
                //? >=1.0.0-beta.8.0.r
                //this.tc1 = this.tc2 = this.tc3 = this.tc4 = c00Z;
            } else {
                this.llx0Z = getShade(tt, this.level, xMinusOne, y, zPlusOne);
                this.llX0Z = getShade(tt, this.level, xPlusOne, y, zPlusOne);
                this.llxyZ = getShade(tt, this.level, x, y - 1, zPlusOne);
                this.ll0YZ = getShade(tt, this.level, x, y + 1, zPlusOne);
                //? >=1.0.0-beta.8.0.r {
                /*this.ccx0Z = tt.getLightColor(this.level, xMinusOne, y, zPlusOne);
                this.ccX0Z = tt.getLightColor(this.level, xPlusOne, y, zPlusOne);
                this.cc0yZ = tt.getLightColor(this.level, x, y - 1, zPlusOne);
                this.cc0YZ = tt.getLightColor(this.level, x, y + 1, zPlusOne);
                *///? }
                if (!this.llTransx0Z && !this.llTrans0yZ) {
                    this.llXyz = this.llx0Z;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxyZ = this.ccx0Z;
                } else {
                    this.llXyz = getShade(tt, this.level, xMinusOne, y - 1, zPlusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxyZ = tt.getLightColor(this.level, xMinusOne, y - 1, zPlusOne);
                }

                if (!this.llTransx0Z && !this.llTrans0YZ) {
                    this.llxYZ = this.llx0Z;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxYZ = this.ccx0Z;
                } else {
                    this.llxYZ = getShade(tt, this.level, xMinusOne, y + 1, zPlusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxYZ = tt.getLightColor(this.level, xMinusOne, y + 1, zPlusOne);
                }

                if (!this.llTransX0Z && !this.llTrans0yZ) {
                    this.llXyZ = this.llX0Z;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXyZ = this.ccX0Z;
                } else {
                    this.llXyZ = getShade(tt, this.level, xPlusOne, y - 1, zPlusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXyZ = tt.getLightColor(this.level, xPlusOne, y - 1, zPlusOne);
                }

                if (!this.llTransX0Z && !this.llTrans0YZ) {
                    this.llXYZ = this.llX0Z;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXYZ = this.ccX0Z;
                } else {
                    this.llXYZ = getShade(tt, this.level, xPlusOne, y + 1, zPlusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXYZ = tt.getLightColor(this.level, xPlusOne, y + 1, zPlusOne);
                }

                ll1 = (this.llx0Z + this.llxYZ + this.ll00Z + this.ll0YZ) / 4.0F;
                ll4 = (this.ll00Z + this.ll0YZ + this.llX0Z + this.llXYZ) / 4.0F;
                ll3 = (this.llxyZ + this.ll00Z + this.llXyZ + this.llX0Z) / 4.0F;
                ll2 = (this.llXyz + this.llx0Z + this.llxyZ + this.ll00Z) / 4.0F;
                //? >=1.0.0-beta.8.0.r {
                /*this.tc1 = this.blend(this.ccx0Z, this.ccxYZ, this.cc0YZ, c00Z);
                this.tc4 = this.blend(this.cc0YZ, this.ccX0Z, this.ccXYZ, c00Z);
                this.tc3 = this.blend(this.cc0yZ, this.ccXyZ, this.ccX0Z, c00Z);
                this.tc2 = this.blend(this.ccxyZ, this.ccx0Z, this.cc0yZ, c00Z);
                *///? }
            }

            this.c1r = this.c2r = this.c3r = this.c4r = (tint3 ? r : 1.0F) * 0.8F;
            this.c1g = this.c2g = this.c3g = this.c4g = (tint3 ? g : 1.0F) * 0.8F;
            this.c1b = this.c2b = this.c3b = this.c4b = (tint3 ? b : 1.0F) * 0.8F;
            this.c1r *= ll1;
            this.c1g *= ll1;
            this.c1b *= ll1;
            this.c2r *= ll2;
            this.c2g *= ll2;
            this.c2b *= ll2;
            this.c3r *= ll3;
            this.c3g *= ll3;
            this.c3b *= ll3;
            this.c4r *= ll4;
            this.c4g *= ll4;
            this.c4b *= ll4;
            int var50 = tt.getTexture(this.level, x, y, z, Facing.SOUTH);
            this.renderSouth(tt, bigX, bigY, bigZ, tt.getTexture(this.level, x, y, z, Facing.SOUTH));
            if (fancy && var50 == 3 && this.fixedTexture < 0) {
                this.c1r *= r;
                this.c2r *= r;
                this.c3r *= r;
                this.c4r *= r;
                this.c1g *= g;
                this.c2g *= g;
                this.c3g *= g;
                this.c4g *= g;
                this.c1b *= b;
                this.c2b *= b;
                this.c3b *= b;
                this.c4b *= b;
                this.renderSouth(tt, bigX, bigY, bigZ, 38);
            }

            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, xMinusOne, y, z, Facing.WEST)) {
            if (this.blsmooth <= 0) {
                ll4 = this.llx00;
                ll3 = this.llx00;
                ll2 = this.llx00;
                ll1 = this.llx00;
                //? >=1.0.0-beta.8.0.r
                //this.tc1 = this.tc2 = this.tc3 = this.tc4 = cx00;
            } else {
                this.llxy0 = getShade(tt, this.level, xMinusOne, y - 1, z);
                this.llx0z = getShade(tt, this.level, xMinusOne, y, zMinusOne);
                this.llx0Z = getShade(tt, this.level, xMinusOne, y, zPlusOne);
                this.llxY0 = getShade(tt, this.level, xMinusOne, y + 1, z);
                //? >=1.0.0-beta.8.0.r {
                /*this.ccxy0 = tt.getLightColor(this.level, xMinusOne, y - 1, z);
                this.ccx0z = tt.getLightColor(this.level, xMinusOne, y, zMinusOne);
                this.ccx0Z = tt.getLightColor(this.level, xMinusOne, y, zPlusOne);
                this.ccxY0 = tt.getLightColor(this.level, xMinusOne, y + 1, z);
                *///? }
                if (!this.llTransx0z && !this.llTransxy0) {
                    this.llxyz = this.llx0z;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxyz = this.ccx0z;
                } else {
                    this.llxyz = getShade(tt, this.level, xMinusOne, y - 1, zMinusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxyz = tt.getLightColor(this.level, xMinusOne, y - 1, zMinusOne);
                }

                if (!this.llTransx0Z && !this.llTransxy0) {
                    this.llxyZ = this.llx0Z;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxyZ = this.ccx0Z;
                } else {
                    this.llxyZ = getShade(tt, this.level, xMinusOne, y - 1, zPlusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxyZ = tt.getLightColor(this.level, xMinusOne, y - 1, zPlusOne);
                }

                if (!this.llTransx0z && !this.llTransxY0) {
                    this.llxYz = this.llx0z;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxYz = this.ccx0z;
                } else {
                    this.llxYz = getShade(tt, this.level, xMinusOne, y + 1, zMinusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxYz = tt.getLightColor(this.level, xMinusOne, y + 1, zMinusOne);
                }

                if (!this.llTransx0Z && !this.llTransxY0) {
                    this.llxYZ = this.llx0Z;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxYZ = this.ccx0Z;
                } else {
                    this.llxYZ = getShade(tt, this.level, xMinusOne, y + 1, zPlusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccxYZ = tt.getLightColor(this.level, xMinusOne, y + 1, zPlusOne);
                }

                ll4 = (this.llxy0 + this.llxyZ + this.llx00 + this.llx0Z) / 4.0F;
                ll1 = (this.llx00 + this.llx0Z + this.llxY0 + this.llxYZ) / 4.0F;
                ll2 = (this.llx0z + this.llx00 + this.llxYz + this.llxY0) / 4.0F;
                ll3 = (this.llxyz + this.llxy0 + this.llx0z + this.llx00) / 4.0F;
                //? >=1.0.0-beta.8.0.r {
                /*this.tc4 = this.blend(this.ccxy0, this.ccxyZ, this.ccx0Z, cx00);
                this.tc1 = this.blend(this.ccx0Z, this.ccxY0, this.ccxYZ, cx00);
                this.tc2 = this.blend(this.ccx0z, this.ccxYz, this.ccxY0, cx00);
                this.tc3 = this.blend(this.ccxyz, this.ccxy0, this.ccx0z, cx00);
                *///? }
            }

            this.c1r = this.c2r = this.c3r = this.c4r = (tint4 ? r : 1.0F) * 0.6F;
            this.c1g = this.c2g = this.c3g = this.c4g = (tint4 ? g : 1.0F) * 0.6F;
            this.c1b = this.c2b = this.c3b = this.c4b = (tint4 ? b : 1.0F) * 0.6F;
            this.c1r *= ll1;
            this.c1g *= ll1;
            this.c1b *= ll1;
            this.c2r *= ll2;
            this.c2g *= ll2;
            this.c2b *= ll2;
            this.c3r *= ll3;
            this.c3g *= ll3;
            this.c3b *= ll3;
            this.c4r *= ll4;
            this.c4g *= ll4;
            this.c4b *= ll4;
            int var51 = tt.getTexture(this.level, x, y, z, Facing.WEST);
            this.renderWest(tt, bigX, bigY, bigZ, var51);
            if (fancy && var51 == 3 && this.fixedTexture < 0) {
                this.c1r *= r;
                this.c2r *= r;
                this.c3r *= r;
                this.c4r *= r;
                this.c1g *= g;
                this.c2g *= g;
                this.c3g *= g;
                this.c4g *= g;
                this.c1b *= b;
                this.c2b *= b;
                this.c3b *= b;
                this.c4b *= b;
                this.renderWest(tt, bigX, bigY, bigZ, 38);
            }

            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, xPlusOne, y, z, Facing.EAST)) {
            if (this.blsmooth <= 0) {
                ll4 = this.llX00;
                ll3 = this.llX00;
                ll2 = this.llX00;
                ll1 = this.llX00;
                //? >=1.0.0-beta.8.0.r
                //this.tc1 = this.tc2 = this.tc3 = this.tc4 = cX00;
            } else {
                this.llXy0 = getShade(tt, this.level, xPlusOne, y - 1, z);
                this.llX0z = getShade(tt, this.level, xPlusOne, y, zMinusOne);
                this.llX0Z = getShade(tt, this.level, xPlusOne, y, zPlusOne);
                this.llXY0 = getShade(tt, this.level, xPlusOne, y + 1, z);
                //? >=1.0.0-beta.8.0.r {
                /*this.ccXy0 = tt.getLightColor(this.level, xPlusOne, y - 1, z);
                this.ccX0z = tt.getLightColor(this.level, xPlusOne, y, zMinusOne);
                this.ccX0Z = tt.getLightColor(this.level, xPlusOne, y, zPlusOne);
                this.ccXY0 = tt.getLightColor(this.level, xPlusOne, y + 1, z);
                *///? }
                if (!this.llTransXy0 && !this.llTransX0z) {
                    this.llXyz = this.llX0z;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXyz = this.ccX0z;
                } else {
                    this.llXyz = getShade(tt, this.level, xPlusOne, y - 1, zMinusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXyz = tt.getLightColor(this.level, xPlusOne, y - 1, zMinusOne);
                }

                if (!this.llTransXy0 && !this.llTransX0Z) {
                    this.llXyZ = this.llX0Z;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXyZ = this.ccX0Z;
                } else {
                    this.llXyZ = getShade(tt, this.level, xPlusOne, y - 1, zPlusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXyZ = tt.getLightColor(this.level, xPlusOne, y - 1, zPlusOne);
                }

                if (!this.llTransXY0 && !this.llTransX0z) {
                    this.llXYz = this.llX0z;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXYz = this.ccX0z;
                } else {
                    this.llXYz = getShade(tt, this.level, xPlusOne, y + 1, zMinusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXYz = tt.getLightColor(this.level, xPlusOne, y + 1, zMinusOne);
                }

                if (!this.llTransXY0 && !this.llTransX0Z) {
                    this.llXYZ = this.llX0Z;
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXYZ = this.ccX0Z;
                } else {
                    this.llXYZ = getShade(tt, this.level, xPlusOne, y + 1, zPlusOne);
                    //? >=1.0.0-beta.8.0.r
                    //this.ccXYZ = tt.getLightColor(this.level, xPlusOne, y + 1, zPlusOne);
                }

                ll1 = (this.llXy0 + this.llXyZ + this.llX00 + this.llX0Z) / 4.0F;
                ll4 = (this.llX00 + this.llX0Z + this.llXY0 + this.llXYZ) / 4.0F;
                ll3 = (this.llX0z + this.llX00 + this.llXYz + this.llXY0) / 4.0F;
                ll2 = (this.llXyz + this.llXy0 + this.llX0z + this.llX00) / 4.0F;
                //? >=1.0.0-beta.8.0.r {
                /*this.tc1 = this.blend(this.ccXy0, this.ccXyZ, this.ccX0Z, cX00);
                this.tc4 = this.blend(this.ccX0Z, this.ccXY0, this.ccXYZ, cX00);
                this.tc3 = this.blend(this.ccX0z, this.ccXYz, this.ccXY0, cX00);
                this.tc2 = this.blend(this.ccXyz, this.ccXy0, this.ccX0z, cX00);
                *///? }
            }

            this.c1r = this.c2r = this.c3r = this.c4r = (tint5 ? r : 1.0F) * 0.6F;
            this.c1g = this.c2g = this.c3g = this.c4g = (tint5 ? g : 1.0F) * 0.6F;
            this.c1b = this.c2b = this.c3b = this.c4b = (tint5 ? b : 1.0F) * 0.6F;
            this.c1r *= ll1;
            this.c1g *= ll1;
            this.c1b *= ll1;
            this.c2r *= ll2;
            this.c2g *= ll2;
            this.c2b *= ll2;
            this.c3r *= ll3;
            this.c3g *= ll3;
            this.c3b *= ll3;
            this.c4r *= ll4;
            this.c4g *= ll4;
            this.c4b *= ll4;
            int var52 = tt.getTexture(this.level, x, y, z, Facing.EAST);
            this.renderEast(tt, bigX, bigY, bigZ, var52);
            if (fancy && var52 == 3 && this.fixedTexture < 0) {
                this.c1r *= r;
                this.c2r *= r;
                this.c3r *= r;
                this.c4r *= r;
                this.c1g *= g;
                this.c2g *= g;
                this.c3g *= g;
                this.c4g *= g;
                this.c1b *= b;
                this.c2b *= b;
                this.c3b *= b;
                this.c4b *= b;
                this.renderEast(tt, bigX, bigY, bigZ, 38);
            }

            changed = true;
        }

        this.blen = false;
        return changed;
    }

    @Override
    public boolean tesselateBlockInWorld(Tile tile, BigInteger x, int y, BigInteger z, float r, float g, float b) {
        final double bigX = FIX_STRIPELANDS ? BigMath.fastAnd(x, 15) : x.doubleValue();
        final double bigY = FIX_STRIPELANDS ? y & 15 : y;
        final double bigZ = FIX_STRIPELANDS ? BigMath.fastAnd(z, 15) : z.doubleValue();
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
        if (tile != Tile.grass) {
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
            this.renderFaceDown(tile, bigX, bigY, bigZ, tile.getTexture(this.level, x, y, z, Facing.DOWN));
            changed = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x, y + 1, z, Facing.UP)) {
            float br = tile.getBrightness(this.level, x, y + 1, z);
            if (tile.yy1 != 1.0 && !tile.material.isLiquid()) {
                br = centerBrightness;
            }

            t.color(r11 * br, g11 * br, b11 * br);
            this.renderFaceUp(tile, bigX, bigY, bigZ, tile.getTexture(this.level, x, y, z, Facing.UP));
            changed = true;
        }

        if (this.noCulling || tile.shouldRenderFace(this.level, x, y, z.subtract(BigInteger.ONE), Facing.NORTH)) {
            float br = tile.getBrightness(this.level, x, y, z.subtract(BigInteger.ONE));
            if (tile.zz0 > 0.0) {
                br = centerBrightness;
            }

            t.color(r2 * br, g2 * br, b2 * br);
            int texture = tile.getTexture(this.level, x, y, z, Facing.NORTH);
            this.renderNorth(tile, bigX, bigY, bigZ, texture);
            if (fancy && texture == 3 && this.fixedTexture < 0) {
                t.color(r2 * br * r, g2 * br * g, b2 * br * b);
                this.renderNorth(tile, bigX, bigY, bigZ, 38);
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
            this.renderSouth(tile, bigX, bigY, bigZ, texture);
            if (fancy && texture == 3 && this.fixedTexture < 0) {
                t.color(r2 * br * r, g2 * br * g, b2 * br * b);
                this.renderSouth(tile, bigX, bigY, bigZ, 38);
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
            this.renderWest(tile, bigX, bigY, bigZ, texture);
            if (fancy && texture == 3 && this.fixedTexture < 0) {
                t.color(r3 * br * r, g3 * br * g, b3 * br * b);
                this.renderWest(tile, bigX, bigY, bigZ, 38);
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
                this.renderEast(tile, bigX, bigY, bigZ, texture);
            if (fancy && texture == 3 && this.fixedTexture < 0) {
                t.color(r3 * var33 * r, g3 * var33 * g, b3 * var33 * b);
                this.renderEast(tile, bigX, bigY, bigZ, 38);
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
        final double bigX = FIX_STRIPELANDS ? BigMath.fastAnd(x, 15) : x.doubleValue();
        final double bigY = FIX_STRIPELANDS ? y & 15 : y;
        final double bigZ = FIX_STRIPELANDS ? BigMath.fastAnd(z, 15) : z.doubleValue();
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
            this.renderFaceDown(tt, bigX, bigY, bigZ, tt.getTexture(this.level, x, y, z, Facing.DOWN));

            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y + 1, z, Facing.UP)) {
            float br = tt.getBrightness(this.level, x, y + 1, z);
            if (tt.yy1 != 1.0 && !tt.material.isLiquid()) {
                br = centerBrightness;
            }

            t.color(r11 * br, g11 * br, b11 * br);
            this.renderFaceUp(tt, bigX, bigY, bigZ, tt.getTexture(this.level, x, y, z, Facing.UP));

            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y, z.subtract(BigInteger.ONE), Facing.NORTH)) {
            float br = tt.getBrightness(this.level, x, y, z.subtract(BigInteger.ONE));
            if (tt.zz0 > 0.0) {
                br = centerBrightness;
            }

            t.color(r2 * br, g2 * br, b2 * br);

            t.addOffset(0.0F, 0.0F, epsilon);
            this.renderNorth(tt, bigX, bigY, bigZ, tt.getTexture(this.level, x, y, z, Facing.NORTH));
            t.addOffset(0.0F, 0.0F, -epsilon);

            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x, y, z.add(BigInteger.ONE), Facing.SOUTH)) {
            float br = tt.getBrightness(this.level, x, y, z.add(BigInteger.ONE));
            if (tt.zz1 < 1.0) {
                br = centerBrightness;
            }

            t.color(r2 * br, g2 * br, b2 * br);

            t.addOffset(0.0F, 0.0F, -epsilon);
            this.renderSouth(tt, bigX, bigY, bigZ, tt.getTexture(this.level, x, y, z, Facing.SOUTH));
            t.addOffset(0.0F, 0.0F, epsilon);

            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x.subtract(BigInteger.ONE), y, z, Facing.WEST)) {
            float br = tt.getBrightness(this.level, x.subtract(BigInteger.ONE), y, z);
            if (tt.xx0 > 0.0) {
                br = centerBrightness;
            }

            t.color(r3 * br, g3 * br, b3 * br);

            t.addOffset(epsilon, 0.0F, 0.0F);
            this.renderWest(tt, bigX, bigY, bigZ, tt.getTexture(this.level, x, y, z, Facing.WEST));
            t.addOffset(-epsilon, 0.0F, 0.0F);

            changed = true;
        }

        if (this.noCulling || tt.shouldRenderFace(this.level, x.add(BigInteger.ONE), y, z, Facing.EAST)) {
            float br = tt.getBrightness(this.level, x.add(BigInteger.ONE), y, z);
            if (tt.xx1 < 1.0) {
                br = centerBrightness;
            }

            t.color(r3 * br, g3 * br, b3 * br);

            t.addOffset(-epsilon, 0.0F, 0.0F);
            this.renderEast(tt, bigX, bigY, bigZ, tt.getTexture(this.level, x, y, z, Facing.EAST));
            t.addOffset(epsilon, 0.0F, 0.0F);

            changed = true;
        }

        return changed;
    }

    public boolean tesselateFenceInWorld(Tile tile, BigInteger x, int y, BigInteger z) {
        boolean changed = false;
        float a = 0.375F;
        float b = 0.625F;
        tile.setShape(a, 0.0F, a, b, 1.0F, b);
        this.tesselateBlockInWorld(tile, x, y, z);
        changed = true;
        boolean vertical = false;
        boolean horizontal = false;
        if (this.level.getTile(x.subtract(BigInteger.ONE), y, z) == tile.id || this.level.getTile(x.add(BigInteger.ONE), y, z) == tile.id) {
            vertical = true;
        }

        if (this.level.getTile(x, y, z.subtract(BigInteger.ONE)) == tile.id || this.level.getTile(x, y, z.add(BigInteger.ONE)) == tile.id) {
            horizontal = true;
        }

        boolean l = this.level.getTile(x.subtract(BigInteger.ONE), y, z) == tile.id;
        boolean r = this.level.getTile(x.add(BigInteger.ONE), y, z) == tile.id;
        boolean u = this.level.getTile(x, y, z.subtract(BigInteger.ONE)) == tile.id;
        boolean d = this.level.getTile(x, y, z.add(BigInteger.ONE)) == tile.id;
        if (!vertical && !horizontal) {
            vertical = true;
        }

        a = 0.4375F;
        b = 0.5625F;
        float h0 = 0.75F;
        float h1 = 0.9375F;
        float x0 = l ? 0.0F : a;
        float x1 = r ? 1.0F : b;
        float z0 = u ? 0.0F : a;
        float z1 = d ? 1.0F : b;
        if (vertical) {
            tile.setShape(x0, h0, a, x1, h1, b);
            this.tesselateBlockInWorld(tile, x, y, z);
            changed = true;
        }

        if (horizontal) {
            tile.setShape(a, h0, z0, b, h1, z1);
            this.tesselateBlockInWorld(tile, x, y, z);
            changed = true;
        }

        h0 = 0.375F;
        h1 = 0.5625F;
        if (vertical) {
            tile.setShape(x0, h0, a, x1, h1, b);
            this.tesselateBlockInWorld(tile, x, y, z);
            changed = true;
        }

        if (horizontal) {
            tile.setShape(a, h0, z0, b, h1, z1);
            this.tesselateBlockInWorld(tile, x, y, z);
            changed = true;
        }

        tile.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        return changed;
    }

    public boolean tesselateStairsInWorld(Tile tile, BigInteger x, int y, BigInteger z) {
        boolean changed = false;
        int dir = this.level.getData(x, y, z);
        if (dir == 0) {
            tile.setShape(0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 1.0F);
            this.tesselateBlockInWorld(tile, x, y, z);
            tile.setShape(0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            this.tesselateBlockInWorld(tile, x, y, z);
            changed = true;
        } else if (dir == 1) {
            tile.setShape(0.0F, 0.0F, 0.0F, 0.5F, 1.0F, 1.0F);
            this.tesselateBlockInWorld(tile, x, y, z);
            tile.setShape(0.5F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
            this.tesselateBlockInWorld(tile, x, y, z);
            changed = true;
        } else if (dir == 2) {
            tile.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 0.5F);
            this.tesselateBlockInWorld(tile, x, y, z);
            tile.setShape(0.0F, 0.0F, 0.5F, 1.0F, 1.0F, 1.0F);
            this.tesselateBlockInWorld(tile, x, y, z);
            changed = true;
        } else if (dir == 3) {
            tile.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.5F);
            this.tesselateBlockInWorld(tile, x, y, z);
            tile.setShape(0.0F, 0.0F, 0.5F, 1.0F, 0.5F, 1.0F);
            this.tesselateBlockInWorld(tile, x, y, z);
            changed = true;
        }

        tile.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        return changed;
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
        final double bigX = FIX_STRIPELANDS ? BigMath.fastAnd(x, 15) : x.doubleValue();
        final double bigZ = FIX_STRIPELANDS ? BigMath.fastAnd(z, 15) : z.doubleValue();
        final double bigY = FIX_STRIPELANDS ? y & 15 : y;
        if (dt.yy0 > 0.0) {
            br = centerBrightness;
        }

        if (Tile.lightEmission[tt.id] > 0) {
            br = 1.0F;
        }

        t.color(c10 * br, c10 * br, c10 * br);
        this.renderFaceDown(tt, bigX, bigY, bigZ, tt.getTexture(this.level, x, y, z, Facing.DOWN));

        changed = true;
        br = tt.getBrightness(this.level, x, y + 1, z);
        if (dt.yy1 < 1.0) {
            br = centerBrightness;
        }

        if (Tile.lightEmission[tt.id] > 0) {
            br = 1.0F;
        }

        t.color(c11 * br, c11 * br, c11 * br);
        this.renderFaceUp(tt, bigX, bigY, bigZ, tt.getTexture(this.level, x, y, z, Facing.UP));

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

        this.renderNorth(tt, bigX, bigY, bigZ, tex);
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

        this.renderSouth(tt, bigX, bigY, bigZ, tex);
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

        this.renderWest(tt, bigX, bigY, bigZ, tex);
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

        this.renderEast(tt, bigX, bigY, bigZ, tex);
        changed = true;
        this.xFlipTexture = false;
        return changed;
    }

    public boolean tesselateLadderInWorld(Tile tt, BigInteger x, int y, BigInteger z) {
        Tesselator t = Tesselator.instance;
        int tex = tt.getTexture(Facing.DOWN);
        if (this.fixedTexture >= 0) {
            tex = this.fixedTexture;
        }

        float br = tt.getBrightness(this.level, x, y, z);
        t.color(br, br, br);
        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u0 = xt / 256.0F;
        double v0 = (xt + 15.99F) / 256.0F;
        double u1 = yt / 256.0F;
        double v1 = (yt + 15.99F) / 256.0F;

        int face = this.level.getData(x, y, z);

        float o = 0 / 16.0F;
        float r = 0.05F;

        // Vanilla uses ints directly here so just use doubles here so ladders still render past the int limit when stripelands are enabled
        double xx = FIX_STRIPELANDS ? BigMath.fastAnd(x, 15) : x.doubleValue();
        double zz = FIX_STRIPELANDS ? BigMath.fastAnd(z, 15) : z.doubleValue();
        int yy = FIX_STRIPELANDS ? y & 15 : y;

        if (face == 5) {
            t.vertexUV(xx + r, yy + 1 + o, zz + 1 + o, u0, u1);
            t.vertexUV(xx + r, yy + 0 - o, zz + 1 + o, u0, v1);
            t.vertexUV(xx + r, yy + 0 - o, zz + 0 - o, v0, v1);
            t.vertexUV(xx + r, yy + 1 + o, zz + 0 - o, v0, u1);
        }

        if (face == 4) {
            t.vertexUV(xx + 1 - r, yy + 0 - o, zz + 1 + o, v0, v1);
            t.vertexUV(xx + 1 - r, yy + 1 + o, zz + 1 + o, v0, u1);
            t.vertexUV(xx + 1 - r, yy + 1 + o, zz + 0 - o, u0, u1);
            t.vertexUV(xx + 1 - r, yy + 0 - o, zz + 0 - o, u0, v1);
        }

        if (face == 3) {
            t.vertexUV(xx + 1 + o, yy + 0 - o, zz + r, v0, v1);
            t.vertexUV(xx + 1 + o, yy + 1 + o, zz + r, v0, u1);
            t.vertexUV(xx + 0 - o, yy + 1 + o, zz + r, u0, u1);
            t.vertexUV(xx + 0 - o, yy + 0 - o, zz + r, u0, v1);
        }

        if (face == 2) {
            t.vertexUV(xx + 1 + o, yy + 1 + o, zz + 1 - r, u0, u1);
            t.vertexUV(xx + 1 + o, yy + 0 - o, zz + 1 - r, u0, v1);
            t.vertexUV(xx + 0 - o, yy + 0 - o, zz + 1 - r, v0, v1);
            t.vertexUV(xx + 0 - o, yy + 1 + o, zz + 1 - r, v0, u1);
        }

        return true;
    }

    public boolean tesselateCrossInWorld(Tile tt, BigInteger x, int y, BigInteger z) {
        Tesselator t = Tesselator.instance;
        //? 1.0.0-beta.8.0.r {
        /*t.tex2(tt.getLightColor(this.level, x, y, z));
        float br = 1;
        *///? } else
        float br = tt.getBrightness(this.level, x, y, z);
        int col = tt.getFoliageColor(this.level, x, y, z);
        float r = (col >> 16 & 0xFF) / 255.0F;
        float g = (col >> 8 & 0xFF) / 255.0F;
        float b = (col & 0xFF) / 255.0F;
        if (GameRenderer.anaglyph3d) {
            float cr = (r * 30.0F + g * 59.0F + b * 11.0F) / 100.0F;
            float cg = (r * 30.0F + g * 70.0F) / 100.0F;
            float cb = (r * 30.0F + b * 70.0F) / 100.0F;
            r = cr;
            g = cg;
            b = cb;
        }

        t.color(br * r, br * g, br * b);
        double xt = FIX_STRIPELANDS ? BigMath.fastAnd(x, 15) : x.doubleValue();
        double yt = FIX_STRIPELANDS ? y & 15 : y;
        double zt = FIX_STRIPELANDS ? BigMath.fastAnd(z, 15) : z.doubleValue();
        if (tt == Tile.tallgrass) {
            long var17 = x.longValue() * 3129871 ^ z.longValue() * 116129781L ^ y;
            var17 = var17 * var17 * 42317861L + var17 * 11L;
            xt += ((float)(var17 >> 16 & 15L) / 15.0F - 0.5) * 0.5;
            yt += ((float)(var17 >> 20 & 15L) / 15.0F - 1.0) * 0.2;
            zt += ((float)(var17 >> 24 & 15L) / 15.0F - 0.5) * 0.5;
        }

        this.tesselateCrossTexture(tt, this.level.getData(x, y, z), xt, yt, zt);
        return true;
    }

    public boolean tesselateRowInWorld(Tile tile, BigInteger x, int y, BigInteger z) {
        Tesselator t = Tesselator.instance;
        float br = tile.getBrightness(this.level, x, y, z);
        t.color(br, br, br);
        if (FIX_STRIPELANDS) {
            this.tesselateRowTexture(tile, this.level.getData(x, y, z), BigMath.fastAnd(x, 15), (y & 15) - 0.0625F, BigMath.fastAnd(z, 15));
        } else {
            this.tesselateRowTexture(tile, this.level.getData(x, y, z), x.doubleValue(), y - 0.0625F, z.doubleValue());
        }
        return true;
    }

    private boolean tesselateRepeaterInWorld(Tile tt, BigInteger x, int y, BigInteger z) {
        int data = this.level.getData(x, y, z);
        int dir = data & 3;
        int delay = (data & 12) >> 2;
        this.tesselateBlockInWorld(tt, x, y, z);
        Tesselator t = Tesselator.instance;
        float br = tt.getBrightness(this.level, x, y, z);
        if (Tile.lightEmission[tt.id] > 0) {
            br = (br + 1.0F) * 0.5F;
        }

        t.color(br, br, br);
        double h = -3.0f / 16.0f;
        double transmitterX = 0.0;
        double transmitterZ = 0.0;
        double receiverX = 0.0;
        double receiverZ = 0.0;
        switch (dir) {
            case Directions.SOUTH:
                receiverZ = -5.0f / 16.0f;
                transmitterZ = RepeaterTile.PARTICLE_OFFSETS[delay];
                break;
            case Directions.WEST:
                receiverX = 5.0f / 16.0f;
                transmitterX = -RepeaterTile.PARTICLE_OFFSETS[delay];
                break;
            case Directions.NORTH:
                receiverZ = 5.0f / 16.0f;
                transmitterZ = -RepeaterTile.PARTICLE_OFFSETS[delay];
                break;
            case Directions.EAST:
                receiverX = -5.0f / 16.0f;
                transmitterX = RepeaterTile.PARTICLE_OFFSETS[delay];
        }

        float xx = FIX_STRIPELANDS ? BigMath.fastAnd(x, 15) : x.floatValue();
        float zz = FIX_STRIPELANDS ? BigMath.fastAnd(z, 15) : x.floatValue();
        float yy = FIX_STRIPELANDS ? y & 15 : y;

        tesselateTorch(tt, xx + transmitterX, yy + h, zz + transmitterZ, 0.0, 0.0);
        tesselateTorch(tt, xx + receiverX, yy + h, zz + receiverZ, 0.0, 0.0);
        int tex = tt.getTexture(Facing.UP);
        int xt = (tex & 15) << 4;
        int yt = tex & 240;
        double u0 = xt / 256.0F;
        double u1 = (xt + 15.99F) / 256.0F;
        double v0 = yt / 256.0F;
        double v1 = (yt + 15.99F) / 256.0F;

        float r = 2.0f / 16.0f;

        float x0 = xx + 1;
        float x1 = xx + 1;
        float x2 = xx + 0;
        float x3 = xx + 0;

        float z0 = zz + 0;
        float z1 = zz + 1;
        float z2 = zz + 1;
        float z3 = zz + 0;

        float y0 = y + r;

        if (dir == Directions.NORTH) {
            x0 = x1 = xx + 0;
            x2 = x3 = xx + 1;
            z0 = z3 = zz + 1;
            z1 = z2 = zz + 0;
        } else if (dir == Directions.EAST) {
            x0 = x3 = xx + 0;
            x1 = x2 = xx + 1;
            z0 = z1 = zz + 0;
            z2 = z3 = zz + 1;
        } else if (dir == Directions.WEST) {
            x0 = x3 = xx + 1;
            x1 = x2 = xx + 0;
            z0 = z1 = zz + 1;
            z2 = z3 = zz + 0;
        }

        t.vertexUV(x3, y0, z3, u0, v0);
        t.vertexUV(x2, y0, z2, u0, v1);
        t.vertexUV(x1, y0, z1, u1, v1);
        t.vertexUV(x0, y0, z0, u1, v0);
        return true;
    }

    private boolean tesselatePistonInWorld(Tile tile, BigInteger x, int y, BigInteger z, boolean head) {
        int data = this.level.getData(x, y, z);
        boolean extended = head || PistonBaseTile.isExtended(data);
        int facing = PistonBaseTile.getFacing(data);
        if (extended) {
            switch (facing) {
                case 0:
                    this.northFlip = 3;
                    this.southFlip = 3;
                    this.eastFlip = 3;
                    this.westFlip = 3;
                    tile.setShape(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;
                case 1:
                    tile.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
                    break;
                case 2:
                    this.eastFlip = 1;
                    this.westFlip = 2;
                    tile.setShape(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
                    break;
                case 3:
                    this.eastFlip = 2;
                    this.westFlip = 1;
                    this.upFlip = 3;
                    this.downFlip = 3;
                    tile.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
                    break;
                case 4:
                    this.northFlip = 1;
                    this.southFlip = 2;
                    this.upFlip = 2;
                    this.downFlip = 1;
                    tile.setShape(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;
                case 5:
                    this.northFlip = 2;
                    this.southFlip = 1;
                    this.upFlip = 1;
                    this.downFlip = 2;
                    tile.setShape(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
            }

            this.tesselateBlockInWorld(tile, x, y, z);
            this.northFlip = 0;
            this.southFlip = 0;
            this.eastFlip = 0;
            this.westFlip = 0;
            this.upFlip = 0;
            this.downFlip = 0;
            tile.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            switch (facing) {
                case 0:
                    this.northFlip = 3;
                    this.southFlip = 3;
                    this.eastFlip = 3;
                    this.westFlip = 3;
                case 1:
                default:
                    break;
                case 2:
                    this.eastFlip = 1;
                    this.westFlip = 2;
                    break;
                case 3:
                    this.eastFlip = 2;
                    this.westFlip = 1;
                    this.upFlip = 3;
                    this.downFlip = 3;
                    break;
                case 4:
                    this.northFlip = 1;
                    this.southFlip = 2;
                    this.upFlip = 2;
                    this.downFlip = 1;
                    break;
                case 5:
                    this.northFlip = 2;
                    this.southFlip = 1;
                    this.upFlip = 1;
                    this.downFlip = 2;
            }

            this.tesselateBlockInWorld(tile, x, y, z);
            this.northFlip = 0;
            this.southFlip = 0;
            this.eastFlip = 0;
            this.westFlip = 0;
            this.upFlip = 0;
            this.downFlip = 0;
        }

        return true;
    }

    public void tesselatePistonArmNoCulling(Tile tile, BigInteger x, int y, BigInteger z, boolean fullArm) {
        this.noCulling = true;
        this.tesselatePistonExtensionInWorld(tile, x, y, z, fullArm);
        this.noCulling = false;
    }

    private boolean tesselatePistonExtensionInWorld(Tile tt, BigInteger x, int y, BigInteger z, boolean fullArm) {
        float xx = FIX_STRIPELANDS ? BigMath.fastAnd(x, 15) : x.floatValue();
        float zz = FIX_STRIPELANDS ? BigMath.fastAnd(z, 15) : z.floatValue();
        float yy = FIX_STRIPELANDS ? y & 15 : y;

        int data = this.level.getData(x, y, z);
        int facing = PistonExtensionTile.getDirection(data);

        float br = tt.getBrightness(this.level, x, y, z);
        float armLength = fullArm ? 1.0F : 0.5F;
        double armLengthPixels = fullArm ? 16.0 : 8.0;

        switch (facing) {
            case Facing.DOWN:
                this.northFlip = FLIP_180;
                this.southFlip = FLIP_180;
                this.eastFlip = FLIP_180;
                this.westFlip = FLIP_180;
                tt.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
                this.tesselateBlockInWorld(tt, x, y, z);
                this.renderPistonArmUpDown(xx + 0.375F, xx + 0.625F, yy + 0.25F, yy + 0.25F + armLength, zz + 0.625F, zz + 0.625F, br * 0.8F, armLengthPixels);
                this.renderPistonArmUpDown(xx + 0.625F, xx + 0.375F, yy + 0.25F, yy + 0.25F + armLength, zz + 0.375F, zz + 0.375F, br * 0.8F, armLengthPixels);
                this.renderPistonArmUpDown(xx + 0.375F, xx + 0.375F, yy + 0.25F, yy + 0.25F + armLength, zz + 0.375F, zz + 0.625F, br * 0.6F, armLengthPixels);
                this.renderPistonArmUpDown(xx + 0.625F, xx + 0.625F, yy + 0.25F, yy + 0.25F + armLength, zz + 0.625F, zz + 0.375F, br * 0.6F, armLengthPixels);
                break;
            case Facing.UP:
                tt.setShape(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                this.tesselateBlockInWorld(tt, x, y, z);
                this.renderPistonArmUpDown(xx + 0.375F, xx + 0.625F, yy - 0.25F + 1.0F - armLength, yy - 0.25F + 1.0F, zz + 0.625F, zz + 0.625F, br * 0.8F, armLengthPixels);
                this.renderPistonArmUpDown(xx + 0.625F, xx + 0.375F, yy - 0.25F + 1.0F - armLength, yy - 0.25F + 1.0F, zz + 0.375F, zz + 0.375F, br * 0.8F, armLengthPixels);
                this.renderPistonArmUpDown(xx + 0.375F, xx + 0.375F, yy - 0.25F + 1.0F - armLength, yy - 0.25F + 1.0F, zz + 0.375F, zz + 0.625F, br * 0.6F, armLengthPixels);
                this.renderPistonArmUpDown(xx + 0.625F, xx + 0.625F, yy - 0.25F + 1.0F - armLength, yy - 0.25F + 1.0F, zz + 0.625F, zz + 0.375F, br * 0.6F, armLengthPixels);
                break;
            case Facing.NORTH:
                this.eastFlip = FLIP_CW;
                this.westFlip = FLIP_CCW;
                tt.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
                this.tesselateBlockInWorld(tt, x, y, z);
                this.renderPistonArmNorthSouth(xx + 0.375F, xx + 0.375F, yy + 0.625F, yy + 0.375F, zz + 0.25F, zz + 0.25F + armLength, br * 0.6F, armLengthPixels);
                this.renderPistonArmNorthSouth(xx + 0.625F, xx + 0.625F, yy + 0.375F, yy + 0.625F, zz + 0.25F, zz + 0.25F + armLength, br * 0.6F, armLengthPixels);
                this.renderPistonArmNorthSouth(xx + 0.375F, xx + 0.625F, yy + 0.375F, yy + 0.375F, zz + 0.25F, zz + 0.25F + armLength, br * 0.5F, armLengthPixels);
                this.renderPistonArmNorthSouth(xx + 0.625F, xx + 0.375F, yy + 0.625F, yy + 0.625F, zz + 0.25F, zz + 0.25F + armLength, br, armLengthPixels);
                break;
            case Facing.SOUTH:
                this.eastFlip = FLIP_CCW;
                this.westFlip = FLIP_CW;
                this.upFlip = FLIP_180;
                this.downFlip = FLIP_180;
                tt.setShape(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
                this.tesselateBlockInWorld(tt, x, y, z);
                this.renderPistonArmNorthSouth(xx + 0.375F, xx + 0.375F, yy + 0.625F, yy + 0.375F, zz - 0.25F + 1.0F - armLength, zz - 0.25F + 1.0F, br * 0.6F, armLengthPixels);
                this.renderPistonArmNorthSouth(xx + 0.625F, xx + 0.625F, yy + 0.375F, yy + 0.625F, zz - 0.25F + 1.0F - armLength, zz - 0.25F + 1.0F, br * 0.6F, armLengthPixels);
                this.renderPistonArmNorthSouth(xx + 0.375F, xx + 0.625F, yy + 0.375F, yy + 0.375F, zz - 0.25F + 1.0F - armLength, zz - 0.25F + 1.0F, br * 0.5F, armLengthPixels);
                this.renderPistonArmNorthSouth(xx + 0.625F, xx + 0.375F, yy + 0.625F, yy + 0.625F, zz - 0.25F + 1.0F - armLength, zz - 0.25F + 1.0F, br, armLengthPixels);
                break;
            case Facing.WEST:
                this.northFlip = FLIP_CW;
                this.southFlip = FLIP_CCW;
                this.upFlip = FLIP_CCW;
                this.downFlip = FLIP_CW;
                tt.setShape(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
                this.tesselateBlockInWorld(tt, x, y, z);
                this.renderPistonArmEastWest(xx + 0.25F, xx + 0.25F + armLength, yy + 0.375F, yy + 0.375F, zz + 0.625F, zz + 0.375F, br * 0.5F, armLengthPixels);
                this.renderPistonArmEastWest(xx + 0.25F, xx + 0.25F + armLength, yy + 0.625F, yy + 0.625F, zz + 0.375F, zz + 0.625F, br, armLengthPixels);
                this.renderPistonArmEastWest(xx + 0.25F, xx + 0.25F + armLength, yy + 0.375F, yy + 0.625F, zz + 0.375F, zz + 0.375F, br * 0.6F, armLengthPixels);
                this.renderPistonArmEastWest(xx + 0.25F, xx + 0.25F + armLength, yy + 0.625F, yy + 0.375F, zz + 0.625F, zz + 0.625F, br * 0.6F, armLengthPixels);
                break;
            case Facing.EAST:
                this.northFlip = FLIP_CCW;
                this.southFlip = FLIP_CW;
                this.upFlip = FLIP_CW;
                this.downFlip = FLIP_CCW;
                tt.setShape(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                this.tesselateBlockInWorld(tt, x, y, z);
                this.renderPistonArmEastWest(xx - 0.25F + 1.0F - armLength, xx - 0.25F + 1.0F, yy + 0.375F, yy + 0.375F, zz + 0.625F, zz + 0.375F, br * 0.5F, armLengthPixels);
                this.renderPistonArmEastWest(xx - 0.25F + 1.0F - armLength, xx - 0.25F + 1.0F, yy + 0.625F, yy + 0.625F, zz + 0.375F, zz + 0.625F, br, armLengthPixels);
                this.renderPistonArmEastWest(xx - 0.25F + 1.0F - armLength, xx - 0.25F + 1.0F, yy + 0.375F, yy + 0.625F, zz + 0.375F, zz + 0.375F, br * 0.6F, armLengthPixels);
                this.renderPistonArmEastWest(xx - 0.25F + 1.0F - armLength, xx - 0.25F + 1.0F, yy + 0.625F, yy + 0.375F, zz + 0.625F, zz + 0.625F, br * 0.6F, armLengthPixels);
        }

        this.northFlip = FLIP_NONE;
        this.southFlip = FLIP_NONE;
        this.eastFlip = FLIP_NONE;
        this.westFlip = FLIP_NONE;
        this.upFlip = FLIP_NONE;
        this.downFlip = FLIP_NONE;
        tt.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        return true;
    }

    // TODO: use big decimal
    public boolean tesselateLeverInWorld(Tile tile, BigInteger x, int y, BigInteger z) {
        int data = this.level.getData(x, y, z);
        int dir = data & 7;
        boolean flipped = (data & 8) > 0;
        Tesselator t = Tesselator.instance;
        boolean hadFixed = this.fixedTexture >= 0;
        if (!hadFixed) {
            this.fixedTexture = Tile.cobblestone.tex;
        }

        float w1 = 0.25F;
        float w2 = 0.1875F;
        float h = 0.1875F;
        if (dir == 5) {
            tile.setShape(0.5F - w2, 0.0F, 0.5F - w1, 0.5F + w2, h, 0.5F + w1);
        } else if (dir == 6) {
            tile.setShape(0.5F - w1, 0.0F, 0.5F - w2, 0.5F + w1, h, 0.5F + w2);
        } else if (dir == 4) {
            tile.setShape(0.5F - w2, 0.5F - w1, 1.0F - h, 0.5F + w2, 0.5F + w1, 1.0F);
        } else if (dir == 3) {
            tile.setShape(0.5F - w2, 0.5F - w1, 0.0F, 0.5F + w2, 0.5F + w1, h);
        } else if (dir == 2) {
            tile.setShape(1.0F - h, 0.5F - w1, 0.5F - w2, 1.0F, 0.5F + w1, 0.5F + w2);
        } else if (dir == 1) {
            tile.setShape(0.0F, 0.5F - w1, 0.5F - w2, h, 0.5F + w1, 0.5F + w2);
        }

        this.tesselateBlockInWorld(tile, x, y, z);
        if (!hadFixed) {
            this.fixedTexture = -1;
        }

        float br = tile.getBrightness(this.level, x, y, z);
        if (Tile.lightEmission[tile.id] > 0) {
            br = 1.0F;
        }

        t.color(br, br, br);
        int var14 = tile.getTexture(0);
        if (this.fixedTexture >= 0) {
            var14 = this.fixedTexture;
        }

        int xt = (var14 & 15) << 4;
        int yt = var14 & 240;
        float u0 = xt / 256.0F;
        float u1 = (xt + 15.99F) / 256.0F;
        float v0 = yt / 256.0F;
        float v1 = (yt + 15.99F) / 256.0F;
        Vec3[] corners = new Vec3[8];
        float xv = 0.0625F;
        float zv = 0.0625F;
        float yv = 0.625F;
        corners[0] = Vec3.newTemp(-xv, 0.0, -zv);
        corners[1] = Vec3.newTemp(xv, 0.0, -zv);
        corners[2] = Vec3.newTemp(xv, 0.0, zv);
        corners[3] = Vec3.newTemp(-xv, 0.0, zv);
        corners[4] = Vec3.newTemp(-xv, yv, -zv);
        corners[5] = Vec3.newTemp(xv, yv, -zv);
        corners[6] = Vec3.newTemp(xv, yv, zv);
        corners[7] = Vec3.newTemp(-xv, yv, zv);

        for (int i = 0; i < 8; i++) {
            if (flipped) {
                corners[i].z -= 0.0625;
                corners[i].xRot((float) Math.PI * 2.0F / 9.0F);
            } else {
                corners[i].z += 0.0625;
                corners[i].xRot((float) -Math.PI * 2.0F / 9.0F);
            }

            if (dir == 6) {
                corners[i].yRot((float) (Math.PI / 2));
            }

            if (dir < 5) {
                corners[i].y -= 0.375;
                corners[i].xRot((float) (Math.PI / 2));
                if (dir == 4) {
                    corners[i].yRot(0.0F);
                }

                if (dir == 3) {
                    corners[i].yRot((float) Math.PI);
                }

                if (dir == 2) {
                    corners[i].yRot((float) (Math.PI / 2));
                }

                if (dir == 1) {
                    corners[i].yRot((float) (-Math.PI / 2));
                }

                corners[i].x += x.doubleValue() + 0.5;
                corners[i].y += y + 0.5F;
                corners[i].z += z.doubleValue() + 0.5;
            } else {
                corners[i].x += x.doubleValue() + 0.5;
                corners[i].y += y + 0.125F;
                corners[i].z += z.doubleValue() + 0.5;
            }
        }

        Vec3 c0 = null;
        Vec3 c1 = null;
        Vec3 c2 = null;
        Vec3 c3 = null;

        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                u0 = (xt + 7) / 256.0F;
                u1 = (xt + 9 - 0.01F) / 256.0F;
                v0 = (yt + 6) / 256.0F;
                v1 = (yt + 8 - 0.01F) / 256.0F;
            } else if (i == 2) {
                u0 = (xt + 7) / 256.0F;
                u1 = (xt + 9 - 0.01F) / 256.0F;
                v0 = (yt + 6) / 256.0F;
                v1 = (yt + 16 - 0.01F) / 256.0F;
            }

            if (i == 0) {
                c0 = corners[0];
                c1 = corners[1];
                c2 = corners[2];
                c3 = corners[3];
            } else if (i == 1) {
                c0 = corners[7];
                c1 = corners[6];
                c2 = corners[5];
                c3 = corners[4];
            } else if (i == 2) {
                c0 = corners[1];
                c1 = corners[0];
                c2 = corners[4];
                c3 = corners[5];
            } else if (i == 3) {
                c0 = corners[2];
                c1 = corners[1];
                c2 = corners[5];
                c3 = corners[6];
            } else if (i == 4) {
                c0 = corners[3];
                c1 = corners[2];
                c2 = corners[6];
                c3 = corners[7];
            } else if (i == 5) {
                c0 = corners[0];
                c1 = corners[3];
                c2 = corners[7];
                c3 = corners[4];
            }

            t.vertexUV(c0.x, c0.y, c0.z, u0, v1);
            t.vertexUV(c1.x, c1.y, c1.z, u1, v1);
            t.vertexUV(c2.x, c2.y, c2.z, u1, v0);
            t.vertexUV(c3.x, c3.y, c3.z, u0, v0);
        }

        return true;
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


        if (!this.level.isSolidBlockingTile(x, y - 1, z) && !Tile.fire.canBurn(this.level, x, y - 1, z)) {
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

            if (Tile.fire.canBurn(this.level, xMinusOne, y, z)) {
                t.vertexUV(xPlusR, y + h + yo, zPlusOneD, u1, v0);
                t.vertexUV(xD, y + 0 + yo, zPlusOneD, u1, v1);
                t.vertexUV(xD, y + 0 + yo, zD, u0, v1);
                t.vertexUV(xPlusR, y + h + yo, zD, u0, v0);
                t.vertexUV(xPlusR, y + h + yo, zD, u0, v0);
                t.vertexUV(xD, y + 0 + yo, zD, u0, v1);
                t.vertexUV(xD, y + 0 + yo, zPlusOneD, u1, v1);
                t.vertexUV(xPlusR, y + h + yo, zPlusOneD, u1, v0);
            }

            if (Tile.fire.canBurn(this.level, xPlusOne, y, z)) {
                t.vertexUV(xPlusOneMinusR, y + h + yo, zD, u0, v0);
                t.vertexUV(xPlusOneD, y + 0 + yo, zD, u0, v1);
                t.vertexUV(xPlusOneD, y + 0 + yo, zPlusOneD, u1, v1);
                t.vertexUV(xPlusOneMinusR, y + h + yo, zPlusOneD, u1, v0);
                t.vertexUV(xPlusOneMinusR, y + h + yo, zPlusOneD, u1, v0);
                t.vertexUV(xPlusOneD, y + 0 + yo, zPlusOneD, u1, v1);
                t.vertexUV(xPlusOneD, y + 0 + yo, zD, u0, v1);
                t.vertexUV(xPlusOneMinusR, y + h + yo, zD, u0, v0);
            }

            if (Tile.fire.canBurn(this.level, x, y, zMinusOne)) {
                t.vertexUV(xD, y + h + yo, zPlusR, u1, v0);
                t.vertexUV(xD, y + 0 + yo, zD, u1, v1);
                t.vertexUV(xPlusOneD, y + 0 + yo, zD, u0, v1);
                t.vertexUV(xPlusOneD, y + h + yo, zPlusR, u0, v0);
                t.vertexUV(xPlusOneD, y + h + yo, zPlusR, u0, v0);
                t.vertexUV(xPlusOneD, y + 0 + yo, zD, u0, v1);
                t.vertexUV(xD, y + 0 + yo, zD, u1, v1);
                t.vertexUV(xD, y + h + yo, zPlusR, u1, v0);
            }

            if (Tile.fire.canBurn(this.level, x, y, zPlusOne)) {
                t.vertexUV(xPlusOneD, y + h + yo, zPlusOneMinusR, u0, v0);
                t.vertexUV(xPlusOneD, y + 0 + yo, zPlusOneD, u0, v1);
                t.vertexUV(xD, y + 0 + yo, zPlusOneD, u1, v1);
                t.vertexUV(xD, y + h + yo, zPlusOneMinusR, u1, v0);
                t.vertexUV(xD, y + h + yo, zPlusOneMinusR, u1, v0);
                t.vertexUV(xD, y + 0 + yo, zPlusOneD, u1, v1);
                t.vertexUV(xPlusOneD, y + 0 + yo, zPlusOneD, u0, v1);
                t.vertexUV(xPlusOneD, y + h + yo, zPlusOneMinusR, u0, v0);
            }

            if (Tile.fire.canBurn(this.level, x, y + 1, z)) {
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
            if (this.level.isSolidBlockingTile(xMinusOne, y, z) && this.level.getTile(xMinusOne, y + 1, z) == Tile.redStoneDust.id) {
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

            if (this.level.isSolidBlockingTile(xPlusOne, y, z) && this.level.getTile(xPlusOne, y + 1, z) == Tile.redStoneDust.id) {
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

            if (this.level.isSolidBlockingTile(x, y, zMinusOne) && this.level.getTile(x, y + 1, zMinusOne) == Tile.redStoneDust.id) {
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

            if (this.level.isSolidBlockingTile(x, y, zPlusOne) && this.level.getTile(x, y + 1, zPlusOne) == Tile.redStoneDust.id) {
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

    @Override
    public boolean tesselateWaterInWorld(Tile tt, final BigInteger x, int y, final BigInteger z) {
        Tesselator t = Tesselator.instance;
        int col = tt.getFoliageColor(this.level, x, y, z);
        float r = (float) (col >> 16 & 0xFF) / 255.0F;
        float g = (float) (col >> 8 & 0xFF) / 255.0F;
        float b = (float) (col & 0xFF) / 255.0F;
        boolean up = tt.shouldRenderFace(this.level, x, y + 1, z, Facing.UP);
        boolean down = tt.shouldRenderFace(this.level, x, y - 1, z, Facing.DOWN);

        double xx = FIX_STRIPELANDS ? BigMath.fastAnd(x, 15) : x.doubleValue();
        double yy = FIX_STRIPELANDS ? y & 15 : y;
        double zz = FIX_STRIPELANDS ? BigMath.fastAnd(z, 15) : z.doubleValue();

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
            //~ if >=1.0.0-beta.8.0.r 'float' -> 'double' {
            float h0 = getWaterHeight(x, y, z, m);
            float h1 = getWaterHeight(x, y, zPlusOne, m);
            float h2 = getWaterHeight(xPlusOne, y, zPlusOne, m);
            float h3 = getWaterHeight(xPlusOne, y, z, m);
            //~ }
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
                //? >=1.0.0-beta.8.0.r {
                /*t.tex2(tt.getLightColor(this.level, x, y, z));
                float br = 1;
                *///? } else
                float br = tt.getBrightness(this.level, x, y, z);
                t.color(c11 * br * r, c11 * br * g, c11 * br * b);
                t.vertexUV(xx, (float) yy + h0, zz, uc - (double) c - (double) s, vc - (double) c + (double) s);
                t.vertexUV(xx, (float) yy + h1, zz + 1, uc - (double) c + (double) s, vc + (double) c + (double) s);
                t.vertexUV(xx + 1, (float) yy + h2, zz + 1, uc + (double) c + (double) s, vc + (double) c - (double) s);
                t.vertexUV(xx + 1, (float) yy + h3, zz, uc + (double) c - (double) s, vc - (double) c - (double) s);
            }

            if (this.noCulling || down) {
                //? >=1.0.0-beta.8.0.r {
                /*t.tex2(tt.getLightColor(this.level, x, y - 1, z));
                float br = 1;
                *///? } else
                float br = tt.getBrightness(this.level, x, y - 1, z);
                t.color(c10 * br, c10 * br, c10 * br);
                this.renderFaceDown(tt, xx, yy, zz, tt.getTexture(Facing.DOWN));
                changed = true;
            }

            for (int face = 0; face < 4; ++face) {
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

            tt.yy0 = yo0;
            tt.yy1 = yo1;
            return changed;
        }
    }

    private boolean renderLiquidFace(
            final Tesselator t,
            final BigInteger x, final int y, final BigInteger z,
            final Tile tt, final int data,
            final boolean[] dirs,
            final float c2, final float c3, final float c11,
            final float r, final float g, final float b,
            //~ if >=1.0.0-beta.8.0.r 'float' -> 'double'
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

        double xx = FIX_STRIPELANDS ? BigMath.fastAnd(x, 15) : x.doubleValue();
        double zz = FIX_STRIPELANDS ? BigMath.fastAnd(z, 15) : z.doubleValue();
        double yy = FIX_STRIPELANDS ? y & 15 : y;

        int texx = tt.getTexture(face + 2, data);
        int xTex = (texx & 15) << 4;
        int yTex = texx & 240;
        if (this.noCulling || dirs[face]) {
            //~ if >=1.0.0-beta.8.0.r 'float h' -> 'double h' {
            float hh0;
            double x1;
            double z1;
            float hh1;
            //~ }
            double x0;
            double z0;
            if (face == 0) {
                hh0 = h0;
                hh1 = h3;
                x0 = xx;
                x1 = (xx + 1);
                z0 = zz;
                z1 = zz;
            } else if (face == 1) {
                hh0 = h2;
                hh1 = h1;
                x0 = (xx + 1);
                x1 = xx;
                z0 = (zz + 1);
                z1 = (zz + 1);
            } else if (face == 2) {
                hh0 = h1;
                hh1 = h0;
                x0 = xx;
                x1 = xx;
                z0 = (zz + 1);
                z1 = zz;
            } else {
                hh0 = h3;
                hh1 = h2;
                x0 = (xx + 1);
                x1 = (xx + 1);
                z0 = zz;
                z1 = (zz + 1);
            }

            double u0 = (double) ((float) (xTex + 0) / 256.0F);
            double u1 = ((double) (xTex + 16) - 0.01) / 256.0;
            double v01 = (double) (((float) yTex + (1.0F - hh0) * 16.0F) / 256.0F);
            double v02 = (double) (((float) yTex + (1.0F - hh1) * 16.0F) / 256.0F);
            double v1 = ((double) (yTex + 16) - 0.01) / 256.0;
            //? >=1.0.0-beta.8.0.r {
            /*t.tex2(tt.getLightColor(this.level, xt, y, zt));
            float br = 1;
            *///? } else
            float br = tt.getBrightness(this.level, xt, y, zt);
            if (face < 2) {
                br *= c2;
            } else {
                br *= c3;
            }

            t.color(c11 * br * r, c11 * br * g, c11 * br * b);
            t.vertexUV(x0, ((float) yy + hh0), z0, u0, v01);
            t.vertexUV(x1, ((float) yy + hh1), z1, u1, v02);
            t.vertexUV(x1, (yy + 0), z1, u1, v1);
            t.vertexUV(x0, (yy + 0), z0, u0, v1);
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
