package me.alphamode.mcbig.level.chunk.storage;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.NbtIo;
import me.alphamode.mcbig.level.chunk.BigChunkPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityIO;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

// Store entities separate from chunks so we can load the game without waiting for chunks
public class McRegionEntityStorage implements EntityStorage {

    private final File baseDir;

    public McRegionEntityStorage(File baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public CompletableFuture<ChunkEntities> load(Level level, BigChunkPos pos) {
        InputStream stream = BigRegionFileCache.getChunkDataInputStream(this.baseDir, pos.x(), pos.z());

        if (stream != null) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    CompoundTag tag = NbtIo.read(stream);
                    return Optional.of(tag);
                } catch (Exception e) {
                    return Optional.<CompoundTag>empty();
                }
            }).thenApplyAsync(optionalTag -> {
                if (optionalTag.isEmpty())
                    return new ChunkEntities(pos, List.of());
                CompoundTag tag = optionalTag.get();
                if (!tag.contains("Entities")) {
                    System.out.println("Chunk file at " + pos.x() + "," + pos.z() + " is missing entity data, skipping");
                    return new ChunkEntities(pos, List.of());
                }
                ChunkEntities entities = load(level, tag);
                final BigInteger x = pos.x();
                final BigInteger z = pos.z();
                if (!entities.isAt(x, z)) {
                    System.out
                            .println("Chunk entities file at " + x + "," + z + " is in the wrong location; relocating. (Expected " + x + ", " + z + ", got " + entities.pos().x() + ", " + entities.pos().z() + ")");
                    tag.putString("xPos", x.toString());
                    tag.putString("zPos", z.toString());
                    entities = load(level, tag);
                }
                return entities;
            });
        }

        return CompletableFuture.completedFuture(new ChunkEntities(pos, List.of()));
    }

    @Override
    public void save(Level level, ChunkEntities entities) {
        level.checkSession();

        try {
            OutputStream output = BigRegionFileCache.getChunkDataOutputStream(this.baseDir, entities.pos().x(), entities.pos().z());
            CompoundTag rootTag = new CompoundTag();
            CompoundTag entitiesTag = new CompoundTag();
            rootTag.putTag("Entities", entitiesTag);
            save(entities, level, entitiesTag);
            NbtIo.write(rootTag, output);
            output.close();
            LevelData var6 = level.getLevelData();
            var6.setSize(var6.getSize() + BigRegionFileCache.getSizeDelta(this.baseDir, entities.pos().x(), entities.pos().z()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ChunkEntities load(Level level, CompoundTag tag) {
        BigInteger x = new BigInteger(tag.getString("xPos"));
        BigInteger z = new BigInteger(tag.getString("zPos"));
        ListTag entitiesTag = tag.getList("Entities");
        List<Entity> entities = new ArrayList<>();
        if (entitiesTag != null) {
            for (int i = 0; i < entitiesTag.size(); i++) {
                CompoundTag entityTag = (CompoundTag) entitiesTag.get(i);
                Entity e = EntityIO.loadStatic(entityTag, level);
                if (e != null) {
                    entities.add(e);
                }
            }
        }

        return new ChunkEntities(new BigChunkPos(x, z), Collections.unmodifiableList(entities));
    }

    public static void save(ChunkEntities entities, Level level, CompoundTag data) {
        level.checkSession();
        data.putString("xPos", entities.pos().x().toString());
        data.putString("zPos", entities.pos().z().toString());
        data.putLong("LastUpdate", level.getTime());
        ListTag entitiesTag = new ListTag();

        for(Entity entity : entities.entities()) {
            CompoundTag tag = new CompoundTag();
            if (entity.save(tag)) {
                entitiesTag.add(tag);
            }
        }

        data.putTag("Entities", entitiesTag);
    }
}
