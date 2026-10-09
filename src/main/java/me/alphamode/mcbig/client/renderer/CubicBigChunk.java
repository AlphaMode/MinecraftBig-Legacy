package me.alphamode.mcbig.client.renderer;

import me.alphamode.mcbig.extensions.BigTileRendererExtension;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.level.CubicRegion;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.math.BigMath;
import net.minecraft.client.renderer.Chunk;
import net.minecraft.client.renderer.TileRenderer;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.phys.AABB;
import org.lwjgl.opengl.GL11;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;

public class CubicBigChunk extends Chunk {

    public BigInteger bigX = BigInteger.ZERO;
    public BigInteger bigY = BigInteger.ZERO;
    public BigInteger bigZ = BigInteger.ZERO;
    public BigInteger bigXm = BigInteger.ZERO;
    public BigInteger bigYm = BigInteger.ZERO;
    public BigInteger bigZm = BigInteger.ZERO;
    public BigInteger xRenderBig = BigInteger.ZERO;
    public BigInteger yRenderBig = BigInteger.ZERO;
    public BigInteger zRenderBig = BigInteger.ZERO;

    public CubicBigChunk(Level level, List<TileEntity> tileEntities, int x, int y, int z, int size, int lists) {
        super(level, tileEntities, x, y, z, size, lists);
        this.bigX = BigInteger.valueOf(-999);
        setPos(BigInteger.valueOf(x), BigInteger.valueOf(y), BigInteger.valueOf(z));
    }

    public void setPos(BigInteger x, BigInteger y, BigInteger z) {
        if (!x.equals(this.bigX) || !y.equals(this.bigY) || !z.equals(this.bigZ)) {
            this.reset();
            this.bigX = x;
            this.bigY = y;
            this.bigZ = z;
            this.bigXm = x.add(BigInteger.valueOf(this.xs / 2));
            this.bigYm = y.add(BigInteger.valueOf(this.ys / 2));
            this.bigZm = z.add(BigInteger.valueOf(this.zs / 2));
            this.xRenderOffs = BigMath.fastAnd(x, 1023);
            this.yRenderOffs = BigMath.fastAnd(y, 1023);
            this.zRenderOffs = BigMath.fastAnd(z, 1023);
            this.xRenderBig = x.subtract(BigInteger.valueOf(this.xRenderOffs));
            this.yRenderBig = y.subtract(BigInteger.valueOf(this.yRenderOffs));
            this.zRenderBig = z.subtract(BigInteger.valueOf(this.zRenderOffs));
            float ss = 6.0F;
            this.bb = AABB.create(
                    (double) (x.doubleValue() - ss),
                    (double) ((float) y.doubleValue() - ss),
                    (double) (z.doubleValue() - ss),
                    (double) ((x.doubleValue() + this.xs) + ss),
                    (double) ((float) (y.doubleValue() + this.ys) + ss),
                    (double) ((z.doubleValue() + this.zs) + ss)
            );
            GL11.glNewList(this.lists + 2, 4864);
            ItemRenderer.renderFlat(
                    AABB.newTemp(
                            (double) ((float) this.xRenderOffs - ss),
                            (double) ((float) this.yRenderOffs - ss),
                            (double) ((float) this.zRenderOffs - ss),
                            (double) ((float) (this.xRenderOffs + this.xs) + ss),
                            (double) ((float) (this.yRenderOffs + this.ys) + ss),
                            (double) ((float) (this.zRenderOffs + this.zs) + ss)
                    )
            );
            GL11.glEndList();
            this.setDirty();
        }
    }

    @Override
    public float distanceToSqr(Entity entity) {
        if (entity.isBigMovementEnabled()) {
            BigEntityExtension bigEntity = (BigEntityExtension) entity;
            float xd = (float) bigEntity.getX().toBigInteger().subtract(this.bigXm).floatValue();
            float yd = (float) (entity.y - (double) this.bigYm.doubleValue());
            float zd = (float) bigEntity.getZ().toBigInteger().subtract(this.bigZm).floatValue();
            return xd * xd + yd * yd + zd * zd;
        }
        float xd = (float) (entity.x - (double) this.bigXm.doubleValue());
        float yd = (float) (entity.y - (double) this.bigYm.doubleValue());
        float zd = (float) (entity.z - (double) this.bigZm.doubleValue());
        return xd * xd + yd * yd + zd * zd;
    }

    @Override
    public void rebuild() {
        if (this.dirty) {
            ++updates;
            BigInteger x0 = this.bigX;
            BigInteger y0 = this.bigY;
            BigInteger z0 = this.bigZ;
            BigInteger x1 = this.bigX.add(BigInteger.valueOf(this.xs));
            BigInteger y1 = this.bigY.add(BigInteger.valueOf(this.ys));
            BigInteger z1 = this.bigZ.add(BigInteger.valueOf(this.zs));

            for (int i = 0; i < 2; ++i) {
                this.empty[i] = true;
            }

            LevelChunk.touchedSky = false;
            HashSet<TileEntity> oldTileEntities = new HashSet<>();
            oldTileEntities.addAll(this.renderableTileEntities);
            this.renderableTileEntities.clear();
            var r = BigInteger.ONE;
            LevelSource region = new CubicRegion(this.level, x0.subtract(r), y0.subtract(r), z0.subtract(r), x1.add(r), y1.add(r), z1.add(r));
            TileRenderer tileRenderer = new TileRenderer(region);

            for (int l = 0; l < 2; ++l) {
                boolean renderNextLayer = false;
                boolean rendered = false;
                boolean started = false;

                for (var y = y0; y.compareTo(y1) < 0; y = y.add(BigInteger.ONE)) {
                    for (var z = z0; z.compareTo(z1) < 0; z = z.add(BigInteger.ONE)) {
                        for (var x = x0; x.compareTo(x1) < 0; x = x.add(BigInteger.ONE)) {
                            int tileId = region.getTile(x, y, z);
                            if (tileId > 0) {
                                if (!started) {
                                    started = true;
                                    GL11.glNewList(this.lists + l, GL11.GL_COMPILE);
                                    GL11.glPushMatrix();
                                    this.translateToPos();
                                    float ss = 1.000001F;
                                    GL11.glTranslatef((float) (-this.zs) / 2.0F, (float) (-this.ys) / 2.0F, (float) (-this.zs) / 2.0F);
                                    GL11.glScalef(ss, ss, ss);
                                    GL11.glTranslatef((float) this.zs / 2.0F, (float) this.ys / 2.0F, (float) this.zs / 2.0F);
                                    tesselator.begin();
                                    if (BigTileRendererExtension.FIX_STRIPELANDS) {
                                        tesselator.setTesselatorOffset(this.bigX.negate(), this.bigZ.negate());
                                        tesselator.offset(0, 0, 0);

                                    } else {
                                        tesselator.offset(this.bigX.negate().doubleValue(), this.bigY.negate().doubleValue(), this.bigZ.negate().doubleValue());
                                    }
                                }

                                if (l == 0 && Tile.isEntityTile[tileId]) {
                                    TileEntity et = region.getTileEntity(x, y, z);
                                    if (TileEntityRenderDispatcher.instance.hasTileEntityRenderer(et)) {
                                        this.renderableTileEntities.add(et);
                                    }
                                }

                                Tile tile = Tile.tiles[tileId];
                                int renderLayer = tile.getRenderLayer();
                                if (renderLayer != l) {
                                    renderNextLayer = true;
                                } else if (renderLayer == l) {
                                    rendered |= tileRenderer.tesselateInWorld(tile, x, y.intValue(), z);
                                }
                            }
                        }
                    }
                }

                if (started) {
                    tesselator.end();
                    GL11.glPopMatrix();
                    GL11.glEndList();
                    tesselator.offset(BigDecimal.ZERO, 0.0, BigDecimal.ZERO);
                    tesselator.offset(0.0, 0.0, 0.0);
                } else {
                    rendered = false;
                }

                if (rendered) {
                    this.empty[l] = false;
                }

                if (!renderNextLayer) {
                    break;
                }
            }

            HashSet<TileEntity> newTileEntities = new HashSet<>();
            newTileEntities.addAll(this.renderableTileEntities);
            newTileEntities.removeAll(oldTileEntities);
            this.globalRenderableTileEntities.addAll(newTileEntities);
            oldTileEntities.removeAll(this.renderableTileEntities);
            this.globalRenderableTileEntities.removeAll(oldTileEntities);
            this.skyLit = LevelChunk.touchedSky;
            this.compiled = true;
        }
    }

    @Override
    public void cull(Culler culler) {
        this.visible = true;
    }
}
