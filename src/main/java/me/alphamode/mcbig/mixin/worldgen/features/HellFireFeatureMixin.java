package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.extensions.BigFeatureExtension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.HellFireFeature;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;
import java.util.Random;

@Mixin(HellFireFeature.class)
public abstract class HellFireFeatureMixin implements BigFeatureExtension {
    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        for (int i = 0; i < 64; i++) {
            BigInteger x2 = x.add(BigInteger.valueOf(random.nextInt(8) - random.nextInt(8)));
            int y2 = y + random.nextInt(4) - random.nextInt(4);
            BigInteger z2 = z.add(BigInteger.valueOf(random.nextInt(8) - random.nextInt(8)));
            if (level.isEmptyTile(x2, y2, z2) && level.getTile(x2, y2 - 1, z2) == Tile.NETHERRACK.id) {
                level.setTile(x2, y2, z2, Tile.FIRE.id);
            }
        }

        return true;
    }
}
