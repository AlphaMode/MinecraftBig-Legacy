package me.alphamode.mcbig.mixin.features.big_movement;

import me.alphamode.mcbig.client.renderer.BigChunk;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import net.minecraft.client.renderer.Chunk;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Mixin(value = BigChunk.class, remap = false)
public abstract class BigChunkMixin extends Chunk {
    @Shadow
    public BigInteger bigXm;
    @Shadow
    public BigInteger bigZm;

    public BigChunkMixin(Level level, List tileEntities, int x, int y, int z, int size, int lists) {
        super(level, tileEntities, x, y, z, size, lists);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public float distanceToSqr(Entity entity) {
        float var2 = (((BigEntityExtension)entity).getX().subtract(new BigDecimal(this.bigXm))).floatValue();
        float var3 = (float)(entity.y - (double)this.ym);
        float var4 = (((BigEntityExtension)entity).getZ().subtract(new BigDecimal(this.bigZm))).floatValue();
        return var2 * var2 + var3 * var3 + var4 * var4;
    }
}
