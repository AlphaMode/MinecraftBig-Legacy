package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.extensions.BigLevelChunkExtension;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import me.alphamode.mcbig.world.phys.BigAABB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin implements BigLevelChunkExtension {
    @Shadow public abstract int getHeightmap(int x, int z);

    @Shadow public List[] entityBlocks;

    @Inject(method = {"<init>(Lnet/minecraft/world/level/Level;II)V", "<init>(Lnet/minecraft/world/level/Level;[BII)V"}, at = @At("TAIL"))
    private void throwOnVanillaInit(CallbackInfo ci) {
        if (!((Object) this instanceof BigLevelChunk) && !((Object) this instanceof EmptyLevelChunk)) {
            throw new RuntimeException("Level chunk is not an instance of BigLevelChunk");
        }
    }

    @Override
    public void getEntities(Entity ignore, BigAABB area, List<Entity> entities) {
        int var4 = Mth.floor((area.y0 - 2.0) / 16.0);
        int var5 = Mth.floor((area.y1 + 2.0) / 16.0);
        if (var4 < 0) {
            var4 = 0;
        }

        if (var5 >= this.entityBlocks.length) {
            var5 = this.entityBlocks.length - 1;
        }

        for(int var6 = var4; var6 <= var5; ++var6) {
            List var7 = this.entityBlocks[var6];

            for(int var8 = 0; var8 < var7.size(); ++var8) {
                Entity var9 = (Entity)var7.get(var8);
                if (var9 != ignore && ((BigEntityExtension)var9).getBigBB().intersects(area)) {
                    entities.add(var9);
                }
            }
        }
    }
}
