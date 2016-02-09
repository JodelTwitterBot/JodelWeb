package org.cacert.gigi.output.template;

import java.security.SecureRandom;

public class RandomToken {

    private static SecureRandom sr = new SecureRandom();

    public static String generateToken(int length) {
        StringBuffer token = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int rand = sr.nextInt(26 * 2 + 10);
            if (rand < 10) {
                token.append((char) ('0' + rand));
                continue;
            }
            rand -= 10;
            if (rand < 26) {
                token.append((char) ('a' + rand));
                continue;
            }
            rand -= 26;
            token.append((char) ('A' + rand));
        }
        return token.toString();
    }
}
