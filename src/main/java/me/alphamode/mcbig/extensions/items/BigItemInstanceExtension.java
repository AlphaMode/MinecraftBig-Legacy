package me.alphamode.mcbig.extensions.items;

import net.minecraft.stats.Stats;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.math.BigInteger;

public interface BigItemInstanceExtension {
    default boolean useOn(Player player, Level level, BigInteger x, int y, BigInteger z, int face) {
        boolean used = ((ItemInstance) this).getItem().useOn((ItemInstance) this, player, level, x, y, z, face);
        if (used) {
            player.awardStat(Stats.STAT_ITEM_USED[((ItemInstance) this).id], 1);
        }

        return used;
    }

    default void mineBlock(int tile, BigInteger x, int y, BigInteger z, Player player) {
        boolean mined = Item.items[((ItemInstance) this).id].mineBlock((ItemInstance) this, tile, x, y, z, player);
        if (mined) {
            player.awardStat(Stats.STAT_ITEM_USED[((ItemInstance) this).id], 1);
        }
    }
}
