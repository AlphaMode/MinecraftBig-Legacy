package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.world.phys.BigHitResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gamemode.CreativeMode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow private float fogBrO;

    @Shadow private float fogBr;

    @Shadow private float oldZOff;

    @Shadow private float zOff;

    @Shadow private float yRotO;

    @Shadow private float yRot;

    @Shadow private float xRotO;

    @Shadow private float xRot;

    @Shadow private float oldFov;

    @Shadow private float fov;

    @Shadow private float camTiltO;

    @Shadow private float camTilt;

    @Shadow private Minecraft mc;

    @Shadow private int tick;

    @Shadow public ItemInHandRenderer itemInHandRenderer;

    @Shadow protected abstract void tickRain();

    @Shadow private Entity hovered;

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
        float distance = (float)(3 - this.mc.options.viewDistance) / 3.0F;
        float var3 = br * (1.0F - distance) + distance;
        this.fogBr += (var3 - this.fogBr) * 0.1F;
        ++this.tick;
        this.itemInHandRenderer.tick();
        this.tickRain();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void pick(float partialTick) {
        if (this.mc.cameraEntity != null) {
            if (this.mc.level != null) {
                double var2 = (double)this.mc.gameMode.getPickRange();
                this.mc.hitResult = this.mc.cameraEntity.pick(var2, partialTick);
                double var4 = var2;
                Vec3 var6 = this.mc.cameraEntity.getPos(partialTick);
                if (this.mc.hitResult != null) {
                    var4 = this.mc.hitResult.pos.distanceTo(var6);
                }

                if (this.mc.gameMode instanceof CreativeMode) {
                    var2 = 32.0;
                    var4 = 32.0;
                } else {
                    if (var4 > 3.0) {
                        var4 = 3.0;
                    }

                    var2 = var4;
                }

                Vec3 var7 = this.mc.cameraEntity.getViewVector(partialTick);
                Vec3 var8 = var6.add(var7.x * var2, var7.y * var2, var7.z * var2);
                this.hovered = null;
                float var9 = 1.0F;
                List var10 = this.mc
                        .level
                        .getEntities(
                                this.mc.cameraEntity, this.mc.cameraEntity.bb.expand(var7.x * var2, var7.y * var2, var7.z * var2).inflate((double)var9, (double)var9, (double)var9)
                        );
                double var11 = 0.0;

                for(int var13 = 0; var13 < var10.size(); ++var13) {
                    Entity var14 = (Entity)var10.get(var13);
                    if (var14.isPickable()) {
                        float var15 = var14.getPickRadius();
                        AABB var16 = var14.bb.inflate((double)var15, (double)var15, (double)var15);
                        HitResult var17 = var16.clip(var6, var8);
                        if (var16.intersects(var6)) {
                            if (0.0 < var11 || var11 == 0.0) {
                                this.hovered = var14;
                                var11 = 0.0;
                            }
                        } else if (var17 != null) {
                            double var18 = var6.distanceTo(var17.pos);
                            if (var18 < var11 || var11 == 0.0) {
                                this.hovered = var14;
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
}
