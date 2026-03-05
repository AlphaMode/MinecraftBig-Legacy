package me.alphamode.mcbig.client.gui;

import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.locale.I18n;
import org.lwjgl.input.Keyboard;

public class McBigWorldOptionsScreen extends Screen {
    private final Screen parent;

    public McBigWorldOptionsScreen(Screen parent) {
        this.parent = parent;
    }

    @Override
    public void init() {
        I18n language = I18n.getInstance();
        Keyboard.enableRepeatEvents(true);

        this.buttons.clear();

        this.buttons.add(new WorldTypeButton(0, this.width / 2 - 155, this.height / 6 + 24));
        this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 120 + 12, language.get("gui.done")));
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.active) {
            if (button.id == 0 && button instanceof WorldTypeButton worldTypeButton) {
                worldTypeButton.clicked();
            }

            if (button.id == 1) {
                this.minecraft.setScreen(this.parent);
            }
        }
    }

    @Override
    public void render(int xm, int ym, float a) {
        this.renderBackground();
        this.drawCenteredString(this.font, "Mc Big World Options", this.width / 2, 20, 16777215);
        this.drawString(this.font, "World Type", this.width / 2 - 153, this.height / 6 + 24 - 12, 10526880);
        super.render(xm, ym, a);
    }
}
