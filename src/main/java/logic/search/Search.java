package logic.search;

import de.tuebingen.uni.sfs.germanet.api.OrthFormVariant;
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
            // TODO: either delete semnet controllers or pass those
            this.similarity = new SemanticSimilarity(this.germaNet.getObject());
        } catch (Exception e) {
            e.printStackTrace();  // TODO: better handling of germanet/wordnet exceptions
        }
    }

    public double calculateSemanticSimilarity(long sSense, int tSense) {
        var soureAsTargetSynset = this.germaNet.equivalentByWordnetOffset(sSense);
        var targetSynset = this.germaNet.getSynsetById(tSense);
        if (soureAsTargetSynset != null && targetSynset != null) {
            System.out.printf("%s --  %s%n",
                    soureAsTargetSynset.getOrthForms(OrthFormVariant.orthForm).get(0),
                    targetSynset.getOrthForms(OrthFormVariant.orthForm).get(0));
            return this.similarity.calculateSemanticSimilarity(soureAsTargetSynset, targetSynset);
        } else {
            // TODO: implement this
            return 0;
        }

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
}
