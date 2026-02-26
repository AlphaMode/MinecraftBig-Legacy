package me.alphamode.mcbig.extensions.tiles;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.tile.Tile;

import java.math.BigInteger;
import java.util.Random;

public interface BigSaplingTileExtension {
    default void growTree(Level level, BigInteger x, int y, BigInteger z, Random random) {
        int type = level.getData(x, y, z) & 3;
        level.setTileNoUpdate(x, y, z, 0);
        Feature tree = null;
        if (type == 1) {
            tree = new SpruceFeature();
        } else if (type == 2) {
            tree = new BirchFeature();
        } else {
            tree = new TreeFeature();
            if (random.nextInt(10) == 0) {
                tree = new BigTreeFeature();
            }
        }

        if (!tree.place(level, random, x, y, z)) {
            level.setTileAndDataNoUpdate(x, y, z, ((Tile) this).id, type);
        }
    }
}
