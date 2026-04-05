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
        if (level.getTile(x, y + 1, z) != Tile.stone.id) return false;
        if (level.getTile(x, y - 1, z) != Tile.stone.id) return false;

        if (level.getTile(x, y, z) != 0 && level.getTile(x, y, z) != Tile.stone.id) return false;

        int rockCount = 0;
        if (level.getTile(x.subtract(BigInteger.ONE), y, z) == Tile.stone.id) rockCount++;
        if (level.getTile(x.add(BigInteger.ONE), y, z) == Tile.stone.id) rockCount++;
        if (level.getTile(x, y, z.subtract(BigInteger.ONE)) == Tile.stone.id) rockCount++;
        if (level.getTile(x, y, z.add(BigInteger.ONE)) == Tile.stone.id) rockCount++;

        int holeCount = 0;
        if (level.isEmptyTile(x.subtract(BigInteger.ONE), y, z)) holeCount++;
        if (level.isEmptyTile(x.add(BigInteger.ONE), y, z)) holeCount++;
        if (level.isEmptyTile(x, y, z.subtract(BigInteger.ONE))) holeCount++;
        if (level.isEmptyTile(x, y, z.add(BigInteger.ONE))) holeCount++;

        if (rockCount == 3 && holeCount == 1) {
            level.setTile(x, y, z, this.tile);
            level.instaTick = true;
            Tile.tiles[this.tile].tick(level, x, y, z, random);
            level.instaTick = false;
        }

        return true;
    }
}
