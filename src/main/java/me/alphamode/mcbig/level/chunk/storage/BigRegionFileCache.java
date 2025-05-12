package me.alphamode.mcbig.level.chunk.storage;

import me.alphamode.mcbig.math.BigConstants;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.chunk.storage.RegionFileCache;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.math.BigInteger;

public class BigRegionFileCache {
    public static synchronized RegionFile getRegionFile(File basePath, BigInteger x, BigInteger z) {
        File var3 = new File(basePath, "region");
        File var4 = new File(var3, "r." + (x.shiftRight(5)) + "." + (z.shiftRight(5)) + ".mcr");
        Reference<RegionFile> var5 = (Reference<RegionFile>) RegionFileCache.cache.get(var4);
        if (var5 != null) {
            RegionFile var6 = var5.get();
            if (var6 != null) {
                return var6;
            }
        }

        if (!var3.exists()) {
            var3.mkdirs();
        }

        if (RegionFileCache.cache.size() >= 256) {
            RegionFileCache.clear();
        }

        RegionFile var7 = new RegionFile(var4);
        RegionFileCache.cache.put(var4, new SoftReference<>(var7));
        return var7;
    }

    public static int getSizeDelta(File basePath, BigInteger x, BigInteger z) {
        RegionFile var3 = getRegionFile(basePath, x, z);
        return var3.getSizeDelta();
    }

    public static DataInputStream getChunkDataInputStream(File basePath, BigInteger x, BigInteger z) {
        RegionFile var3 = getRegionFile(basePath, x, z);
        return var3.readChunk(x.and(BigConstants.THIRTY_ONE).intValue(), z.and(BigConstants.THIRTY_ONE).intValue());
    }

    public static DataOutputStream getChunkDataOutputStream(File basePath, BigInteger x, BigInteger z) {
        RegionFile var3 = getRegionFile(basePath, x, z);
        return var3.open(x.and(BigConstants.THIRTY_ONE).intValue(), z.and(BigConstants.THIRTY_ONE).intValue());
    }
}
