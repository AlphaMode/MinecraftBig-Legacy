package me.alphamode.mcbig.mixin.worldgen.features;

import me.alphamode.mcbig.extensions.BigFeatureExtension;
import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.world.level.levelgen.FoliageCoords;
import me.alphamode.mcbig.world.phys.BigVec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.BasicTree;
import net.minecraft.world.level.tile.Tile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Mixin(BasicTree.class)
public abstract class BasicTreeMixin implements BigFeatureExtension {
    private static final double TRUNK_HEIGHT_SCALE = 0.618;
    private static final double CLUSTER_DENSITY_MAGIC = 1.382;
    private static final double BRANCH_SLOPE = 0.381;
    private static final double BRANCH_LENGTH_MAGIC = 0.328;
    private BigVec3i originBig = BigVec3i.ZERO;

    private List<FoliageCoords> foliageCoords;

    @Shadow
    private Level thisLevel;

    @Shadow
    private Random rnd;

    @Shadow
    private int[] origin;

    @Shadow
    private int height;

    @Shadow
    private int heightVariance;

    @Shadow
    @Final
    private static byte[] axisConversionArray;

    @Shadow
    private int trunkHeight;

    @Shadow
    private double trunkHeightScale;

    @Shadow
    private double foliageDensity;

    @Shadow
    abstract float treeShape(int i);

    @Shadow
    private double widthScale;

    @Shadow
    private int foliageHeight;

    @Shadow
    private double branchSlope;

    @Shadow
    abstract void makeFoliage();

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void prepare() {
        this.trunkHeight = (int)(this.height * this.trunkHeightScale);
        if (this.trunkHeight >= this.height) {
            this.trunkHeight = this.height - 1;
        }

        int clustersPerY = (int)(CLUSTER_DENSITY_MAGIC + Math.pow(this.foliageDensity * this.height / 13.0, 2.0));
        if (clustersPerY < 1) {
            clustersPerY = 1;
        }

        int treeHeight = this.originBig.y() + this.height - this.foliageHeight;
        int trunkTop = this.originBig.y() + this.trunkHeight;
        int relativeY = treeHeight - this.originBig.y();
        List<FoliageCoords> foliageCoords = new ArrayList<>(clustersPerY * this.height);
        foliageCoords.add(new FoliageCoords(new BigVec3i(this.originBig.x(), treeHeight, this.originBig.z()), trunkTop));

        while (relativeY >= 0) {
            float treeShape = this.treeShape(relativeY);
            if (treeShape < 0.0F) {
                relativeY--;
            } else {
                for (int i = 0; i < clustersPerY; i++) {
                    double radius = this.widthScale * (treeShape * (this.rnd.nextFloat() + BRANCH_LENGTH_MAGIC));
                    double angle = this.rnd.nextFloat() * 2.0 * 3.14159;
                    double x = radius * Math.sin(angle) + 0.5;
                    double z = radius * Math.cos(angle) + 0.5;
                    BigVec3i checkStart = this.originBig.offset(BigMath.floor(x), relativeY - 1, BigMath.floor(z));
                    BigVec3i checkEnd = checkStart.above(this.foliageHeight);
                    if (this.checkLine(checkStart, checkEnd)) {
                        int dx = originBig.x().subtract(checkStart.x()).intValue();
                        int dz = originBig.z().subtract(checkStart.z()).intValue();
                        double branchHeight = checkStart.y() - Math.sqrt(dx * dx + dz * dz) * this.branchSlope;
                        int branchTop = branchHeight > trunkTop ? trunkTop : (int)branchHeight;

                        BigVec3i checkBranchBase = new BigVec3i(originBig.x(), branchTop, originBig.z());
                        if (this.checkLine(checkBranchBase, checkStart)) {
                            foliageCoords.add(new FoliageCoords(checkStart, checkBranchBase.y()));
                        }
                    }
                }

                relativeY--;
            }
        }

        this.foliageCoords = foliageCoords;
    }

//    private void crossection(BigInteger i, int j, BigInteger k, float f, byte b, int l) {
//        int var7 = (int)(f + 0.618);
//        byte var8 = axisConversionArray[b];
//        byte var9 = axisConversionArray[b + 3];
//        int[] var10 = new int[]{i, j, k};
//        int[] var11 = new int[]{0, 0, 0};
//        int var12 = -var7;
//        int var13 = -var7;
//
//        for (var11[b] = var10[b]; var12 <= var7; var12++) {
//            var11[var8] = var10[var8] + var12;
//            var13 = -var7;
//
//            while (var13 <= var7) {
//                double var15 = Math.sqrt(Math.pow(Math.abs(var12) + 0.5, 2.0) + Math.pow(Math.abs(var13) + 0.5, 2.0));
//                if (var15 > f) {
//                    var13++;
//                } else {
//                    var11[var9] = var10[var9] + var13;
//                    int var14 = this.thisLevel.getTile(var11[0], var11[1], var11[2]);
//                    if (var14 != 0 && var14 != 18) {
//                        var13++;
//                    } else {
//                        this.thisLevel.setTileNoUpdate(var11[0], var11[1], var11[2], l);
//                        var13++;
//                    }
//                }
//            }
//        }
//    }

    private boolean checkLine(BigVec3i startPos, BigVec3i endPos) {
        if (Objects.equals(startPos, endPos)) {
            return true;
        }
        BigVec3i delta = endPos.offset(startPos.x().negate(), -startPos.y(), startPos.x().negate());
        int steps = this.getSteps(delta);
        float dx = (float)delta.x().intValue() / steps;
        float dy = (float)delta.y() / steps;
        float dz = (float)delta.z().intValue() / steps;

        for (int i = 0; i <= steps; i++) {
            BigVec3i blockPos = startPos.offset(BigMath.floor(0.5F + i * dx), Mth.floor(0.5F + i * dy), BigMath.floor(0.5F + i * dz));
            if (!this.isFree(blockPos)) {
                return false;
            }
        }

       return true;
    }

    private int getSteps(final BigVec3i pos) {
        int absX = pos.x().abs().intValue();
        int absY = Math.abs(pos.y());
        int absZ = pos.z().abs().intValue();
        return Math.max(absX, Math.max(absY, absZ));
    }

    private boolean isFree(BigVec3i pos) {
        int tile = this.thisLevel.getTile(pos.x(), pos.y(), pos.z());
        if (tile != 0 && tile != Tile.LEAVES.id) {
            return false;
        }

        return true;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean checkLocation() {
        BigVec3i startCheck = this.originBig;
        BigVec3i endCheck = startCheck.above(this.height - 1);
        int var3 = this.thisLevel.getTile(this.origin[0], this.origin[1] - 1, this.origin[2]);
        if (var3 != 2 && var3 != 3) {
            return false;
        } else {
            return this.checkLine(startCheck, endCheck);
//            int var4 = this.checkLine(startCheck, endCheck);
//            if (var4 == -1) {
//                return true;
//            } else if (var4 < 6) {
//                return false;
//            } else {
//                this.height = var4;
//                return true;
//            }
        }
    }

    @Override
    public boolean place(Level level, Random random, BigInteger x, int y, BigInteger z) {
        this.thisLevel = level;
        long var6 = random.nextLong();
        this.rnd.setSeed(var6);
        this.originBig = new BigVec3i(x, y, z);
        if (this.height == 0) {
            this.height = 5 + this.rnd.nextInt(this.heightVariance);
        }

        if (!this.checkLocation()) {
            return false;
        } else {
            this.prepare();
//            this.makeFoliage();
//            this.makeTrunk();
//            this.makeBranches();
            return true;
        }
    }
}
