package at.ofai.punderstanding.puncat.logic.similarity;

import java.util.Map;

import at.ofai.punderstanding.puncat.logic.aline.ALINE;


public class PhoneticSimilarity {
    private final Map<String, String> word2ipa;

    public PhoneticSimilarity(Map<String, String> word2ipa) {
        this.word2ipa = word2ipa;
    }

    public double calculatePhoneticSimilarity(String word1, String word2, algs selectedPhonAlg) {
        String ipa1 = this.word2ipa.get(word1.toLowerCase());
        String ipa2 = this.word2ipa.get(word2.toLowerCase());
        if (ipa1 == null) {
            System.err.println(word1 + " ipa transcription null");
            ipa1 = "";
        } else if (ipa2 == null) {
            System.err.println(word2 + " ipa transcription null");
            ipa2 = "";
        }

        double result;
        //noinspection SwitchStatementWithTooFewBranches
        switch (selectedPhonAlg) {
            case ALINE -> {
                // remove character not handled by the ALINE class
                StringBuilder sb;
                sb = new StringBuilder(ipa1);
                while (sb.indexOf("ː") != -1) {
                    sb.deleteCharAt(sb.indexOf("ː"));
                }
                ipa1 = sb.toString();

                sb = new StringBuilder(ipa2);
                while (sb.indexOf("ː") != -1) {
                    sb.deleteCharAt(sb.indexOf("ː"));
                }
                ipa2 = sb.toString();

                result = ALINE.similarity(ipa1, ipa2);
            }
            default -> throw new RuntimeException("Unknown phonetic similarity algorithm");
        }
        return result;
    }

    public enum algs {
        ALINE,
    }
}
