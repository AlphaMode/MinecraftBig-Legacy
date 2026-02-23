package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.TileEntityTile;
import net.minecraft.world.level.tile.entity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(TileEntityTile.class)
public abstract class TileEntityTileMixin extends Tile implements BigTileExtension {
    @Shadow
    protected abstract TileEntity newTileEntity();

    protected TileEntityTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public void onPlace(Level level, BigInteger x, int y, BigInteger z) {
        super.onPlace(level, x, y, z);
        level.setTileEntity(x, y, z, this.newTileEntity());
    }

    @Override
    public void onRemove(Level level, BigInteger x, int y, BigInteger z) {
        super.onRemove(level, x, y, z);
        level.removeTileEntity(x, y, z);
    }
}
