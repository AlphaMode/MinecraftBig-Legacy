//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.world.level.biome;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.alphamode.mcbig.level.chunk.BigChunkPos;
import net.minecraft.server.util.LongHashMap;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class BigBiomeCache {
    private static final int DECAY_TIME = 1000 * 30;
    public static final int ZONE_SIZE_BITS = 4;
    public static final int ZONE_SIZE = 1 << ZONE_SIZE_BITS;
    public static final int ZONE_SIZE_MASK = ZONE_SIZE - 1;
    public static final BigInteger BIG_ZONE_SIZE_MASK = BigInteger.valueOf(ZONE_SIZE_MASK);
    private final BiomeSource source;
    private long lastUpdateTime = 0L;
    private final Object2ObjectMap<BigChunkPos, Block> cached = new Object2ObjectOpenHashMap<>();
    private final List<Block> all = new ArrayList<>();

    public BigBiomeCache(BiomeSource source) {
        this.source = source;
    }

    private Block getBlockAt(BigInteger x, BigInteger z) {
        x = x.shiftLeft(ZONE_SIZE_BITS);
        z = z.shiftLeft(ZONE_SIZE_BITS);
        BigChunkPos slot = new BigChunkPos(x, z);
        Block block = this.cached.get(slot);
        if (block == null) {
            block = new Block(slot);
            this.cached.put(slot, block);
            this.all.add(block);
        }

        block.lastUse = System.currentTimeMillis();
        return block;
    }

    public Biome getBiome(BigInteger x, BigInteger z) {
        return this.getBlockAt(x, z).getBiome(x, z);
    }

    public float getTemperature(BigInteger x, BigInteger z) {
        return this.getBlockAt(x, z).getTemperature(x, z);
    }

    public float getDownfall(BigInteger x, BigInteger z) {
        return this.getBlockAt(x, z).getDownfall(x, z);
    }

    public void update() {
        long now = System.currentTimeMillis();
        long utime = now - this.lastUpdateTime;
        if (utime > DECAY_TIME / 4 || utime < 0L) {
            this.lastUpdateTime = now;

            for (int i = 0; i < this.all.size(); i++) {
                Block block = this.all.get(i);
                long time = now - block.lastUse;
                if (time > DECAY_TIME || time < 0L) {
                    this.all.remove(i--);
                    BigChunkPos slot = block.pos;
                    this.cached.remove(slot);
                }
            }
        }
    }

    public Biome[] getBiomeBlockAt(BigInteger x, BigInteger z) {
        return this.getBlockAt(x, z).biomes;
    }

    public class Block {
        public float[] temps = new float[256];
        public float[] downfall = new float[256];
        public Biome[] biomes = new Biome[256];
        public BigChunkPos pos;
        public long lastUse;

        public Block(BigChunkPos pos) {
            this.pos = pos;
            BigInteger xc = pos.x().shiftLeft(ZONE_SIZE_BITS);
            BigInteger zc = pos.z().shiftLeft(ZONE_SIZE_BITS);
            source.getTemperatureBlock(this.temps, xc, zc, ZONE_SIZE, ZONE_SIZE);
            source.getDownfallBlock(this.downfall, xc, zc, ZONE_SIZE, ZONE_SIZE);
            source.getBiomeBlock(this.biomes, xc, zc, ZONE_SIZE, ZONE_SIZE, false);
        }

        public Biome getBiome(BigInteger x, BigInteger z) {
            return this.biomes[x.and(BIG_ZONE_SIZE_MASK).intValue() | (z.and(BIG_ZONE_SIZE_MASK).intValue()) << ZONE_SIZE_BITS];
        }

        public float getTemperature(BigInteger x, BigInteger z) {
            return this.temps[x.and(BIG_ZONE_SIZE_MASK).intValue() | (z.and(BIG_ZONE_SIZE_MASK).intValue()) << ZONE_SIZE_BITS];
        }

        public float getDownfall(BigInteger x, BigInteger z) {
            return this.downfall[x.and(BIG_ZONE_SIZE_MASK).intValue() | (z.and(BIG_ZONE_SIZE_MASK).intValue()) << ZONE_SIZE_BITS];
        }
    }
}
*///? }
