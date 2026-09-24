package me.alphamode.mcbig.networking.payload;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import me.alphamode.mcbig.commands.CommandSource;
import net.minecraft.network.PacketListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public record CommandsPayload(Node rootNode) implements Payload {
    public static final Type<CommandsPayload> TYPE = Type.create(PayloadIds.COMMANDS, CommandsPayload::encode, CommandsPayload::decode);

    public CommandsPayload(RootCommandNode<? extends CommandSource> rootNode) {
        this(createNode(rootNode));
    }

    public static Node createNode(CommandNode<? extends CommandSource> node) {
        Map<String, Node> children = new LinkedHashMap<>();
        for (CommandNode<? extends CommandSource> child : node.getChildren()) {
            children.put(child.getName(), createNode(child));
        }

        Node redirect = createNode(node.getRedirect());

        NodeType type = switch (node) {
            case RootCommandNode<?> root -> new RootNodeType();
            case LiteralCommandNode<?> literal -> new LiteralNodeType(literal.getLiteral());
            case ArgumentCommandNode<?, ?> argument -> new ArgumentNodeType(argument.getName());
            default -> throw new IllegalStateException("Unexpected value: " + node);
        };

        return new Node(type, children, redirect);
    }

    public static void encode(DataOutputStream output, CommandsPayload payload) {

    }

    public static CommandsPayload decode(DataInputStream input) {
        return null;
    }

    @Override
    public Type<CommandsPayload> type() {
        return TYPE;
    }

    @Override
    public boolean handle(PacketListener listener) {
        return listener.handleCommands(this);
    }

    public record Node(NodeType type, Map<String, Node> children, Node redirect) {

    }

    public sealed interface NodeType {
        int getId();
    }

    public record RootNodeType() implements NodeType {
        @Override
        public int getId() {
            return 0;
        }
    }

    public record LiteralNodeType(String literal) implements NodeType {
        @Override
        public int getId() {
            return 1;
        }
    }

    public record ArgumentNodeType(String name) implements NodeType {
        @Override
        public int getId() {
            return 2;
        }
    }
}