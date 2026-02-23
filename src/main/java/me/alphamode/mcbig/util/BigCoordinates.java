package me.alphamode.mcbig.util;

import me.alphamode.mcbig.commands.CommandSource;
import me.alphamode.mcbig.math.BigMath;
import me.alphamode.mcbig.world.phys.BigVec3;
import me.alphamode.mcbig.world.phys.BigVec3i;
import net.minecraft.util.Mth;

import java.math.BigDecimal;
import java.math.BigInteger;

public sealed interface BigCoordinates {
    BigVec3 getBigVec3(CommandSource source);

    BigVec3i getBlockPos(CommandSource source);

    record BigIntegerCoordinates(BigWorldCoordinate<BigInteger> x, BigWorldCoordinate<Integer> y, BigWorldCoordinate<BigInteger> z) implements BigCoordinates {
        @Override
        public BigVec3 getBigVec3(CommandSource source) {
            return BigVec3.create(new BigDecimal(this.x.get(BigMath.floor(source.getX()))), this.y.get(Mth.floor(source.getEntity().y)), new BigDecimal(this.z.get(BigMath.floor(source.getZ()))));
        }

        @Override
        public BigVec3i getBlockPos(CommandSource source) {
            return new BigVec3i(this.x.get(BigMath.floor(source.getX())), this.y.get(Mth.floor(source.getEntity().y)), this.z.get(BigMath.floor(source.getZ())));
        }
    }

    record BigDecimalCoordinates(BigWorldCoordinate<BigDecimal> x, BigWorldCoordinate<Double> y, BigWorldCoordinate<BigDecimal> z) implements BigCoordinates {
        @Override
        public BigVec3 getBigVec3(CommandSource source) {
            return BigVec3.create(this.x.get(source.getX()), this.y.get(source.getEntity().y), this.z.get(source.getZ()));
        }

        @Override
        public BigVec3i getBlockPos(CommandSource source) {
            return null;
        }
    }
}
