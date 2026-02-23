package me.alphamode.mcbig.mixin;

import com.mojang.nbt.CompoundTag;
import me.alphamode.mcbig.extensions.BigLevelDataExtension;
import net.minecraft.world.level.storage.LevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigInteger;

@Mixin(LevelData.class)
public class LevelDataMixin implements BigLevelDataExtension {
    @Shadow
    private int spawnX;
    @Shadow
    private int spawnZ;
    @Shadow
    private int spawnY;
    private BigInteger spawnXBig = BigInteger.ZERO;
    private BigInteger spawnZBig = BigInteger.ZERO;

    @Inject(method = "<init>(Lcom/mojang/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void readBigSpawn(CompoundTag tag, CallbackInfo ci) {
        if (tag.hasKey("BigSpawnX")) {
            this.spawnXBig = new BigInteger(tag.getString("BigSpawnX"));
        } else {
            this.spawnXBig = BigInteger.valueOf(this.spawnX);
        }

        if (tag.hasKey("BigSpawnZ")) {
            this.spawnZBig = new BigInteger(tag.getString("BigSpawnZ"));
        } else {
            this.spawnZBig = BigInteger.valueOf(this.spawnZ);
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/storage/LevelData;)V", at = @At("TAIL"))
    private void getBigSpawnData(LevelData data, CallbackInfo ci) {
        this.spawnXBig = data.getBigSpawnX();
        this.spawnZBig = data.getBigSpawnZ();
    }

    @Inject(method = "save(Lcom/mojang/nbt/CompoundTag;Lcom/mojang/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void saveBigSpawn(CompoundTag base, CompoundTag player, CallbackInfo ci) {
        base.putString("BigSpawnX", this.spawnXBig.toString());
        base.putString("BigSpawnZ", this.spawnZBig.toString());
    }

    @Override
    public BigInteger getBigSpawnX() {
        return this.spawnXBig;
    }

    @Override
    public BigInteger getBigSpawnZ() {
        return this.spawnZBig;
    }

    @Override
    public void setBigSpawnX(BigInteger x) {
        this.spawnXBig = x;
    }

    @Override
    public void setBigSpawnZ(BigInteger z) {
        this.spawnZBig = z;
    }

    @Override
    public void setBigSpawnXYZ(BigInteger x, int y, BigInteger z) {
        this.spawnXBig = x;
        this.spawnY = y;
        this.spawnZBig = z;
    }
}
