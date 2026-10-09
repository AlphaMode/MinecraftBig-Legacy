package me.alphamode.mcbig.mixin.cubic;

import me.alphamode.mcbig.extensions.BigLevelExtension;
import me.alphamode.mcbig.extensions.BigLevelSourceExtension;
import me.alphamode.mcbig.level.cube.CubeSource;
import me.alphamode.mcbig.level.cube.LevelCube;
import me.alphamode.mcbig.level.cube.storage.CubeStorage;
import me.alphamode.mcbig.level.levelgen.CubeLevelSource;
import me.alphamode.mcbig.level.levelgen.ServerCubeCache;
import me.alphamode.mcbig.math.BigMath;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigInteger;

@Mixin(Level.class)
public abstract class CubicLevelMixin implements BigLevelExtension, BigLevelSourceExtension {

    @Shadow
    public abstract long getSeed();

    protected CubeSource cubeSource;

    @Inject(method = {
            "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/dimension/Dimension;)V",
            "<init>(Lnet/minecraft/world/level/storage/LevelStorage;Ljava/lang/String;Lnet/minecraft/world/level/dimension/Dimension;J)V",
            "<init>(Lnet/minecraft/world/level/storage/LevelStorage;Ljava/lang/String;JLnet/minecraft/world/level/dimension/Dimension;)V"
    }, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;createChunkSource()Lnet/minecraft/world/level/chunk/ChunkSource;"))
    private void onCreateChunkSource(CallbackInfo ci) {
        this.cubeSource = createCubeSource();
    }

    protected CubeSource createCubeSource() {
        CubeStorage storage = null;//this.levelStorage.createChunkStorage(this.dimension);
        return new ServerCubeCache((Level) (Object) this, storage, /*this.dimension.createRandomLevelSource()*/new CubeLevelSource((Level) (Object) this, getSeed()));
    }

    @Override
    public int getTile(BigInteger x, BigInteger y, BigInteger z) {
        return getCube(x.shiftRight(4), y.shiftRight(4), z.shiftRight(4)).getTile(BigMath.fastAnd(x, 15), BigMath.fastAnd(y, 15), BigMath.fastAnd(z, 15));
    }

    @Override
    public LevelCube getCube(BigInteger x, BigInteger y, BigInteger z) {
        return this.cubeSource.getCube(x, y, z);
    }
}
