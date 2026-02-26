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
                                this.mc.cameraEntity, this.mc.cameraEntity.bb.expand(view.x * pickRange, view.y * pickRange, view.z * pickRange).inflate(range, range, range)
                        );
                double var11 = 0.0;

                for(int i = 0; i < entities.size(); ++i) {
                    Entity entity = entities.get(i);
                    if (entity.isPickable()) {
                        float pickRadius = entity.getPickRadius();
                        AABB bb = entity.bb.inflate(pickRadius, pickRadius, pickRadius);
                        BigHitResult hit = (BigHitResult) bb.clip(cameraPos.toVanilla(), pickVec.toVanilla());
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
}
