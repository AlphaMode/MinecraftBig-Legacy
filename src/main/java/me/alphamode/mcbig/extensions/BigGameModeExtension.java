package me.alphamode.mcbig.extensions;

import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.math.BigInteger;

public interface BigGameModeExtension {
    default void startDestroyBlock(BigInteger x, int y, BigInteger z, int face) {
        throw new UnsupportedOperationException();
    }

    default boolean destroyBlock(BigInteger x, int y, BigInteger z, int face) {
        throw new UnsupportedOperationException();
    }

    default void continueDestroyBlock(BigInteger x, int y, BigInteger z, int face) {}

    default boolean useItemOn(Player player, Level level, ItemInstance item, BigInteger x, int y, BigInteger z, int face) {
        throw new UnsupportedOperationException();
    }
}
