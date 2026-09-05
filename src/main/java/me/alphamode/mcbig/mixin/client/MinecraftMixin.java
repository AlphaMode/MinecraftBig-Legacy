package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.world.phys.BigHitResult;
import net.minecraft.Pos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ProgressRenderer;
import net.minecraft.client.gamemode.CreativeMode;
import net.minecraft.client.gamemode.GameMode;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkCache;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.math.BigInteger;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public ProgressRenderer progressRenderer;

    @Shadow
    public Level level;

    @Shadow
    public LocalPlayer player;

    @Shadow
    public HitResult hitResult;

    @Shadow
    public GameMode gameMode;

    @Shadow
    private int missTime;

    @Shadow
    public GameRenderer gameRenderer;

    @Shadow
    public ParticleEngine particleEngine;

    @Shadow
    public boolean appletMode;

    //? >=1.0.0-beta.8.0.r {
    /*@Shadow
    private int rightClickDelay;
    *///? }

    @Inject(method = "init", at = @At("HEAD"))
    private void fixQuitButton(CallbackInfo ci) {
        this.appletMode = false;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void prepareLevel(String string) {
        //? >= 1.0.0-beta.8.0.r
        //if (this.progressRenderer != null) {
            this.progressRenderer.setHeader(string);
            this.progressRenderer.progressStage("Building terrain");
        //? >= 1.0.0-beta.8.0.r
        //}
        int r = 128;
        //? >= 1.0.0-beta.8.0.r
        //if (this.gameMode.isCutScene()) r = 64;

        int pp = 0;
        int max = r * 2 / 16 + 1;
        max = max * max;
        ChunkSource cs = this.level.getChunkSource();
        Pos spawnPos = this.level.getSpawnPos();
        if (this.player != null) {
            spawnPos.x = (int) this.player.x;
            spawnPos.z = (int) this.player.z;
        }

        if (cs instanceof ChunkCache) {
            ChunkCache spcc = (ChunkCache) cs;
            spcc.centerOn(spawnPos.x >> 4, spawnPos.z >> 4);
        }

        for (int x = -r; x <= r; x += 16) {
            for (int z = -r; z <= r; z += 16) {
                //? >= 1.0.0-beta.8.0.r
                //if (this.progressRenderer != null)
                    this.progressRenderer.progressStagePercentage(pp++ * 100 / max);
                this.level.getTile(BigInteger.valueOf(spawnPos.x + x), 64, BigInteger.valueOf(spawnPos.z + z));

                //? >= 1.0.0-beta.8.0.r
                //if (!this.gameMode.isCutScene()) {
                    while (this.level.updateLights()) {
                    }
                //? >= 1.0.0-beta.8.0.r
                //}
            }
        }

        //? >= 1.0.0-beta.8.0.r {
        /*if (!this.gameMode.isCutScene()) {
            if (this.progressRenderer != null)
                *///? }
                this.progressRenderer.progressStage("Simulating world for a bit");
            max = 2000;
            this.level.prepare();
        //? >= 1.0.0-beta.8.0.r
        //}
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;animateTick(III)V"))
    private void redirectAnimateTick(Level instance, int x, int y, int z) {
        instance.animateTick(BigMath.floor(this.player.x), Mth.floor(this.player.y), BigMath.floor(this.player.z));
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void handleMouseDown(int button, boolean down) {
        //? <1.0.0-beta.8.0.r
        if (this.gameMode.instaBuild) return;
        if (!down) this.missTime = 0;

        if (button == 0 && this.missTime > 0) return;

        if (down && this.hitResult != null && this.hitResult.hitType == HitResult.HitType.TILE && button == 0) {
            BigInteger x = ((BigHitResult) this.hitResult).xBig;
            int y = this.hitResult.y;
            BigInteger z = ((BigHitResult) this.hitResult).zBig;
            this.gameMode.continueDestroyBlock(x, y, z, this.hitResult.face);
            //? >=1.0.0-beta.8.0.r
            //if (this.player.mayUseItemAt(x, y, z)) {
                this.particleEngine.crack(x.intValue(), y, z.intValue(), this.hitResult.face);
                //? >=1.0.0-beta.8.0.r {
                /*this.player.swing();
            }
            *///? }
        } else {
            this.gameMode.stopDestroyBlock();
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void handleMouseClick(int button) {
        //? >=1.0.0-beta.8.0.r {
        /*if (button == 0 && this.missTime > 0) return;
        if (button == 0) {
            this.player.swing();
        }

        if (button == 1) {
            this.rightClickDelay = 6;
        }

        boolean mayUse = true;
        ItemInstance item = this.player.inventory.getSelected();
        if (this.hitResult == null) {
            if (button == 0 && this.gameMode.hasMissTime()) {
                this.missTime = 10;
            }
        } else if (this.hitResult.hitType == HitResult.HitType.ENTITY) {
            if (button == 0) {
                this.gameMode.attack(this.player, this.hitResult.entity);
            }

            if (button == 1) {
                this.gameMode.interact(this.player, this.hitResult.entity);
            }
        } else if (this.hitResult.hitType == HitResult.HitType.TILE) {
            BigInteger x = ((BigHitResult) this.hitResult).xBig;
            int y = this.hitResult.y;
            BigInteger z = ((BigHitResult) this.hitResult).zBig;
            int face = this.hitResult.face;
            if (button == 0) {
                this.gameMode.startDestroyBlock(x, y, z, this.hitResult.face);
            } else {
                int oldCount = item != null ? item.count : 0;
                if (this.gameMode.useItemOn(this.player, this.level, item, x, y, z, face)) {
                    mayUse = false;
                    this.player.swing();
                }

                if (item == null) {
                    return;
                }

                if (item.count == 0) {
                    this.player.inventory.items[this.player.inventory.selected] = null;
                } else if (item.count != oldCount || this.gameMode.hasInfiniteItems()) {
                    this.gameRenderer.itemInHandRenderer.itemPlaced();
                }
            }
        }

        if (mayUse && button == 1) {
            ItemInstance selected = this.player.inventory.getSelected();
            if (selected != null && this.gameMode.useItem(this.player, this.level, selected)) {
                this.gameRenderer.itemInHandRenderer.itemUsed();
            }
        }
        *///? } else {
        if (button != 0 || this.missTime <= 0) {
            if (button == 0) {
                this.player.swing();
            }

            boolean mayUse = true;
            if (this.hitResult == null) {
                if (button == 0 && !(this.gameMode instanceof CreativeMode)) {
                    this.missTime = 10;
                }
            } else if (this.hitResult.hitType == HitResult.HitType.ENTITY) {
                if (button == 0) {
                    this.gameMode.attack(this.player, this.hitResult.entity);
                }

                if (button == 1) {
                    this.gameMode.interact(this.player, this.hitResult.entity);
                }
            } else if (this.hitResult.hitType == HitResult.HitType.TILE) {
                BigInteger x = ((BigHitResult) this.hitResult).xBig;
                int y = this.hitResult.y;
                BigInteger z = ((BigHitResult) this.hitResult).zBig;
                int face = this.hitResult.face;
                if (button == 0) {
                    this.gameMode.startDestroyBlock(x, y, z, this.hitResult.face);
                } else {
                    ItemInstance item = this.player.inventory.getSelected();
                    int oldCount = item != null ? item.count : 0;
                    if (this.gameMode.useItemOn(this.player, this.level, item, x, y, z, face)) {
                        mayUse = false;
                        this.player.swing();
                    }

                    if (item == null) {
                        return;
                    }

                    if (item.count == 0) {
                        this.player.inventory.items[this.player.inventory.selected] = null;
                    } else if (item.count != oldCount) {
                        this.gameRenderer.itemInHandRenderer.itemPlaced();
                    }
                }
            }

            if (mayUse && button == 1) {
                ItemInstance selected = this.player.inventory.getSelected();
                if (selected != null && this.gameMode.useItem(this.player, this.level, selected)) {
                    this.gameRenderer.itemInHandRenderer.itemUsed();
                }
            }
        }
        //? }
    }
}
