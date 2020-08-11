package gui.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import logic.search.Search;
import org.decimal4j.util.DoubleRounder;

public class SimilarityModel {
    private Search search;
    private final DoubleProperty semanticSimilarityScore = new SimpleDoubleProperty(this, "semantic_similarity");
    private double pair1Similarity;
    private double pair2Similarity;


    public void setSearch(Search semanticSearch) {
        this.search = semanticSearch;
        this.semanticSimilarityScore.set(0);
    }

    public void calculateSimilarity(long sSense1, long sSense2, int tSense1, int tSense2) {
        double s1 = this.search.calculateSemanticSimilarity(sSense1, tSense1);
        System.out.println(s1);
        double s2 = this.search.calculateSemanticSimilarity(sSense2, tSense2);
        System.out.println(s2);
        System.out.println();
        this.semanticSimilarityScore.set(DoubleRounder.round((s1+s2)/2, 2));
    }

    public double getSemanticSimilarityScore() {
        return semanticSimilarityScore.get();
    }

    public DoubleProperty semanticSimilarityScoreProperty() {
        return semanticSimilarityScore;
    }

    public void setSemanticSimilarityScore(double semanticSimilarityScore) {
        this.semanticSimilarityScore.set(semanticSimilarityScore);
    }
}
