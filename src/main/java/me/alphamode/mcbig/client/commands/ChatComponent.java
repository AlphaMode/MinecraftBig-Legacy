package me.alphamode.mcbig.client.commands;

import java.util.ArrayList;
import java.util.List;

public class ChatComponent {
    private final List<String> recentChat = new ArrayList<>(100);

    public List<String> getRecentChat() {
        return this.recentChat;
    }
}
