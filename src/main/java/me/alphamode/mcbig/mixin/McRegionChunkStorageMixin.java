package me.alphamode.mcbig.mixin;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.NbtIo;
import me.alphamode.mcbig.extensions.BigChunkStorageExtension;
import me.alphamode.mcbig.level.chunk.BigLevelChunk;
import me.alphamode.mcbig.level.chunk.storage.BigRegionFileCache;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.OldChunkStorage;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.McRegionChunkStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

@Mixin(McRegionChunkStorage.class)
public class McRegionChunkStorageMixin implements BigChunkStorageExtension {
    @Shadow @Final private File basePath;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void save(Level level, LevelChunk chunk) {
        level.checkSession();

        try {
            BigLevelChunk bigChunk = (BigLevelChunk) chunk;
            OutputStream var3 = BigRegionFileCache.getChunkDataOutputStream(this.basePath, bigChunk.bigX, bigChunk.bigZ);
            CompoundTag worldTag = new CompoundTag();
            CompoundTag levelTag = new CompoundTag();
            worldTag.putTag("Level", levelTag);
            OldChunkStorage.save(chunk, level, levelTag);
            NbtIo.write(worldTag, var3);
            var3.close();
            LevelData var6 = level.getLevelData();
            var6.setSize(var6.getSize() + (long) BigRegionFileCache.getSizeDelta(this.basePath, bigChunk.bigX, bigChunk.bigZ));
        } catch (Exception var7) {
            var7.printStackTrace();
        }
    }

    @Override
    public BigLevelChunk load(Level level, BigInteger x, BigInteger z) throws IOException {
        InputStream var4 = BigRegionFileCache.getChunkDataInputStream(this.basePath, x, z);
        if (var4 != null) {
            CompoundTag var5 = NbtIo.read(var4);
            if (!var5.hasKey("Level")) {
                System.out.println("Chunk file at " + x + "," + z + " is missing level data, skipping");
                return null;
            } else if (!var5.getCompoundTag("Level").hasKey("Blocks")) {
                System.out.println("Chunk file at " + x + "," + z + " is missing block data, skipping");
                return null;
            } else {
                BigLevelChunk var6 = (BigLevelChunk) OldChunkStorage.load(level, var5.getCompoundTag("Level"));
                if (!var6.isAt(x, z)) {
                    System.out
                            .println("Chunk file at " + x + "," + z + " is in the wrong location; relocating. (Expected " + x + ", " + z + ", got " + var6.x + ", " + var6.z + ")");
                    var5.putString("xPos", x.toString());
                    var5.putString("zPos", z.toString());
                    var6 = (BigLevelChunk) OldChunkStorage.load(level, var5.getCompoundTag("Level"));
                }

                var6.onLoad();
                return var6;
            }
        } else {
            return null;
        }
    }
}
