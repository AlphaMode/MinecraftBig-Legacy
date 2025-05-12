package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.math.BigMath;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Slime.class)
public abstract class SlimeMixin extends Entity {
    public SlimeMixin(Level level) {
        super(level);
    }

    @Shadow public abstract int getSize();

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean canSpawn() {
        LevelChunk var1 = this.level.getChunkAt(BigMath.floor(this.x), BigMath.floor(this.z));
        return (this.getSize() == 1 || this.level.difficulty > 0) && this.random.nextInt(10) == 0 && var1.getRandom(987234911L).nextInt(10) == 0 && this.y < 16.0;
    }
}
