package me.alphamode.mcbig.mixin.commands.server;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alphamode.mcbig.extensions.features.commands.MinecraftServerExtension;
import me.alphamode.mcbig.server.commands.ServerCommandSource;
import net.minecraft.server.ConsoleCommands;
import net.minecraft.server.ConsoleInput;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConsoleCommands.class)
public class ConsoleCommandsMixin {
    @Shadow
    private MinecraftServer server;

    @Inject(method = "handleCommand", at = @At("HEAD"), cancellable = true)
    private void handleMcBigCommand(ConsoleInput input, CallbackInfo ci) {
        try {
            ((MinecraftServerExtension) this.server).getCommands().getDispatcher().execute(input.msg, new ServerCommandSource(this.server, input.source));
        } catch (CommandSyntaxException e) {
            input.source.info(e.getMessage());
        }
        ci.cancel();
    }
}
