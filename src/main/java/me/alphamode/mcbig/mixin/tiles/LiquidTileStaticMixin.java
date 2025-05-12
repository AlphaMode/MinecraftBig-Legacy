package me.alphamode.mcbig.mixin.tiles;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.LiquidTileStatic;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;

import java.math.BigInteger;
import java.util.Random;

@Mixin(LiquidTileStatic.class)
public abstract class LiquidTileStaticMixin extends Tile {
    protected LiquidTileStaticMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public void neighborChanged(Level level, BigInteger x, int y, BigInteger z, int tile) {
        super.neighborChanged(level, x, y, z, tile);
        if (level.getTile(x, y, z) == this.id) {
            this.setDynamic(level, x, y, z);
        }
    }

    private void setDynamic(Level level, BigInteger x, int y, BigInteger z) {
        int var5 = level.getData(x, y, z);
        level.noNeighborUpdate = true;
        level.setTileAndDataNoUpdate(x, y, z, this.id - 1, var5);
        level.setTilesDirty(x, y, z, x, y, z);
        level.addToTickNextTick(x, y, z, this.id - 1, this.getTickDelay());
        level.noNeighborUpdate = false;
    }

    @Override
    public void tick(Level level, BigInteger x, int y, BigInteger z, Random random) {
        if (this.material == Material.LAVA) {
            int var6 = random.nextInt(3);

            for(int var7 = 0; var7 < var6; ++var7) {
                x = x.add(BigInteger.valueOf(random.nextInt(3) - 1));
                ++y;
                z = z.add(BigInteger.valueOf(random.nextInt(3) - 1));
                int var8 = level.getTile(x, y, z);
                if (var8 == 0) {
                    if (this.isFlammable(level, x.subtract(BigInteger.ONE), y, z)
                            || this.isFlammable(level, x.add(BigInteger.ONE), y, z)
                            || this.isFlammable(level, x, y, z.subtract(BigInteger.ONE))
                            || this.isFlammable(level, x, y, z.add(BigInteger.ONE))
                            || this.isFlammable(level, x, y - 1, z)
                            || this.isFlammable(level, x, y + 1, z)) {
                        level.setTile(x, y, z, Tile.FIRE.id);
                        return;
                    }
                } else if (Tile.tiles[var8].material.blocksMotion()) {
                    return;
                }
            }
        }
    }

    private boolean isFlammable(Level level, BigInteger x, int y, BigInteger z) {
        return level.getMaterial(x, y, z).isFlammable();
    }
}
