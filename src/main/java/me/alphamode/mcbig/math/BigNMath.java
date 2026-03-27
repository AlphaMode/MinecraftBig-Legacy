package me.alphamode.mcbig.math;

import me.alphamode.mcbig.core.NBigDec;
import me.alphamode.mcbig.core.NBigInt;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigNMath {
    public static NBigDec decimal(int value) {
        return new NBigDec(value);
    }

    public static NBigDec decimal(double value) {
        return new NBigDec(value);
    }

    public static NBigDec decimalW(NBigInt value) {
        return new NBigDec(value);
    }
}
