package me.alphamode.mcbig.world.entity;

import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import net.minecraft.world.entity.Entity;

public class EntityHelper {
    public static <T extends Entity> boolean isBigMovementEnabled(Class<T> entity) {
        return entity.isAssignableFrom(BigEntityExtension.class);
    }
}
