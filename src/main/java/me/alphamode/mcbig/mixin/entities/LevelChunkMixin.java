package me.alphamode.mcbig.mixin.entities;

import me.alphamode.mcbig.extensions.BigLevelChunkExtension;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(LevelChunk.class)
public class LevelChunkMixin implements BigLevelChunkExtension {

    @Shadow
    public Level level;

    /**
     * @author AlphaMode
     * @reason detach entities from chunks
     */
    @Overwrite
    public void addEntity(Entity e) {
        this.level.getEntityManager().getOrCreateEntitySections(getX(), getZ()).addEntity(e);
    }

    /**
     * @author AlphaMode
     * @reason detach entities from chunks
     */
    @Overwrite
    public void removeEntity(Entity e, int yc) {
        this.level.getEntityManager().getOrCreateEntitySections(getX(), getZ()).removeEntity(e, yc);
    }

    /**
     * @author AlphaMode
     * @reason detach entities from chunks
     */
    @Overwrite
    public void getEntities(Entity except, AABB bb, List<Entity> es) {
        this.level.getEntityManager().getOrCreateEntitySections(getX(), getZ()).getEntities(except, bb, es);
    }

    /**
     * @author AlphaMode
     * @reason detach entities from chunks
     */
    @Overwrite
    public <T extends Entity> void getEntitiesOfClass(Class<? extends T> ec, AABB bb, List<T> es) {
        this.level.getEntityManager().getOrCreateEntitySections(getX(), getZ()).getEntitiesOfClass(ec, bb, es);
    }
}
