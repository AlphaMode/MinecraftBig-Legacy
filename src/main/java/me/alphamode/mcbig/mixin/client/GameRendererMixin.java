package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.extensions.features.big_movement.BigMobExtension;
import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.world.phys.BigHitResult;
import me.alphamode.mcbig.world.phys.BigVec3;
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
}
