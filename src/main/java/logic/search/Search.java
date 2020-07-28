package logic.search;

import logic.semnet.GermanetController;
import logic.semnet.WordnetController;
import logic.similarity.SemanticSimilarity;
import net.sf.extjwnl.data.Synset;

import java.util.List;

public class Search {
    private GermanetController germaNet;
    private WordnetController wordNet;
    private SemanticSimilarity similarity;

    public Search() {
        try {
            this.wordNet = new WordnetController();
            this.germaNet = new GermanetController();
            this.similarity = new SemanticSimilarity(this.germaNet.getObject());
        } catch (Exception e) {
            e.printStackTrace();  // TODO: better handling of germanet/wordnet exceptions
        }
    }

    public double calculateSemanticSimilarity(int senseId1, int senseId2) {
        return this.similarity.calculateSemanticSimilarity(senseId1, senseId2);
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
}
