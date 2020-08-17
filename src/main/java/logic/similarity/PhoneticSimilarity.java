package logic.similarity;

import logic.aline.ALINE;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PhoneticSimilarity {
    private final Map<String, String> word2ipa;

    public PhoneticSimilarity(Map<String, String> word2ipa) {
        this.word2ipa = word2ipa;
    }

    public double calculatePhoneticSimilarity(String word1, String word2) {
        String ipa1 = this.word2ipa.get(word1.toLowerCase());
        String ipa2 = this.word2ipa.get(word2.toLowerCase());

        // remove the character not handled by the ALINE class
        var sb = new StringBuilder(ipa1);
        while (sb.indexOf("ː") != -1) {
            sb.deleteCharAt(sb.indexOf("ː"));
        }
        ipa1 = sb.toString();

        sb = new StringBuilder(ipa2);
        while (sb.indexOf("ː") != -1) {
            sb.deleteCharAt(sb.indexOf("ː"));
        }
        ipa2 = sb.toString();

        return ALINE.similarity(ipa1, ipa2);
    }
}
