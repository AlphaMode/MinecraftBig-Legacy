package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.alphamode.mcbig.commands.arguments.BigVec3Argument;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.util.BigCoordinates;
import me.alphamode.mcbig.world.phys.BigVec3;
import net.minecraft.world.entity.player.Player;

public class TeleportCommand {
    public static <S extends CommandSource> void register(CommandDispatcher<S> dispatcher) {
        dispatcher.register(
                Commands.<S>literal("tp")
                        .then(Commands.<S, BigCoordinates>argument("location", new BigVec3Argument()).executes(context -> {
                            BigCoordinates coordinates = context.getArgument("location", BigCoordinates.class);
                            CommandSource source = context.getSource();
                            BigEntityExtension bigEntity = (BigEntityExtension) source.getEntity();
                            BigVec3 pos = coordinates.getBigVec3(source);
                            if (bigEntity instanceof Player player)
                                player.teleport(pos.x, pos.y, pos.z);
                            else
                                bigEntity.setPos(pos.x, pos.y, pos.z);
                            return Command.SINGLE_SUCCESS;
                        })
        ));
    }
}
