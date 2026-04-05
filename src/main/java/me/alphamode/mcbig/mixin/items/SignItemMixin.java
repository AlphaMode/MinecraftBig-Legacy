package me.alphamode.mcbig.mixin.items;

import net.minecraft.util.Facing;
import net.minecraft.util.Mth;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.SignTileEntity;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(SignItem.class)
public abstract class SignItemMixin extends Item {
    protected SignItemMixin(int id) {
        super(id);
    }

    @Override
    public boolean useOn(ItemInstance item, Player player, Level level, BigInteger x, int y, BigInteger z, int face) {
        if (face == Facing.DOWN) return false;
        if (!level.getMaterial(x, y, z).isSolid()) return false;

        if (face == Facing.UP) y++;
        if (face == Facing.NORTH) z = z.subtract(BigInteger.ONE);
        if (face == Facing.SOUTH) z = z.add(BigInteger.ONE);
        if (face == Facing.WEST) x = x.subtract(BigInteger.ONE);
        if (face == Facing.EAST) x = x.add(BigInteger.ONE);

        if (!Tile.sign.mayPlace(level, x, y, z)) return false;

        if (face == 1) {
            int rot = Mth.floor((player.yRot + 180.0F) * 16.0F / 360.0F + 0.5) & 15;
            level.setTileAndData(x, y, z, Tile.sign.id, rot);
        } else {
            level.setTileAndData(x, y, z, Tile.wallSign.id, face);
        }

        item.count--;
        SignTileEntity ste = (SignTileEntity)level.getTileEntity(x, y, z);
        if (ste != null) player.openSign(ste);

        return true;
    }
}
