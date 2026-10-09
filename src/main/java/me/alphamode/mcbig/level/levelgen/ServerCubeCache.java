package me.alphamode.mcbig.level.levelgen;

import it.unimi.dsi.fastutil.ints.*;
import me.alphamode.mcbig.level.cube.CubeSource;
import me.alphamode.mcbig.level.cube.EmptyLevelCube;
import me.alphamode.mcbig.level.cube.LevelCube;
import me.alphamode.mcbig.level.cube.storage.CubeStorage;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkStorage;

import java.math.BigInteger;
import java.util.*;

public class ServerCubeCache implements CubeSource {

    private IntSet toDrop = new IntOpenHashSet();
    private LevelCube emptyCube;
    private CubeSource source;
    private CubeStorage storage;
    private Int2ObjectMap<LevelCube> cache = new Int2ObjectOpenHashMap<>();
    private List<LevelCube> cubes = new ArrayList<>();
    private Level level;

    public ServerCubeCache(Level level, CubeStorage storage, CubeSource wrapped) {
        this.emptyCube = new EmptyLevelCube(level, new byte[LevelCube.SIZE * LevelCube.SIZE * LevelCube.SIZE], BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO);
        this.level = level;
        this.storage = storage;
        this.source = wrapped;
    }

    @Override
    public boolean hasCube(BigInteger x, BigInteger y, BigInteger z) {
        return this.cache.containsKey(Objects.hash(x, y, z));
    }

    @Override
    public LevelCube getCube(BigInteger x, BigInteger y, BigInteger z) {
        LevelCube cube = this.cache.get(Objects.hash(x, y, z));
        return cube == null ? create(x, y, z) : cube;
    }

    @Override
    public LevelCube create(BigInteger x, BigInteger y, BigInteger z) {
        int pos = Objects.hash(x, y, z);
        this.toDrop.remove(pos);
        LevelCube cube = this.cache.get(pos);
        if (cube == null) {
            cube = readCube(x, y, z);
            if (cube == null) {
                if (this.source == null) {
                    cube = this.emptyCube;
                } else {
                    cube = this.source.getCube(x, y, z);
                }
            }

            this.cache.put(pos, cube);
            this.cubes.add(cube);
            if (cube != null) {
//                cube.lightLava();
//                cube.load();
            }

            //? >=1.0.0-beta.8.0.r {
            /*cube.checkPostProcess(this, this, x, y, z);
             *///? } else {
            BigInteger xmo = x.subtract(BigInteger.ONE);
            BigInteger ymo = y.subtract(BigInteger.ONE);
            BigInteger zmo = z.subtract(BigInteger.ONE);

            BigInteger xpo = x.add(BigInteger.ONE);
            BigInteger ypo = y.add(BigInteger.ONE);
            BigInteger zpo = z.add(BigInteger.ONE);

//            if (!cube.terrainPopulated && this.hasCube(xpo, zpo) && this.hasCube(x, zpo) && this.hasCube(xpo, z)) {
//                this.postProcess(this, x, z);
//            }
//
//            if (this.hasCube(xmo, z)
//                    && !this.getCube(xmo, z).terrainPopulated
//                    && this.hasCube(xmo, zpo)
//                    && this.hasCube(x, zpo)
//                    && this.hasCube(xmo, z)) {
//                this.postProcess(this, xmo, z);
//            }
//
//            if (this.hasCube(x, zmo)
//                    && !this.getCube(x, zmo).terrainPopulated
//                    && this.hasCube(xpo, zmo)
//                    && this.hasCube(x, zmo)
//                    && this.hasCube(xpo, z)) {
//                this.postProcess(this, x, zmo);
//            }
//
//            if (this.hasCube(xmo, zmo)
//                    && !this.getCube(xmo, zmo).terrainPopulated
//                    && this.hasCube(xmo, zmo)
//                    && this.hasCube(x, zmo)
//                    && this.hasCube(xmo, z)) {
//                this.postProcess(this, xmo, zmo);
//            }
            //? }
        }

        return cube;
    }

    @Override
    public void postProcess(ChunkSource generator, BigInteger x, BigInteger y, BigInteger z) {
        LevelCube cube = getCube(x, y, z);
        if (!cube.terrainPopulated) {
            cube.terrainPopulated = true;
            if (this.source != null) {
                this.source.postProcess(generator, x, y, z);
//                cube.markUnsaved();
            }
        }
    }

    private LevelCube readCube(BigInteger x, BigInteger y, BigInteger z) {
        if (this.storage == null) {
            return null;
        } else {
            try {
                LevelCube cube = this.storage.load(this.level, x, y, z);
                if (cube != null) {
                    cube.lastSaveTime = this.level.getTime();
                }

                return cube;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public boolean save(boolean force, ProgressListener listener) {
        return false;
    }

    @Override
    public boolean tick() {
        return false;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public String gatherStats() {
        return "ServerCubeCache: " + this.cache.size() + " Drop: " + this.toDrop.size();
    }
}
