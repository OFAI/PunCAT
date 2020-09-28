package at.ofai.punderstanding.puncat.logic.similarity;

import de.tuebingen.uni.sfs.germanet.api.SemRelMeasure;
import de.tuebingen.uni.sfs.germanet.api.SemanticUtils;
import de.tuebingen.uni.sfs.germanet.api.Synset;


public class SemanticSimilarity {
    private final SemanticUtils semanticUtils;

    public SemanticSimilarity(SemanticUtils su) {
        this.semanticUtils = su;
    }

    public double calculateSemanticSimilarity(Synset sense1, Synset sense2, algs selectedSemanticAlg) {
        var sense1WordCat = sense1.getLexUnits().get(0).getWordCategory();
        var sense2WordCat = sense2.getLexUnits().get(0).getWordCategory();
        var differentPOS = sense1WordCat != sense2WordCat;

        Double result;
        switch (selectedSemanticAlg) {
            case JiangAndConrath -> {
                result = this.semanticUtils.getSimilarity(
                        SemRelMeasure.JiangAndConrath, sense1, sense2, 1);
                if (differentPOS) printDifferentPOSwarning();
            }
            case WuAndPalmer -> {
                result = this.semanticUtils.getSimilarity(
                        SemRelMeasure.WuAndPalmer, sense1, sense2, 1);
                if (differentPOS) printDifferentPOSwarning();
            }
            case LeacockAndChodorow -> {
                result = this.semanticUtils.getSimilarity(
                        SemRelMeasure.LeacockAndChodorow, sense1, sense2, 1);
                if (differentPOS) printDifferentPOSwarning();
            }
            case Lin -> {
                result = this.semanticUtils.getSimilarity(
                        SemRelMeasure.Lin, sense1, sense2, 1);
                if (differentPOS) printDifferentPOSwarning();
            }
            case Resnik -> {
                result = this.semanticUtils.getSimilarity(
                        SemRelMeasure.Resnik, sense1, sense2, 1);
                if (differentPOS) printDifferentPOSwarning();
            }
            case SimplePath -> {
                result = this.semanticUtils.getSimilarity(
                        SemRelMeasure.SimplePath, sense1, sense2, 1);
                if (differentPOS) printDifferentPOSwarning();
            }
            default -> throw new RuntimeException("Unknown semantic similarity algorithm");
        }
        return result == null ? 0d : result;
    }

    private void printDifferentPOSwarning() {
        System.err.println("Different parts of speech for semantic similarity calculation! Returning score 0.");
    }

    public enum algs {
        JiangAndConrath,
        WuAndPalmer,
        LeacockAndChodorow,
        Lin,
        Resnik,
        SimplePath
    }
}
