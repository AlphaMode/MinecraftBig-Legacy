package me.alphamode.mcbig.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.EditBox;

public class McBigEditBox extends EditBox {
    private final String suggestion;

    public McBigEditBox(Screen parent, Font font, int x, int y, int width, int height, String value, String suggestion) {
        super(parent, font, x, y, width, height, value);
        this.suggestion = suggestion;
        this.blitOffset = -900;
    }

    @Override
    public void render() {
        //-8355712
        super.render();
        if (this.value != null && this.value.isEmpty()) {
            this.drawString(this.font, this.suggestion, this.x + 4, this.y + (this.height - 8) / 2, -8355712);
        }
    }
}
