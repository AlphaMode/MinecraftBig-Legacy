//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.mixin.worldgen.features;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SandFeature;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(SandFeature.class)
public abstract class SandFeatureMixin extends Feature {
    @Shadow
    private int radius;

    @Shadow
    private int tile;

    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        if (level.getMaterial(x, y, z) != Material.water) return false;

        int r = random.nextInt(this.radius - 2) + 2;
        var _r = BigInteger.valueOf(r);
        int yr = 2;

        for (var xx = x.subtract(_r); xx.compareTo(x.add(_r)) <= 0; xx = xx.add(BigInteger.ONE)) {
            for (var zz = z.subtract(_r); zz.compareTo(z.add(_r)) <= 0; zz = zz.add(BigInteger.ONE)) {
                int xd = xx.subtract(x).intValue();
                int zd = zz.subtract(z).intValue();
                if (xd * xd + zd * zd <= r * r) {
                    for (int yy = y - yr; yy <= y + yr; yy++) {
                        int t = level.getTile(xx, yy, zz);
                        if (t == Tile.dirt.id || t == Tile.grass.id) {
                            level.setTileNoUpdate(xx, yy, zz, this.tile);
                        }
                    }
                }
            }
        }

        return true;
    }
}
*///? }