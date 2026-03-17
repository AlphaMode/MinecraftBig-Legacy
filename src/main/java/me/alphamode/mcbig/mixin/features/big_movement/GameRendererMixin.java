package me.alphamode.mcbig.mixin.features.big_movement;

import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.extensions.features.big_movement.BigMobExtension;
import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.world.phys.BigAABB;
import me.alphamode.mcbig.world.phys.BigHitResult;
import me.alphamode.mcbig.world.phys.BigVec3;
import net.minecraft.client.Lighting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gamemode.CreativeMode;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.GameRenderer;
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
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;
import java.util.List;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    private Minecraft mc;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void pick(float partialTick) {
        if (this.mc.cameraEntity != null) {
            if (this.mc.level != null) {
                double pickRange = this.mc.gameMode.getPickRange();
                this.mc.hitResult = this.mc.cameraEntity.pick(pickRange, partialTick);
                double pickDistance = pickRange;
                BigEntityExtension bigCamera = (BigEntityExtension) this.mc.cameraEntity;
                BigVec3 cameraPos = ((BigMobExtension)this.mc.cameraEntity).getBigPos(partialTick);
                if (this.mc.hitResult != null) {
                    pickDistance = ((BigHitResult) this.mc.hitResult).posBig.distanceTo(cameraPos);
                }

                if (this.mc.gameMode instanceof CreativeMode) {
                    pickRange = 32.0;
                    pickDistance = 32.0;
                } else {
                    if (pickDistance > 3.0) {
                        pickDistance = 3.0;
                    }

                    pickRange = pickDistance;
                }

                Vec3 view = this.mc.cameraEntity.getViewVector(partialTick);
                BigVec3 pickVec = cameraPos.add(view.x * pickRange, view.y * pickRange, view.z * pickRange);
                this.hovered = null;
                float range = 1.0F;
                List<Entity> entities = this.mc
                        .level
                        .getEntities(
                                this.mc.cameraEntity, bigCamera.getBigBB().expand(view.x * pickRange, view.y * pickRange, view.z * pickRange).inflate(range, range, range)
                        );
                double var11 = 0.0;

                for(int i = 0; i < entities.size(); ++i) {
                    Entity entity = entities.get(i);
                    if (entity.isPickable()) {
                        float pickRadius = entity.getPickRadius();
                        AABB bb = entity.bb.inflate(pickRadius, pickRadius, pickRadius);
                        BigHitResult hit = (BigHitResult) BigAABB.from(bb).clip(cameraPos, pickVec);
                        if (bb.intersects(cameraPos.toVanilla())) {
                            if (0.0 < var11 || var11 == 0.0) {
                                this.hovered = entity;
                                var11 = 0.0;
                            }
                        } else if (hit != null) {
                            double var18 = cameraPos.distanceTo(hit.posBig);
                            if (var18 < var11 || var11 == 0.0) {
                                this.hovered = entity;
                                var11 = var18;
                            }
                        }
                    }
                }

                if (this.hovered != null && !(this.mc.gameMode instanceof CreativeMode)) {
                    this.mc.hitResult = new BigHitResult(this.hovered);
                }
            }
        }
    }

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

    @Shadow
    private float camTilt;
    @Shadow
    private float camTiltO;


    @Shadow
    private float oldZOff;

    @Shadow
    private float zOff;

    @Shadow
    private float yRotO;

    @Shadow
    private float yRot;

    @Shadow
    private float xRotO;

    @Shadow
    private float xRot;

    @Shadow
    private boolean thickFog;

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void moveCameraToPlayer(float a) {
        Mob player = this.mc.cameraEntity;
        BigDecimal bigAlpha = BigDecimal.valueOf(a);
        BigEntityExtension bigPlayer = (BigEntityExtension) player;
        float eyeHeight = player.heightOffset - 1.62F;
        BigDecimal x = bigPlayer.getXO().add(bigPlayer.getX().subtract(bigPlayer.getXO()).multiply(bigAlpha));
        double y = player.yo + (player.y - player.yo) * a - eyeHeight;
        BigDecimal z = bigPlayer.getZO().add(bigPlayer.getZ().subtract(bigPlayer.getZO().multiply(bigAlpha)));
        GL11.glRotatef(this.camTiltO + (this.camTilt - this.camTiltO) * a, 0.0F, 0.0F, 1.0F);
        if (player.isSleeping()) {
            eyeHeight = (float)(eyeHeight + 1.0);
            GL11.glTranslatef(0.0F, 0.3F, 0.0F);
            if (!this.mc.options.fixedCamera) {
                int t = this.mc.level.getTile(Mth.floor(player.x), Mth.floor(player.y), Mth.floor(player.z));
                if (t == Tile.BED.id) {
                    int data = this.mc.level.getData(Mth.floor(player.x), Mth.floor(player.y), Mth.floor(player.z));
                    int direction = data & 3;
                    GL11.glRotatef(direction * 90, 0.0F, 1.0F, 0.0F);
                }

                GL11.glRotatef(player.yRotO + (player.yRot - player.yRotO) * a + 180.0F, 0.0F, -1.0F, 0.0F);
                GL11.glRotatef(player.xRotO + (player.xRot - player.xRotO) * a, -1.0F, 0.0F, 0.0F);
            }
        } else if (this.mc.options.thirdPersonView) {
            double cameraDist = this.oldZOff + (this.zOff - this.oldZOff) * a;
            if (this.mc.options.fixedCamera) {
                float rotationY = this.yRotO + (this.yRot - this.yRotO) * a;
                float xRot = this.xRotO + (this.xRot - this.xRotO) * a;
                GL11.glTranslatef(0.0F, 0.0F, (float)(-cameraDist));
                GL11.glRotatef(xRot, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(rotationY, 0.0F, 1.0F, 0.0F);
            } else {
                float yRot = player.yRot;
                float xRot = player.xRot;
                double xd = -Mth.sin(yRot / 180.0F * (float) Math.PI) * Mth.cos(xRot / 180.0F * (float) Math.PI) * cameraDist;
                double zd = Mth.cos(yRot / 180.0F * (float) Math.PI) * Mth.cos(xRot / 180.0F * (float) Math.PI) * cameraDist;
                double yd = -Mth.sin(xRot / 180.0F * (float) Math.PI) * cameraDist;

                for (int i = 0; i < 8; i++) {
                    float xo = (i & 1) * 2 - 1;
                    float yo = (i >> 1 & 1) * 2 - 1;
                    float zo = (i >> 2 & 1) * 2 - 1;
                    xo *= 0.1F;
                    yo *= 0.1F;
                    zo *= 0.1F;
                    HitResult hr = this.mc
                            .level
                            .clip(BigVec3.newTemp(x.add(BigMath.decimal(xo)), y + yo, z.add(BigMath.decimal(zo))), BigVec3.newTemp(x.subtract(BigMath.decimal(xd + xo + zo)), y - yd + yo, z.subtract(BigMath.decimal(zd + zo))));
                    if (hr != null) {
                        double dist = ((BigHitResult)hr).posBig.distanceTo(BigVec3.newTemp(x, y, z));
                        if (dist < cameraDist) {
                            cameraDist = dist;
                        }
                    }
                }

                GL11.glRotatef(player.xRot - xRot, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(player.yRot - yRot, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 0.0F, (float)(-cameraDist));
                GL11.glRotatef(yRot - player.yRot, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(xRot - player.xRot, 1.0F, 0.0F, 0.0F);
            }
        } else {
            GL11.glTranslatef(0.0F, 0.0F, -0.1F);
        }

        if (!this.mc.options.fixedCamera) {
            GL11.glRotatef(player.xRotO + (player.xRot - player.xRotO) * a, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(player.yRotO + (player.yRot - player.yRotO) * a + 180.0F, 0.0F, 1.0F, 0.0F);
        }

        GL11.glTranslatef(0.0F, eyeHeight, 0.0F);
//        xo = player.xo + (player.x - player.xo) * a;
//        yo = player.yo + (player.y - player.yo) * a - eyeHeight;
//        zo = player.zo + (player.z - player.zo) * a;
        this.thickFog = false;//this.mc.levelRenderer.hasThickFog(xo, yo, zo, a);
    }

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
        double x = camera.xOld + (camera.x - camera.xOld) * partialTick;
        double y = camera.yOld + (camera.y - camera.yOld) * partialTick;
        double z = camera.zOld + (camera.z - camera.zOld) * partialTick;
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
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
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
