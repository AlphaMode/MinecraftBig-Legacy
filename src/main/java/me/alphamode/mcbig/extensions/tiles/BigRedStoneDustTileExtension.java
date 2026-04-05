package me.alphamode.mcbig.extensions.tiles;

import net.minecraft.util.Direction;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.tile.Tile;

import java.math.BigInteger;

public interface BigRedStoneDustTileExtension {

    static boolean isPowerSourceAt(LevelSource level, BigInteger x, int y, BigInteger z, int direction) {
        int t = level.getTile(x, y, z);
        if (t == Tile.redStoneDust.id) {
            return true;
        } else if (t == 0) {
            return false;
        } else if (Tile.tiles[t].isSignalSource()) {
            return true;
        } else if (t != Tile.diode_off.id && t != Tile.diode_on.id) {
            return false;
        } else {
            int d = level.getData(x, y, z);
            return direction == Direction.DIRECTION_OPPOSITE[d & 3];
        }
    }
}
