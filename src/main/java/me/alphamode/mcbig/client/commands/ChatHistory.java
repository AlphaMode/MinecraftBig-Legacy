package me.alphamode.mcbig.client.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

public class ChatHistory {
    private static final int MAX_PERSISTED_COMMAND_HISTORY = 50;
    private final Path commandsPath;
    private final Deque<String> lastCommands = new ArrayDeque<>(MAX_PERSISTED_COMMAND_HISTORY);

    public ChatHistory(final Path gameFolder) {
        this.commandsPath = gameFolder.resolve("command_history.txt");
        if (Files.exists(this.commandsPath)) {
            try {
                BufferedReader reader = Files.newBufferedReader(this.commandsPath, StandardCharsets.UTF_8);

                try {
                    this.lastCommands.addAll(reader.lines().toList());
                } catch (Throwable var6) {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Throwable e) {
                            var6.addSuppressed(e);
                        }
                    }

                    throw var6;
                }

                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                System.err.println("Failed to read command_history.txt, command history will be missing");
                e.printStackTrace();
            }
        }
    }

    public void addCommand(final String command) {
        if (!command.equals(this.lastCommands.peekLast())) {
            if (this.lastCommands.size() >= 50) {
                this.lastCommands.removeFirst();
            }

            this.lastCommands.addLast(command);
            this.save();
        }
    }

    private void save() {
        try {
            BufferedWriter writer = Files.newBufferedWriter(this.commandsPath, StandardCharsets.UTF_8);

            try {
                for (String command : this.lastCommands) {
                    writer.write(command);
                    writer.newLine();
                }
            } catch (Throwable e) {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (Throwable suppressed) {
                        e.addSuppressed(suppressed);
                    }
                }

                throw e;
            }

            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            System.err.println("Failed to write command_history.txt, command history will be missing");
            e.printStackTrace();
        }
    }

    public Collection<String> history() {
        return this.lastCommands;
    }
}
