//? >=1.0.0-beta.8.0.r {
/*package me.alphamode.mcbig.mixin.worldgen.biome;

import me.alphamode.mcbig.extensions.biome.BigBiomeDecoratorExtension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeDecorator;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.Random;

@Mixin(BiomeDecorator.class)
public abstract class BiomeDecoratorMixin implements BigBiomeDecoratorExtension {
    private BigInteger xoBig = BigInteger.ZERO;
    private BigInteger zoBig = BigInteger.ZERO;

    @Shadow
    private Level level;

    @Shadow
    private Random random;

    @Shadow
    protected abstract void decorateOres();

    @Shadow
    protected int sandCount;

    @Shadow
    protected int clayCount;

    @Shadow
    protected int gravelCount;

    @Shadow
    protected int treeCount;

    @Shadow
    private Biome biome;

    @Shadow
    protected int flowerCount;

    @Shadow
    protected Feature yellowFlowerFeature;

    @Shadow
    protected Feature roseFlowerFeature;

    @Shadow
    protected int grassCount;

    @Shadow
    protected int deadBushCount;

    @Shadow
    protected Feature brownMushroomFeature;

    @Shadow
    protected Feature redMushroomFeature;

    @Shadow
    protected int reedsCount;

    @Shadow
    protected Feature reedsFeature;

    @Shadow
    protected int cactusCount;

    @Shadow
    protected Feature cactusFeature;

    @Shadow
    protected Feature sandFeature;

    @Shadow
    protected Feature clayFeature;

    @Shadow
    protected int waterlilyCount;

    @Override
    public void decorate(Level level, Random random, BigInteger xo, BigInteger zo) {
//        if (this.level != null) {
//            throw new RuntimeException("Already decorating!!");
//        } else {
            this.level = level;
            this.random = random;
            this.xoBig = xo;
            this.zoBig = zo;
            this.decorate();
            this.level = null;
            this.random = null;
//        }
    }

    /^*
     * @author
     * @reason
     ^/
    @Overwrite
    public void decorate() {
        this.decorateOres();

        for (int i = 0; i < this.sandCount; i++) {
            BigInteger x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            this.sandFeature.place(this.level, this.random, x, this.level.getTopSolidBlock(x, z), z);
        }

        for (int i = 0; i < this.clayCount; i++) {
            BigInteger x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            this.clayFeature.place(this.level, this.random, x, this.level.getTopSolidBlock(x, z), z);
        }

        for (int i = 0; i < this.gravelCount; i++) {
            BigInteger x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            this.sandFeature.place(this.level, this.random, x, this.level.getTopSolidBlock(x, z), z);
        }

        int forests = this.treeCount;
        if (this.random.nextInt(10) == 0) {
            forests++;
        }

        for (int i = 0; i < forests; i++) {
            BigInteger x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            Feature tree = this.biome.getTreeFeature(this.random);
            tree.init(1.0, 1.0, 1.0);
            tree.place(this.level, this.random, x, this.level.getHeightmap(x, z), z);
        }

        for (int i = 0; i < this.flowerCount; i++) {
            BigInteger x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            this.yellowFlowerFeature.place(this.level, this.random, x, y, z);
            if (this.random.nextInt(4) == 0) {
                x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
                y = this.random.nextInt(128);
                z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
                this.roseFlowerFeature.place(this.level, this.random, x, y, z);
            }
        }

        for (int i = 0; i < this.grassCount; i++) {
            byte grassType = 1;
            BigInteger x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new GrassFeature(Tile.tallgrass.id, grassType).place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < this.deadBushCount; i++) {
            BigInteger x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new BushFeature(Tile.deadBush.id).place(this.level, this.random, x, y, z);
        }

        // This is mushroomCount ornithe intermediary is just wrong
        for (int i = 0; i < this.waterlilyCount; i++) {
            if (this.random.nextInt(4) == 0) {
                BigInteger x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
                BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
                int y = this.level.getHeightmap(x, z);
                this.brownMushroomFeature.place(this.level, this.random, x, y, z);
            }

            if (this.random.nextInt(8) == 0) {
                BigInteger x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
                BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
                int y = this.random.nextInt(128);
                this.redMushroomFeature.place(this.level, this.random, x, y, z);
            }
        }

        if (this.random.nextInt(4) == 0) {
            BigInteger x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            this.brownMushroomFeature.place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(8) == 0) {
            BigInteger x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            this.redMushroomFeature.place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < this.reedsCount; i++) {
            BigInteger x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            this.reedsFeature.place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 10; i++) {
            BigInteger x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            this.reedsFeature.place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(32) == 0) {
            BigInteger x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new PumpkinFeature().place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < this.cactusCount; i++) {
            BigInteger x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(128);
            BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            this.cactusFeature.place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 50; i++) {
            BigInteger x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(this.random.nextInt(128 - 8) + 8);
            BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new SpringFeature(Tile.water.id).place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 20; i++) {
            BigInteger x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            int y = this.random.nextInt(this.random.nextInt(this.random.nextInt(128 - 16) + 8) + 8);
            BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16) + 8));
            new SpringFeature(Tile.lava.id).place(this.level, this.random, x, y, z);
        }
    }

    /^*
     * @author
     * @reason
     ^/
    @Overwrite
    public void decorateDepthSpan(int count, Feature feature, int y0, int y1) {
        for (int i = 0; i < count; i++) {
            int rx = this.random.nextInt(16);
            BigInteger x = this.xoBig.add(BigInteger.valueOf(rx));
            int y = this.random.nextInt(y1 - y0) + y0;
            BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16)));
            feature.place(this.level, this.random, x, y, z);
        }
    }

    /^*
     * @author
     * @reason
     ^/
    @Overwrite
    public void decorateDepthAverage(int count, Feature feature, int yMid, int ySpan) {
        for (int i = 0; i < count; i++) {
            BigInteger x = this.xoBig.add(BigInteger.valueOf(this.random.nextInt(16)));
            int y = this.random.nextInt(ySpan) + this.random.nextInt(ySpan) + (yMid - ySpan);
            BigInteger z = this.zoBig.add(BigInteger.valueOf(this.random.nextInt(16)));
            feature.place(this.level, this.random, x, y, z);
        }
    }
}
*///? }
