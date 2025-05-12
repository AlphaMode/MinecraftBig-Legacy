package me.alphamode.mcbig.mixin.client;

import me.alphamode.mcbig.level.BigRegion;
import net.minecraft.client.renderer.Chunk;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.renderer.TileRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderDispatcher;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Region;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.TileEntity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;

@Mixin(Chunk.class)
public abstract class ChunkMixin {
    @Shadow public boolean[] empty;

    @Shadow public List globalRenderableTileEntities;

    @Shadow public boolean skyLit;

    @Shadow public boolean compiled;

    @Shadow public List renderableTileEntities;

    @Shadow public static Tesselator tesselator;

    @Shadow public int zs;

    @Shadow public int ys;

    @Shadow public int x;

    @Shadow public int y;

    @Shadow public int z;

    @Shadow public int lists;

    @Shadow public abstract void translateToPos();

    @Shadow public Level level;

    @Shadow public static int updates;

    @Shadow public boolean dirty;

    @Shadow public int xs;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void rebuild() {
        if (this.dirty) {
            ++updates;
            int var1 = this.x;
            int var2 = this.y;
            int var3 = this.z;
            int var4 = this.x + this.xs;
            int var5 = this.y + this.ys;
            int var6 = this.z + this.zs;

            for(int var7 = 0; var7 < 2; ++var7) {
                this.empty[var7] = true;
            }

            LevelChunk.touchedSky = false;
            HashSet var21 = new HashSet();
            var21.addAll(this.renderableTileEntities);
            this.renderableTileEntities.clear();
            byte var8 = 1;
            BigRegion var9 = new BigRegion(this.level, BigInteger.valueOf(var1 - var8), var2 - var8, BigInteger.valueOf(var3 - var8), BigInteger.valueOf(var4 + var8), var5 + var8, BigInteger.valueOf(var6 + var8));
            TileRenderer var10 = new TileRenderer(var9);

            for(int var11 = 0; var11 < 2; ++var11) {
                boolean var12 = false;
                boolean var13 = false;
                boolean var14 = false;

                for(int var15 = var2; var15 < var5; ++var15) {
                    for(int var16 = var3; var16 < var6; ++var16) {
                        for(int var17 = var1; var17 < var4; ++var17) {
                            int var18 = var9.getTile(var17, var15, var16);
                            if (var18 > 0) {
                                if (!var14) {
                                    var14 = true;
                                    GL11.glNewList(this.lists + var11, 4864);
                                    GL11.glPushMatrix();
                                    this.translateToPos();
                                    float var19 = 1.000001F;
                                    GL11.glTranslatef((float)(-this.zs) / 2.0F, (float)(-this.ys) / 2.0F, (float)(-this.zs) / 2.0F);
                                    GL11.glScalef(var19, var19, var19);
                                    GL11.glTranslatef((float)this.zs / 2.0F, (float)this.ys / 2.0F, (float)this.zs / 2.0F);
                                    tesselator.begin();
                                    tesselator.offset((double)(-this.x), (double)(-this.y), (double)(-this.z));
                                }

                                if (var11 == 0 && Tile.isEntityTile[var18]) {
                                    TileEntity var23 = var9.getTileEntity(var17, var15, var16);
                                    if (TileEntityRenderDispatcher.instance.hasTileEntityRenderer(var23)) {
                                        this.renderableTileEntities.add(var23);
                                    }
                                }

                                Tile var24 = Tile.tiles[var18];
                                int var20 = var24.getRenderLayer();
                                if (var20 != var11) {
                                    var12 = true;
                                } else if (var20 == var11) {
                                    var13 |= var10.tesselateInWorld(var24, var17, var15, var16);
                                }
                            }
                        }
                    }
                }

                if (var14) {
                    tesselator.end();
                    GL11.glPopMatrix();
                    GL11.glEndList();
                    tesselator.offset(0.0, 0.0, 0.0);
                } else {
                    var13 = false;
                }

                if (var13) {
                    this.empty[var11] = false;
                }

                if (!var12) {
                    break;
                }
            }

            HashSet var22 = new HashSet();
            var22.addAll(this.renderableTileEntities);
            var22.removeAll(var21);
            this.globalRenderableTileEntities.addAll(var22);
            var21.removeAll(this.renderableTileEntities);
            this.globalRenderableTileEntities.removeAll(var21);
            this.skyLit = LevelChunk.touchedSky;
            this.compiled = true;
        }
    }
}
