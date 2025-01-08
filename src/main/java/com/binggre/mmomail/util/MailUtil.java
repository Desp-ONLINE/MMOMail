package com.binggre.mmomail.util;

import java.util.ArrayList;
import java.util.List;

public class MailUtil {

    public static List<String> splitLetter(String letter, int chunkSize) {
        List<String> result = new ArrayList<>();

        if (letter == null || letter.isEmpty()) {
            return result;
        }
        for (int i = 0; i < letter.length(); i += chunkSize) {
            int end = Math.min(i + chunkSize, letter.length());
            result.add("§f" + letter.substring(i, end));
        }

        return result;
    }

}
