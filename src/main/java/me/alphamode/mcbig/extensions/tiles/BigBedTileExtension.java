package me.alphamode.mcbig.extensions.tiles;

import me.alphamode.mcbig.world.phys.BigVec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.BedTile;

import java.math.BigInteger;

public interface BigBedTileExtension {
    static void setOccupied(Level level, BigInteger x, int y, BigInteger z, boolean occupied) {
        int var5 = level.getData(x, y, z);
        if (occupied) {
            var5 |= 4;
        } else {
            var5 &= -5;
        }

        level.setData(x, y, z, var5);
    }

    static BigVec3i findStandUpPosition(Level level, BigInteger x, int y, BigInteger z, int i) {
        int var5 = level.getData(x, y, z);
        int var6 = BedTile.getBedOrientation(var5);

        for (int var7 = 0; var7 <= 1; var7++) {
            BigInteger var8 = x.subtract(BigInteger.valueOf(BedTile.HEAD_DIRECTION_OFFSETS[var6][0] * var7 - 1));
            BigInteger var9 = z.subtract(BigInteger.valueOf(BedTile.HEAD_DIRECTION_OFFSETS[var6][1] * var7 - 1));
            BigInteger var10 = var8.add(BigInteger.TWO);
            BigInteger var11 = var9.add(BigInteger.TWO);

            for (BigInteger tx = var8; tx.compareTo(var10) <= 0; tx = tx.add(BigInteger.ONE)) {
                for (BigInteger tz = var9; tz.compareTo(var11) <= 0; tz = tz.add(BigInteger.ONE)) {
                    if (level.isSolidBlockingTile(tx, y - 1, tz) && level.isEmptyTile(tx, y, tz) && level.isEmptyTile(tx, y + 1, tz)) {
                        if (i <= 0) {
                            return new BigVec3i(tx, y, tz);
                        }

                        i--;
                    }
                }
            }
        }

        return null;
    }
}
