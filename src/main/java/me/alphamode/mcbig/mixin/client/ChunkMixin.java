package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.level.BigRegion;
import net.minecraft.client.renderer.Chunk;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.renderer.TileRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderDispatcher;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Region;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.TileEntity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;

@Mixin(Chunk.class)
public abstract class ChunkMixin {
    @Shadow public boolean[] empty;

    @Shadow public List<TileEntity> globalRenderableTileEntities;

    @Shadow public boolean skyLit;

    @Shadow public boolean compiled;

    @Shadow public List<TileEntity> renderableTileEntities;

    @Shadow public static Tesselator tesselator;

    @Shadow public int zs;

    @Shadow public int ys;

    @Shadow public int x;

    @Shadow public int y;

    @Shadow public int z;

    @Shadow public int lists;

    @Shadow public abstract void translateToPos();

    @Shadow public Level level;

    @Shadow public static int updates;

    @Shadow public boolean dirty;

    @Shadow public int xs;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void rebuild() {
        if (this.dirty) {
            ++updates;
            int x0 = this.x;
            int y0 = this.y;
            int z0 = this.z;
            int x1 = this.x + this.xs;
            int y1 = this.y + this.ys;
            int z1 = this.z + this.zs;

            for(int i = 0; i < 2; ++i) {
                this.empty[i] = true;
            }

            LevelChunk.touchedSky = false;
            HashSet<TileEntity> var21 = new HashSet<>();
            var21.addAll(this.renderableTileEntities);
            this.renderableTileEntities.clear();
            int r = 1;
            BigRegion region = new BigRegion(this.level, BigInteger.valueOf(x0 - r), y0 - r, BigInteger.valueOf(z0 - r), BigInteger.valueOf(x1 + r), y1 + r, BigInteger.valueOf(z1 + r));
            TileRenderer tileRenderer = new TileRenderer(region);

            for(int l = 0; l < 2; ++l) {
                boolean renderNextLayer = false;
                boolean rendered = false;
                boolean started = false;

                for(int y = y0; y < y1; ++y) {
                    for(int z = z0; z < z1; ++z) {
                        for(int x = x0; x < x1; ++x) {
                            int tileId = region.getTile(x, y, z);
                            if (tileId > 0) {
                                if (!started) {
                                    started = true;
                                    GL11.glNewList(this.lists + l, GL11.GL_COMPILE);
                                    GL11.glPushMatrix();
                                    this.translateToPos();
                                    float ss = 1.000001F;
                                    GL11.glTranslatef(-this.zs / 2.0F, -this.ys / 2.0F, -this.zs / 2.0F);
                                    GL11.glScalef(ss, ss, ss);
                                    GL11.glTranslatef(this.zs / 2.0F, this.ys / 2.0F, this.zs / 2.0F);
                                    tesselator.begin();
                                    tesselator.offset(-this.x, -this.y, -this.z);
                                }

                                if (l == 0 && Tile.isEntityTile[tileId]) {
                                    TileEntity te = region.getTileEntity(x, y, z);
                                    if (TileEntityRenderDispatcher.instance.hasTileEntityRenderer(te)) {
                                        this.renderableTileEntities.add(te);
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

            HashSet<TileEntity> var22 = new HashSet<TileEntity>();
            var22.addAll(this.renderableTileEntities);
            var22.removeAll(var21);
            this.globalRenderableTileEntities.addAll(var22);
            var21.removeAll(this.renderableTileEntities);
            this.globalRenderableTileEntities.removeAll(var21);
            this.skyLit = LevelChunk.touchedSky;
            this.compiled = true;
        }
    }
}
