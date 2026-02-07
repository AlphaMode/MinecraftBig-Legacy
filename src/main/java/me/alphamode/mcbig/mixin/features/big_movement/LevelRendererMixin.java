package me.alphamode.mcbig.mixin.features.big_movement;

import me.alphamode.mcbig.client.renderer.BigChunk;
import me.alphamode.mcbig.client.renderer.BigDistanceChunkSorter;
import me.alphamode.mcbig.extensions.BigLevelListenerExtension;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.extensions.features.big_movement.BigLevelRendererExtension;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.world.phys.BigAABB;
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
import net.minecraft.world.level.LevelListener;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitType;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements BigLevelListenerExtension, LevelListener, BigLevelRendererExtension {
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

    @Shadow private TileRenderer tileRenderer;
    @Shadow public float destroyProgress;
    BigDecimal xOldBig = BigDecimal.valueOf(-9999.0);
    BigDecimal zOldBig = BigDecimal.valueOf(-9999.0);

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void setLevel(Level level) {
        if (this.level != null) {
            this.level.removeListener(this);
        }

        this.xOldBig = BigDecimal.valueOf(-9999.0);
        this.yOld = -9999.0;
        this.zOldBig = BigDecimal.valueOf(-9999.0);
        EntityRenderDispatcher.INSTANCE.setLevel(level);
        this.level = level;
        this.tileRenderer = new TileRenderer(level);
        if (level != null) {
            level.addListener(this);
            this.allChanged();
        }
    }

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

        for(int var4 = 0; var4 < this.dirtyChunks.size(); ++var4) {
            ((Chunk)this.dirtyChunks.get(var4)).dirty = false;
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
            Mob camera = this.mc.cameraEntity;
            if (camera != null) {
                this.resortChunks(BigMath.floor(((BigEntityExtension) camera).getX()), Mth.floor(camera.y), BigMath.floor(((BigEntityExtension) camera).getZ()));
                Arrays.sort((BigChunk[]) this.sortedChunks, new BigDistanceChunkSorter(camera));
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
        BigEntityExtension bigCamera = (BigEntityExtension) camera;
        for(int i = 0; i < 10; ++i) {
            this.chunkFixOffs = (this.chunkFixOffs + 1) % this.chunks.length;
            Chunk c = this.chunks[this.chunkFixOffs];
            if (c.dirty && !this.dirtyChunks.contains(c)) {
                this.dirtyChunks.add(c);
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

        BigDecimal bigAlpha = new BigDecimal(alpha);
        BigDecimal xOff = bigCamera.getXOld().add((bigCamera.getX().subtract(bigCamera.getXOld()))).multiply(bigAlpha);
        double yOff = camera.yOld + (camera.y - camera.yOld) * alpha;
        BigDecimal zOff = bigCamera.getZOld().add((bigCamera.getZ().subtract(bigCamera.getZOld()))).multiply(bigAlpha);
        double xd = bigCamera.getX().subtract(this.xOldBig).doubleValue();
        double yd = camera.y - this.yOld;
        double zd = bigCamera.getZ().subtract(this.zOldBig).doubleValue();
        if (xd * xd + yd * yd + zd * zd > 16.0) {
            this.xOldBig = bigCamera.getX();
            this.yOld = camera.y;
            this.zOldBig = bigCamera.getZ();
            this.resortChunks(BigMath.floor(bigCamera.getX()), Mth.floor(camera.y), BigMath.floor(bigCamera.getZ()));
            Arrays.sort((BigChunk[]) this.sortedChunks, new BigDistanceChunkSorter(camera));
        }

        Lighting.turnOff();
        int count = 0;
        if (this.occlusionCheck && this.mc.options.advancedOpengl && !this.mc.options.anaglyph3d && layer == 0) {
            int from = 0;
            int to = 16;
            this.checkQueryResults(from, to);

            for(int ix = from; ix < to; ++ix) {
                this.sortedChunks[ix].occlusion_visible = true;
            }

            count += this.renderChunks(from, to, layer, alpha);

            do {
                from = to;
                to *= 2;
                if (to > this.sortedChunks.length) {
                    to = this.sortedChunks.length;
                }

                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_FOG);
                GL11.glColorMask(false, false, false, false);
                GL11.glDepthMask(false);
                this.checkQueryResults(from, to);
                GL11.glPushMatrix();
                float xo = 0.0F;
                float yo = 0.0F;
                float zo = 0.0F;

                for(int ix = from; ix < to; ++ix) {
                    if (this.sortedChunks[ix].isEmpty()) {
                        this.sortedChunks[ix].visible = false;
                    } else {
                        if (!this.sortedChunks[ix].visible) {
                            this.sortedChunks[ix].occlusion_visible = true;
                        }

                        if (this.sortedChunks[ix].visible && !this.sortedChunks[ix].occlusion_querying) {
                            float dist = Mth.sqrt(this.sortedChunks[ix].distanceToSqr(camera));
                            int frequency = (int)(1.0F + dist / 128.0F);
                            if (this.ticks % frequency == ix % frequency) {
                                BigChunk chunk = (BigChunk) this.sortedChunks[ix];
                                float xt = (float)(new BigDecimal(chunk.xRenderBig).subtract(xOff).floatValue());
                                float yt = (float)((double)chunk.yRender - yOff);
                                float zt = (float)(new BigDecimal(chunk.zRenderBig).subtract(zOff).floatValue());
                                float xdd = xt - xo;
                                float ydd = yt - yo;
                                float zdd = zt - zo;
                                if (xdd != 0.0F || ydd != 0.0F || zdd != 0.0F) {
                                    GL11.glTranslatef(xdd, ydd, zdd);
                                    xo += xdd;
                                    yo += ydd;
                                    zo += zdd;
                                }

                                ARBOcclusionQuery.glBeginQueryARB(35092, this.sortedChunks[ix].occlusion_id);
                                this.sortedChunks[ix].renderBB();
                                ARBOcclusionQuery.glEndQueryARB(35092);
                                this.sortedChunks[ix].occlusion_querying = true;
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
                count += this.renderChunks(from, to, layer, alpha);
            } while(to < this.sortedChunks.length);
        } else {
            count += this.renderChunks(0, this.sortedChunks.length, layer, alpha);
        }

        return count;
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
    public void renderHit(Player p, HitResult r, int mode, ItemInstance item, float a) {
        BigHitResult result = (BigHitResult) r;
        BigEntityExtension player = (BigEntityExtension) p;
        Tesselator t = Tesselator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, (Mth.sin((float)System.currentTimeMillis() / 100.0F) * 0.2F + 0.4F) * 0.5F);
        if (mode == 0) {
            if (this.destroyProgress > 0.0F) {
                GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR);
                int tex = this.textures.loadTexture("/terrain.png");
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
                GL11.glPushMatrix();
                int tileId = this.level.getTile(result.xBig, result.y, result.zBig);
                Tile tt = tileId > 0 ? Tile.tiles[tileId] : null;
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glPolygonOffset(-3.0F, -3.0F);
                GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                BigDecimal bigA = new BigDecimal(a);
                BigDecimal xOff = player.getXOld().add((player.getX().subtract(player.getXOld())).multiply(bigA));
                double yOff = p.yOld + (p.y - p.yOld) * (double)a;
                BigDecimal zOff = player.getZOld().add((player.getZ().subtract(player.getZOld())).multiply(bigA));
                if (tt == null) {
                    tt = Tile.STONE;
                }

                GL11.glEnable(GL11.GL_ALPHA_TEST);
                t.begin();
                t.offset(xOff.negate(), -yOff, zOff.negate());
                t.noColor();
                this.tileRenderer.tesselateInWorld(tt, result.xBig, result.y, result.zBig, 240 + (int)(this.destroyProgress * 10.0F));
                t.end();
                t.offset(BigDecimal.ZERO, 0.0, BigDecimal.ZERO);
                t.offset(0.0, 0.0, 0.0);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glPolygonOffset(0.0F, 0.0F);
                GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glDepthMask(true);
                GL11.glPopMatrix();
            }
        } else if (item != null) {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            float c = Mth.sin((float)System.currentTimeMillis() / 100.0F) * 0.2F + 0.8F;
            GL11.glColor4f(c, c, c, Mth.sin((float)System.currentTimeMillis() / 200.0F) * 0.2F + 0.5F);
            int tex = this.textures.loadTexture("/terrain.png");
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
            int xt = result.x;
            int yt = result.y;
            int zt = result.z;
            if (result.face == 0) {
                --yt;
            }

            if (result.face == 1) {
                ++yt;
            }

            if (result.face == 2) {
                --zt;
            }

            if (result.face == 3) {
                ++zt;
            }

            if (result.face == 4) {
                --xt;
            }

            if (result.face == 5) {
                ++xt;
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

//    /**
//     * @author
//     * @reason
//     */
//    @Overwrite
//    public void renderHitOutline(Player player, HitResult r, int mode, ItemInstance inventoryItem, float a) {
//        BigHitResult result = (BigHitResult) r;
//        if (mode == 0 && result.hitType == HitType.TILE) {
//            GL11.glEnable(GL11.GL_BLEND);
//            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
//            GL11.glLineWidth(2.0F);
//            GL11.glDisable(GL11.GL_TEXTURE_2D);
//            GL11.glDepthMask(false);
//            float ss = 0.002F;
//            int tile = this.level.getTile(result.xBig, result.y, result.zBig);
//            if (tile > 0) {
//                Tile.tiles[tile].updateShape(this.level, result.xBig, result.y, result.zBig);
//                BigDecimal bigA = new BigDecimal(a);
//                BigEntityExtension bigPlayer = (BigEntityExtension) player;
//                BigDecimal xc = bigPlayer.getXOld().add(bigPlayer.getX().subtract(bigPlayer.getXOld())).multiply(bigA);
//                double yc = player.yOld + (player.y - player.yOld) * (double)a;
//                BigDecimal zc = bigPlayer.getZOld().add(bigPlayer.getZ().subtract(bigPlayer.getZOld())).multiply(bigA);
//                this.render(
//                        Tile.tiles[tile].getTileBigAABB(this.level, result.xBig, result.y, result.zBig).inflate(ss, ss, ss).offset(xc.negate(), -yc, zc.negate())
//                );
//            }
//
//            GL11.glDepthMask(true);
//            GL11.glEnable(GL11.GL_TEXTURE_2D);
//            GL11.glDisable(GL11.GL_BLEND);
//        }
//    }

    @Override
    public void render(BigAABB aabb) {
        Tesselator t = Tesselator.instance;
        t.begin(3);
        t.vertex(aabb.x0, aabb.y0, aabb.z0);
        t.vertex(aabb.x1, aabb.y0, aabb.z0);
        t.vertex(aabb.x1, aabb.y0, aabb.z1);
        t.vertex(aabb.x0, aabb.y0, aabb.z1);
        t.vertex(aabb.x0, aabb.y0, aabb.z0);
        t.end();
        t.begin(3);
        t.vertex(aabb.x0, aabb.y1, aabb.z0);
        t.vertex(aabb.x1, aabb.y1, aabb.z0);
        t.vertex(aabb.x1, aabb.y1, aabb.z1);
        t.vertex(aabb.x0, aabb.y1, aabb.z1);
        t.vertex(aabb.x0, aabb.y1, aabb.z0);
        t.end();
        t.begin(1);
        t.vertex(aabb.x0, aabb.y0, aabb.z0);
        t.vertex(aabb.x0, aabb.y1, aabb.z0);
        t.vertex(aabb.x1, aabb.y0, aabb.z0);
        t.vertex(aabb.x1, aabb.y1, aabb.z0);
        t.vertex(aabb.x1, aabb.y0, aabb.z1);
        t.vertex(aabb.x1, aabb.y1, aabb.z1);
        t.vertex(aabb.x0, aabb.y0, aabb.z1);
        t.vertex(aabb.x0, aabb.y1, aabb.z1);
        t.end();
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
        BigEntityExtension bigPlayer = (BigEntityExtension) player;
        BigDecimal a = BigDecimal.valueOf(alpha);
        BigDecimal xOff = bigPlayer.getXOld().add((bigPlayer.getX().subtract(bigPlayer.getXOld())).multiply(a));
        double yOff = player.yOld + (player.y - player.yOld) * alpha;
        BigDecimal zOff = bigPlayer.getZOld().add((bigPlayer.getZ().subtract(bigPlayer.getZOld())).multiply(a));
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
