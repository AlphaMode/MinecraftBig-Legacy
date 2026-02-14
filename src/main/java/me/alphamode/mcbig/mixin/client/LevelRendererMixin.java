package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.client.renderer.BigChunk;
import me.alphamode.mcbig.client.renderer.BigDistanceChunkSorter;
import me.alphamode.mcbig.extensions.BigLevelListenerExtension;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.world.phys.BigHitResult;
import net.minecraft.client.Lighting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.math.BigInteger;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements BigLevelListenerExtension {
    @Shadow private int lastViewDistance;

    @Shadow private Minecraft mc;

    @Shadow private Chunk[] chunks;

    @Shadow private int xChunks;

    @Shadow private int yChunks;

    @Shadow private int zChunks;

    @Shadow private Chunk[] sortedChunks;

    @Shadow private int xMinChunk;

    @Shadow private int yMinChunk;

    @Shadow private int zMinChunk;

    @Shadow private int xMaxChunk;

    @Shadow private int yMaxChunk;

    @Shadow private int zMaxChunk;

    @Shadow private List dirtyChunks;

    @Shadow public List renderableTileEntities;

    @Shadow private Level level;

    @Shadow private boolean occlusionCheck;

    @Shadow private IntBuffer occlusionCheckIds;

    @Shadow private int chunkLists;

    @Shadow protected abstract void resortChunks(int xc, int yc, int zc);

    @Shadow private int noEntityRenderFrames;

    @Shadow private int ticks;

    @Shadow protected abstract void checkQueryResults(int from, int to);

    @Shadow private double xOld;

    @Shadow private double yOld;

    @Shadow private double zOld;

    @Shadow private int chunkFixOffs;

    @Shadow private int totalChunks;

    @Shadow private int offscreenChunks;

    @Shadow private int occludedChunks;

    @Shadow private int renderedChunks;

    @Shadow private int emptyChunks;

    @Shadow protected abstract void render(AABB aabb);

    @Shadow private int totalEntities;

    @Shadow private int renderedEntities;

    @Shadow private int culledEntities;

    @Shadow private Textures textures;

    @Shadow private OffsettedRenderList[] renderLists;

    @Shadow private List renderChunks;

    @Shadow public abstract void renderSameAsLast(int layer, double alpha);

    /**
     * @author AlphaMode
     * @reason
     */
    @Overwrite
    public void allChanged() {
        Tile.LEAVES.setFancy(this.mc.options.fancyGraphics);
        this.lastViewDistance = this.mc.options.viewDistance;
        if (this.chunks != null) {
            for(int var1 = 0; var1 < this.chunks.length; ++var1) {
                this.chunks[var1].delete();
            }
        }

        int var7 = 64 << 3 - this.lastViewDistance;
        if (var7 > 400) {
            var7 = 400;
        }

        this.xChunks = var7 / 16 + 1;
        this.yChunks = 8;
        this.zChunks = var7 / 16 + 1;
        this.chunks = new BigChunk[this.xChunks * this.yChunks * this.zChunks];
        this.sortedChunks = new BigChunk[this.xChunks * this.yChunks * this.zChunks];
        int var2 = 0;
        int var3 = 0;
        this.xMinChunk = 0;
        this.yMinChunk = 0;
        this.zMinChunk = 0;
        this.xMaxChunk = this.xChunks;
        this.yMaxChunk = this.yChunks;
        this.zMaxChunk = this.zChunks;

        for(int i = 0; i < this.dirtyChunks.size(); ++i) {
            ((Chunk)this.dirtyChunks.get(i)).dirty = false;
        }

        this.dirtyChunks.clear();
        this.renderableTileEntities.clear();

        for(int var8 = 0; var8 < this.xChunks; ++var8) {
            for(int var5 = 0; var5 < this.yChunks; ++var5) {
                for(int var6 = 0; var6 < this.zChunks; ++var6) {
                    this.chunks[(var6 * this.yChunks + var5) * this.xChunks + var8] = new BigChunk(
                            this.level, this.renderableTileEntities, var8 * 16, var5 * 16, var6 * 16, 16, this.chunkLists + var2
                    );
                    if (this.occlusionCheck) {
                        this.chunks[(var6 * this.yChunks + var5) * this.xChunks + var8].occlusion_id = this.occlusionCheckIds.get(var3);
                    }

                    this.chunks[(var6 * this.yChunks + var5) * this.xChunks + var8].occlusion_querying = false;
                    this.chunks[(var6 * this.yChunks + var5) * this.xChunks + var8].occlusion_visible = true;
                    this.chunks[(var6 * this.yChunks + var5) * this.xChunks + var8].visible = true;
                    this.chunks[(var6 * this.yChunks + var5) * this.xChunks + var8].id = var3++;
                    this.chunks[(var6 * this.yChunks + var5) * this.xChunks + var8].setDirty();
                    this.sortedChunks[(var6 * this.yChunks + var5) * this.xChunks + var8] = this.chunks[(var6 * this.yChunks + var5) * this.xChunks + var8];
                    this.dirtyChunks.add(this.chunks[(var6 * this.yChunks + var5) * this.xChunks + var8]);
                    var2 += 3;
                }
            }
        }

        if (this.level != null) {
            Mob var9 = this.mc.cameraEntity;
            if (var9 != null) {
                this.resortChunks(BigMath.floor(var9.x), Mth.floor(var9.y), BigMath.floor(var9.z));
                Arrays.sort((BigChunk[]) this.sortedChunks, new BigDistanceChunkSorter(var9));
            }
        }

        this.noEntityRenderFrames = 2;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int render(Mob camera, int layer, double alpha) {
        for(int var5 = 0; var5 < 10; ++var5) {
            this.chunkFixOffs = (this.chunkFixOffs + 1) % this.chunks.length;
            Chunk var6 = this.chunks[this.chunkFixOffs];
            if (var6.dirty && !this.dirtyChunks.contains(var6)) {
                this.dirtyChunks.add(var6);
            }
        }

        if (this.mc.options.viewDistance != this.lastViewDistance) {
            this.allChanged();
        }

        if (layer == 0) {
            this.totalChunks = 0;
            this.offscreenChunks = 0;
            this.occludedChunks = 0;
            this.renderedChunks = 0;
            this.emptyChunks = 0;
        }

        double var33 = camera.xOld + (camera.x - camera.xOld) * alpha;
        double var7 = camera.yOld + (camera.y - camera.yOld) * alpha;
        double var9 = camera.zOld + (camera.z - camera.zOld) * alpha;
        double var11 = camera.x - this.xOld;
        double var13 = camera.y - this.yOld;
        double var15 = camera.z - this.zOld;
        if (var11 * var11 + var13 * var13 + var15 * var15 > 16.0) {
            this.xOld = camera.x;
            this.yOld = camera.y;
            this.zOld = camera.z;
            this.resortChunks(BigMath.floor(camera.x), Mth.floor(camera.y), BigMath.floor(camera.z));
            Arrays.sort((BigChunk[]) this.sortedChunks, new BigDistanceChunkSorter(camera));
        }

        Lighting.turnOff();
        int var17 = 0;
        if (this.occlusionCheck && this.mc.options.advancedOpengl && !this.mc.options.anaglyph3d && layer == 0) {
            int var18 = 0;
            int var19 = 16;
            this.checkQueryResults(var18, var19);

            for(int var20 = var18; var20 < var19; ++var20) {
                this.sortedChunks[var20].occlusion_visible = true;
            }

            var17 += this.renderChunks(var18, var19, layer, alpha);

            do {
                var18 = var19;
                var19 *= 2;
                if (var19 > this.sortedChunks.length) {
                    var19 = this.sortedChunks.length;
                }

                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_FOG);
                GL11.glColorMask(false, false, false, false);
                GL11.glDepthMask(false);
                this.checkQueryResults(var18, var19);
                GL11.glPushMatrix();
                float var36 = 0.0F;
                float var21 = 0.0F;
                float var22 = 0.0F;

                for(int var23 = var18; var23 < var19; ++var23) {
                    if (this.sortedChunks[var23].isEmpty()) {
                        this.sortedChunks[var23].visible = false;
                    } else {
                        if (!this.sortedChunks[var23].visible) {
                            this.sortedChunks[var23].occlusion_visible = true;
                        }

                        if (this.sortedChunks[var23].visible && !this.sortedChunks[var23].occlusion_querying) {
                            float var24 = Mth.sqrt(this.sortedChunks[var23].distanceToSqr(camera));
                            int var25 = (int)(1.0F + var24 / 128.0F);
                            if (this.ticks % var25 == var23 % var25) {
                                BigChunk var26 = (BigChunk) this.sortedChunks[var23];
                                float var27 = (float)((double)var26.xRenderBig.doubleValue() - var33);
                                float var28 = (float)((double)var26.yRender - var7);
                                float var29 = (float)((double)var26.zRenderBig.doubleValue() - var9);
                                float var30 = var27 - var36;
                                float var31 = var28 - var21;
                                float var32 = var29 - var22;
                                if (var30 != 0.0F || var31 != 0.0F || var32 != 0.0F) {
                                    GL11.glTranslatef(var30, var31, var32);
                                    var36 += var30;
                                    var21 += var31;
                                    var22 += var32;
                                }

                                ARBOcclusionQuery.glBeginQueryARB(35092, this.sortedChunks[var23].occlusion_id);
                                this.sortedChunks[var23].renderBB();
                                ARBOcclusionQuery.glEndQueryARB(35092);
                                this.sortedChunks[var23].occlusion_querying = true;
                            }
                        }
                    }
                }

                GL11.glPopMatrix();
                if (this.mc.options.anaglyph3d) {
                    if (GameRenderer.currentRenderLayer == 0) {
                        GL11.glColorMask(false, true, true, true);
                    } else {
                        GL11.glColorMask(true, false, false, true);
                    }
                } else {
                    GL11.glColorMask(true, true, true, true);
                }

                GL11.glDepthMask(true);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_FOG);
                var17 += this.renderChunks(var18, var19, layer, alpha);
            } while(var19 < this.sortedChunks.length);
        } else {
            var17 += this.renderChunks(0, this.sortedChunks.length, layer, alpha);
        }

        return var17;
    }

    private void resortChunks(BigInteger xc, int yc, BigInteger zc) {
        xc = xc.subtract(BigConstants.EIGHT);
        yc -= 8;
        zc = zc.subtract(BigConstants.EIGHT);
        this.xMinChunk = Integer.MAX_VALUE;
        this.yMinChunk = Integer.MAX_VALUE;
        this.zMinChunk = Integer.MAX_VALUE;
        this.xMaxChunk = Integer.MIN_VALUE;
        this.yMaxChunk = Integer.MIN_VALUE;
        this.zMaxChunk = Integer.MIN_VALUE;
        int s2 = this.xChunks * 16;
        int s1 = s2 / 2;

        for(int x = 0; x < this.xChunks; ++x) {
            BigInteger xx = BigInteger.valueOf(x * 16);
            BigInteger xOff = xx.add(BigInteger.valueOf(s1)).subtract(xc);
            if (xOff.compareTo(BigInteger.ZERO) < 0) {
                xOff = xOff.subtract(BigInteger.valueOf(s2 - 1));
            }

            xOff = xOff.divide(BigInteger.valueOf(s2));
            xx = xx.subtract(xOff.multiply(BigInteger.valueOf(s2)));
            if (xx.compareTo(BigInteger.valueOf(this.xMinChunk)) < 0) {
                this.xMinChunk = xx.intValue();
            }

            if (xx.compareTo(BigInteger.valueOf(this.xMaxChunk)) > 0) {
                this.xMaxChunk = xx.intValue();
            }

            for(int z = 0; z < this.zChunks; ++z) {
                BigInteger zz = BigInteger.valueOf(z * 16);
                BigInteger zOff = zz.add(BigInteger.valueOf(s1)).subtract(zc);
                if (zOff.compareTo(BigInteger.ZERO) < 0) {
                    zOff = zOff.subtract(BigInteger.valueOf(s2 - 1));
                }

                zOff = zOff.divide(BigInteger.valueOf(s2));
                zz = zz.subtract(zOff.multiply(BigInteger.valueOf(s2)));
                if (zz.compareTo(BigInteger.valueOf(this.zMinChunk)) < 0) {
                    this.zMinChunk = zz.intValue();
                }

                if (zz.compareTo(BigInteger.valueOf(this.zMaxChunk)) > 0) {
                    this.zMaxChunk = zz.intValue();
                }

                for(int y = 0; y < this.yChunks; ++y) {
                    int yy = y * 16;
                    if (yy < this.yMinChunk) {
                        this.yMinChunk = yy;
                    }

                    if (yy > this.yMaxChunk) {
                        this.yMaxChunk = yy;
                    }

                    BigChunk chunk = (BigChunk) this.chunks[(z * this.yChunks + y) * this.xChunks + x];
                    boolean wasDirty = chunk.dirty;
                    chunk.setPos(xx, yy, zz);
                    if (!wasDirty && chunk.dirty) {
                        this.dirtyChunks.add(chunk);
                    }
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void renderHitOutline(Player player, HitResult r, int mode, ItemInstance inventoryItem, float a) {
        BigHitResult result = (BigHitResult) r;
        if (mode == 0 && result.hitType == HitResult.HitType.TILE) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
            GL11.glLineWidth(2.0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(false);
            float ss = 0.002F;
            int tile = this.level.getTile(result.xBig, result.y, result.zBig);
            if (tile > 0) {
                Tile.tiles[tile].updateShape(this.level, result.xBig, result.y, result.zBig);
                double var8 = player.xOld + (player.x - player.xOld) * (double)a;
                double var10 = player.yOld + (player.y - player.yOld) * (double)a;
                double var12 = player.zOld + (player.z - player.zOld) * (double)a;
                this.render(
                        Tile.tiles[tile].getTileAABB(this.level, result.xBig, result.y, result.zBig).inflate((double)ss, (double)ss, (double)ss).offset(-var8, -var10, -var12)
                );
            }

            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void renderEntities(Vec3 cam, Culler culler, float partialTick) {
        if (this.noEntityRenderFrames > 0) {
            --this.noEntityRenderFrames;
        } else {
            TileEntityRenderDispatcher.instance.prepare(this.level, this.textures, this.mc.font, this.mc.cameraEntity, partialTick);
            EntityRenderDispatcher.INSTANCE.prepare(this.level, this.textures, this.mc.font, this.mc.cameraEntity, this.mc.options, partialTick);
            this.totalEntities = 0;
            this.renderedEntities = 0;
            this.culledEntities = 0;
            Mob var4 = this.mc.cameraEntity;
            EntityRenderDispatcher.xOff = var4.xOld + (var4.x - var4.xOld) * (double)partialTick;
            EntityRenderDispatcher.yOff = var4.yOld + (var4.y - var4.yOld) * (double)partialTick;
            EntityRenderDispatcher.zOff = var4.zOld + (var4.z - var4.zOld) * (double)partialTick;
            TileEntityRenderDispatcher.xOff = var4.xOld + (var4.x - var4.xOld) * (double)partialTick;
            TileEntityRenderDispatcher.yOff = var4.yOld + (var4.y - var4.yOld) * (double)partialTick;
            TileEntityRenderDispatcher.zOff = var4.zOld + (var4.z - var4.zOld) * (double)partialTick;
            List<Entity> entities = this.level.getAllEntities();
            this.totalEntities = entities.size();

            for(int i = 0; i < this.level.globalEntities.size(); ++i) {
                Entity entity = (Entity)this.level.globalEntities.get(i);
                ++this.renderedEntities;
                if (entity.shouldRender(cam)) {
                    EntityRenderDispatcher.INSTANCE.render(entity, partialTick);
                }
            }

            for (Entity entity : entities) {
                if (entity.shouldRender(cam)
                        && (entity.noCulling || culler.isVisible(entity.bb))
                        && (entity != this.mc.cameraEntity || this.mc.options.thirdPersonView || this.mc.cameraEntity.isSleeping())) {
                    int var8 = Mth.floor(entity.y);
                    if (var8 < 0) {
                        var8 = 0;
                    }

                    if (var8 >= 128) {
                        var8 = 127;
                    }

                    if (this.level.hasChunkAt(BigMath.floor(entity.x), var8, BigMath.floor(entity.z))) {
                        ++this.renderedEntities;
                        EntityRenderDispatcher.INSTANCE.render(entity, partialTick);
                    }
                }
            }

            for (Object renderableTileEntity : this.renderableTileEntities) {
                TileEntityRenderDispatcher.instance.render((TileEntity) renderableTileEntity, partialTick);
            }
        }
    }

    @Override
    public void tileChanged(BigInteger x, int y, BigInteger z) {
        this.setDirty(x.subtract(BigInteger.ONE), y - 1, z.subtract(BigInteger.ONE), x.add(BigInteger.ONE), y + 1, z.add(BigInteger.ONE));
    }

    @Override
    public void setTilesDirty(BigInteger minX, int minY, BigInteger minZ, BigInteger maxX, int maxY, BigInteger maxZ) {
        this.setDirty(minX.subtract(BigInteger.ONE), minY - 1, minZ.subtract(BigInteger.ONE), maxX.add(BigInteger.ONE), maxY + 1, maxZ.add(BigInteger.ONE));
    }

    public void setDirty(BigInteger x0, int y0, BigInteger z0, BigInteger x1, int y1, BigInteger z1) {
        BigInteger _x0 = BigMath.intFloorDiv(x0, BigConstants.SIXTEEN);
        int _y0 = Mth.intFloorDiv(y0, 16);
        BigInteger _z0 = BigMath.intFloorDiv(z0, BigConstants.SIXTEEN);
        BigInteger _x1 = BigMath.intFloorDiv(x1, BigConstants.SIXTEEN);
        int _y1 = Mth.intFloorDiv(y1, 16);
        BigInteger _z1 = BigMath.intFloorDiv(z1, BigConstants.SIXTEEN);

        for(BigInteger x = _x0; x.compareTo(_x1) <= 0; x = x.add(BigInteger.ONE)) {
            int xx = x.remainder(BigInteger.valueOf(this.xChunks)).intValue();
            if (xx < 0) {
                xx += this.xChunks;
            }

            for(int y = _y0; y <= _y1; ++y) {
                int yy = y % this.yChunks;
                if (yy < 0) {
                    yy += this.yChunks;
                }

                for(BigInteger z = _z0; z.compareTo(_z1) <= 0; z = z.add(BigInteger.ONE)) {
                    int zz = z.remainder(BigInteger.valueOf(this.zChunks)).intValue();
                    if (zz < 0) {
                        zz += this.zChunks;
                    }

                    int p = (zz * this.yChunks + yy) * this.xChunks + xx;
                    Chunk chunk = this.chunks[p];
                    if (!chunk.dirty) {
                        this.dirtyChunks.add(chunk);
                        chunk.setDirty();
                    }
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private int renderChunks(int from, int to, int layer, double alpha) {
        this.renderChunks.clear();
        int count = 0;

        for(int i = from; i < to; ++i) {
            if (layer == 0) {
                ++this.totalChunks;
                if (this.sortedChunks[i].empty[layer]) {
                    ++this.emptyChunks;
                } else if (!this.sortedChunks[i].visible) {
                    ++this.offscreenChunks;
                } else if (this.occlusionCheck && !this.sortedChunks[i].occlusion_visible) {
                    ++this.occludedChunks;
                } else {
                    ++this.renderedChunks;
                }
            }

            if (!this.sortedChunks[i].empty[layer] && this.sortedChunks[i].visible && (!this.occlusionCheck || this.sortedChunks[i].occlusion_visible)) {
                int list = this.sortedChunks[i].getList(layer);
                if (list >= 0) {
                    this.renderChunks.add(this.sortedChunks[i]);
                    ++count;
                }
            }
        }

        Mob player = this.mc.cameraEntity;
        double xOff = player.xOld + (player.x - player.xOld) * alpha;
        double yOff = player.yOld + (player.y - player.yOld) * alpha;
        double zOff = player.zOld + (player.z - player.zOld) * alpha;
        int lists = 0;

        for (OffsettedRenderList renderList : this.renderLists) {
            renderList.clear();
        }

        for(int i = 0; i < this.renderChunks.size(); ++i) {
            BigChunk chunk = (BigChunk) this.renderChunks.get(i);
            int list = -1;

            for(int l = 0; l < lists; ++l) {
                if (this.renderLists[l].isAt(chunk.xRenderBig, chunk.yRender, chunk.zRenderBig)) {
                    list = l;
                }
            }

            if (list < 0) {
                list = lists++;
                this.renderLists[list].init(chunk.xRenderBig, chunk.yRender, chunk.zRenderBig, xOff, yOff, zOff);
            }

            this.renderLists[list].add(chunk.getList(layer));
        }

        this.renderSameAsLast(layer, alpha);
        return count;
    }
}
