package me.alphamode.mcbig.mixin.tiles;

import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.HalfTransparentTile;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;

@Mixin(HalfTransparentTile.class)
public abstract class HalfTransparentTileMixin extends Tile {
    @Shadow
    private boolean allowSame;

    protected HalfTransparentTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public boolean shouldRenderFace(LevelSource level, BigInteger x, int y, BigInteger z, int face) {
        int tile = level.getTile(x, y, z);
        return !this.allowSame && tile == this.id ? false : super.shouldRenderFace(level, x, y, z, face);
    }
}
