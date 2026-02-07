package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.BigLargeFeatureExtension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.levelgen.LargeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(LargeFeature.class)
public class LargeFeatureMixin implements BigLargeFeatureExtension {
    @Shadow protected int radius;

    @Shadow protected Random random;

    @Override
    public void apply(ChunkSource source, Level level, BigInteger x, BigInteger z, byte[] tiles) {
        BigInteger r = BigInteger.valueOf(this.radius);
        this.random.setSeed(level.getSeed());
        long xr = this.random.nextLong() / 2L * 2L + 1L;
        long zr = this.random.nextLong() / 2L * 2L + 1L;

        for(BigInteger minX = x.subtract(r); minX.compareTo(x.add(r)) <= 0; minX = minX.add(BigInteger.ONE)) {
            for(BigInteger minZ = z.subtract(r); minZ.compareTo(z.add(r)) <= 0; minZ = minZ.add(BigInteger.ONE)) {
                this.random.setSeed(minX.longValue() * xr + minZ.longValue() * zr ^ level.getSeed());
                addFeature(level, minX, minZ, x, z, tiles);
            }
        }
    }

    @Override
    public void addFeature(Level level, BigInteger minX, BigInteger minZ, BigInteger maxX, BigInteger maxZ, byte[] tiles) {

    }
}
