package at.ofai.punderstanding.puncat.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;

import at.ofai.punderstanding.puncat.logic.semnet.GermanetController;
import at.ofai.punderstanding.puncat.logic.semnet.WordnetController;
import at.ofai.punderstanding.puncat.logic.similarity.PhoneticSimilarity;
import at.ofai.punderstanding.puncat.logic.similarity.SemanticSimilarity;


public class Search {
    private final Map<String, String> word2ipaGER = new HashMap<>();
    private final Map<String, String> word2ipaENG = new HashMap<>();
    private GermanetController germaNet;
    private WordnetController wordNet;
    private SemanticSimilarity semSimilarity;
    private PhoneticSimilarity phonSimilarity;

    public Search() {
        var csvFile = getClass().getResourceAsStream(ResourcePaths.germanet2ipaPath);
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] pair = line.split(cvsSplitBy);
                this.word2ipaGER.put(pair[0], pair[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        csvFile = getClass().getResourceAsStream(ResourcePaths.wordnet2ipaPath);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] pair = line.split(cvsSplitBy);
                this.word2ipaENG.put(pair[0], pair[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.wordNet = new WordnetController();
            this.germaNet = new GermanetController();
            this.semSimilarity = new SemanticSimilarity(this.germaNet.getObject());
            this.phonSimilarity = new PhoneticSimilarity(word2ipaGER);
        } catch (Exception e) {
            e.printStackTrace();  // TODO: better handling of germanet/wordnet exceptions
        }
    }

    public double calculateSemanticSimilarity(long sSense, int tSense, SemanticSimilarity.algs selectedSemanticAlg) {
        var soureAsTargetSynset = this.germaNet.equivalentByWordnetOffset(sSense);
        var targetSynset = this.germaNet.getSynsetById(tSense);
        if (soureAsTargetSynset != null && targetSynset != null) {
            return this.semSimilarity.calculateSemanticSimilarity(soureAsTargetSynset, targetSynset, selectedSemanticAlg);
        } else {
            if (soureAsTargetSynset == null) {
                System.err.println("WordNet synset " + sSense + " not mapped to GermaNet. Returning score 0.");
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
        String baseForm = this.wordNet.getBaseForm(word, synset);
        String result = this.word2ipaENG.get(baseForm.toLowerCase());
        if (result == null) {
            System.err.println("No IPA transcription found for " + word);
            return "";
        } else {
            return result;
        }
    }

    public String getIpaTranscriptionGerman(String word) {
        String result = this.word2ipaGER.get(word.toLowerCase());
        return result == null ? "" : result;
    }

    public List<Synset> wordnetGetSenses(String word) {
        return wordNet.getSynsets(word.toLowerCase());
    }

    public List<de.tuebingen.uni.sfs.germanet.api.Synset> germanetGetSenses(String word) {
        return germaNet.getSynsets(word);
    }

    public de.tuebingen.uni.sfs.germanet.api.Synset mapWordnetOffsetToGermanet(long offset) {
        return germaNet.equivalentByWordnetOffset(offset);
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
