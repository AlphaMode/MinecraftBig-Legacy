package me.alphamode.mcbig.level.entity;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.alphamode.mcbig.level.chunk.BigChunkPos;
import me.alphamode.mcbig.level.chunk.storage.ChunkEntities;
import me.alphamode.mcbig.level.chunk.storage.EntityStorage;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Detach entities from chunks to allow the world to load while chunks are loading
public class EntityManager {
    private final Level level;
    private final EntityStorage storage;
    private final Object2ObjectMap<BigChunkPos, EntitySections> sections = new Object2ObjectOpenHashMap<>();

    public EntityManager(Level level, EntityStorage storage) {
        this.level = level;
        this.storage = storage;
    }

    @Nullable
    public EntitySections getEntitySections(BigInteger x, BigInteger z) {
        return sections.get(new BigChunkPos(x, z));
    }

    public EntitySections getOrCreateEntitySections(BigInteger x, BigInteger z) {
        return this.sections.computeIfAbsent(new BigChunkPos(x, z), pos -> new EntitySections(this.level, x, z));
    }

    public boolean hasSection(BigInteger x, BigInteger z) {
        return this.sections.containsKey(new BigChunkPos(x, z));
    }

    public boolean save(boolean force, ProgressListener listener) {
        for (EntitySections section : this.sections.values()) {
            List<Entity> entities = new ArrayList<>();
            for (List<Entity> entityList : section.entityBlocks) {
                entities.addAll(entityList);
            }
            this.storage.save(this.level, new ChunkEntities(new BigChunkPos(section.getX(), section.getZ()), entities));
        }

        return true;
    }
}
