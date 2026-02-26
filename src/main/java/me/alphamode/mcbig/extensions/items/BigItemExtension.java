package me.alphamode.mcbig.extensions.items;

import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.math.BigInteger;

public interface BigItemExtension {
    default boolean useOn(ItemInstance item, Player player, Level level, BigInteger x, int y, BigInteger z, int face) {
        return false;
    }

    default boolean mineBlock(ItemInstance item, int tile, BigInteger x, int y, BigInteger z, Mob entity) {
        return false;
    }
}
