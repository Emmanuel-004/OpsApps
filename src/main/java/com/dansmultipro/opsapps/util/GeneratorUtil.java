package com.dansmultipro.opsapps.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class GeneratorUtil {

    public String generateCode(Integer length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            result.append(chars.charAt(index));
        }
        return result.toString();
    }
}
