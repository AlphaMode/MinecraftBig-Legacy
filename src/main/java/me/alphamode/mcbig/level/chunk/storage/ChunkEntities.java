package me.alphamode.mcbig.level.chunk.storage;

import me.alphamode.mcbig.level.chunk.BigChunkPos;
import net.minecraft.world.entity.Entity;

import java.math.BigInteger;
import java.util.List;

public record ChunkEntities(BigChunkPos pos, List<Entity> entities) {
    public boolean isEmpty() {
        return this.entities.isEmpty();
    }

    public boolean isAt(BigInteger x, BigInteger z) {
        return x.equals(this.pos.x()) && z.equals(this.pos.z());
    }
}
