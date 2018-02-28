package com.ioanap.classbook.utils;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

/**
 * Created by ioana on 2/24/2018.
 */

public class UniqueStringGenerator {

    private static final String alphanum = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "0123456789" +
            "@#&*-?+";
    private static final int length = 8;
    private static Random random;
    private static char[] symbols;
    private static char[] buf;

    public UniqueStringGenerator() {
    }

    /**
     * Generate a random string.
     */
    public static String nextString() {
        random = Objects.requireNonNull(new SecureRandom());
        symbols = alphanum.toCharArray();
        buf = new char[length];

        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }


}
