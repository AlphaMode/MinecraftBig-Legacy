package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.world.phys.BigHitResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ProgressRenderer;
import net.minecraft.client.gamemode.CreativeMode;
import net.minecraft.client.gamemode.GameMode;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.Vec3i;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkCache;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.math.BigInteger;

@Mixin(Minecraft.class)
public class MinecraftMixin {
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

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void prepareLevel(String string) {
        this.progressRenderer.setHeader(string);
        this.progressRenderer.progressStage("Building terrain");
        short var2 = 128;
        int var3 = 0;
        int var4 = var2 * 2 / 16 + 1;
        var4 *= var4;
        ChunkSource source = this.level.getChunkSource();
        Vec3i spawn = this.level.getSpawnPos();
        if (this.player != null) {
            spawn.x = (int) this.player.x;
            spawn.z = (int) this.player.z;
        }

        if (source instanceof ChunkCache) {
            ChunkCache cache = (ChunkCache) source;
            cache.centerOn(spawn.x >> 4, spawn.z >> 4);
        }

        for (int xOff = -var2; xOff <= var2; xOff += 16) {
            for (int zOff = -var2; zOff <= var2; zOff += 16) {
                this.progressRenderer.progressStagePercentage(var3++ * 100 / var4);
                this.level.getTile(BigInteger.valueOf(spawn.x + xOff), 64, BigInteger.valueOf(spawn.z + zOff));

                while (this.level.updateLights()) {
                }
            }
        }

        this.progressRenderer.progressStage("Simulating world for a bit");
        var4 = 2000;
        this.level.prepare();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;animateTick(III)V"))
    private void redirectAnimateTick(Level instance, int y, int z, int i) {
        instance.animateTick(BigMath.floor(this.player.x), Mth.floor(this.player.y), BigMath.floor(this.player.z));
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void handleMouseDown(int i, boolean bl) {
        if (!this.gameMode.instaBuild) {
            if (!bl) {
                this.missTime = 0;
            }

            if (i != 0 || this.missTime <= 0) {
                if (bl && this.hitResult != null && this.hitResult.hitType == HitResult.HitType.TILE && i == 0) {
                    BigInteger xt = ((BigHitResult) this.hitResult).xBig;
                    int yt = this.hitResult.y;
                    BigInteger zt = ((BigHitResult) this.hitResult).zBig;
                    this.gameMode.continueDestroyBlock(xt, yt, zt, this.hitResult.face);
                    this.particleEngine.crack(xt.intValue(), yt, zt.intValue(), this.hitResult.face);
                } else {
                    this.gameMode.stopDestroyBlock();
                }
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void handleMouseClick(int i) {
        if (i != 0 || this.missTime <= 0) {
            if (i == 0) {
                this.player.swing();
            }

            boolean var2 = true;
            if (this.hitResult == null) {
                if (i == 0 && !(this.gameMode instanceof CreativeMode)) {
                    this.missTime = 10;
                }
            } else if (this.hitResult.hitType == HitResult.HitType.ENTITY) {
                if (i == 0) {
                    this.gameMode.attack(this.player, this.hitResult.entity);
                }

                if (i == 1) {
                    this.gameMode.interact(this.player, this.hitResult.entity);
                }
            } else if (this.hitResult.hitType == HitResult.HitType.TILE) {
                BigInteger xt = ((BigHitResult) this.hitResult).xBig;
                int yt = this.hitResult.y;
                BigInteger zt = ((BigHitResult) this.hitResult).zBig;
                int var6 = this.hitResult.face;
                if (i == 0) {
                    this.gameMode.startDestroyBlock(xt, yt, zt, this.hitResult.face);
                } else {
                    ItemInstance var7 = this.player.inventory.getSelected();
                    int var8 = var7 != null ? var7.count : 0;
                    if (this.gameMode.useItemOn(this.player, this.level, var7, xt, yt, zt, var6)) {
                        var2 = false;
                        this.player.swing();
                    }

                    if (var7 == null) {
                        return;
                    }

                    if (var7.count == 0) {
                        this.player.inventory.items[this.player.inventory.selected] = null;
                    } else if (var7.count != var8) {
                        this.gameRenderer.itemInHandRenderer.itemPlaced();
                    }
                }
            }

            if (var2 && i == 1) {
                ItemInstance var9 = this.player.inventory.getSelected();
                if (var9 != null && this.gameMode.useItem(this.player, this.level, var9)) {
                    this.gameRenderer.itemInHandRenderer.itemUsed();
                }
            }
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isOnline()Z", ordinal = 0))
    private boolean allowChat(Minecraft instance) {
        return true;
    }
}
