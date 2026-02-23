package me.alphamode.mcbig.extensions.server;

import net.minecraft.world.level.tile.entity.TileEntity;

import java.math.BigInteger;
import java.util.List;

public interface BigServerLevelExtension {
    default List<TileEntity> getTileEntities(BigInteger x0, int y0, BigInteger z0, BigInteger x1, int y1, BigInteger z1) {
        throw new UnsupportedOperationException();
    }
}
