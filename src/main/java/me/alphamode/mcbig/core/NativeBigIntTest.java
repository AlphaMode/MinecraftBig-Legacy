package me.alphamode.mcbig.core;

import com.google.common.base.Stopwatch;
import me.alphamode.mcbig.world.level.levelgen.synth.BigImprovedNoise;
import me.alphamode.mcbig.world.level.levelgen.synth.BigNativeImprovedNoise;
import org.apache.commons.lang3.time.StopWatch;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class NativeBigIntTest {
    public static void main(String[] args) {
        Random r = new Random(2);

        for (int i = 0; i < 10; i++) {
            IO.println(r.nextInt());
        }
        StopWatch start = new StopWatch();
//        start.start();

        BigImprovedNoise noise = new BigImprovedNoise(new Random(2));
        BigNativeImprovedNoise noise2 = new BigNativeImprovedNoise(new Random(2));

        BigDecimal startD = new BigDecimal(Double.MAX_VALUE);
        NBigDec startD2 = new NBigDec(startD.toPlainString());
        boolean first = true;
        for (int i = 0; i < 10000; i++) {
            start.start();
            noise.getValue(startD, startD);
            IO.println("Vanilla Elapsed: " + start.getTime());
            start.reset();
            start.start();
            noise2.getValue(startD2, startD2);
            IO.println("Native Elapsed: " + start.getTime());
            start.reset();
            startD = startD.add(new BigDecimal(1));
        }
        IO.println("Vanilla Elapsed: " + start.getTime());

        start.reset();
        start.start();




        for (int i = 0; i < 10000; i++) {
            noise2.getValue(startD2, startD2);
            startD2 = startD2.add(new NBigDec(1));
        }
//        IO.println("Native Elapsed: " + start.elapsed(TimeUnit.NANOSECONDS));
    }
}
