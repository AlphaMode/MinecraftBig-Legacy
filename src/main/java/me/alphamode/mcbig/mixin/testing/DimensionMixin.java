package me.alphamode.mcbig.mixin.testing;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.Dimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Dimension.class)
public class DimensionMixin {
    @Shadow
    public Level level;

//    /**
//     * @author
//     * @reason
//     */
//    @Overwrite
//    public ChunkSource createRandomLevelSource() {
//        return new FlatLevelSource(this.level);
//    }
}
