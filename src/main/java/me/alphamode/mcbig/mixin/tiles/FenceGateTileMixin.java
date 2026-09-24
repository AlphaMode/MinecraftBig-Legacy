//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.world.phys.BigAABB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.FenceGateTile;
import net.minecraft.world.level.tile.LevelEvent;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;
import java.math.BigInteger;

@Mixin(FenceGateTile.class)
public abstract class FenceGateTileMixin extends Tile {
    private static final int OPEN_BIT = 4;

    @Shadow
    public static boolean isOpen(int data) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    public static int getDirection(int data) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    protected FenceGateTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public boolean mayPlace(Level level, BigInteger x, int y, BigInteger z) {
        return !level.getMaterial(x, y - 1, z).isSolid() ? false : super.mayPlace(level, x, y, z);
    }

    @Override
    public AABB getAABB(Level level, BigInteger x, int y, BigInteger z) {
        int data = level.getData(x, y, z);
        return isOpen(data) ? null : AABB.newTemp(x.doubleValue(), y, z.doubleValue(), x.add(BigInteger.ONE).doubleValue(), y + 1.5F, z.add(BigInteger.ONE).doubleValue());
    }

    @Override
    public BigAABB getBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        int data = level.getData(x, y, z);
        return isOpen(data) ? null : BigAABB.create(new BigDecimal(x), y, new BigDecimal(z), new BigDecimal(x.add(BigInteger.ONE)), y + 1.5F, new BigDecimal(z.add(BigInteger.ONE)));
    }

    @Override
    public void setPlacedBy(Level level, BigInteger x, int y, BigInteger z, Mob entity) {
        int dir = (Mth.floor(entity.yRot * 4.0F / 360.0F + 0.5) & 3) % 4;
        level.setData(x, y, z, dir);
    }

    @Override
    public boolean use(Level level, BigInteger x, int y, BigInteger z, Player player) {
        int data = level.getData(x, y, z);
        if (isOpen(data)) {
            level.setData(x, y, z, data & -5);
        } else {
            // open the door from the player
            int dir = (Mth.floor(player.yRot * 4.0F / 360.0F + 0.5) & 3) % 4;
            int current = getDirection(data);
            if (current == (dir + 2) % 4) {
                data = dir;
            }

            level.setData(x, y, z, data | OPEN_BIT);
        }

        level.levelEvent(player, LevelEvent.SOUND_OPEN_OR_CLOSE, x, y, z, 0);
        return true;
    }
}
*///? }
