package me.alphamode.mcbig.extensions.server;

import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.math.BigInteger;

public interface BigServerPlayerGameModeExtension {
    default void handleStartDestroyTile(BigInteger x, int y, BigInteger z, int direction) {
        throw new UnsupportedOperationException();
    }

    default void handleStopDestroyTile(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default boolean destroyTile(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default boolean destroyAndAck(BigInteger x, int y, BigInteger z) {
        throw new UnsupportedOperationException();
    }

    default boolean useItemOn(Player player, Level level, ItemInstance item, BigInteger x, int y, BigInteger z, int face) {
        throw new UnsupportedOperationException();
    }
}
