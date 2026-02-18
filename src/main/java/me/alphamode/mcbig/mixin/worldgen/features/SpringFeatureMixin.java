package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.extensions.BigFeatureExtension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.SpringFeature;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(SpringFeature.class)
public class SpringFeatureMixin implements BigFeatureExtension {
    @Shadow
    private int tile;

    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        if (level.getTile(x, y + 1, z) != Tile.STONE.id) {
            return false;
        } else if (level.getTile(x, y - 1, z) != Tile.STONE.id) {
            return false;
        } else if (level.getTile(x, y, z) != 0 && level.getTile(x, y, z) != Tile.STONE.id) {
            return false;
        } else {
            int var6 = 0;
            if (level.getTile(x.subtract(BigInteger.ONE), y, z) == Tile.STONE.id) {
                var6++;
            }

            if (level.getTile(x.add(BigInteger.ONE), y, z) == Tile.STONE.id) {
                var6++;
            }

            if (level.getTile(x, y, z.subtract(BigInteger.ONE)) == Tile.STONE.id) {
                var6++;
            }

            if (level.getTile(x, y, z.add(BigInteger.ONE)) == Tile.STONE.id) {
                var6++;
            }

            int var7 = 0;
            if (level.isEmptyTile(x.subtract(BigInteger.ONE), y, z)) {
                var7++;
            }

            if (level.isEmptyTile(x.add(BigInteger.ONE), y, z)) {
                var7++;
            }

            if (level.isEmptyTile(x, y, z.subtract(BigInteger.ONE))) {
                var7++;
            }

            if (level.isEmptyTile(x, y, z.add(BigInteger.ONE))) {
                var7++;
            }

            if (var6 == 3 && var7 == 1) {
                level.setTile(x, y, z, this.tile);
                level.instaTick = true;
                Tile.tiles[this.tile].tick(level, x, y, z, random);
                level.instaTick = false;
            }

            return true;
        }
    }
}
