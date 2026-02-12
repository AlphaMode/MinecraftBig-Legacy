package me.alphamode.mcbig.mixin;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.NbtIo;
import me.alphamode.mcbig.extensions.BigChunkStorageExtension;
import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import me.alphamode.mcbig.math.BigConstants;
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
                if (!var6.hasKey("Level")) {
                    System.out.println("Chunk file at " + x + "," + z + " is missing level data, skipping");
                    return null;
                }

                if (!var6.getCompoundTag("Level").hasKey("Blocks")) {
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
        File var3 = this.getFile(bigChunk.bigX, bigChunk.bigZ);
        if (var3.exists()) {
            LevelData var4 = level.getLevelData();
            var4.setSize(var4.getSize() - var3.length());
        }

        try {
            File var10 = new File(this.dir, "tmp_chunk.dat");
            FileOutputStream var5 = new FileOutputStream(var10);
            CompoundTag var6 = new CompoundTag();
            CompoundTag var7 = new CompoundTag();
            var6.putTag("Level", var7);
            save(chunk, level, var7);
            NbtIo.write(var6, var5);
            var5.close();
            if (var3.exists()) {
                var3.delete();
            }

            var10.renameTo(var3);
            LevelData var8 = level.getLevelData();
            var8.setSize(var8.getSize() + var3.length());
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
        ListTag var3 = new ListTag();

        for(int var4 = 0; var4 < chunk.entityBlocks.length; ++var4) {
            for(Object var6 : chunk.entityBlocks[var4]) {
                chunk.lastSaveHadEntities = true;
                CompoundTag var7 = new CompoundTag();
                if (((Entity) var6).save(var7)) {
                    var3.add(var7);
                }
            }
        }

        data.putTag("Entities", var3);
        ListTag var8 = new ListTag();

        for(Object var10 : chunk.tileEntities.values()) {
            CompoundTag var11 = new CompoundTag();
            ((TileEntity)var10).save(var11);
            var8.add(var11);
        }

        data.putTag("TileEntities", var8);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static LevelChunk load(Level level, CompoundTag tag) {
        BigInteger var2 = new BigInteger(tag.getString("xPos"));
        BigInteger var3 = new BigInteger(tag.getString("zPos"));
        BigLevelChunk var4 = new BigLevelChunk(level, var2, var3);
        var4.blocks = tag.getByteArray("Blocks");
        var4.data = new DataLayer(tag.getByteArray("Data"));
        var4.skyLight = new DataLayer(tag.getByteArray("SkyLight"));
        var4.blockLight = new DataLayer(tag.getByteArray("BlockLight"));
        var4.heightMap = tag.getByteArray("HeightMap");
        var4.terrainPopulated = tag.getBoolean("TerrainPopulated");
        if (!var4.data.isValid()) {
            var4.data = new DataLayer(var4.blocks.length);
        }

        if (var4.heightMap == null || !var4.skyLight.isValid()) {
            var4.heightMap = new byte[256];
            var4.skyLight = new DataLayer(var4.blocks.length);
            var4.recalcHeightmap();
        }

        if (!var4.blockLight.isValid()) {
            var4.blockLight = new DataLayer(var4.blocks.length);
            var4.recalcBlockLights();
        }

        ListTag var5 = tag.getList("Entities");
        if (var5 != null) {
            for(int var6 = 0; var6 < var5.size(); ++var6) {
                CompoundTag var7 = (CompoundTag)var5.get(var6);
                Entity var8 = EntityIO.loadStatic(var7, level);
                var4.lastSaveHadEntities = true;
                if (var8 != null) {
                    var4.addEntity(var8);
                }
            }
        }

        ListTag var10 = tag.getList("TileEntities");
        if (var10 != null) {
            for(int var11 = 0; var11 < var10.size(); ++var11) {
                CompoundTag var12 = (CompoundTag)var10.get(var11);
                TileEntity var9 = TileEntity.loadStatic(var12);
                if (var9 != null) {
                    var4.addTileEntity(var9);
                }
            }
        }

        return var4;
    }
}
