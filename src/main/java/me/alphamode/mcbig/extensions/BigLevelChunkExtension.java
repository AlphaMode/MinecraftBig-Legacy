package me.alphamode.mcbig.extensions;

import me.alphamode.mcbig.world.phys.BigAABB;
import net.minecraft.world.entity.Entity;

import java.math.BigInteger;
import java.util.List;

public interface BigLevelChunkExtension {
    default void getEntities(Entity ignore, BigAABB area, List<Entity> entities) {
        throw new UnsupportedOperationException();
    }

    default BigInteger getX() {
        throw new UnsupportedOperationException();
    }

    default BigInteger getZ() {
        throw new UnsupportedOperationException();
    }
}
