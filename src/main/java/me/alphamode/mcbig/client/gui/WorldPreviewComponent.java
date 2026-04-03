package me.alphamode.mcbig.client.gui;

import net.minecraft.client.MemoryTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.renderer.OffsettedRenderList;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.renderer.TileRenderer;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.tile.Tile;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector4f;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.FloatBuffer;

public class WorldPreviewComponent extends GuiComponent {
    public int lists = -1;
    private final PreviewRegion region;
    private final Minecraft mc;
    public boolean[] empty = new boolean[2];
    private final Screen parent;
    private final OffsettedRenderList renderList = new OffsettedRenderList();
    private final int size;
    private final BigInteger xOff;
    private final BigInteger zOff;

    private boolean built = false;

    public WorldPreviewComponent(Screen parent, Minecraft mc, ChunkSource source, int size, BigInteger xOff, BigInteger zOff) {
        this.size = size;
        this.mc = mc;
        this.parent = parent;
        this.region = new PreviewRegion(source, size);
        this.xOff = xOff;
        this.zOff = zOff;
        this.lists = MemoryTracker.genLists(3);
        this.renderList.init(BigInteger.ZERO, 0, BigInteger.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO);
    }

    public void render(int xm, int ym, float a) {
        if (!built) {
            build();
            built = true;
            renderList.add(this.lists);
        }
        updateMouse(xm, ym, a);

        GL11.glPushMatrix();
        GL11.glTranslatef(this.parent.width / 2F, (this.parent.height / 2F) + 120, 0.0F);


        float center = (this.size * 16) / 2.0F;
        GL11.glTranslatef(center, center, center);

        GL11.glMultMatrix(toMatrix(rotation));

        GL11.glScalef(6.0F, 6.0F, 6.0F);
        GL11.glTranslatef(-center, -center, -center);

        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.textures.loadTexture("/terrain.png"));

        GL11.glCallList(this.lists);
        if (!this.empty[1]) {
            GL11.glCallList(this.lists + 1);
        }

        GL11.glPopMatrix();
    }

    public void build() {
        Tesselator t = Tesselator.instance;
        TileRenderer tileRenderer = new TileRenderer(this.region);

        BigInteger x0 = this.xOff;
        BigInteger x1 = this.xOff.add(BigInteger.valueOf(this.size * 16));
        BigInteger z0 = this.zOff;
        BigInteger z1 = this.zOff.add(BigInteger.valueOf(this.size * 16));

        int y0 = 0;
        int y1 = 128;

        for(int i = 0; i < 2; ++i) {
            this.empty[i] = true;
        }

        for(int l = 0; l < 2; ++l) {
            boolean renderNextLayer = false;
            boolean rendered = false;
            boolean started = false;

            for(int y = y0; y < y1; ++y) {
                for(BigInteger z = z0; z.compareTo(z1) < 0; z = z.add(BigInteger.ONE)) {
                    for(BigInteger x = x0; x.compareTo(x1) < 0; x = x.add(BigInteger.ONE)) {
                        int tileId = region.getTile(x, y, z);
                        if (tileId > 0) {
                            if (!started) {
                                started = true;
                                GL11.glNewList(this.lists + l, GL11.GL_COMPILE);
                                GL11.glPushMatrix();

//                                this.translateToPos();
                                float ss = 1.000001F;
//                                GL11.glTranslatef((float)(-this.zs) / 2.0F, (float)(-this.ys) / 2.0F, (float)(-this.zs) / 2.0F);
//                                GL11.glScalef(ss, ss, ss);
//                                GL11.glTranslatef((float)this.zs / 2.0F, (float)this.ys / 2.0F, (float)this.zs / 2.0F);
                                t.begin();
//                                t.offset(new BigDecimal(this.bigX.negate()), -this.y, new BigDecimal(this.bigZ.negate()));
                            }

                            Tile tile = Tile.tiles[tileId];
                            int renderLayer = tile.getRenderLayer();
                            if (renderLayer != l) {
                                renderNextLayer = true;
                            } else if (renderLayer == l) {
                                rendered |= tileRenderer.tesselateInWorld(tile, x, y, z);
                            }
                        }
                    }
                }
            }

            if (started) {
                t.end();
                GL11.glPopMatrix();
                GL11.glEndList();
                t.offset(BigDecimal.ZERO, 0.0, BigDecimal.ZERO);
                t.offset(0.0, 0.0, 0.0);
            } else {
                rendered = false;
            }

            if (rendered) {
                this.empty[l] = false;
            }

            if (!renderNextLayer) {
                break;
            }
        }
    }

    private boolean mouseDown = false;
    private final Quaternion rotation = new Quaternion();
    private float lastMouseX = 0;
    private float lastMouseY = 0;

    public void updateMouse(int xm, int ym, float a) {
        if (Mouse.isButtonDown(0)) {
            if (!mouseDown) {
                mouseDown = true;
                lastMouseX = xm;
                lastMouseY = ym;
                return;
            }

            float dx = xm - lastMouseX;
            float dy = ym - lastMouseY;

            lastMouseX = xm;
            lastMouseY = ym;

            // Reduce sensitivity a lot — quaternion updates are easy to overdo
            float sensitivity = 0.005F;

            Quaternion yawDelta = fromAxisAngle(0.0F, -1.0F, 0.0F, dx * sensitivity);
            Quaternion pitchDelta = fromAxisAngle(1.0F, 0.0F, 0.0F, dy * sensitivity);

            Quaternion temp = new Quaternion();
            Quaternion.mul(rotation, pitchDelta, temp);
            Quaternion.mul(temp, yawDelta, rotation);

            rotation.normalise(rotation);
        } else {
            mouseDown = false;
        }
    }

    private static Quaternion fromAxisAngle(float x, float y, float z, float radians) {
        Quaternion q = new Quaternion();
        q.setFromAxisAngle(new Vector4f(x, y, z, radians));
        return q;
    }

    private static FloatBuffer toMatrix(Quaternion q) {
        float x = q.x;
        float y = q.y;
        float z = q.z;
        float w = q.w;

        float xx = x * x;
        float yy = y * y;
        float zz = z * z;
        float xy = x * y;
        float xz = x * z;
        float yz = y * z;
        float wx = w * x;
        float wy = w * y;
        float wz = w * z;

        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        buffer.put(new float[] {
                1.0F - 2.0F * (yy + zz), 2.0F * (xy - wz),         2.0F * (xz + wy),         0.0F,
                2.0F * (xy + wz),         1.0F - 2.0F * (xx + zz), 2.0F * (yz - wx),         0.0F,
                2.0F * (xz - wy),         2.0F * (yz + wx),         1.0F - 2.0F * (xx + yy), 0.0F,
                0.0F,                     0.0F,                     0.0F,                     1.0F
        });
        buffer.flip();
        return buffer;
    }
}
