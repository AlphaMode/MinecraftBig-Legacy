package me.alphamode.mcbig.extensions.tiles;

import net.minecraft.util.Directions;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.tile.Tile;

import java.math.BigInteger;

public interface BigRedStoneDustTileExtension {

    static boolean isPowerSourceAt(LevelSource level, BigInteger x, int y, BigInteger z, int direction) {
        int t = level.getTile(x, y, z);
        if (t == Tile.REDSTONE.id) {
            return true;
        } else if (t == 0) {
            return false;
        } else if (Tile.tiles[t].isSignalSource()) {
            return true;
        } else if (t != Tile.REPEATER_OFF.id && t != Tile.REPEATER_ON.id) {
            return false;
        } else {
            int d = level.getData(x, y, z);
            return direction == Directions.DIRECTION_OPPOSITE[d & 3];
        }
    }
}
