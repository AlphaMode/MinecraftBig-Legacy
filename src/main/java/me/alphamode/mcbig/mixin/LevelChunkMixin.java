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

    @Shadow public List<Entity>[] entityBlocks;

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
            List<Entity> entityBlock = this.entityBlocks[var6];

            for (int i = 0; i < entityBlock.size(); ++i) {
                Entity e = entityBlock.get(i);
                if (e != ignore && ((BigEntityExtension)e).getBigBB().intersects(area)) {
                    entities.add(e);
                }
            }
        }
    }
}
