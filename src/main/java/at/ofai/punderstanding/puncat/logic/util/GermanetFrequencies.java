package at.ofai.punderstanding.puncat.logic.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import de.tuebingen.uni.sfs.germanet.api.Synset;


public class GermanetFrequencies {
    private final Map<String, Map<String, Long>> wordCatMap;

    public GermanetFrequencies(Map<String, Long> nomenFreq, Map<String, Long> verbenFreq, Map<String, Long> adjFreq) {
        this.wordCatMap = new HashMap<>();
        this.wordCatMap.put("nomen", nomenFreq);
        this.wordCatMap.put("verben", verbenFreq);
        this.wordCatMap.put("adj", adjFreq);
    }

    public static GermanetFrequencies loadFrequencies(String nomenPath, String verbenPath, String adjPath) {
        try {
            Map<String, Long> nomenFreq = loadFile(new File(nomenPath));
            Map<String, Long> verbenFreq = loadFile(new File(verbenPath));
            Map<String, Long> adjFreq = loadFile(new File(adjPath));
            return new GermanetFrequencies(nomenFreq, verbenFreq, adjFreq);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private static Map<String, Long> loadFile(File path) throws IOException {
        Map<String, Long> freqMap = new HashMap<>();
        Files.lines(Paths.get(path.getPath()))
                .map(line -> line.split("\\s+"))
                .forEach(splits -> {
                    String word = splits[0];
                    Long freq = Long.valueOf(splits[1]);
                    freqMap.put(word, freq);
                });
        return freqMap;
    }

    public Long getSynsetCumulativeFrequency(Synset synset) {
        long frequency = 0L;
        String wordCat = synset.getWordCategory().toString();
        for (String form : synset.getAllOrthForms()) {
            Long f = this.wordCatMap.get(wordCat).get(form);
            if (f != null) {
                frequency += f;
            }
        }
        return frequency;
    }

    public String getMostFrequentOrthForm(Synset synset) {
        String mostFrequent = null;
        long maxFreq = -1L;
        String wordCat = synset.getWordCategory().toString();
        for (String form : synset.getAllOrthForms()) {
            Long f = this.wordCatMap.get(wordCat).get(form);
            if (f == null) {
                f = 0L;  // TODO: do something about the missing entries
            }
            if (f > maxFreq) {
                maxFreq = f;
                mostFrequent = form;
            }
        }
        if (mostFrequent == null) {
            throw new RuntimeException();
        }
        return mostFrequent;
    }
}
