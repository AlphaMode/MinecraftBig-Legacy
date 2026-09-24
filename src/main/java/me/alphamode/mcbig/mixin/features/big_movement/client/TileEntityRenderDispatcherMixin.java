package me.alphamode.mcbig.mixin.features.big_movement.client;

import me.alphamode.mcbig.client.renderer.entity.EntityRenderDispatcherData;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.Textures;
import net.minecraft.client.renderer.tileentity.TileEntityRenderDispatcher;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.entity.TileEntity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.math.BigDecimal;

@Mixin(TileEntityRenderDispatcher.class)
public abstract class TileEntityRenderDispatcherMixin {
    @Shadow
    public Level level;

    @Shadow
    public Textures textureManager;

    @Shadow
    public Mob player;

    @Shadow
    private Font font;

    @Shadow
    public float yRot;

    @Shadow
    public float xRot;

    @Shadow public double cameraX;
    @Shadow public double cameraY;
    @Shadow public double cameraZ;

    @Shadow
    public abstract void setLevel(Level level);

    @Shadow
    public static double yOff;

    @Shadow
    public abstract void render(TileEntity te, double x, double y, double z, float a);

    @Shadow
    public static double xOff;
    @Shadow
    public static double zOff;
    private BigDecimal cameraXBig = BigDecimal.ZERO, cameraZBig = BigDecimal.ZERO;

    private boolean bigMovementEnabled;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void prepare(Level level, Textures textureManager, Font font, Mob camera, float a) {
        if (this.level != level) {
            this.setLevel(level);
        }

        bigMovementEnabled = camera.isBigMovementEnabled();

        this.textureManager = textureManager;
        this.player = camera;
        this.font = font;
        this.yRot = camera.yRotO + (camera.yRot - camera.yRotO) * a;
        this.xRot = camera.xRotO + (camera.xRot - camera.xRotO) * a;
        if (bigMovementEnabled) {
            BigEntityExtension cameraBig = (BigEntityExtension) camera;
            BigDecimal ab = new BigDecimal(a);
            cameraXBig = cameraBig.getXOld().add(cameraBig.getX().subtract(cameraBig.getXOld()).multiply(ab));
            cameraZBig = cameraBig.getZOld().add(cameraBig.getZ().subtract(cameraBig.getZOld()).multiply(ab));
        } else {
            this.cameraX = camera.xOld + (camera.x - camera.xOld) * a;
            this.cameraZ = camera.zOld + (camera.z - camera.zOld) * a;
        }
        this.cameraY = camera.yOld + (camera.y - camera.yOld) * a;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void render(TileEntity te, float a) {
        if (bigMovementEnabled) {
            if (te.distanceSqrt(this.cameraXBig, this.cameraY, this.cameraZBig) < 4096.0) {
                float br = this.level.getBrightness(te.x, te.y, te.z);
                GL11.glColor3f(br, br, br);
                this.render(te, new BigDecimal(te.getX()).subtract(EntityRenderDispatcherData.xOff).doubleValue(), te.y - yOff, new BigDecimal(te.getZ()).subtract(EntityRenderDispatcherData.zOff).doubleValue(), a);
            }
        } else {
            if (te.distanceSqrt(this.cameraX, this.cameraY, this.cameraZ) < 4096.0) {
                float br = this.level.getBrightness(te.x, te.y, te.z);
                GL11.glColor3f(br, br, br);
                this.render(te, new BigDecimal(te.getX()).subtract(new BigDecimal(xOff)).doubleValue(), te.y - yOff, new BigDecimal(te.getZ()).subtract(new BigDecimal(zOff)).doubleValue(), a);
            }
        }
    }
}
