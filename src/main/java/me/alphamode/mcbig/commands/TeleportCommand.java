package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.alphamode.mcbig.commands.arguments.BigVec3Argument;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.util.BigCoordinate;

public class TeleportCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("tp")
                        .then(Commands.argument("location", new BigVec3Argument()).executes(context -> {
                            BigCoordinate coordinates = context.getArgument("location", BigCoordinate.class);
                            CommandSource source = context.getSource();
                            BigEntityExtension bigEntity = (BigEntityExtension) source.getEntity();
                            bigEntity.setPos(coordinates.x().get(bigEntity.getX()), coordinates.y().get(source.getEntity().y), coordinates.z().get(bigEntity.getZ()));
                            return Command.SINGLE_SUCCESS;
                        })
        ));
    }
}
