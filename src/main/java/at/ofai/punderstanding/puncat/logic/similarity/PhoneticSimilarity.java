package at.ofai.punderstanding.puncat.logic.similarity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import at.ofai.punderstanding.puncat.logic.ResourcePaths;
import at.ofai.punderstanding.puncat.logic.aline.ALINE;


public class PhoneticSimilarity {
    private final Map<String, String> germanet2ipa = new HashMap<>();

    public PhoneticSimilarity() {
        InputStream csvFile ;
        try {
            csvFile = new FileInputStream(ResourcePaths.germanet2ipaPath.toFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] pair = line.split(csvSplitBy);
                this.germanet2ipa.put(pair[0], pair[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public double calculatePhoneticSimilarity(String word1, String word2, algs selectedPhonAlg) {
        String ipa1 = this.germanet2ipa.get(word1.toLowerCase());
        String ipa2 = this.germanet2ipa.get(word2.toLowerCase());
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
            case ALINE -> result = ALINE.similarity(ipa1, ipa2);
            default -> throw new RuntimeException("Unknown phonetic similarity algorithm");
        }
        return result;
    }

    public enum algs {
        ALINE,
    }
}
