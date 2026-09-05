package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.math.BigMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Shadow private Minecraft mc;

    //? >=1.0.0-beta.8.0.r {
    /*@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getLightColor(IIII)I"))
    private int fixPlayerLightColor(Level instance, int x, int y, int z, int emitt) {
        return this.mc.level.getLightColor(BigMath.floor(this.mc.player.x), y, BigMath.floor(this.mc.player.z), emitt);
    }
    *///? }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBrightness(III)F"))
    private float fixPlayerBrightness(Level level, int x, int y, int z) {
        return this.mc.level.getBrightness(BigMath.floor(this.mc.player.x), y, BigMath.floor(this.mc.player.z));
    }
}
