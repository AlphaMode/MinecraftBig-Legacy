package me.alphamode.mcbig.client.renderer;

import me.alphamode.mcbig.level.BigRegion;
import me.alphamode.mcbig.math.BigConstants;
import net.minecraft.client.renderer.Chunk;
import net.minecraft.client.renderer.TileRenderer;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.Region;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.phys.AABB;
import org.lwjgl.opengl.GL11;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class BigChunk extends Chunk {

    public BigInteger bigX = BigInteger.ZERO;
    public BigInteger bigZ = BigInteger.ZERO;
    public BigInteger bigXm = BigInteger.ZERO;
    public BigInteger bigZm = BigInteger.ZERO;
    public BigInteger xRenderBig = BigInteger.ZERO;
    public BigInteger zRenderBig = BigInteger.ZERO;

    public BigChunk(Level level, List<TileEntity> tileEntities, int x, int y, int z, int size, int lists) {
        super(level, tileEntities, x, y, z, size, lists);
        this.bigX = BigInteger.valueOf(-999);
        setPos(BigInteger.valueOf(x), y, BigInteger.valueOf(z));
    }

    public void setPos(BigInteger x, int y, BigInteger z) {
        if (!x.equals(this.bigX) || y != this.y || !z.equals(this.bigZ)) {
            this.reset();
            this.bigX = x;
            this.y = y;
            this.bigZ = z;
            this.bigXm = x.add(BigInteger.valueOf(this.xs / 2));
            this.ym = y + this.ys / 2;
            this.bigZm = z.add(BigInteger.valueOf(this.zs / 2));
            this.xRenderOffs = x.and(BigConstants.CHUNK_OFFSET).intValue();
            this.yRenderOffs = y;
            this.zRenderOffs = z.and(BigConstants.CHUNK_OFFSET).intValue();
            this.xRenderBig = x.subtract(BigInteger.valueOf(this.xRenderOffs));
            this.yRender = y - this.yRenderOffs;
            this.zRenderBig = z.subtract(BigInteger.valueOf(this.zRenderOffs));
            float var4 = 6.0F;
            this.bb = AABB.create(
                    (double)(x.doubleValue() - var4),
                    (double)((float)y - var4),
                    (double)(z.doubleValue() - var4),
                    (double)((x.doubleValue() + this.xs) + var4),
                    (double)((float)(y + this.ys) + var4),
                    (double)((z.doubleValue() + this.zs) + var4)
            );
            GL11.glNewList(this.lists + 2, 4864);
            ItemRenderer.renderFlat(
                    AABB.newTemp(
                            (double)((float)this.xRenderOffs - var4),
                            (double)((float)this.yRenderOffs - var4),
                            (double)((float)this.zRenderOffs - var4),
                            (double)((float)(this.xRenderOffs + this.xs) + var4),
                            (double)((float)(this.yRenderOffs + this.ys) + var4),
                            (double)((float)(this.zRenderOffs + this.zs) + var4)
                    )
            );
            GL11.glEndList();
            this.setDirty();
        }
    }

    @Override
    public float distanceToSqr(Entity entity) {
        float var2 = (float)(entity.x - (double)this.bigXm.doubleValue());
        float var3 = (float)(entity.y - (double)this.ym);
        float var4 = (float)(entity.z - (double)this.bigZm.doubleValue());
        return var2 * var2 + var3 * var3 + var4 * var4;
    }

    @Override
    public void rebuild() {
        if (this.dirty) {
            ++updates;
            BigInteger x0 = this.bigX;
            int y0 = this.y;
            BigInteger z0 = this.bigZ;
            BigInteger x1 = this.bigX.add(BigInteger.valueOf(this.xs));
            int y1 = this.y + this.ys;
            BigInteger z1 = this.bigZ.add(BigInteger.valueOf(this.zs));

            for(int i = 0; i < 2; ++i) {
                this.empty[i] = true;
            }

            LevelChunk.touchedSky = false;
            HashSet<TileEntity> oldTileEntities = new HashSet();
            oldTileEntities.addAll(this.renderableTileEntities);
            this.renderableTileEntities.clear();
            int r = 1;
            LevelSource region = new BigRegion(this.level, x0.subtract(BigInteger.ONE), y0 - r, z0.subtract(BigInteger.ONE), x1.add(BigInteger.ONE), y1 + r, z1.add(BigInteger.ONE));
            TileRenderer tileRenderer = new TileRenderer(region);

            for(int l = 0; l < 2; ++l) {
                boolean renderNextLayer = false;
                boolean rendered = false;
                boolean started = false;

                for(int y = y0; y < y1; ++y) {
                    for(BigInteger z = z0; z.compareTo(z1) < 0; z = z.add(BigInteger.ONE)) {
                        for(BigInteger x = x0; x.compareTo(x1) < 0; x = x.add(BigInteger.ONE)) {
                            int tileId = region.getTile(x, y, z);
                            if (tileId > 0) {
                                if (!started) {
                                    started = true;
                                    GL11.glNewList(this.lists + l, 4864);
                                    GL11.glPushMatrix();
                                    this.translateToPos();
                                    float ss = 1.000001F;
                                    GL11.glTranslatef((float)(-this.zs) / 2.0F, (float)(-this.ys) / 2.0F, (float)(-this.zs) / 2.0F);
                                    GL11.glScalef(ss, ss, ss);
                                    GL11.glTranslatef((float)this.zs / 2.0F, (float)this.ys / 2.0F, (float)this.zs / 2.0F);
                                    tesselator.begin();
                                    tesselator.offset((double)(this.bigX.negate()).doubleValue(), (double)(-this.y), (double)(this.bigZ.negate()).doubleValue());
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
                                    rendered |= tileRenderer.tesselateInWorld(tile, x, y, z);
                                }
                            }
                        }
                    }
                }

                if (started) {
                    tesselator.end();
                    GL11.glPopMatrix();
                    GL11.glEndList();
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

            HashSet<TileEntity> newTileEntities = new HashSet();
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
