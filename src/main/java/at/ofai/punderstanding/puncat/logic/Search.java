package at.ofai.punderstanding.puncat.logic;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;

import at.ofai.punderstanding.puncat.logic.semnet.GermanetController;
import at.ofai.punderstanding.puncat.logic.semnet.WordnetController;
import at.ofai.punderstanding.puncat.logic.similarity.PhoneticSimilarity;
import at.ofai.punderstanding.puncat.logic.similarity.SemanticSimilarity;


public class Search {
    private final Map<String, String> germanet2ipa = new HashMap<>();
    private final Map<String, String> wordnet2ipa = new HashMap<>();
    private final GermanetController germaNet;
    private final WordnetController wordNet;
    private final SemanticSimilarity semSimilarity;
    private final PhoneticSimilarity phonSimilarity;

    public Search() {
        //var csvFile = getClass().getResourceAsStream(ResourcePaths.germanet2ipaPath);
        InputStream csvFile;
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

        try {
            csvFile = new FileInputStream(ResourcePaths.wordnet2ipaPath.toFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] pair = line.split(csvSplitBy);
                this.wordnet2ipa.put(pair[0], pair[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            this.wordNet = new WordnetController();
            this.germaNet = new GermanetController();
            this.semSimilarity = new SemanticSimilarity(this.germaNet.getSemanticUtils());
            this.phonSimilarity = new PhoneticSimilarity();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public double calculateSemanticSimilarity(POS sSensePOS, long sSenseOffset,
                                              int tSenseId,
                                              SemanticSimilarity.algs selectedSemanticAlg) {
        var soureAsTargetSynset = this.germaNet.equivalentByWordnetOffset(sSensePOS, sSenseOffset);
        var targetSynset = this.germaNet.getSynsetById(tSenseId);
        if (soureAsTargetSynset != null && targetSynset != null) {
            return this.semSimilarity.calculateSemanticSimilarity(soureAsTargetSynset, targetSynset, selectedSemanticAlg);
        } else {
            if (soureAsTargetSynset == null) {
                System.err.printf("WordNet Synset %d (%s) not mapped to GermaNet. Returning score 0.%n",
                        sSenseOffset, sSensePOS);
                return 0;
            } else {
                throw new RuntimeException("targetSynset null");
            }
        }
    }

    public double calculatePhoneticSimilarity(String word1, String word2, PhoneticSimilarity.algs selectedPhonAlg) {
        return this.phonSimilarity.calculatePhoneticSimilarity(word1, word2, selectedPhonAlg);
    }

    public Long germanetGetSynsetCumulativeFrequency(de.tuebingen.uni.sfs.germanet.api.Synset synset) {
        return this.germaNet.getSynsetCumulativeFrequency(synset);
    }

    public String germanetGetMostFrequentOrthForm(de.tuebingen.uni.sfs.germanet.api.Synset synset) {
        return this.germaNet.getMostFrequentOrthForm(synset);
    }

    public String getIpaTranscriptionEnglish(String word, Synset synset) {
        String baseForm = this.wordNet.getBaseFormOrNull(word.toLowerCase(), synset);
        String result;
        result = this.wordnet2ipa.get(Objects.requireNonNullElse(baseForm, word).toLowerCase());
        if (result == null) {
            System.err.println("No IPA transcription found for " + word);
            return "";
        } else {
            return result;
        }
    }

    public String getIpaTranscriptionGerman(String word) {
        String result = this.germanet2ipa.get(word.toLowerCase());
        return result == null ? "" : result;
    }

    public List<Synset> wordnetGetSenses(String word) {
        return wordNet.getSynsets(word.toLowerCase());
    }

    public List<de.tuebingen.uni.sfs.germanet.api.Synset> germanetGetSenses(String word) {
        return germaNet.getSynsets(word);
    }

    public de.tuebingen.uni.sfs.germanet.api.Synset wordnetSynsetToGermanetOrNull(POS pos, long offset) {
        return germaNet.equivalentByWordnetOffset(pos, offset);
    }

    public List<de.tuebingen.uni.sfs.germanet.api.Synset> germanetGetHypernymsOrderedByFrequency(int synsetId) {
        var hypernyms = this.germaNet.getHypernyms(synsetId);
        hypernyms.sort(Comparator.comparing(this::germanetGetSynsetCumulativeFrequency));
        Collections.reverse(hypernyms);
        return hypernyms;
    }

    public List<de.tuebingen.uni.sfs.germanet.api.Synset> germanetGetHyponymsOrderedByFrequency(int synsetId) {
        var hyponyms = this.germaNet.getHyponyms(synsetId);
        hyponyms.sort(Comparator.comparing(this::germanetGetSynsetCumulativeFrequency));
        Collections.reverse(hyponyms);
        return hyponyms;
    }

    public de.tuebingen.uni.sfs.germanet.api.Synset germanetGetSynsetById(int id) {
        return this.germaNet.getSynsetById(id);
    }

    public String germanetGetOrthFormByLexUnitId(int lexUnitId) {
        return this.germaNet.getLexUnitById(lexUnitId);
    }

    public Word wordnetGetWordBySenseKey(String senseKey) {
        return this.wordNet.getWordBySenseKey(senseKey);
    }
}
