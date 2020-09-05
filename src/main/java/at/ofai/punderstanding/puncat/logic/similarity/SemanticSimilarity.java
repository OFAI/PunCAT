package at.ofai.punderstanding.puncat.logic.similarity;

import java.io.IOException;

import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import de.tuebingen.uni.sfs.germanet.api.SemRelMeasure;
import de.tuebingen.uni.sfs.germanet.api.SemanticUtils;
import de.tuebingen.uni.sfs.germanet.api.Synset;


public class SemanticSimilarity {
    private final SemanticUtils semanticUtils;

    public SemanticSimilarity(GermaNet gn) throws IOException {
        this.semanticUtils = gn.getSemanticUtils();
    }

    public double calculateSemanticSimilarity(Synset sense1, Synset sense2) {
        return this.semanticUtils.getSimilarity(SemRelMeasure.JiangAndConrath, sense1, sense2, 1);
    }
}
