package me.alphamode.mcbig.mixin.testing;

import me.alphamode.mcbig.testing.FlatLevelSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.Dimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Dimension.class)
public class DimensionMixin {
    @Shadow
    public Level level;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public ChunkSource createRandomLevelSource() {
        return new FlatLevelSource(this.level);
    }
}
