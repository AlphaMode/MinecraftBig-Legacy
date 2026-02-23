package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.HalfTransparentTile;
import net.minecraft.world.level.tile.IceTile;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;
import java.util.Random;

@Mixin(IceTile.class)
public abstract class IceTileMixin extends HalfTransparentTile implements BigTileExtension {
    protected IceTileMixin(int id, int tex, Material material, boolean allowSame) {
        super(id, tex, material, allowSame);
    }

    @Override
    public boolean shouldRenderFace(LevelSource level, BigInteger x, int y, BigInteger z, int face) {
        return super.shouldRenderFace(level, x, y, z, 1 - face);
    }

    @Override
    public void playerDestroy(Level level, Player player, BigInteger x, int y, BigInteger z, int meta) {
        super.playerDestroy(level, player, x, y, z, meta);
        Material var7 = level.getMaterial(x, y - 1, z);
        if (var7.blocksMotion() || var7.isLiquid()) {
            level.setTile(x, y, z, Tile.FLOWING_WATER.id);
        }

    }

    @Override
    public void tick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        if (level.getBrightness(LightLayer.BLOCK, x, y, z) > 11 - Tile.lightBlock[this.id]) {
            this.dropResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, Tile.WATER.id);
        }
    }
}
