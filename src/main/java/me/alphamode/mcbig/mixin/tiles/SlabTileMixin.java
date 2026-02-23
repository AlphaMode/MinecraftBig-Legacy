package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import net.minecraft.util.Facing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.SlabTile;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;

@Mixin(SlabTile.class)
public abstract class SlabTileMixin extends Tile implements BigTileExtension {
    protected SlabTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public void onPlace(Level level, BigInteger x, int y, BigInteger z) {
        if (this != Tile.SLAB) {
            super.onPlace(level, x, y, z);
        }

        int var5 = level.getTile(x, y - 1, z);
        int var6 = level.getData(x, y, z);
        int var7 = level.getData(x, y - 1, z);
        if (var6 == var7) {
            if (var5 == SLAB.id) {
                level.setTile(x, y, z, 0);
                level.setTileAndData(x, y - 1, z, Tile.DOUBLE_SLAB.id, var6);
            }
        }
    }

    @Override
    public boolean shouldRenderFace(LevelSource level, BigInteger x, int y, BigInteger z, int face) {
        if (this != Tile.SLAB) {
            super.shouldRenderFace(level, x, y, z, face);
        }

        if (face == Facing.UP) {
            return true;
        } else if (!super.shouldRenderFace(level, x, y, z, face)) {
            return false;
        } else {
            return face == Facing.DOWN ? true : level.getTile(x, y, z) != this.id;
        }
    }
}
