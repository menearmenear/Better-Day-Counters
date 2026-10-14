package net.menear.betterdaycounters;

public final class RomanNumerals {
    private RomanNumerals() {
    }

    public static String toRoman(long value) {
        if (value <= 0) {
            return "0";
        }
        if (value > 3999) {
            return Long.toString(value);
        }
        int[] vals = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] syms = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder sb = new StringBuilder();
        long n = value;
        for (int i = 0; i < vals.length && n > 0; i++) {
            while (n >= vals[i]) {
                n -= vals[i];
                sb.append(syms[i]);
            }
        }
        return sb.toString();
    }
}