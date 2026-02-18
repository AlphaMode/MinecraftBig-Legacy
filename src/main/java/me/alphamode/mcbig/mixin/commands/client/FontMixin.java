package me.alphamode.mcbig.mixin.commands.client;

import me.alphamode.mcbig.extensions.features.commands.FontExtension;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.Font;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Font.class)
public class FontMixin implements FontExtension {
    @Shadow
    private int[] charWidths;

    public int width(char character) {
        if (character == 167) {
            return -1;
        } else {
            int var4 = SharedConstants.acceptableLetters.indexOf(character);
            if (var4 >= 0) {
                return this.charWidths[var4 + 32];
            }
        }

        return 0;
    }
}
