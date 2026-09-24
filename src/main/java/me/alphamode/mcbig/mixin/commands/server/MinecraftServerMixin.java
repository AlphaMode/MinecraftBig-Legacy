package me.alphamode.mcbig.mixin.commands.server;

import me.alphamode.mcbig.commands.Commands;
import me.alphamode.mcbig.extensions.features.commands.MinecraftServerExtension;
import me.alphamode.mcbig.server.commands.ServerCommands;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements MinecraftServerExtension {

    private final Commands commands = new Commands();

    @Inject(method = "initServer", at = @At("HEAD"))
    private void initCommands(CallbackInfoReturnable<Boolean> cir) {
        ServerCommands.init(commands.getDispatcher());
    }

    @Override
    public Commands getCommands() {
        return this.commands;
    }
}
