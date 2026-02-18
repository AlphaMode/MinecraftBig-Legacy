package me.alphamode.mcbig.mixin.commands.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    public Level level;

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isOnline()Z", ordinal = 0))
    private boolean allowSingleplayerChat(Minecraft instance, Operation<Boolean> original) {
        return this.level != null;
    }

    /**
     * @author ALphaMode
     * @reason Actually return true if the message is a command
     */
    @Overwrite
    public boolean isCommand(String msg) {
        if (msg.startsWith("/")) {
            return true;
        }

        return false;
    }
}
