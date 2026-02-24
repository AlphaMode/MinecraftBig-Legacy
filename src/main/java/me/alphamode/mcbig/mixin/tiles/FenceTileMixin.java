package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.FenceTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigDecimal;
import java.math.BigInteger;

@Mixin(FenceTile.class)
public abstract class FenceTileMixin extends Tile implements BigTileExtension {
    protected FenceTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public boolean mayPlace(Level level, BigInteger x, int y, BigInteger z) {
        if (level.getTile(x, y - 1, z) == this.id) {
            return true;
        } else {
            return !level.getMaterial(x, y - 1, z).isSolid() ? false : super.mayPlace(level, x, y, z);
        }
    }

    @Override
    public AABB getAABB(Level level, BigInteger x, int y, BigInteger z) {
        return AABB.newTemp(x.doubleValue(), y, z.doubleValue(), x.add(BigInteger.ONE).doubleValue(), y + 1.5F, z.add(BigInteger.ONE).doubleValue());
    }

    @Override
    public BigAABB getBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        return BigAABB.create(new BigDecimal(x), y, new BigDecimal(z), new BigDecimal(x.add(BigInteger.ONE)), y + 1.5F, new BigDecimal(z.add(BigInteger.ONE)));
    }
}
