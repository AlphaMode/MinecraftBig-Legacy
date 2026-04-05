package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.extensions.BigFeatureExtension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.HellSpringFeature;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(HellSpringFeature.class)
public abstract class HellSpringFeatureMixin implements BigFeatureExtension {
    @Shadow
    private int tile;

    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        if (level.getTile(x, y + 1, z) != Tile.hellRock.id) return false;
        if (level.getTile(x, y, z) != 0 && level.getTile(x, y, z) != Tile.hellRock.id) return false;

        BigInteger xMinusOne = x.subtract(BigInteger.ONE);
        BigInteger xPlusOne = x.add(BigInteger.ONE);
        BigInteger zMinusOne = z.subtract(BigInteger.ONE);
        BigInteger zPlusOne = z.add(BigInteger.ONE);

        int rockCount = 0;
        if (level.getTile(xMinusOne, y, z) == Tile.hellRock.id) rockCount++;
        if (level.getTile(xPlusOne, y, z) == Tile.hellRock.id) rockCount++;
        if (level.getTile(x, y, zMinusOne) == Tile.hellRock.id) rockCount++;
        if (level.getTile(x, y, zPlusOne) == Tile.hellRock.id) rockCount++;
        if (level.getTile(x, y - 1, z) == Tile.hellRock.id) rockCount++;

        int holeCount = 0;
        if (level.isEmptyTile(xMinusOne, y, z)) holeCount++;
        if (level.isEmptyTile(xPlusOne, y, z)) holeCount++;
        if (level.isEmptyTile(x, y, zMinusOne)) holeCount++;
        if (level.isEmptyTile(x, y, zPlusOne)) holeCount++;
        if (level.isEmptyTile(x, y - 1, z)) holeCount++;

        if (rockCount == 4 && holeCount == 1) {
            level.setTile(x, y, z, this.tile);
            level.instaTick = true;
            Tile.tiles[this.tile].tick(level, x, y, z, random);
            level.instaTick = false;
        }

        return true;
    }
}
