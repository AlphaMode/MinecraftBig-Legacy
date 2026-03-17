package me.alphamode.mcbig.mixin.entities;

import me.alphamode.mcbig.extensions.BigLevelExtension;
import me.alphamode.mcbig.level.chunk.storage.EntityStorage;
import me.alphamode.mcbig.level.entity.EntityManager;
import me.alphamode.mcbig.world.level.levelgen.ThreadedChunkCache;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.levelgen.ServerChunkCache;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigInteger;

@Mixin(priority = 1000, value = Level.class)
public class LevelMixin implements BigLevelExtension {
    private EntityManager entityManager;

    @Inject(method = "<init>*", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;createLevelSource()Lnet/minecraft/world/level/chunk/ChunkSource;"))
    private void createEntityManager(CallbackInfo ci) {
        this.entityManager = createEntityManager();
    }

    @Shadow
    @Final
    protected LevelStorage levelStorage;

    @Shadow
    @Final
    public Dimension dimension;

    private EntityManager createEntityManager() {
        EntityStorage storage = this.levelStorage.createEntityStorage(this.dimension);
        return new EntityManager((Level) (Object) this, storage);
    }

    @Override
    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public ChunkSource createLevelSource() {
        ChunkStorage storage = this.levelStorage.createChunkStorage(this.dimension);
        return new ServerChunkCache((Level) (Object) this, storage, this.dimension.createRandomLevelSource());
    }

    @Inject(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/ChunkSource;save(ZLnet/minecraft/util/ProgressListener;)Z"))
    private void saveEntities(boolean force, ProgressListener listener, CallbackInfo ci) {
        this.entityManager.save(force, listener);
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Redirect(method = "tick(Lnet/minecraft/world/entity/Entity;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;hasChunksAt(Ljava/math/BigInteger;ILjava/math/BigInteger;Ljava/math/BigInteger;ILjava/math/BigInteger;)Z"))
    private boolean alwaysHasChunks(Level instance, BigInteger x0, int y0, BigInteger z0, BigInteger x1, int y1, BigInteger z1) {
        return true;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Redirect(
            method = "tick(Lnet/minecraft/world/entity/Entity;Z)V",
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/world/level/Level;hasChunk(Ljava/math/BigInteger;Ljava/math/BigInteger;)Z",
                    ordinal = 0
            )
    )
    private boolean removeEntityFromSection(Level instance, BigInteger x, BigInteger z, Entity e, boolean actual) {
        if (this.entityManager.hasSection(x, z)) {
            this.entityManager.getEntitySections(e.getXChunk(), e.getZChunk()).removeEntity(e, e.yChunk);
        }
        return false;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Redirect(
            method = "tick(Lnet/minecraft/world/entity/Entity;Z)V",
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/world/level/Level;hasChunk(Ljava/math/BigInteger;Ljava/math/BigInteger;)Z",
                    ordinal = 1
            )
    )
    private boolean addEntityToSection(Level instance, BigInteger x, BigInteger z, Entity e, boolean actual) {
        e.inChunk = true;
        this.entityManager.getOrCreateEntitySections(x, z).addEntity(e);
        return false;
    }
}
