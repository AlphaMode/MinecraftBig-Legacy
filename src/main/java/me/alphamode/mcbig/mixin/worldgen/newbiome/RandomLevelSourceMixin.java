package me.alphamode.mcbig.mixin.worldgen.newbiome;

import dev.kikugie.fletching_table.mixin.MixinIgnore;
import me.alphamode.mcbig.extensions.BigChunkSourceExtension;
import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.LargeFeature;
import net.minecraft.world.level.levelgen.RandomLevelSource;
//? >=1.0.0-beta.8.0.r {
/*import net.minecraft.world.level.levelgen.feature.StrongholdFeature;
import net.minecraft.world.level.levelgen.structure.MineShaftFeature;
import net.minecraft.world.level.levelgen.structure.VillageFeature;
*///? }
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@MixinIgnore
@Mixin(RandomLevelSource.class)
public abstract class RandomLevelSourceMixin implements ChunkSource, BigChunkSourceExtension {

    @Shadow
    private Random random;

    @Shadow
    public abstract void prepareHeights(int par1, int par2, byte[] par3);

    @Shadow
    private Biome[] biomes;

    @Shadow
    private Level level;

    @Shadow
    public abstract void buildSurfaces(int xOffs, int zOffs, byte[] blocks, Biome[] biomes);

    @Shadow
    private LargeFeature caveFeature;

    //? >=1.0.0-beta.8.0.r {
    /*@Shadow
    @Final
    private boolean generateStructures;

    @Shadow
    public StrongholdFeature strongholdFeature;

    @Shadow
    public MineShaftFeature mineShaftFeature;

    @Shadow
    public VillageFeature villageFeature;

    @Shadow
    private LargeFeature canyonFeature;
    *///? }

    @Override
    public boolean hasChunk(BigInteger x, BigInteger z) {
        return true;
    }

    @Override
    public LevelChunk getChunk(BigInteger x, BigInteger z) {
        this.random.setSeed(x.longValue() * 341873128712L + z.longValue() * 132897987541L);
        byte[] blocks = new byte[16 * 128 * 16];
        BigLevelChunk lc = new BigLevelChunk(this.level, blocks, x, z);
        this.prepareHeights(x.intValue(), z.intValue(), blocks);
        this.biomes = this.level.getBiomeSource().getBiomeBlock(this.biomes, x.intValue() * 16, z.intValue() * 16, 16, 16);
        this.buildSurfaces(x.intValue(), z.intValue(), blocks, this.biomes);
        this.caveFeature.apply(this, this.level, x, z, blocks);
        //? >=1.0.0-beta.8.0.r {
        /*if (this.generateStructures) {
            this.strongholdFeature.apply(this, this.level, x, z, blocks);
            this.mineShaftFeature.apply(this, this.level, x, z, blocks);
            this.villageFeature.apply(this, this.level, x, z, blocks);
        }

        this.canyonFeature.apply(this, this.level, x, z, blocks);
        *///? }
        lc.recalcHeightmap();
        return lc;
    }

    @Override
    public LevelChunk create(BigInteger x, BigInteger z) {
        return getChunk(x, z);
    }

    @Override
    public void postProcess(ChunkSource generator, BigInteger x, BigInteger z) {
        this.postProcess(generator, x.intValue(), z.intValue());
    }
}
