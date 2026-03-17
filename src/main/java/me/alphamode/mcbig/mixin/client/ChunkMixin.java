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

                for(int var15 = y0; var15 < y1; ++var15) {
                    for(int var16 = z0; var16 < z1; ++var16) {
                        for(int var17 = x0; var17 < x1; ++var17) {
                            int var18 = region.getTile(var17, var15, var16);
                            if (var18 > 0) {
                                if (!started) {
                                    started = true;
                                    GL11.glNewList(this.lists + l, 4864);
                                    GL11.glPushMatrix();
                                    this.translateToPos();
                                    float var19 = 1.000001F;
                                    GL11.glTranslatef((float)(-this.zs) / 2.0F, (float)(-this.ys) / 2.0F, (float)(-this.zs) / 2.0F);
                                    GL11.glScalef(var19, var19, var19);
                                    GL11.glTranslatef((float)this.zs / 2.0F, (float)this.ys / 2.0F, (float)this.zs / 2.0F);
                                    tesselator.begin();
                                    tesselator.offset((double)(-this.x), (double)(-this.y), (double)(-this.z));
                                }

                                if (l == 0 && Tile.isEntityTile[var18]) {
                                    TileEntity var23 = region.getTileEntity(var17, var15, var16);
                                    if (TileEntityRenderDispatcher.instance.hasTileEntityRenderer(var23)) {
                                        this.renderableTileEntities.add(var23);
                                    }
                                }

                                Tile tile = Tile.tiles[var18];
                                int renderLayer = tile.getRenderLayer();
                                if (renderLayer != l) {
                                    renderNextLayer = true;
                                } else if (renderLayer == l) {
                                    rendered |= tileRenderer.tesselateInWorld(tile, var17, var15, var16);
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
