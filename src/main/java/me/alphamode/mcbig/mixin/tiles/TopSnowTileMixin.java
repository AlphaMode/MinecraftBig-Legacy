package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.extensions.BigTileExtension;
import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.stats.Stats;
import net.minecraft.util.Facing;
import net.minecraft.world.ItemInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.TopSnowTile;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Random;

@Mixin(TopSnowTile.class)
public abstract class TopSnowTileMixin extends Tile implements BigTileExtension {
    protected TopSnowTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public AABB getAABB(Level level, BigInteger x, int y, BigInteger z) {
        int var5 = level.getData(x, y, z) & 7;
        return var5 >= 3 ? AABB.newTemp(x.doubleValue() + this.xx0, y + this.yy0, z.doubleValue() + this.zz0, x.doubleValue() + this.xx1, y + 0.5F, z.doubleValue() + this.zz1) : null;
    }

    @Override
    public BigAABB getBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        int var5 = level.getData(x, y, z) & 7;
        return var5 >= 3 ? BigAABB.create(BigMath.addD(x, this.xx0), y + this.yy0, BigMath.addD(z, this.zz0), BigMath.addD(x, this.xx1), y + 0.5F, BigMath.addD(z, this.zz1)) : null;
    }

    @Override
    public void updateShape(LevelSource source, BigInteger x, int y, BigInteger z) {
        int var5 = source.getData(x, y, z) & 7;
        float var6 = 2 * (1 + var5) / 16.0F;
        this.setShape(0.0F, 0.0F, 0.0F, 1.0F, var6, 1.0F);
    }

    @Override
    public boolean mayPlace(Level level, BigInteger x, int y, BigInteger z) {
        int t = level.getTile(x, y - 1, z);
        return t != 0 && Tile.tiles[t].isSolidRender() ? level.getMaterial(x, y - 1, z).blocksMotion() : false;
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int tile) {
        this.update(level, x, y, z);
    }

    private boolean update(Level level, BigInteger x, int y, BigInteger z) {
        if (!this.mayPlace(level, x, y, z)) {
            this.dropResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BigInteger x, int y, BigInteger z, int meta) {
        int item = Item.SNOWBALL.id;
        float itemOffset = 0.7F;
        double xo = level.random.nextFloat() * itemOffset + (1.0F - itemOffset) * 0.5;
        double yo = level.random.nextFloat() * itemOffset + (1.0F - itemOffset) * 0.5;
        double zo = level.random.nextFloat() * itemOffset + (1.0F - itemOffset) * 0.5;
        ItemEntity e = new ItemEntity(level, x.doubleValue() + xo, y + yo, z.doubleValue() + zo, new ItemInstance(item, 1, 0));
        e.throwTime = 10;
        level.addEntity(e);
        level.setTile(x, y, z, 0);
        player.awardStat(Stats.blockMined[this.id], 1);
    }

    @Override
    public void tick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        if (level.getBrightness(LightLayer.BLOCK, x, y, z) > 11) {
            this.dropResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
        }
    }

    @Override
    public boolean shouldRenderFace(LevelSource level, BigInteger x, int y, BigInteger z, int face) {
        return face == Facing.UP ? true : super.shouldRenderFace(level, x, y, z, face);
    }
}
