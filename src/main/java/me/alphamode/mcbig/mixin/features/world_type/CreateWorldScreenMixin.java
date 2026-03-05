package me.alphamode.mcbig.mixin.features.world_type;

import me.alphamode.mcbig.client.gui.McBigWorldOptionsScreen;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.worldselection.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
    @Inject(method = "init", at = @At("TAIL"))
    private void addWorldTypeButton(CallbackInfo ci) {
        this.buttons.add(new Button(2, (this.width / 2 - 100) + 200 + 6, 116, 80, 20, "World Options"));
    }

    @Inject(method = "buttonClicked", at = @At("HEAD"))
    private void handleMcBigButtons(Button button, CallbackInfo ci) {
        if (button.active && button.id == 2) {
            this.minecraft.setScreen(new McBigWorldOptionsScreen(this));
        }
    }
}
