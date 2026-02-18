package me.alphamode.mcbig.mixin.tiles;

import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.CakeTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigDecimal;
import java.math.BigInteger;

@Mixin(CakeTile.class)
public abstract class CakeTileMixin extends Tile {
    protected CakeTileMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public void updateShape(LevelSource source, BigInteger x, int y, BigInteger z) {
        int data = source.getData(x, y, z);
        float epsilon = 0.0625F;
        float bitesSize = (1 + data * 2) / 16.0F;
        float height = 0.5F;
        this.setShape(bitesSize, 0.0F, epsilon, 1.0F - epsilon, height, 1.0F - epsilon);
    }

    @Override
    public AABB getAABB(Level level, BigInteger x, int y, BigInteger z) {
        int data = level.getData(x, y, z);
        float epsilon = 0.0625F;
        float bitesSize = (1 + data * 2) / 16.0F;
        float height = 0.5F;
        return AABB.newTemp(x.doubleValue() + bitesSize, y, z.doubleValue() + epsilon, x.add(BigInteger.ONE).doubleValue() - epsilon, y + height - epsilon, z.add(BigInteger.ONE).doubleValue() - epsilon);
    }

    @Override
    public BigAABB getBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        int data = level.getData(x, y, z);
        float epsilon = 0.0625F;
        BigDecimal bitesSize = BigDecimal.valueOf((1 + data * 2) / 16.0F);
        float height = 0.5F;
        return BigAABB.create(new BigDecimal(x).add(bitesSize), y, new BigDecimal(z).add(BigConstants.EPSILON), new BigDecimal(x.add(BigInteger.ONE)).subtract(BigConstants.EPSILON), y + height - epsilon, new BigDecimal(z.add(BigInteger.ONE)).subtract(BigConstants.EPSILON));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public AABB getTileAABB(Level level, BigInteger x, int y, BigInteger z) {
        int data = level.getData(x, y, z);
        float epsilon = 0.0625F;
        float bitesSize = (1 + data * 2) / 16.0F;
        float height = 0.5F;
        return AABB.newTemp(x.doubleValue() + bitesSize, y, z.doubleValue() + epsilon, x.add(BigInteger.ONE).doubleValue() - epsilon, y + height, z.add(BigInteger.ONE).doubleValue() - epsilon);
    }

    @Override
    public BigAABB getTileBigAABB(Level level, BigInteger x, int y, BigInteger z) {
        int data = level.getData(x, y, z);
        float epsilon = 0.0625F;
        BigDecimal bitesSize = BigDecimal.valueOf((1 + data * 2) / 16.0F);
        float height = 0.5F;
        return BigAABB.create(new BigDecimal(x).add(bitesSize), y, new BigDecimal(z).add(BigConstants.EPSILON), new BigDecimal(x.add(BigInteger.ONE)).subtract(BigConstants.EPSILON), y + height, new BigDecimal(z.add(BigInteger.ONE)).subtract(BigConstants.EPSILON));
    }

    @Override
    public boolean use(Level level, BigInteger x, int y, BigInteger z, Player player) {
        this.eat(level, x, y, z, player);
        return true;
    }

    @Override
    public void attack(Level level, BigInteger x, int y, BigInteger z, Player player) {
        this.eat(level, x, y, z, player);
    }

    private void eat(Level level, BigInteger x, int y, BigInteger z, Player player) {
        if (player.health < 20) {
            player.heal(3);
            int data = level.getData(x, y, z) + 1;
            if (data >= 6) {
                level.setTile(x, y, z, 0);
            } else {
                level.setData(x, y, z, data);
                level.setTileDirty(x, y, z);
            }
        }
    }

    @Override
    public boolean mayPlace(Level level, BigInteger x, int y, BigInteger z) {
        return !super.mayPlace(level, x, y, z) ? false : this.canPlace(level, x, y, z);
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int tile) {
        if (!this.canPlace(level, x, y, z)) {
            this.dropResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
        }
    }

    @Override
    public boolean canPlace(Level level, BigInteger x, int y, BigInteger z) {
        return level.getMaterial(x, y - 1, z).isSolid();
    }
}
