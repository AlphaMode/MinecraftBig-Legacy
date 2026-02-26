package me.alphamode.mcbig.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.alphamode.mcbig.commands.arguments.AxisArgument;
import me.alphamode.mcbig.commands.arguments.EnumArgument;
import me.alphamode.mcbig.extensions.features.big_movement.BigEntityExtension;
import me.alphamode.mcbig.util.Axis;
import net.minecraft.world.entity.Entity;

import java.math.BigDecimal;

public class TeleportConstantCommand {
    // Minecraft Limits
    public static final BigDecimal STRIPE_LANDS = new BigDecimal("9007199254740992");
    public static final BigDecimal FAR_LANDS = new BigDecimal("12550821");

    // Primitive Limits
    public static final BigDecimal MIN_INT_LIMIT = new BigDecimal(Integer.MAX_VALUE);
    public static final BigDecimal MAX_INT_LIMIT = new BigDecimal(Integer.MAX_VALUE);
    public static final BigDecimal MIN_LONG_LIMIT = new BigDecimal(Long.MAX_VALUE);
    public static final BigDecimal MAX_LONG_LIMIT = new BigDecimal(Long.MAX_VALUE);
    public static final BigDecimal MIN_FLOAT_LIMIT = new BigDecimal(Float.MAX_VALUE);
    public static final BigDecimal MAX_FLOAT_LIMIT = new BigDecimal(Float.MAX_VALUE);
    public static final BigDecimal MIN_DOUBLE_LIMIT = new BigDecimal(Double.MAX_VALUE);
    public static final BigDecimal MAX_DOUBLE_LIMIT = new BigDecimal(Double.MAX_VALUE);

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        var command = Commands.literal("tpconstant")
                .then(Commands.argument("axis", new AxisArgument())
                        .then(
                                Commands.argument("constant", new EnumArgument<>(TeleportConstants.class, TeleportConstants.values()))
                                        .executes(context -> {
                                            return teleport(context.getSource().getEntity(), context.getArgument("axis", Axis.class), context.getArgument("constant", TeleportConstants.class).value());
                                        })
                        )
                );
        dispatcher.register(command);
    }

    private static int teleport(Entity entity, Axis axis, BigDecimal value) {
        if (entity.isBigMovementEnabled()) {
            BigEntityExtension bigEntity = (BigEntityExtension) entity;
            switch (axis) {
                case X -> bigEntity.setPos(value, entity.y, bigEntity.getZ());
                case Y -> bigEntity.setPos(bigEntity.getX(), value.doubleValue(), bigEntity.getZ());
                case Z -> bigEntity.setPos(bigEntity.getX(), entity.y, value);
            }
        } else {
            switch (axis) {
                case X -> entity.setPos(value.doubleValue(), entity.y, entity.z);
                case Y -> entity.setPos(entity.x, value.doubleValue(), entity.z);
                case Z -> entity.setPos(entity.x, entity.y, value.doubleValue());
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    public enum TeleportConstants implements EnumArgument.EnumData {
        STRIPE_LANDS(TeleportConstantCommand.STRIPE_LANDS, "Location of the stripe lands"),
        FAR_LANDS(TeleportConstantCommand.FAR_LANDS, "Location of the far lands"),
        MIN_INT_LIMIT(TeleportConstantCommand.MIN_INT_LIMIT),
        MAX_INT_LIMIT(TeleportConstantCommand.MAX_INT_LIMIT),
        MIN_LONG_LIMIT(TeleportConstantCommand.MIN_LONG_LIMIT),
        MAX_LONG_LIMIT(TeleportConstantCommand.MAX_LONG_LIMIT),
        MIN_FLOAT_LIMIT(TeleportConstantCommand.MIN_FLOAT_LIMIT),
        MAX_FLOAT_LIMIT(TeleportConstantCommand.MAX_FLOAT_LIMIT),
        MIN_DOUBLE_LIMIT(TeleportConstantCommand.MIN_DOUBLE_LIMIT),
        MAX_DOUBLE_LIMIT(TeleportConstantCommand.MAX_DOUBLE_LIMIT);

        private final BigDecimal value;
        private final String description;

        TeleportConstants(BigDecimal value) {
            this(value, "");
        }

        TeleportConstants(BigDecimal value, String description) {
            this.value = value;
            this.description = description;
        }

        public BigDecimal value() {
            return value;
        }

        @Override
        public String description() {
            return this.description;
        }
    }
}
