package me.alphamode.mcbig.mixin.features.big_movement;

import me.alphamode.mcbig.extensions.BigOffsettedRenderListExtension;
import net.minecraft.client.renderer.OffsettedRenderList;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.IntBuffer;

@Mixin(OffsettedRenderList.class)
public class OffsettedRenderListMixin implements BigOffsettedRenderListExtension {

    private BigInteger xBig;
    private BigInteger zBig;

    private BigDecimal cameraXD = BigDecimal.ZERO;
    private double cameraYD;
    private BigDecimal cameraZD = BigDecimal.ZERO;

    @Shadow
    private boolean inited;

    @Shadow
    private IntBuffer lists;

    @Shadow
    private int y;

    @Shadow
    private boolean rendered;

    @Override
    public void init(BigInteger x, int y, BigInteger z, double cameraX, double cameraY, double cameraZ) {
        init(x, y, z, BigDecimal.valueOf(cameraX), cameraY, BigDecimal.valueOf(cameraZ));
    }

    @Override
    public void init(BigInteger x, int y, BigInteger z, BigDecimal cameraX, double cameraY, BigDecimal cameraZ) {
        this.inited = true;
        this.lists.clear();
        this.xBig = x;
        this.y = y;
        this.zBig = z;
        this.cameraXD = cameraX;
        this.cameraYD = cameraY;
        this.cameraZD = cameraZ;
    }

    @Override
    public boolean isAt(BigInteger x, int y, BigInteger z) {
        if (!this.inited) {
            return false;
        } else {
            return x.equals(this.xBig) && y == this.y && z.equals(this.zBig);
        }
    }

    /**
     * @author AlphaMode
     * @reason fix camera jitter
     */
    @Overwrite
    public void render() {
        if (this.inited) {
            if (!this.rendered) {
                this.lists.flip();
                this.rendered = true;
            }

            if (this.lists.remaining() > 0) {
                GL11.glPushMatrix();
                GL11.glTranslatef(new BigDecimal(this.xBig).subtract(this.cameraXD).floatValue(), (float) (this.y - this.cameraYD), new BigDecimal(this.zBig).subtract(this.cameraZD).floatValue());
                GL11.glCallLists(this.lists);
                GL11.glPopMatrix();
            }
        }
    }
}
