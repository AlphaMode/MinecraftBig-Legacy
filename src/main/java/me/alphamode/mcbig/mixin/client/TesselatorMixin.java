package me.alphamode.mcbig.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.alphamode.mcbig.extensions.BigTesselatorExtension;
import net.minecraft.client.renderer.Tesselator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.math.BigDecimal;

@Mixin(Tesselator.class)
public abstract class TesselatorMixin implements BigTesselatorExtension {
    @Shadow private double yo;

    @Shadow public abstract void tex(double u, double v);

    @Shadow private int count;
    @Shadow private int mode;
    @Shadow private static boolean TRIANGLE_MODE;
    @Shadow private boolean hasTexture;
    @Shadow private int[] array;
    @Shadow private int p;
    @Shadow private boolean hasColor;
    @Shadow private int vertices;
    @Shadow private double u;
    @Shadow private double v;
    @Shadow private int packedColor;
    @Shadow private boolean hasNormal;
    @Shadow private int normal;
    @Shadow private int size;

    @Shadow public abstract void end();

    @Shadow private boolean tesselating;

    private BigDecimal xoBig = BigDecimal.ZERO;
    private BigDecimal zoBig = BigDecimal.ZERO;

    @Override
    public void vertexUV(BigDecimal x, double y, BigDecimal z, double u, double v) {
        tex(u, v);
        vertex(x, y, z);
    }

    @Override
    public void vertex(BigDecimal x, double y, BigDecimal z) {
        ++this.count;
        if (this.mode == 7 && TRIANGLE_MODE && this.count % 4 == 0) {
            for(int i = 0; i < 2; ++i) {
                int offs = 8 * (3 - i);
                if (this.hasTexture) {
                    this.array[this.p + 3] = this.array[this.p - offs + 3];
                    this.array[this.p + 4] = this.array[this.p - offs + 4];
                }

                if (this.hasColor) {
                    this.array[this.p + 5] = this.array[this.p - offs + 5];
                }

                this.array[this.p + 0] = this.array[this.p - offs + 0];
                this.array[this.p + 1] = this.array[this.p - offs + 1];
                this.array[this.p + 2] = this.array[this.p - offs + 2];
                ++this.vertices;
                this.p += 8;
            }
        }

        if (this.hasTexture) {
            this.array[this.p + 3] = Float.floatToRawIntBits((float)this.u);
            this.array[this.p + 4] = Float.floatToRawIntBits((float)this.v);
        }

        if (this.hasColor) {
            this.array[this.p + 5] = this.packedColor;
        }

        if (this.hasNormal) {
            this.array[this.p + 6] = this.normal;
        }

        this.array[this.p + 0] = Float.floatToRawIntBits(x.add(this.xoBig).floatValue());
        this.array[this.p + 1] = Float.floatToRawIntBits((float)(y + this.yo));
        this.array[this.p + 2] = Float.floatToRawIntBits(z.add(this.zoBig).floatValue());
        this.p += 8;
        ++this.vertices;
        if (this.vertices % 4 == 0 && this.p >= this.size - 32) {
            end();
            this.tesselating = true;
        }
    }

    @Override
    public void offset(BigDecimal x, double y, BigDecimal z) {
        this.xoBig = x;
        this.yo = y;
        this.zoBig = z;
    }

    @Override
    public void addOffset(BigDecimal xo, float yo, BigDecimal zo) {
        this.xoBig = this.xoBig.add(xo);
        this.yo += yo;
        this.zoBig = this.zoBig.add(zo);
    }
}
