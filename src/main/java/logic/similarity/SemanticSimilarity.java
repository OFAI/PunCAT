package logic.similarity;

import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import de.tuebingen.uni.sfs.germanet.api.SemRelMeasure;
import de.tuebingen.uni.sfs.germanet.api.SemanticUtils;
import de.tuebingen.uni.sfs.germanet.api.Synset;
import net.sf.extjwnl.dictionary.Dictionary;

import java.io.IOException;

public class SemanticSimilarity {

    private final GermaNet germaNet;
    private final SemanticUtils semanticUtils;

    public SemanticSimilarity(GermaNet gn) throws IOException {
        this.germaNet = gn;
        this.semanticUtils = gn.getSemanticUtils();
    }

    public double calculateSemanticSimilarity(Synset sense1, Synset sense2) {
        return this.semanticUtils.getSimilarity(SemRelMeasure.JiangAndConrath, sense1, sense2, 1);
    }
}
