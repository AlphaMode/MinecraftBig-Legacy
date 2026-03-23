package me.alphamode.mcbig.util;

import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BigDecimalNumberFormatter {
    public static int abbreviation_precision = 3;
    private static final String thousand = "thousand";

    private static final List<String> specials = List.of("mi", "bi", "tri", "quadri", "quin", "sex", "sept", "oct", "non");
    private static final List<String> units = List.of("un", "duo", "tre", "quattuor", "quin", "sex", "septen", "octo", "novem");
    private static final List<String> tens = List.of("dec", "vigin", "trigin", "quadragin", "quinquagin", "sexagin", "septuagin", "octogin", "nonagin");
    private static final List<String> hundreds = List.of("cen", "duocen", "trecen", "quadringen", "quingen", "sescen", "septingen", "octingen", "nongen");
    private static final String MILLIA = "millia";

    private static final String prefix = " ";
    private static final String suffix = "llion";
    private static final String illion = "i";
    private static final String tillion = "ti";

    public static String format(BigDecimal number) {
        var plain = number.toPlainString();

        var decimal = plain.indexOf('.');
        plain = plain.substring(0, decimal == -1 ? plain.length() : decimal);

        if (plain.isEmpty()) return "0";

        var mod = plain.length() % 3;
        if (mod == 0) mod = 3;
        var power = (plain.length() - mod) / 3;

        if (power <= 0) {
            return addSeparators(plain);
        }

        var scaled = new BigDecimal(plain).movePointLeft(power * 3);
        var mantissa = scaled
                .setScale(abbreviation_precision, RoundingMode.DOWN)
                .stripTrailingZeros()
                .toPlainString();

        var kiloName = getKiloName(power);
        return kiloName == null ? mantissa : mantissa + kiloName;
    }

    public static String addSeparators(String number) {
        var decimal = number.indexOf('.');
        var plain = decimal == -1 ? number : number.substring(0, decimal);
        var result = new StringBuilder();

        var separator = ",";

        for (int i = 0; i < plain.length(); i++) {
            if (i > 0 && (plain.length() - i) % 3 == 0) {
                result.append(separator);
            }
            result.append(Character.toString(plain.charAt(i)));
        }

        if (decimal != -1) result.append(number.substring(decimal));

        return result.toString();
    }

    private static List<String> splitKilos(String s) {
        var reversed = new StringBuilder(s).reverse().toString();
        var kiloKilos = new ArrayList<String>();
        for (int i = 0; i < reversed.length(); i += 3) {
            kiloKilos.add(new StringBuilder(reversed.substring(i, Math.min(i + 3, reversed.length()))).reverse().toString());
        }
        return kiloKilos.reversed();
    }

    private static String getKiloKilo(int latinPowerKilo, int milliaCount, List<Integer> kilos) {
        var kiloOnes = latinPowerKilo % 10;
        var kiloTens = Mth.floor(latinPowerKilo / 10d) % 10;
        var kiloHundreds = Mth.floor(latinPowerKilo / 100d) % 10;
        var lastKilo = kilos.size() - 1;
        var prefixFragments = new ArrayList<String>();

        if (kiloOnes > 0 && (
                lastKilo == 0 ||
                        milliaCount < lastKilo ||
                        milliaCount == lastKilo && latinPowerKilo > 1
        )) {
            prefixFragments.addFirst(
                    latinPowerKilo < 10 && milliaCount < 1 && lastKilo < 1 ?
                            specials.get(kiloOnes - 1) :
                            units.get(kiloOnes - 1)
            );
        }

        if (kiloTens > 0) prefixFragments.add(tens.get(kiloTens - 1));

        if (kiloHundreds > 0) prefixFragments.addFirst(hundreds.get(kiloHundreds - 1));

        if (latinPowerKilo > 0 && milliaCount > 0) {
            var millia = new StringBuilder();
            millia.append(String.valueOf(BigDecimalNumberFormatter.MILLIA).repeat(Math.max(0, kilos.size() - milliaCount)));
            prefixFragments.addFirst(millia.toString());
        }

        return prefixFragments.stream()
                .filter(s -> s != null && !s.isBlank())
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }

    private static String getKiloPrefix(int latinPower) {
        var kilos = splitKilos(String.valueOf(latinPower))
                .stream()
                .map(Integer::parseInt)
                .toList();

        List<String> kiloPrefixParts = new ArrayList<>();
        for (int i = 0; i < kilos.size(); i++) {
            kiloPrefixParts.add(getKiloKilo(kilos.get(i), i, kilos));
        }

        return formatList(kiloPrefixParts);
    }

    public static <T> String formatList(Collection<? extends T> collection) {
        if (collection.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (T item : collection) {
            builder.append(item.toString());
        }

        return builder.toString();
    }

    @Nullable
    private static String getIllion(int latinPower) {
        var powerKilo = latinPower % 1000;

        if (powerKilo < 5 && powerKilo > 0 && latinPower < 1000) return null;

        if (powerKilo >= 7 && powerKilo <= 10 || Mth.floor(powerKilo / 10d) % 10 == 1) return illion;

        return tillion;
    }

    @Nullable
    private static String getKiloName(int power) {
        List<String> fragments;

        if (power < 2) return power == 1 ? prefix + thousand : null;

        fragments = new ArrayList<>();
        fragments.add(prefix);
        fragments.add(getKiloPrefix(power - 1));
        fragments.add(getIllion(power - 1));
        fragments.add(suffix);

        return fragments.stream()
                .filter(s -> s != null && !s.isBlank())
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }
}
