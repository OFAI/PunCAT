package at.ofai.punderstanding.puncat.logic.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.extjwnl.data.Synset;

import at.ofai.punderstanding.puncat.logic.semnet.GermanetController;
import at.ofai.punderstanding.puncat.logic.semnet.WordnetController;
import at.ofai.punderstanding.puncat.logic.similarity.PhoneticSimilarity;
import at.ofai.punderstanding.puncat.logic.similarity.SemanticSimilarity;
import at.ofai.punderstanding.puncat.logic.util.Consts;


public class Search {
    private final Map<String, String> word2ipaDE = new HashMap<>();
    private final Map<String, String> word2ipaEN = new HashMap<>();
    private GermanetController germaNet;
    private WordnetController wordNet;
    private SemanticSimilarity semSimilarity;
    private PhoneticSimilarity phonSimilarity;

    public Search() {
        var csvFile = getClass().getResourceAsStream(Consts.germanet2ipaPath);
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] pair = line.split(cvsSplitBy);
                this.word2ipaDE.put(pair[0], pair[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        csvFile = getClass().getResourceAsStream(Consts.wordnet2ipaPath);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] pair = line.split(cvsSplitBy);
                this.word2ipaEN.put(pair[0], pair[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }

        try {
            this.wordNet = new WordnetController();
            this.germaNet = new GermanetController();
            this.semSimilarity = new SemanticSimilarity(this.germaNet.getObject());
            this.phonSimilarity = new PhoneticSimilarity(word2ipaDE);
        } catch (Exception e) {
            e.printStackTrace();  // TODO: better handling of germanet/wordnet exceptions
        }
    }

    public double calculateSemanticSimilarity(long sSense, int tSense) {
        var soureAsTargetSynset = this.germaNet.equivalentByWordnetOffset(sSense);
        var targetSynset = this.germaNet.getSynsetById(tSense);
        if (soureAsTargetSynset != null && targetSynset != null) {
            return this.semSimilarity.calculateSemanticSimilarity(soureAsTargetSynset, targetSynset);
        } else {
            // TODO: this can happen if a source is not mapped to GermaNet in the interlingual index
            if (soureAsTargetSynset == null) {
                throw new RuntimeException("Source null");
            } else {
                throw new RuntimeException("Target null");
            }
        }
    }

    public Long getGermanetSynsetCumulativeFrequency(de.tuebingen.uni.sfs.germanet.api.Synset synset) {
        return this.germaNet.getSynsetCumulativeFrequency(synset);
    }

    public String getGermanetMostFrequentOrthForm(de.tuebingen.uni.sfs.germanet.api.Synset synset) {
        return this.germaNet.getMostFrequentOrthForm(synset);
    }

    public double calculatePhoneticSimilarity(String word1, String word2) {
        return this.phonSimilarity.calculatePhoneticSimilarity(word1, word2);
    }

    public String getIpaTranscription(String word, String lang) {
        String result = null;
        if (lang.equals("en")) {
            result = this.word2ipaEN.get(word.toLowerCase());
        } else if (lang.equals("de")) {
            result = this.word2ipaDE.get(word.toLowerCase());
        }
        return result != null ? result : "";
    }

    public List<Synset> getSourceSenses(String word) {
        return wordNet.getSynsets(word);
    }

    public List<de.tuebingen.uni.sfs.germanet.api.Synset> getTargetSenses(String word) {
        return germaNet.getSynsets(word);
    }

    public de.tuebingen.uni.sfs.germanet.api.Synset mapToGermanet(long offset) {
        return germaNet.equivalentByWordnetOffset(offset);
    }

    public List<de.tuebingen.uni.sfs.germanet.api.Synset> getTargetHypernyms(int synsetId) {
        return this.germaNet.getHypernyms(synsetId);
    }

    public List<de.tuebingen.uni.sfs.germanet.api.Synset> getTargetHyponyms(int synsetId) {
        return this.germaNet.getHyponyms(synsetId);
    }

    public de.tuebingen.uni.sfs.germanet.api.Synset getTargetSynsetById(int id) {
        return this.germaNet.getSynsetById(id);
    }

    public String getGermanetOrthFormByLexUnitId(int lexUnitId) {
        return this.germaNet.getLexUnitById(lexUnitId);
    }
}
