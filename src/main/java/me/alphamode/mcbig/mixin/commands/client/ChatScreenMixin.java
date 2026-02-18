package me.alphamode.mcbig.mixin.commands.client;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alphamode.mcbig.commands.Commands;
import me.alphamode.mcbig.util.Mth2;
import me.alphamode.mcbig.client.commands.gui.ChatEditBox;
import me.alphamode.mcbig.client.commands.gui.CommandSuggestions;
import me.alphamode.mcbig.util.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Screen;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen {
    @Shadow
    protected String message;
    @Shadow
    @Final
    private static String allowedChars;
    @Shadow
    private int frame;
    private String historyBuffer = "";
    private int historyPos = -1;
    private CommandSuggestions commandSuggestions;
    private ChatEditBox editBox;

    @Inject(method = "init", at = @At("TAIL"))
    private void initSuggestions(CallbackInfo ci) {
        this.editBox = new ChatEditBox(this.font, 4, this.height - 12, this.width - 4, 12, newValue -> message = newValue, () -> message);
        this.editBox.setResponder(this::onEdited);
        this.commandSuggestions = new CommandSuggestions(this.minecraft, this, this.editBox, this.font, 1, 10, -805306368);
        this.commandSuggestions.updateCommandInfo();
    }

    private void onEdited(final String value) {
        this.commandSuggestions.setAllowSuggestions(true);
        this.commandSuggestions.updateCommandInfo();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ChatScreen;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V"))
    private void renderEditBox(ChatScreen instance, Font font, String text, int x, int y, int color) {
        this.editBox.render(this.frame / 6 % 2 == 0, color);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderSuggestions(int mouseX, int mouseY, float a, CallbackInfo ci) {
        this.commandSuggestions.render(mouseX, mouseY);
    }

//    @Inject(method = "keyPressed", at = @At("HEAD"))
//    private void chatHistory(char eventCharacter, int eventKey, CallbackInfo ci) {
//        switch (eventKey) {
//            case Keyboard.KEY_DOWN -> this.moveInHistory(1);
//            case Keyboard.KEY_UP -> this.moveInHistory(-1);
//            case Keyboard.KEY_V -> {
//                if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
//
//                }
//            }
//        }
//        this.commandSuggestions.keyPressed(eventCharacter, eventKey);
//    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(int mouseX, int mouseY, int button, CallbackInfo ci) {
        if (this.commandSuggestions.mouseClicked(mouseX, mouseY, button)) {
            ci.cancel();
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void keyPressed(char eventCharacter, int eventKey) {
        boolean shiftDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
        boolean altDown = Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
        boolean controlDown = Minecraft.getPlatform() == Minecraft.OS.MACOS ? (Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA)) : Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
        switch (eventKey) {
            case Keyboard.KEY_DOWN -> this.moveInHistory(1);
            case Keyboard.KEY_UP -> this.moveInHistory(-1);
            case Keyboard.KEY_A -> {
                if (controlDown && !shiftDown && !altDown) {
                    this.editBox.moveCursorToEnd(false);
                    this.editBox.setHighlightPos(0);
                }
            }
            case Keyboard.KEY_C -> {
                if (controlDown) {
                    ScreenUtil.setClipboard(this.editBox.getHighlighted());
                }
            }
            case Keyboard.KEY_RIGHT -> {
                if (controlDown) {
                    this.editBox.moveCursorTo(this.editBox.getWordPosition(1), shiftDown);
                } else {
                    this.editBox.moveCursor(1, shiftDown);
                }
            }
            case Keyboard.KEY_LEFT -> {
                if (controlDown) {
                    this.editBox.moveCursorTo(this.editBox.getWordPosition(-1), shiftDown);
                } else {
                    this.editBox.moveCursor(-1, shiftDown);
                }
            }
            case Keyboard.KEY_V -> {
                if (controlDown) {
                    this.editBox.insertText(ScreenUtil.getClipboard());
                }
            }
        }

        this.commandSuggestions.keyPressed(eventCharacter, eventKey);
        if (eventKey == 1) {
            this.minecraft.setScreen(null);
        } else if (eventKey == Keyboard.KEY_RETURN) {
            String trimmed = this.message.trim();
            if (trimmed.length() > 0) {
                String message = this.message.trim();
                if (this.minecraft.isCommand(message)) {
                    executeCommand(message.substring(1));
                } else {
                    this.minecraft.player.chat(message);
                }
            }

            this.minecraft.setScreen(null);
        } else {
            if (eventKey == Keyboard.KEY_BACK && this.message.length() > 0) {
                this.editBox.deleteText(-1, controlDown);
            }

            if (eventKey == Keyboard.KEY_DELETE && this.message.length() > 0) {
                this.editBox.deleteText(1, controlDown);
            }

            if (allowedChars.indexOf(eventCharacter) >= 0 && this.message.length() < 100) {
                this.editBox.insertText(Character.toString(eventCharacter));
            }
        }
    }

    private void executeCommand(String command) {
        try {
            Commands.DISPATCHER.execute(command, this.minecraft.player.getCommandSource());
        } catch (CommandSyntaxException e) {
            this.minecraft.gui.addMessage("Failed to execute command: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void moveInHistory(final int dir) {
        int newPos = this.historyPos + dir;
        int max = this.minecraft.gui.getChat().getRecentChat().size();
        newPos = Mth2.clamp(newPos, 0, max);
        if (newPos != this.historyPos) {
            if (newPos == max) {
                this.historyPos = max;
                this.message = this.historyBuffer;
            } else {
                if (this.historyPos == max) {
                    this.historyBuffer = this.message;
                }

                this.message = this.minecraft.gui.getChat().getRecentChat().get(newPos);
                this.commandSuggestions.setAllowSuggestions(false);
                this.historyPos = newPos;
            }
        }
    }
}
