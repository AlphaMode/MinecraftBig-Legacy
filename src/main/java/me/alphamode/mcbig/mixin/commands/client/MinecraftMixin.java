package me.alphamode.mcbig.mixin.commands.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.alphamode.mcbig.client.commands.CommandHistory;
import me.alphamode.mcbig.extensions.features.commands.ChatMinecraftExtension;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class MinecraftMixin implements ChatMinecraftExtension {
    @Shadow
    public Level level;

    private final CommandHistory commandHistory = new CommandHistory(FabricLoader.getInstance().getGameDir());

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

    @Override
    public CommandHistory commandHistory() {
        return this.commandHistory;
    }
}
