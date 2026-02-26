package me.alphamode.mcbig.mixin.items;

import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.RecordPlayerTile;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(RecordItem.class)
public abstract class RecordItemMixin extends Item {
    protected RecordItemMixin(int id) {
        super(id);
    }

    @Override
    public boolean useOn(ItemInstance item, Player player, Level level, BigInteger x, int y, BigInteger z, int face) {
        if (level.getTile(x, y, z) != Tile.JUKEBOX.id || level.getData(x, y, z) != 0) {
            return false;
        } else if (level.isClientSide) {
            return true;
        } else {
            ((RecordPlayerTile) Tile.JUKEBOX).setRecord(level, x, y, z, this.id);
            level.levelEvent(null, 1005, x, y, z, this.id);
            item.count--;
            return true;
        }
    }
}
