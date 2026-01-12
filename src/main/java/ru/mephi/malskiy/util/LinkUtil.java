package ru.mephi.malskiy.util;

import java.math.BigInteger;

public class LinkUtil {
    // строка из 62 символов которые используются для кодирования числа в base62
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    // основание системы счисления base62
    private static final BigInteger BASE = BigInteger.valueOf(ALPHABET.length());

    public static String toBase62 (long value) {
        if (value == 0) return "a"; // если пришел 0 возвращаем "a"

        BigInteger copyValue = BigInteger.valueOf(value).abs(); // создаем копию входного значения и берем его по модулю

        StringBuilder code = new StringBuilder();

        while (copyValue.signum() > 0) { // пока copyValue больше нуля
            BigInteger[] divRem = copyValue.divideAndRemainder(BASE); // получаем частное и остаток от деления
            int rem = divRem[1].intValue(); // переводим остаток от деления в int
            code.append(ALPHABET.charAt(rem)); // берем символ по индексу rem
            copyValue = divRem[0]; // присваеваем copyValue значение частного
        }

        return code.reverse().toString(); // делаем реверc так ка собирали в обратном порядке, переводим в строку и возвращаем
    }
}
