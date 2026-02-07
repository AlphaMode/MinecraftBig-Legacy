package me.alphamode.mcbig.mixin.features.big_movement;

import net.minecraft.client.Lighting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GlConst;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.FrustumCuller;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.ChunkCache;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.material.Material;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    private Minecraft mc;

    @Shadow
    public abstract void pick(float partialTick);

    @Shadow
    public static int currentRenderLayer;

    @Shadow
    protected abstract void setupClearColor(float partialTicks);

    @Shadow
    protected abstract void setupCamera(float partialTick, int nanoSeconds);

    @Shadow
    protected abstract void setupFog(int layer, float partialTick);

    @Shadow
    protected abstract void renderSnowAndRain(float partialTick);

    @Shadow
    private Entity hovered;

    @Shadow
    private double zoom;

    @Shadow protected abstract void renderItemInHand(float partialTick, int renderLayer);

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void render(float partialTick, long nanoTime) {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        if (this.mc.cameraEntity == null) {
            this.mc.cameraEntity = this.mc.player;
        }

        pick(partialTick);
        Mob camera = this.mc.cameraEntity;
        LevelRenderer levelRenderer = this.mc.levelRenderer;
        ParticleEngine particleEngine = this.mc.particleEngine;
        double x = camera.xOld + (camera.x - camera.xOld) * (double) partialTick;
        double y = camera.yOld + (camera.y - camera.yOld) * (double) partialTick;
        double z = camera.zOld + (camera.z - camera.zOld) * (double) partialTick;
        ChunkSource source = this.mc.level.getChunkSource();
        if (source instanceof ChunkCache) {
            ChunkCache cache = (ChunkCache) source;
            int xc = Mth.floor((float) ((int) x)) >> 4;
            int zc = Mth.floor((float) ((int) z)) >> 4;
            cache.centerOn(xc, zc);
        }

        for (int renderLayer = 0; renderLayer < 2; ++renderLayer) {
            if (this.mc.options.anaglyph3d) {
                currentRenderLayer = renderLayer;
                if (currentRenderLayer == 0) {
                    GL11.glColorMask(false, true, true, false);
                } else {
                    GL11.glColorMask(true, false, false, false);
                }
            }

            GL11.glViewport(0, 0, this.mc.width, this.mc.height);
            setupClearColor(partialTick);
            GL11.glClear(GlConst.GL_COLOR_AND_DEPTH_BUFFER_BIT);
            GL11.glEnable(GL11.GL_CULL_FACE);
            setupCamera(partialTick, renderLayer);
            Frustum.getFrustum();
            if (this.mc.options.viewDistance < 2) {
                setupFog(-1, partialTick);
                levelRenderer.renderSky(partialTick);
            }

            GL11.glEnable(GL11.GL_FOG);
            this.setupFog(1, partialTick);
            if (this.mc.options.ao) {
                GL11.glShadeModel(GL11.GL_SMOOTH);
            }

            FrustumCuller culler = new FrustumCuller();
            culler.prepare(x, y, z);
            this.mc.levelRenderer.cull(culler, partialTick);
            if (renderLayer == 0) {
                while (!this.mc.levelRenderer.updateDirtyChunks(camera, false) && nanoTime != 0L) {
                    long var20 = nanoTime - System.nanoTime();
                    if (var20 < 0L || var20 > 1000000000L) {
                        break;
                    }
                }
            }

            setupFog(0, partialTick);
            GL11.glEnable(GL11.GL_FOG);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.textures.loadTexture("/terrain.png"));
            Lighting.turnOff();
            levelRenderer.render(camera, 0, partialTick);
            GL11.glShadeModel(GL11.GL_FLAT);
            Lighting.turnOn();
            levelRenderer.renderEntities(camera.getPos(partialTick), culler, partialTick);
            particleEngine.renderLit(camera, partialTick);
            Lighting.turnOff();
            setupFog(0, partialTick);
            particleEngine.render(camera, partialTick);
            if (this.mc.hitResult != null && camera.isUnderLiquid(Material.WATER) && camera instanceof Player) {
                Player player = (Player) camera;
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                levelRenderer.renderHit(player, this.mc.hitResult, 0, player.inventory.getSelected(), partialTick);
                levelRenderer.renderHitOutline(player, this.mc.hitResult, 0, player.inventory.getSelected(), partialTick);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
            }

            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            setupFog(0, partialTick);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.textures.loadTexture("/terrain.png"));
            if (this.mc.options.fancyGraphics) {
                if (this.mc.options.ao) {
                    GL11.glShadeModel(GL11.GL_SMOOTH);
                }

                GL11.glColorMask(false, false, false, false);
                int renderedChunks = levelRenderer.render(camera, 1, partialTick);
                if (this.mc.options.anaglyph3d) {
                    if (currentRenderLayer == 0) {
                        GL11.glColorMask(false, true, true, true);
                    } else {
                        GL11.glColorMask(true, false, false, true);
                    }
                } else {
                    GL11.glColorMask(true, true, true, true);
                }

                if (renderedChunks > 0) {
                    levelRenderer.renderSameAsLast(1, partialTick);
                }

                GL11.glShadeModel(GL11.GL_FLAT);
            } else {
                levelRenderer.render(camera, 1, partialTick);
            }

            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
            if (this.zoom == 1.0 && camera instanceof Player && this.mc.hitResult != null && !camera.isUnderLiquid(Material.WATER)) {
                Player player = (Player) camera;
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                levelRenderer.renderHit(player, this.mc.hitResult, 0, player.inventory.getSelected(), partialTick);
                levelRenderer.renderHitOutline(player, this.mc.hitResult, 0, player.inventory.getSelected(), partialTick);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
            }

            renderSnowAndRain(partialTick);
            GL11.glDisable(GL11.GL_FOG);
            if (this.hovered != null) {
            }

            this.setupFog(0, partialTick);
            GL11.glEnable(GL11.GL_FOG);
            levelRenderer.renderClouds(partialTick);
            GL11.glDisable(GL11.GL_FOG);
            this.setupFog(1, partialTick);
            if (this.zoom == 1.0) {
                GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
                renderItemInHand(partialTick, renderLayer);
            }

            if (!this.mc.options.anaglyph3d) {
                return;
            }
        }

        GL11.glColorMask(true, true, true, false);
    }
}
