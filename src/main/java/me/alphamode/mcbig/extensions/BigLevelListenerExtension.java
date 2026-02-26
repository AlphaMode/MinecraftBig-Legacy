package me.alphamode.mcbig.extensions;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.tile.entity.TileEntity;

import java.math.BigInteger;

public interface BigLevelListenerExtension {
    default void tileChanged(BigInteger x, int y, BigInteger z) {}

    default void setTilesDirty(BigInteger minX, int minY, BigInteger minZ, BigInteger maxX, int maxY, BigInteger maxZ) {}

    default void playStreamingMusic(String track, BigInteger x, int y, BigInteger z) {}

    default void tileEntityChanged(BigInteger x, int y, BigInteger z, TileEntity te) {}

    default void levelEvent(Player player, int event, BigInteger x, int y, BigInteger z, int data) {}
}
