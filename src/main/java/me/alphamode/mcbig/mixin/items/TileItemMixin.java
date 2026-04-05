package me.alphamode.mcbig.mixin.items;

import me.alphamode.mcbig.constants.LevelConstants;
import net.minecraft.util.Facing;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(TileItem.class)
public abstract class TileItemMixin extends Item {
    @Shadow
    private int tileId;

    protected TileItemMixin(int id) {
        super(id);
    }

    @Override
    public boolean useOn(ItemInstance item, Player player, Level level, BigInteger x, int y, BigInteger z, int face) {
        if (level.getTile(x, y, z) == Tile.topSnow.id) {
            face = 0;
        } else {
            if (face == Facing.DOWN) y--;
            if (face == Facing.UP) y++;
            if (face == Facing.NORTH) z = z.subtract(BigInteger.ONE);
            if (face == Facing.SOUTH) z = z.add(BigInteger.ONE);
            if (face == Facing.WEST) x = x.subtract(BigInteger.ONE);
            if (face == Facing.EAST) x = x.add(BigInteger.ONE);
        }

        if (item.count == 0) return false;

        if (y == LevelConstants.MAX_BUILD_HEIGHT - 1 && Tile.tiles[this.tileId].material.isSolid()) return false;

        if (level.mayPlace(this.tileId, x, y, z, false, face)) {
            Tile tile = Tile.tiles[this.tileId];
            if (level.setTileAndData(x, y, z, this.tileId, this.getLevelDataForAuxValue(item.getAuxValue()))) {
                Tile.tiles[this.tileId].setPlacedOnFace(level, x, y, z, face);
                Tile.tiles[this.tileId].setPlacedBy(level, x, y, z, player);
                level.playSound(x.doubleValue() + 0.5F, y + 0.5F, z.doubleValue() + 0.5F, tile.soundType.getStepSound(), (tile.soundType.getVolume() + 1.0F) / 2.0F, tile.soundType.getPitch() * 0.8F);
                item.count--;
            }

            return true;
        } else {
            return false;
        }
    }
}
