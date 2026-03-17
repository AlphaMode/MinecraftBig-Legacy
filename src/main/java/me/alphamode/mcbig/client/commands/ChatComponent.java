package me.alphamode.mcbig.client.commands;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class ChatComponent {
    private static final int MAX_CHAT_HISTORY = 100;
    private final List<String> recentChat = new ArrayList<>(MAX_CHAT_HISTORY);

    private final Minecraft minecraft;

    public ChatComponent(Minecraft minecraft) {
        this.minecraft = minecraft;
        this.recentChat.addAll(minecraft.commandHistory().history());
    }

    public List<String> getRecentChat() {
        return this.recentChat;
    }

    public String peekLast() {
        return this.recentChat.isEmpty() ? null : this.recentChat.getLast();
    }

    public void addRecentChat(final String message) {
        if (!message.equals(peekLast())) {
            if (this.recentChat.size() >= MAX_CHAT_HISTORY) {
                this.recentChat.removeFirst();
            }

            this.recentChat.addLast(message);
        }

        if (message.startsWith("/")) {
            this.minecraft.commandHistory().addCommand(message);
        }
    }
}
