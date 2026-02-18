package me.alphamode.mcbig.extensions.features.commands;

import me.alphamode.mcbig.client.commands.ChatComponent;

public interface GuiExtension {
    default ChatComponent getChat() {
        throw new UnsupportedOperationException();
    }
}
