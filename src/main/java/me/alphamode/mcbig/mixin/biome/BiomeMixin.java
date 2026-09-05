//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.mixin.biome;

import me.alphamode.mcbig.extensions.biome.BigBiomeExtension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(Biome.class)
public class BiomeMixin implements BigBiomeExtension {
    @Shadow
    public BiomeDecorator decorator;

    public void decorate(Level level, Random random, BigInteger xo, BigInteger zo) {
        this.decorator.decorate(level, random, xo, zo);
    }
}
*///? }
