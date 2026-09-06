package me.alphamode.mcbig.mixin.features.big_movement.client;

import me.alphamode.mcbig.client.renderer.entity.EntityRenderDispatcherData;
import me.alphamode.mcbig.extensions.client.renderer.entity.BigEntityRenderDispatcherExtension;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.math.BigMath;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.Textures;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;
import java.math.BigInteger;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin implements BigEntityRenderDispatcherExtension {

    public BigDecimal xPlayerBig = BigDecimal.ZERO;
    public BigDecimal zPlayerBig = BigDecimal.ZERO;

    @Shadow
    public double yPlayer;

    @Shadow
    public Level level;

    @Shadow
    public Textures textures;

    @Shadow
    public Options options;

    @Shadow
    public Mob cameraEntity;

    @Shadow
    private Font font;

    @Shadow
    public float playerRotY;

    @Shadow
    public float playerRotX;

    @Shadow
    public double xPlayer;
    @Shadow
    public double zPlayer;
    @Shadow
    public static double xOff;
    @Shadow
    public static double yOff;
    @Shadow
    public static double zOff;

    @Shadow
    public abstract void render(Entity entity, double x, double y, double z, float yRot, float a);

    private boolean bigMovementEnabled;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void prepare(Level level, Textures textureManager, Font font, Mob cameraEntity, Options options, float a) {
        this.level = level;
        this.textures = textureManager;
        this.options = options;
        this.cameraEntity = cameraEntity;
        this.bigMovementEnabled = cameraEntity.isBigMovementEnabled();
        this.font = font;
        BigInteger xt;
        BigInteger zt;
        BigEntityExtension bigCamera = (BigEntityExtension) cameraEntity;
        if (bigMovementEnabled) {
            xt = BigMath.floor(bigCamera.getX());
            zt = BigMath.floor(bigCamera.getZ());
        } else {
            xt = BigMath.floor(cameraEntity.x);
            zt = BigMath.floor(cameraEntity.z);
        }
        if (cameraEntity.isSleeping()) {
            int t = level.getTile(xt, Mth.floor(cameraEntity.y), zt);
            if (t == Tile.bed.id) {
                int data = level.getData(xt, Mth.floor(cameraEntity.y), zt);
                int dir = data & 3;
                this.playerRotY = dir * 90 + 180;
                this.playerRotX = 0.0F;
            }
        } else {
            this.playerRotY = cameraEntity.yRotO + (cameraEntity.yRot - cameraEntity.yRotO) * a;
            this.playerRotX = cameraEntity.xRotO + (cameraEntity.xRot - cameraEntity.xRotO) * a;
        }

        this.yPlayer = cameraEntity.yOld + (cameraEntity.y - cameraEntity.yOld) * a;
        if (bigMovementEnabled) {
            BigDecimal ab = BigDecimal.valueOf(a);
            this.xPlayerBig = bigCamera.getXOld().add((bigCamera.getX().subtract(bigCamera.getXOld())).multiply(ab));
            this.zPlayerBig = bigCamera.getZOld().add((bigCamera.getZ().subtract(bigCamera.getZOld())).multiply(ab));
        } else {
            this.xPlayer = cameraEntity.xOld + (cameraEntity.x - cameraEntity.xOld) * a;
            this.zPlayer = cameraEntity.zOld + (cameraEntity.z - cameraEntity.zOld) * a;
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void render(Entity entity, float a) {
        if (entity.isBigMovementEnabled()) {
            BigEntityExtension e = (BigEntityExtension) entity;
            BigDecimal ab = BigDecimal.valueOf(a);
            BigDecimal x = e.getXOld().add((e.getX().subtract(e.getXOld())).multiply(ab));
            BigDecimal z = e.getZOld().add((e.getZ().subtract(e.getZOld())).multiply(ab));
            double y = entity.yOld + (entity.y - entity.yOld) * a;
            float r = entity.yRotO + (entity.yRot - entity.yRotO) * a;
            float br = entity.getBrightness(a);
            GL11.glColor3f(br, br, br);
            this.render(entity, x.subtract(EntityRenderDispatcherData.xOff).doubleValue(), y - yOff, z.subtract(EntityRenderDispatcherData.zOff).doubleValue(), r, a);
            return;
        }
        double x = entity.xOld + (entity.x - entity.xOld) * a;
        double y = entity.yOld + (entity.y - entity.yOld) * a;
        double z = entity.zOld + (entity.z - entity.zOld) * a;
        float r = entity.yRotO + (entity.yRot - entity.yRotO) * a;
        float br = entity.getBrightness(a);
        GL11.glColor3f(br, br, br);
        this.render(entity, x - EntityRenderDispatcherData.xOff.doubleValue(), y - yOff, z - EntityRenderDispatcherData.zOff.doubleValue(), r, a);
    }

    @Override
    public double distanceToSqr(BigDecimal x, double y, BigDecimal z) {
        double xd;
        double zd;
        if (bigMovementEnabled) {
            xd = x.subtract(this.xPlayerBig).doubleValue();
            zd = z.subtract(this.zPlayerBig).doubleValue();
        } else {
            xd = x.subtract(BigDecimal.valueOf(this.xPlayer)).doubleValue();
            zd = z.subtract(BigDecimal.valueOf(this.zPlayer)).doubleValue();
        }
        double yd = y - this.yPlayer;
        return xd * xd + yd * yd + zd * zd;
    }
}
