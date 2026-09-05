package me.alphamode.mcbig.mixin;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.NbtIo;
import me.alphamode.mcbig.extensions.BigChunkStorageExtension;
import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import me.alphamode.mcbig.math.BigConstants;
import me.alphamode.mcbig.world.level.LevelConstants;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityIO;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.OldChunkStorage;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.tile.entity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;

@Mixin(OldChunkStorage.class)
public abstract class OldChunkStorageMixin implements BigChunkStorageExtension {
    @Shadow private File dir;

    @Shadow private boolean create;

    private File getFile(BigInteger x, BigInteger z) {
        String var3 = "c." + x.toString(36) + "." + z.toString(36) + ".dat";
        String var4 = x.and(BigConstants.REGION_MASK).toString(36);
        String var5 = z.and(BigConstants.REGION_MASK).toString(36);
        File var6 = new File(this.dir, var4);
        if (!var6.exists()) {
            if (!this.create) {
                return null;
            }

            var6.mkdir();
        }

        var6 = new File(var6, var5);
        if (!var6.exists()) {
            if (!this.create) {
                return null;
            }

            var6.mkdir();
        }

        var6 = new File(var6, var3);
        return !var6.exists() && !this.create ? null : var6;
    }

    @Override
    public BigLevelChunk load(Level level, BigInteger x, BigInteger z) {
        File var4 = this.getFile(x, z);
        if (var4 != null && var4.exists()) {
            try {
                FileInputStream var5 = new FileInputStream(var4);
                CompoundTag var6 = NbtIo.read(var5);
                if (!var6.contains("Level")) {
                    System.out.println("Chunk file at " + x + "," + z + " is missing level data, skipping");
                    return null;
                }

                if (!var6.getCompoundTag("Level").contains("Blocks")) {
                    System.out.println("Chunk file at " + x + "," + z + " is missing block data, skipping");
                    return null;
                }

                BigLevelChunk var7 = (BigLevelChunk) load(level, var6.getCompoundTag("Level"));
                if (!var7.isAt(x, z)) {
                    System.out
                            .println("Chunk file at " + x + "," + z + " is in the wrong location; relocating. (Expected " + x + ", " + z + ", got " + var7.x + ", " + var7.z + ")");
                    var6.putString("xPos", x.toString());
                    var6.putString("zPos", z.toString());
                    var7 = (BigLevelChunk) load(level, var6.getCompoundTag("Level"));
                }

                var7.onLoad();
                return var7;
            } catch (Exception var8) {
                var8.printStackTrace();
            }
        }

        return null;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void save(Level level, LevelChunk chunk) {
        level.checkSession();
        BigLevelChunk bigChunk = (BigLevelChunk) chunk;
        File file = this.getFile(bigChunk.bigX, bigChunk.bigZ);
        if (file.exists()) {
            LevelData levelData = level.getLevelData();
            levelData.setSize(levelData.getSize() - file.length());
        }

        try {
            File tmpFile = new File(this.dir, "tmp_chunk.dat");
            FileOutputStream fos = new FileOutputStream(tmpFile);
            CompoundTag tag = new CompoundTag();
            CompoundTag levelData = new CompoundTag();
            tag.putTag("Level", levelData);
            save(chunk, level, levelData);
            NbtIo.write(tag, fos);
            fos.close();
            if (file.exists()) {
                file.delete();
            }

            tmpFile.renameTo(file);
            LevelData levelInfo = level.getLevelData();
            levelInfo.setSize(levelInfo.getSize() + file.length());
        } catch (Exception var9) {
            var9.printStackTrace();
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void save(LevelChunk chunk, Level level, CompoundTag data) {
        level.checkSession();
        BigLevelChunk bigChunk = (BigLevelChunk) chunk;
        data.putString("xPos", bigChunk.bigX.toString());
        data.putString("zPos", bigChunk.bigZ.toString());
        data.putLong("LastUpdate", level.getTime());
        data.putByteArray("Blocks", chunk.blocks);
        data.putByteArray("Data", chunk.data.data);
        data.putByteArray("SkyLight", chunk.skyLight.data);
        data.putByteArray("BlockLight", chunk.blockLight.data);
        data.putByteArray("HeightMap", chunk.heightMap);
        data.putBoolean("TerrainPopulated", chunk.terrainPopulated);
        chunk.lastSaveHadEntities = false;
        ListTag entityTags = new ListTag();

        for(int i = 0; i < chunk.entityBlocks.length; ++i) {
            for(Entity e : chunk.entityBlocks[i]) {
                chunk.lastSaveHadEntities = true;
                CompoundTag teTag = new CompoundTag();
                if (e.save(teTag)) {
                    entityTags.add(teTag);
                }
            }
        }

        data.putTag("Entities", entityTags);
        ListTag tileEntityTags = new ListTag();

        for(TileEntity te : chunk.tileEntities.values()) {
            CompoundTag teTag = new CompoundTag();
            te.save(teTag);
            tileEntityTags.add(teTag);
        }

        data.putTag("TileEntities", tileEntityTags);
    }

    private static DataLayer createDataLayer(byte[] data) {
        //? >=1.0.0-beta.8.0.r {
        /*return new DataLayer(data, LevelConstants.genDepthBits);
        *///? } else {
        return new DataLayer(data);
        //? }
    }

    private static DataLayer createDataLayer(int length) {
        //? >=1.0.0-beta.8.0.r {
        /*return new DataLayer(length, LevelConstants.genDepthBits);
         *///? } else {
        return new DataLayer(length);
        //? }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static LevelChunk load(Level level, CompoundTag tag) {
        BigInteger x = new BigInteger(tag.getString("xPos"));
        BigInteger z = new BigInteger(tag.getString("zPos"));
        BigLevelChunk levelChunk = new BigLevelChunk(level, x, z);
        levelChunk.blocks = tag.getByteArray("Blocks");
        levelChunk.data = createDataLayer(tag.getByteArray("Data"));
        levelChunk.skyLight = createDataLayer(tag.getByteArray("SkyLight"));
        levelChunk.blockLight = createDataLayer(tag.getByteArray("BlockLight"));
        levelChunk.heightMap = tag.getByteArray("HeightMap");
        levelChunk.terrainPopulated = tag.getBoolean("TerrainPopulated");
        if (!levelChunk.data.isValid()) {
            levelChunk.data = createDataLayer(levelChunk.blocks.length);
        }

        if (levelChunk.heightMap == null || !levelChunk.skyLight.isValid()) {
            levelChunk.heightMap = new byte[256];
            levelChunk.skyLight = createDataLayer(levelChunk.blocks.length);
            levelChunk.recalcHeightmap();
        }

        if (!levelChunk.blockLight.isValid()) {
            levelChunk.blockLight = createDataLayer(levelChunk.blocks.length);
            levelChunk.recalcBlockLights();
        }

        ListTag entityTags = tag.getList("Entities");
        if (entityTags != null) {
            for(int i = 0; i < entityTags.size(); ++i) {
                CompoundTag teTag = (CompoundTag)entityTags.get(i);
                Entity te = EntityIO.loadStatic(teTag, level);
                levelChunk.lastSaveHadEntities = true;
                if (te != null) {
                    levelChunk.addEntity(te);
                }
            }
        }

        ListTag tileEntityTags = tag.getList("TileEntities");
        if (tileEntityTags != null) {
            for(int i = 0; i < tileEntityTags.size(); ++i) {
                CompoundTag teTag = (CompoundTag)tileEntityTags.get(i);
                TileEntity te = TileEntity.loadStatic(teTag);
                if (te != null) {
                    levelChunk.addTileEntity(te);
                }
            }
        }

        return levelChunk;
    }
}
