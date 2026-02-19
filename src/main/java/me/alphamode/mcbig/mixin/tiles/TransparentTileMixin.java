package me.alphamode.mcbig.mixin.tiles;

import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.TransparentTile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(TransparentTile.class)
public abstract class TransparentTileMixin extends Tile {
    @Shadow
    protected boolean allowSame;

    protected TransparentTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public boolean shouldRenderFace(LevelSource level, BigInteger x, int y, BigInteger z, int face) {
        int tile = level.getTile(x, y, z);
        return !this.allowSame && tile == this.id ? false : super.shouldRenderFace(level, x, y, z, face);
    }
}
