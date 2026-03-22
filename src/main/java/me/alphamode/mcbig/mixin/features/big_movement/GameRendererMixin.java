package me.alphamode.mcbig.mixin.features.big_movement;

import me.alphamode.mcbig.extensions.features.big_movement.BigCullerExtension;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.extensions.features.big_movement.BigMobExtension;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.world.phys.BigAABB;
import me.alphamode.mcbig.world.phys.BigHitResult;
import me.alphamode.mcbig.world.phys.BigVec3;
import net.minecraft.client.Lighting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gamemode.CreativeMode;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.FrustumCuller;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
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
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    private Minecraft mc;

    @Shadow
    public static int currentRenderLayer;

    @Shadow
    protected abstract void setupClearColor(float partialTicks);

    @Shadow
    protected abstract void setupCamera(float partialTick, int nanoSeconds);

    @Shadow
    protected abstract void setupFog(int layer, float partialTick);

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

    @Shadow
    private float fogBr;

    @Shadow
    private float fogBrO;

    @Shadow
    private float oldFov;

    @Shadow
    private float fov;

    @Shadow
    private int tick;

    @Shadow
    public ItemInHandRenderer itemInHandRenderer;

    @Shadow
    private Random random;

    @Shadow
    private int rainSoundTime;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void pick(float partialTick) {
        if (this.mc.cameraEntity != null) {
            if (this.mc.level != null) {
                double range = this.mc.gameMode.getPickRange();

                this.mc.hitResult = this.mc.cameraEntity.pick(range, partialTick);

                double dist = range;
                BigEntityExtension bigCamera = (BigEntityExtension) this.mc.cameraEntity;
                BigVec3 from = ((BigMobExtension)this.mc.cameraEntity).getBigPos(partialTick);
                if (this.mc.hitResult != null) {
                    dist = ((BigHitResult) this.mc.hitResult).posBig.distanceTo(from);
                }

                if (this.mc.gameMode instanceof CreativeMode) {
                    range = 32.0;
                    dist = 32.0;
                } else {
                    if (dist > 3.0) {
                        dist = 3.0;
                    }

                    range = dist;
                }

                Vec3 pickDirection = this.mc.cameraEntity.getViewVector(partialTick);
                BigVec3 to = from.add(pickDirection.x * range, pickDirection.y * range, pickDirection.z * range);
                this.hovered = null;
                float g = 1.0F;
                List<Entity> entities = this.mc.level.getEntities(
                        this.mc.cameraEntity,
                        bigCamera.getBigBB().expand(pickDirection.x * range, pickDirection.y * range, pickDirection.z * range)
                                .inflate(g, g, g)
                );
                double nearest = 0.0;

                for(int i = 0; i < entities.size(); ++i) {
                    Entity e = entities.get(i);
                    if (e.isPickable()) {
                        float rr = e.getPickRadius();
                        AABB bb = e.bb.inflate(rr, rr, rr);
                        BigHitResult p = (BigHitResult) BigAABB.from(bb).clip(from, to);
                        if (bb.intersects(from.toVanilla())) {
                            if (0.0 < nearest || nearest == 0.0) {
                                this.hovered = e;
                                nearest = 0.0;
                            }
                        } else if (p != null) {
                            double dd = from.distanceTo(p.posBig);
                            if (dd < nearest || nearest == 0.0) {
                                this.hovered = e;
                                nearest = dd;
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

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tick() {
        this.fogBrO = this.fogBr;
        this.oldZOff = this.zOff;
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;
        this.oldFov = this.fov;
        this.camTiltO = this.camTilt;
        if (this.mc.cameraEntity == null) {
            this.mc.cameraEntity = this.mc.player;
        }

        float br = this.mc.level.getBrightness(BigMath.floor(this.mc.cameraEntity.x), Mth.floor(this.mc.cameraEntity.y), BigMath.floor(this.mc.cameraEntity.z));
        float whiteness = (float)(3 - this.mc.options.viewDistance) / 3.0F;
        float fogBrT = br * (1.0F - whiteness) + whiteness;
        this.fogBr += (fogBrT - this.fogBr) * 0.1F;
        ++this.tick;
        this.itemInHandRenderer.tick();
        this.tickRain();
    }

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
        BigDecimal z = bigPlayer.getZO().add(bigPlayer.getZ().subtract(bigPlayer.getZO()).multiply(bigAlpha));
        GL11.glRotatef(this.camTiltO + (this.camTilt - this.camTiltO) * a, 0.0F, 0.0F, 1.0F);
        if (player.isSleeping()) {
            eyeHeight = (float)(eyeHeight + 1.0);
            GL11.glTranslatef(0.0F, 0.3F, 0.0F);
            if (!this.mc.options.fixedCamera) {
                int t = this.mc.level.getTile(BigMath.floor(bigPlayer.getX()), Mth.floor(player.y), BigMath.floor(bigPlayer.getZ()));
                if (t == Tile.BED.id) {
                    int data = this.mc.level.getData(BigMath.floor(bigPlayer.getX()), Mth.floor(player.y), BigMath.floor(bigPlayer.getZ()));
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
                    HitResult hr = this.mc.level.clip(
                        BigVec3.newTemp(BigMath.addD(x, xo), y + yo, BigMath.addD(z, zo)),
                        BigVec3.newTemp(BigMath.subD(x, xd + xo + zo), y - yd + yo, BigMath.subD(z, zd + zo))
                    );
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
    public void render(float a, long nanoTime) {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        if (this.mc.cameraEntity == null) {
            this.mc.cameraEntity = this.mc.player;
        }

        pick(a);
        Mob camera = this.mc.cameraEntity;
        BigEntityExtension cameraBig = (BigEntityExtension) this.mc.cameraEntity;
        LevelRenderer levelRenderer = this.mc.levelRenderer;
        ParticleEngine particleEngine = this.mc.particleEngine;
        BigDecimal aBig = BigDecimal.valueOf(a);
        BigDecimal x = cameraBig.getXOld().add(cameraBig.getX().subtract(cameraBig.getXOld()).multiply(aBig));
        double y = camera.yOld + (camera.y - camera.yOld) * a;
        BigDecimal z = cameraBig.getZOld().add(cameraBig.getZ().subtract(cameraBig.getZOld()).multiply(aBig));
        ChunkSource source = this.mc.level.getChunkSource();
        if (source instanceof ChunkCache) {
            ChunkCache cache = (ChunkCache) source;
            int xc = Mth.floor((float) ((int) x.doubleValue())) >> 4;
            int zc = Mth.floor((float) ((int) z.doubleValue())) >> 4;
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
            setupClearColor(a);
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
            GL11.glEnable(GL11.GL_CULL_FACE);
            setupCamera(a, renderLayer);
            Frustum.getFrustum();
            if (this.mc.options.viewDistance < 2) {
                setupFog(-1, a);
                levelRenderer.renderSky(a);
            }

            GL11.glEnable(GL11.GL_FOG);
            this.setupFog(1, a);
            if (this.mc.options.ao) {
                GL11.glShadeModel(GL11.GL_SMOOTH);
            }

            FrustumCuller culler = new FrustumCuller();
            ((BigCullerExtension)culler).prepare(x, y, z);
            this.mc.levelRenderer.cull(culler, a);
            if (renderLayer == 0) {
                while (!this.mc.levelRenderer.updateDirtyChunks(camera, false) && nanoTime != 0L) {
                    long var20 = nanoTime - System.nanoTime();
                    if (var20 < 0L || var20 > 1000000000L) {
                        break;
                    }
                }
            }

            setupFog(0, a);
            GL11.glEnable(GL11.GL_FOG);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.textures.loadTexture("/terrain.png"));
            Lighting.turnOff();
            levelRenderer.render(camera, 0, a);
            GL11.glShadeModel(GL11.GL_FLAT);
            Lighting.turnOn();
            levelRenderer.renderEntities(camera.getPos(a), culler, a);
            particleEngine.renderLit(camera, a);
            Lighting.turnOff();
            setupFog(0, a);
            particleEngine.render(camera, a);
            if (this.mc.hitResult != null && camera.isUnderLiquid(Material.WATER) && camera instanceof Player) {
                Player player = (Player) camera;
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                levelRenderer.renderHit(player, this.mc.hitResult, 0, player.inventory.getSelected(), a);
                levelRenderer.renderHitOutline(player, this.mc.hitResult, 0, player.inventory.getSelected(), a);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
            }

            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            setupFog(0, a);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.textures.loadTexture("/terrain.png"));
            if (this.mc.options.fancyGraphics) {
                if (this.mc.options.ao) {
                    GL11.glShadeModel(GL11.GL_SMOOTH);
                }

                GL11.glColorMask(false, false, false, false);
                int renderedChunks = levelRenderer.render(camera, 1, a);
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
                    levelRenderer.renderSameAsLast(1, a);
                }

                GL11.glShadeModel(GL11.GL_FLAT);
            } else {
                levelRenderer.render(camera, 1, a);
            }

            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
            if (this.zoom == 1.0 && camera instanceof Player && this.mc.hitResult != null && !camera.isUnderLiquid(Material.WATER)) {
                Player player = (Player) camera;
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                levelRenderer.renderHit(player, this.mc.hitResult, 0, player.inventory.getSelected(), a);
                levelRenderer.renderHitOutline(player, this.mc.hitResult, 0, player.inventory.getSelected(), a);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
            }

            renderSnowAndRain(a);
            GL11.glDisable(GL11.GL_FOG);
            if (this.hovered != null) {
            }

            this.setupFog(0, a);
            GL11.glEnable(GL11.GL_FOG);
            levelRenderer.renderClouds(a);
            GL11.glDisable(GL11.GL_FOG);
            this.setupFog(1, a);
            if (this.zoom == 1.0) {
                GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
                renderItemInHand(a, renderLayer);
            }

            if (!this.mc.options.anaglyph3d) {
                return;
            }
        }

        GL11.glColorMask(true, true, true, false);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void tickRain() {
        float rainLevel = this.mc.level.getRainLevel(1.0F);
        if (!this.mc.options.fancyGraphics)
            rainLevel /= 2.0F;

        if (rainLevel == 0.0F)
            return;

        this.random.setSeed(this.tick * 312987231L);
        Mob player = this.mc.cameraEntity;
        Level level = this.mc.level;

        BigInteger x0 = BigMath.floor(player.x);
        int y0 = Mth.floor(player.y);
        BigInteger z0 = BigMath.floor(player.z);

        int r = 10;

        double rainPosX = 0.0;
        double rainPosY = 0.0;
        double rainPosZ = 0.0;
        int rainPosSamples = 0;

        int rainCount = (int)(100.0F * rainLevel * rainLevel);
        for (int i = 0; i < rainCount; i++) {
            BigInteger x = x0.add(BigInteger.valueOf(this.random.nextInt(r) - this.random.nextInt(r)));
            BigInteger z = z0.add(BigInteger.valueOf(this.random.nextInt(r) - this.random.nextInt(r)));
            int y = level.getTopSolidBlock(x, z);
            int t = level.getTile(x, y - 1, z);
            if (y <= y0 + r && y >= y0 - r && level.getBiomeSource().getBiome(x, z).hasRain()) {
                float xa = this.random.nextFloat();
                float za = this.random.nextFloat();
                if (t > 0) {
                    if (Tile.tiles[t].material == Material.LAVA) {
                        this.mc.particleEngine.add(new SmokeParticle(level, x.doubleValue() + xa, y + 0.1F - Tile.tiles[t].yy0, z.doubleValue() + za, 0.0, 0.0, 0.0));
                    } else {
                        if (this.random.nextInt(++rainPosSamples) == 0) {
                            rainPosX = x.doubleValue() + xa;
                            rainPosY = y + 0.1F - Tile.tiles[t].yy0;
                            rainPosZ = z.doubleValue() + za;
                        }

                        this.mc.particleEngine.add(new WaterDropParticle(level, x.doubleValue() + xa, y + 0.1F - Tile.tiles[t].yy0, z.doubleValue() + za));
                    }
                }
            }
        }

        if (rainPosSamples > 0 && this.random.nextInt(3) < this.rainSoundTime++) {
            this.rainSoundTime = 0;
            if (rainPosY > player.y + 1.0 && level.getTopSolidBlock(Mth.floor(player.x), Mth.floor(player.z)) > Mth.floor(player.y)) {
                this.mc.level.playSound(rainPosX, rainPosY, rainPosZ, "ambient.weather.rain", 0.1F, 0.5F);
            } else {
                this.mc.level.playSound(rainPosX, rainPosY, rainPosZ, "ambient.weather.rain", 0.2F, 1.0F);
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void renderSnowAndRain(float a) {
        float rainLevel = this.mc.level.getRainLevel(a);
        if (!(rainLevel <= 0.0F)) {
            Mob player = this.mc.cameraEntity;
            BigEntityExtension playerBig = (BigEntityExtension) player;
            Level level = this.mc.level;
            BigInteger x0 = BigMath.floor(playerBig.getX());
            int y0 = Mth.floor(player.y);
            BigInteger z0 = BigMath.floor(playerBig.getZ());
            Tesselator t = Tesselator.instance;
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.01F);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.textures.loadTexture("/environment/snow.png"));
            BigDecimal bigA = new BigDecimal(a);
            BigDecimal xo = playerBig.getXOld().add(playerBig.getX().subtract(playerBig.getXOld()).multiply(bigA)).negate();
            double yo = player.yOld + (player.y - player.yOld) * a;
            BigDecimal zo = playerBig.getZOld().add(playerBig.getZ().subtract(playerBig.getZOld()).multiply(bigA)).negate();
            int yMin = Mth.floor(yo);
            int r = 5;
            if (this.mc.options.fancyGraphics) {
                r = 10;
            }
            BigInteger rBig = BigInteger.valueOf(r);

            Biome[] biomes = level.getBiomeSource().getBiomeBlock(x0.subtract(rBig), z0.subtract(rBig), r * 2 + 1, r * 2 + 1);
            int var18 = 0;

            for (BigInteger x = x0.subtract(rBig); x.compareTo(x0.add(rBig)) <= 0; x = x.add(BigInteger.ONE)) {
                BigDecimal xD = new BigDecimal(x);
                for (BigInteger z = z0.subtract(rBig); z.compareTo(z0.add(rBig)) <= 0; z = z.add(BigInteger.ONE)) {
                    Biome b = biomes[var18++];
                    if (b.hasPrecipitation()) {
                        int floor = level.getTopSolidBlock(x, z);
                        if (floor < 0) {
                            floor = 0;
                        }

                        int yl = floor;
                        if (floor < yMin) {
                            yl = yMin;
                        }

                        int yy0 = y0 - r;
                        int yy1 = y0 + r;
                        if (yy0 < floor) {
                            yy0 = floor;
                        }

                        if (yy1 < floor) {
                            yy1 = floor;
                        }

                        float s = 1;
                        if (yy0 != yy1) {
                            this.random.setSeed(x.longValue() * x.longValue() * 3121 + x.longValue() * 45238971 + z.longValue() * z.longValue() * 418711 + z.longValue() * 13761);
                            float time = this.tick + a;
                            float ra = ((this.tick & 511) + a) / 512.0F;
                            float uo = this.random.nextFloat() + time * 0.01F * (float)this.random.nextGaussian();
                            float vo = this.random.nextFloat() + time * (float)this.random.nextGaussian() * 0.001F;
                            double xd = x.doubleValue() + 0.5F - player.x;
                            double zd = z.doubleValue() + 0.5F - player.z;
                            float dd = Mth.sqrt(xd * xd + zd * zd) / r;
                            t.begin();
                            float br = level.getBrightness(x, yl, z);
                            GL11.glColor4f(br, br, br, ((1.0F - dd * dd) * 0.3F + 0.5F) * rainLevel);
                            BigDecimal zD = new BigDecimal(z);
                            t.offset(xo, -yo, zo);
                            BigDecimal xPlusOne = xD.add(BigDecimal.ONE);
                            BigDecimal xPlusPointFive = xD.add(BigConstants.POINT_FIVE);
                            BigDecimal zPlusOne = zD.add(BigDecimal.ONE);
                            BigDecimal zPlusPointFive = zD.add(BigConstants.POINT_FIVE);
                            t.vertexUV(xD, yy0, zPlusPointFive, 0.0F * s + uo, yy0 * s / 4.0F + ra * s + vo);
                            t.vertexUV(xPlusOne, yy0, zPlusPointFive, 1.0F * s + uo, yy0 * s / 4.0F + ra * s + vo);
                            t.vertexUV(xPlusOne, yy1, zPlusPointFive, 1.0F * s + uo, yy1 * s / 4.0F + ra * s + vo);
                            t.vertexUV(xD, yy1, zPlusPointFive, 0.0F * s + uo, yy1 * s / 4.0F + ra * s + vo);
                            t.vertexUV(xPlusPointFive, yy0, zD, 0.0F * s + uo, yy0 * s / 4.0F + ra * s + vo);
                            t.vertexUV(xPlusPointFive, yy0, zPlusOne, 1.0F * s + uo, yy0 * s / 4.0F + ra * s + vo);
                            t.vertexUV(xPlusPointFive, yy1, zPlusOne, 1.0F * s + uo, yy1 * s / 4.0F + ra * s + vo);
                            t.vertexUV(xPlusPointFive, yy1, zD, 0.0F * s + uo, yy1 * s / 4.0F + ra * s + vo);
                            t.offset(0.0, 0.0, 0.0);
                            t.end();
                        }
                    }
                }
            }

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.textures.loadTexture("/environment/rain.png"));
            if (this.mc.options.fancyGraphics) {
                r = 10;
            }

            var18 = 0;

            BigInteger bigR = BigInteger.valueOf(r);
            for (BigInteger x = x0.subtract(bigR); x.compareTo(x0.add(bigR)) <= 0; x = x.add(BigInteger.ONE)) {
                BigDecimal xD = new BigDecimal(x);
                for (BigInteger z = z0.subtract(bigR); z.compareTo(z0.add(bigR)) <= 0; z = z.add(BigInteger.ONE)) {
                    Biome b = biomes[var18++];
                    if (b.hasRain()) {
                        int floor = level.getTopSolidBlock(x, z);
                        int yy0 = y0 - r;
                        int yy1 = y0 + r;
                        if (yy0 < floor) {
                            yy0 = floor;
                        }

                        if (yy1 < floor) {
                            yy1 = floor;
                        }

                        float o = 1.0F;
                        if (yy0 != yy1) {
                            long longX = x.longValue();
                            long longZ = z.longValue();
                            this.random.setSeed(longX * longX * 3121 + longX * 45238971 + longZ * longZ * 418711 + longZ * 13761);
                            float ra = ((this.tick + longX * longX * 3121 + longX * 45238971 + longZ * longZ * 418711 + longZ * 13761 & 31) + a)
                                    / 32.0F
                                    * (3.0F + this.random.nextFloat());
                            BigDecimal zD = new BigDecimal(z);
                            double xd = xD.add(BigConstants.POINT_FIVE).subtract(playerBig.getX()).doubleValue();
                            double zd = zD.add(BigConstants.POINT_FIVE).subtract(playerBig.getZ()).doubleValue();
                            float dd = Mth.sqrt(xd * xd + zd * zd) / r;
                            t.begin();
                            float br = level.getBrightness(x, 128, z) * 0.85F + 0.15F;
                            GL11.glColor4f(br, br, br, ((1.0F - dd * dd) * 0.5F + 0.5F) * rainLevel);
                            t.offset(xo, -yo, zo);
                            BigDecimal xPlusOne = xD.add(BigDecimal.ONE);
                            BigDecimal xPlusPointFive = xD.add(BigConstants.POINT_FIVE);
                            BigDecimal zPlusOne = zD.add(BigDecimal.ONE);
                            BigDecimal zPlusPointFive = zD.add(BigConstants.POINT_FIVE);
                            t.vertexUV(xD, yy0, zPlusPointFive, 0.0F * o, yy0 * o / 4.0F + ra * o);
                            t.vertexUV(xPlusOne, yy0, zPlusPointFive, 1.0F * o, yy0 * o / 4.0F + ra * o);
                            t.vertexUV(xPlusOne, yy1, zPlusPointFive, 1.0F * o, yy1 * o / 4.0F + ra * o);
                            t.vertexUV(xD, yy1, zPlusPointFive, 0.0F * o, yy1 * o / 4.0F + ra * o);
                            t.vertexUV(xPlusPointFive, yy0, zD, 0.0F * o, yy0 * o / 4.0F + ra * o);
                            t.vertexUV(xPlusPointFive, yy0, zPlusOne, 1.0F * o, yy0 * o / 4.0F + ra * o);
                            t.vertexUV(xPlusPointFive, yy1, zPlusOne, 1.0F * o, yy1 * o / 4.0F + ra * o);
                            t.vertexUV(xPlusPointFive, yy1, zD, 0.0F * o, yy1 * o / 4.0F + ra * o);
                            t.offset(0.0, 0.0, 0.0);
                            t.end();
                        }
                    }
                }
            }

            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        }
    }
}
