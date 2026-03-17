package me.alphamode.mcbig.mixin.entities;

import me.alphamode.mcbig.extensions.entities.BigLevelStorageExtension;
import me.alphamode.mcbig.level.chunk.storage.EntityStorage;
import me.alphamode.mcbig.level.chunk.storage.McRegionEntityStorage;
import me.alphamode.mcbig.world.level.dimension.DimensionUtil;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.storage.DirectoryLevelStorage;
import net.minecraft.world.level.storage.McRegionLevelStorage;
import org.spongepowered.asm.mixin.Mixin;

import java.io.File;

@Mixin(McRegionLevelStorage.class)
public class McRegionLevelStorageMixin extends DirectoryLevelStorage implements BigLevelStorageExtension {
    public McRegionLevelStorageMixin(File saveFile, String levelName, boolean createPlayerDir) {
        super(saveFile, levelName, createPlayerDir);
    }

    @Override
    public EntityStorage createEntityStorage(Dimension dimension) {
        return new McRegionEntityStorage(DimensionUtil.getStorageFolder(dimension, getFolder().toPath()).resolve("entities").toFile());
    }
}
