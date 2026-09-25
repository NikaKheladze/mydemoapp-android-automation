package com.mydemoapp.automation.utils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Converts UI text such as {@code "$ 29.99"} or {@code "2 Items"} into typed values. */
public final class TextParser {

    private static final Pattern PRICE = Pattern.compile("\\d+(?:\\.\\d+)?");
    private static final Pattern INTEGER = Pattern.compile("\\d+");

    private TextParser() {
    }

    public static BigDecimal parsePrice(String text) {
        return new BigDecimal(firstMatch(PRICE, text, "price"));
    }

    public static int parseFirstInteger(String text) {
        return Integer.parseInt(firstMatch(INTEGER, text, "number"));
    }

    private static String firstMatch(Pattern pattern, String text, String what) {
        Matcher matcher = pattern.matcher(text == null ? "" : text.replace(",", ""));
        if (!matcher.find()) {
            throw new IllegalArgumentException("No " + what + " found in text '" + text + "'");
        }
        return matcher.group();
    }
}
