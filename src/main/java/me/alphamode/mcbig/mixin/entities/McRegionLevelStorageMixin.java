package me.alphamode.mcbig.mixin.entities;

import me.alphamode.mcbig.extensions.entities.BigLevelStorageExtension;
import me.alphamode.mcbig.level.chunk.storage.EntityStorage;
import me.alphamode.mcbig.level.chunk.storage.McRegionEntityStorage;
import me.alphamode.mcbig.level.storage.MemoryChunkStorage;
import me.alphamode.mcbig.world.level.dimension.DimensionUtil;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.dimension.HellDimension;
import net.minecraft.world.level.storage.DirectoryLevelStorage;
import net.minecraft.world.level.storage.McRegionChunkStorage;
import net.minecraft.world.level.storage.McRegionLevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.File;

@Mixin(McRegionLevelStorage.class)
public class McRegionLevelStorageMixin extends DirectoryLevelStorage implements BigLevelStorageExtension {
    public McRegionLevelStorageMixin(File saveFile, String levelName, boolean createPlayerDir) {
        super(saveFile, levelName, createPlayerDir);
    }

//    @Override
//    public EntityStorage createEntityStorage(Dimension dimension) {
//        return new McRegionEntityStorage(DimensionUtil.getStorageFolder(dimension, getFolder().toPath()).resolve("entities").toFile());
//    }

    @Overwrite
    public ChunkStorage createChunkStorage(Dimension dimension) {
//        File var2 = this.getFolder();
//        if (dimension instanceof HellDimension) {
//            File var3 = new File(var2, "DIM-1");
//            var3.mkdirs();
//            return new McRegionChunkStorage(var3);
//        } else {
//            return new McRegionChunkStorage(var2);
//        }
        return new MemoryChunkStorage();
    }
}
