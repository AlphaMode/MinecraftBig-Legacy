package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.BigLevelListenerExtension;
import net.minecraft.world.level.LevelListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelListener.class)
public interface LevelListenerMixin extends BigLevelListenerExtension {
}
