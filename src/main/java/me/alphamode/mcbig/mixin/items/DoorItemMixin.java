package me.alphamode.mcbig.mixin.items;

import net.minecraft.util.Facing;
import net.minecraft.util.Mth;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DoorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(DoorItem.class)
public abstract class DoorItemMixin extends Item {
    @Shadow
    private Material material;

    protected DoorItemMixin(int id) {
        super(id);
    }

    @Override
    public boolean useOn(ItemInstance item, Player player, Level level, BigInteger x, int y, BigInteger z, int face) {
        if (face != Facing.UP) return false;
        y++;

        Tile tile;

        if (this.material == Material.wood) tile = Tile.door_wood;
        else tile = Tile.door_iron;

        if (!tile.mayPlace(level, x, y, z)) return false;

        int dir = Mth.floor((player.yRot + 180.0F) * 4.0F / 360.0F - 0.5) & 3;

        BigInteger xra = BigInteger.ZERO;
        BigInteger zra = BigInteger.ZERO;

        if (dir == 0) zra = BigInteger.ONE;
        if (dir == 1) xra = BigInteger.ONE.negate();
        if (dir == 2) zra = BigInteger.ONE.negate();
        if (dir == 3) xra = BigInteger.ONE;

        int solidLeft = (level.isSolidBlockingTile(x.subtract(xra), y, z.subtract(zra)) ? 1 : 0) + (level.isSolidBlockingTile(x.subtract(xra), y + 1, z.subtract(zra)) ? 1 : 0);
        int solidRight = (level.isSolidBlockingTile(x.add(xra), y, z.add(zra)) ? 1 : 0) + (level.isSolidBlockingTile(x.add(xra), y + 1, z.add(zra)) ? 1 : 0);

        boolean doorLeft = level.getTile(x.subtract(xra), y, z.subtract(zra)) == tile.id || level.getTile(x.subtract(xra), y + 1, z.subtract(zra)) == tile.id;
        boolean doorRight = level.getTile(x.add(xra), y, z.add(zra)) == tile.id || level.getTile(x.add(xra), y + 1, z.add(zra)) == tile.id;

        boolean flip = false;
        if (doorLeft && !doorRight) flip = true;
        else if (solidRight > solidLeft) flip = true;

        if (flip) {
            dir = dir - 1 & 3;
            dir += 4;
        }

        level.noNeighborUpdate = true;
        level.setTileAndData(x, y, z, tile.id, dir);
        level.setTileAndData(x, y + 1, z, tile.id, dir + 8);
        level.noNeighborUpdate = false;
        level.updateNeighborsAt(x, y, z, tile.id);
        level.updateNeighborsAt(x, y + 1, z, tile.id);
        item.count--;
        return true;
    }
}
