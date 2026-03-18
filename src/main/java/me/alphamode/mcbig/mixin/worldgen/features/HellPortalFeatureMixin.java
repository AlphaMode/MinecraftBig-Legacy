package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.extensions.BigFeatureExtension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.HellPortalFeature;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;
import java.util.Random;

@Mixin(HellPortalFeature.class)
public class HellPortalFeatureMixin implements BigFeatureExtension {
    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        if (!level.isEmptyTile(x, y, z)) return false;
        if (level.getTile(x, y + 1, z) != Tile.NETHERRACK.id) return false;
        level.setTile(x, y, z, Tile.GLOWSTONE.id);

        for (int i = 0; i < 1500; i++) {
            BigInteger x2 = x.add(BigInteger.valueOf(random.nextInt(8) - random.nextInt(8)));
            int y2 = y - random.nextInt(12);
            BigInteger z2 = z.add(BigInteger.valueOf(random.nextInt(8) - random.nextInt(8)));
            if (level.getTile(x2, y2, z2) != 0) continue;
            BigInteger x2MinusOne = x2.subtract(BigInteger.ONE);
            BigInteger x2PlusOne = x2.add(BigInteger.ONE);
            BigInteger z2MinusOne = z2.subtract(BigInteger.ONE);
            BigInteger z2PlusOne = z2.add(BigInteger.ONE);

            int count = 0;
            for (int t = 0; t < 6; t++) {
                int tile = 0;
                if (t == 0) tile = level.getTile(x2MinusOne, y2, z2);
                if (t == 1) tile = level.getTile(x2PlusOne, y2, z2);
                if (t == 2) tile = level.getTile(x2, y2 - 1, z2);
                if (t == 3) tile = level.getTile(x2, y2 + 1, z2);
                if (t == 4) tile = level.getTile(x2, y2, z2MinusOne);
                if (t == 5) tile = level.getTile(x2, y2, z2PlusOne);

                if (tile == Tile.GLOWSTONE.id) count++;
            }

            if (count == 1) level.setTile(x2, y2, z2, Tile.GLOWSTONE.id);
        }

        return true;
    }
}
