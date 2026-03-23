package me.alphamode.mcbig.core;

public class NativeBigIntTest {
    public static void main(String[] args) {
        NBigInt test = new NBigInt("5236267273");
        IO.println(test.toString());
        IO.println(test.intValue());
        IO.println(test.longValue());
        IO.println(test.floatValue());
        IO.println(test.doubleValue());
//        test.compareTo(1);
    }
}
