package me.alphamode.mcbig.mixin.features.world_type;

import dev.kikugie.fletching_table.mixin.MixinEnvironment;
import me.alphamode.mcbig.client.gui.McBigWorldOptionsScreen;
import me.alphamode.mcbig.world.level.levelgen.WorldType;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.Button;
import net.minecraft.client.gui.worldselection.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {

    @Shadow
    private EditBox seedEdit;
    @Shadow
    private EditBox nameEdit;
    private WorldType selected = WorldType.VANILLA;

    private String lastName = null;
    private String lastSeed = null;

    @Inject(method = "init", at = @At("TAIL"))
    private void addWorldTypeButton(CallbackInfo ci) {
        this.buttons.add(new Button(24, (this.width / 2 - 100) + 200 + 6, 116, 80, 20, "World Options"));
        if (lastName != null)
            nameEdit.value = lastName;
        if (lastSeed != null)
            seedEdit.value = lastSeed;
    }

    @Inject(method = "buttonClicked", at = @At("HEAD"))
    private void handleMcBigButtons(Button button, CallbackInfo ci) {
        if (button.active && button.id == 24) {
            this.lastName = this.nameEdit.value;
            this.lastSeed = this.seedEdit.value;
            this.minecraft.setScreen(new McBigWorldOptionsScreen(this, selected, worldType -> this.selected = worldType));
        }
    }

    @Inject(method = "buttonClicked", at = @At(value = "NEW", target = "(Lnet/minecraft/client/Minecraft;)Lnet/minecraft/client/gamemode/SurvivalGameMode;"))
    private void setWorldType(Button b, CallbackInfo ci) {
        WorldType.SELECTED = selected;
    }
}
