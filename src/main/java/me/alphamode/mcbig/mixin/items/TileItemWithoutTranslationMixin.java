package me.alphamode.mcbig.mixin.items;

import net.minecraft.util.Facing;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TileItemWithoutTranslation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(TileItemWithoutTranslation.class)
public abstract class TileItemWithoutTranslationMixin extends Item {
    @Shadow
    private int tile;

    protected TileItemWithoutTranslationMixin(int id) {
        super(id);
    }

    @Override
    public boolean useOn(ItemInstance item, Player player, Level level, BigInteger x, int y, BigInteger z, int face) {
        if (level.getTile(x, y, z) == Tile.SNOW_LAYER.id) {
            face = Facing.DOWN;
        } else {
            if (face == Facing.DOWN) {
                y--;
            }

            if (face == Facing.UP) {
                y++;
            }

            if (face == Facing.NORTH) {
                z = z.subtract(BigInteger.ONE);
            }

            if (face == Facing.SOUTH) {
                z = z.add(BigInteger.ONE);
            }

            if (face == Facing.WEST) {
                x = x.subtract(BigInteger.ONE);
            }

            if (face == Facing.EAST) {
                x = x.add(BigInteger.ONE);
            }
        }

        if (item.count == 0) {
            return false;
        } else {
            if (level.mayPlace(this.tile, x, y, z, false, face)) {
                Tile var8 = Tile.tiles[this.tile];
                if (level.setTile(x, y, z, this.tile)) {
                    Tile.tiles[this.tile].setPlacedOnFace(level, x, y, z, face);
                    Tile.tiles[this.tile].setPlacedBy(level, x, y, z, player);
                    level.playSound(x.doubleValue() + 0.5F, y + 0.5F, z.doubleValue() + 0.5F, var8.soundType.getStepSound(), (var8.soundType.getVolume() + 1.0F) / 2.0F, var8.soundType.getPitch() * 0.8F);
                    item.count--;
                }
            }

            return true;
        }
    }
}
