package me.alphamode.mcbig.networking.payload;

import net.minecraft.network.PacketListener;
import net.minecraft.network.packets.MovePlayerPacket;

import java.math.BigDecimal;

public sealed interface BigMovePlayerPayload extends Payload {
    default BigDecimal x() {
        return BigDecimal.ZERO;
    }

    default double y() {
        return 0;
    }

    default double yView() {
        return 0;
    }

    default BigDecimal z() {
        return BigDecimal.ZERO;
    }

    default float yRot() {
        return 0;
    }

    default float xRot() {
        return 0;
    }

    boolean onGround();

    boolean hasPos();

    boolean hasRot();

    @Override
    default boolean handle(PacketListener listener) {
        return listener.handleBigMovePlayer(this);
    }

    record StatusOnly(boolean onGround) implements BigMovePlayerPayload {
        public static final Type<StatusOnly> TYPE = Type.create(PayloadIds.MOVE_PLAYER_STATUS, (output, payload) -> {
            output.writeBoolean(payload.onGround());
        }, input -> new StatusOnly(input.readBoolean()), StatusOnly::toVanilla);

        public MovePlayerPacket toVanilla() {
            MovePlayerPacket packet = new MovePlayerPacket();
            packet.onGround = onGround;
            return packet;
        }

        @Override
        public boolean hasPos() {
            return false;
        }

        @Override
        public boolean hasRot() {
            return false;
        }

        @Override
        public Type<StatusOnly> type() {
            return TYPE;
        }
    }

    record Pos(BigDecimal x, double y, double yView, BigDecimal z, boolean onGround) implements BigMovePlayerPayload {
        public static final Type<Pos> TYPE = Type.create(PayloadIds.MOVE_PLAYER_POS, (output, payload) -> {
            output.writeUTF(payload.x().toString());
            output.writeDouble(payload.y());
            output.writeDouble(payload.yView());
            output.writeUTF(payload.z().toString());
            output.writeBoolean(payload.onGround());
        }, input -> {
            BigDecimal x = new BigDecimal(input.readUTF());
            double y = input.readDouble();
            double yView = input.readDouble();
            BigDecimal z = new BigDecimal(input.readUTF());
            return new Pos(x, y, yView, z, input.readBoolean());
        }, Pos::toVanilla);

        public MovePlayerPacket.Pos toVanilla() {
            MovePlayerPacket.Pos pos = new MovePlayerPacket.Pos();
            pos.x = x.doubleValue();
            pos.y = y;
            pos.yView = yView;
            pos.z = z.doubleValue();
            pos.onGround = onGround;
            return pos;
        }

        @Override
        public boolean hasPos() {
            return true;
        }

        @Override
        public boolean hasRot() {
            return false;
        }

        @Override
        public Type<Pos> type() {
            return TYPE;
        }
    }

    record PosRot(BigDecimal x, double y, double yView, BigDecimal z, float yRot, float xRot,
                  boolean onGround) implements BigMovePlayerPayload {
        public static final Type<PosRot> TYPE = Type.create(PayloadIds.MOVE_PLAYER_POS_ROT, (output, payload) -> {
            output.writeUTF(payload.x().toString());
            output.writeDouble(payload.y());
            output.writeDouble(payload.yView());
            output.writeUTF(payload.z().toString());
            output.writeFloat(payload.yRot());
            output.writeFloat(payload.xRot());
            output.writeBoolean(payload.onGround());
        }, input -> {
            BigDecimal x = new BigDecimal(input.readUTF());
            double y = input.readDouble();
            double yView = input.readDouble();
            BigDecimal z = new BigDecimal(input.readUTF());
            float yRot = input.readFloat();
            float xRot = input.readFloat();
            return new PosRot(x, y, yView, z, yRot, xRot, input.readBoolean());
        }, PosRot::toVanilla);

        public MovePlayerPacket.PosRot toVanilla() {
            return new MovePlayerPacket.PosRot(x.doubleValue(), y, yView, z.doubleValue(), yRot, xRot, onGround);
        }

        @Override
        public boolean hasPos() {
            return true;
        }

        @Override
        public boolean hasRot() {
            return true;
        }

        @Override
        public Type<PosRot> type() {
            return TYPE;
        }
    }

    record Rot(float yRot, float xRot, boolean onGround) implements BigMovePlayerPayload {
        public static final Type<Rot> TYPE = Type.create(PayloadIds.MOVE_PLAYER_ROT, (output, payload) -> {
            output.writeFloat(payload.yRot());
            output.writeFloat(payload.xRot());
            output.writeBoolean(payload.onGround());
        }, input -> new Rot(input.readFloat(), input.readFloat(), input.readBoolean())
        , Rot::toVanilla);

        public MovePlayerPacket.Rot toVanilla() {
            MovePlayerPacket.Rot packet = new MovePlayerPacket.Rot();
            packet.yRot = yRot;
            packet.xRot = xRot;
            packet.onGround = onGround;
            return packet;
        }

        @Override
        public boolean hasPos() {
            return false;
        }

        @Override
        public boolean hasRot() {
            return true;
        }

        @Override
        public Type<Rot> type() {
            return TYPE;
        }
    }
}
