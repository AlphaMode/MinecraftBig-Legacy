package me.alphamode.mcbig.util;

import net.minecraft.world.level.tile.Tile;
import org.jspecify.annotations.Nullable;
import org.opentest4j.AssertionFailedError;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.AssertionFailureBuilder.assertionFailure;

public class TestUtils {
    static String formatIndexes(@Nullable Deque<Integer> indexes) {
        if (indexes == null || indexes.isEmpty()) {
            return "";
        }
        String indexesString = indexes.stream().map(Object::toString).collect(joining("][", "[", "]"));
        return " at index " + indexesString;
    }

    public static IntStream byteStream(byte[] a) {
        return IntStream.range(0, a.length).map(idx -> a[idx]);
    }

    private static void failArraysNotEqual(@Nullable byte expected, @Nullable byte actual,
                                           @Nullable Deque<Integer> indexes, @Nullable Object messageOrSupplier) {

        assertionFailure() //
                .message(messageOrSupplier) //
                .reason("array contents differ" + formatIndexes(indexes)) //
                .expected("T: " + Tile.tiles[expected].getDescriptionId() + ", ID: " + expected) //
                .actual("T: " + Tile.tiles[actual].getDescriptionId() + ", ID: " + actual) //
                .buildAndThrow();
    }

    private static void assertArraysHaveSameLength(int expected, int actual, @Nullable Deque<Integer> indexes,
                                                   @Nullable Object messageOrSupplier) {

        if (expected != actual) {
            assertionFailure() //
                    .message(messageOrSupplier) //
                    .reason("array lengths differ" + formatIndexes(indexes)) //
                    .expected(expected) //
                    .actual(actual) //
                    .buildAndThrow();
        }
    }

    private static AssertionFailedError actualArrayIsNullFailure(@Nullable Deque<Integer> indexes,
                                                                 @Nullable Object messageOrSupplier) {
        return assertionFailure() //
                .message(messageOrSupplier) //
                .reason("actual array was <null>" + formatIndexes(indexes)) //
                .build();
    }

    private static AssertionFailedError expectedArrayIsNullFailure(@Nullable Deque<Integer> indexes,
                                                                   @Nullable Object messageOrSupplier) {
        return assertionFailure() //
                .message(messageOrSupplier) //
                .reason("expected array was <null>" + formatIndexes(indexes)) //
                .build();
    }

    public static void assertArrayEquals(byte @Nullable [] expected, byte @Nullable [] actual, @Nullable String message) {
        assertArrayEquals(expected, actual, null, message);
    }

    private static void assertArrayEquals(byte @Nullable [] expected, byte @Nullable [] actual,
                                          @Nullable Deque<Integer> indexes, @Nullable Object messageOrSupplier) {

        if (expected == actual) {
            return;
        }

        if (expected == null) {
            throw expectedArrayIsNullFailure(indexes, messageOrSupplier);
        }
        if (actual == null) {
            throw actualArrayIsNullFailure(indexes, messageOrSupplier);
        }
        assertArraysHaveSameLength(expected.length, actual.length, indexes, messageOrSupplier);

        for (int i = 0; i < expected.length; i++) {
            if (expected[i] != actual[i]) {
                failArraysNotEqual(expected[i], actual[i], nullSafeIndexes(indexes, i), messageOrSupplier);
            }
        }
    }

    private static Deque<Integer> nullSafeIndexes(@Nullable Deque<Integer> indexes, int newIndex) {
        Deque<Integer> result = (indexes != null ? indexes : new ArrayDeque<>());
        result.addLast(newIndex);
        return result;
    }
}
