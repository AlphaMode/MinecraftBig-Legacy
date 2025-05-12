package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.BigLevelSourceExtension;
import net.minecraft.world.level.LevelSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelSource.class)
public interface LevelSourceMixin extends BigLevelSourceExtension {
}
