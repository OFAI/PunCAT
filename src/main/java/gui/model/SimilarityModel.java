package gui.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import logic.search.Search;
import org.decimal4j.util.DoubleRounder;

public class SimilarityModel {
    private Search search;
    private final DoubleProperty semanticSimilarityScore = new SimpleDoubleProperty(this, "semantic_similarity");


    public void setSearch(Search semanticSearch) {
        this.search = semanticSearch;
        this.semanticSimilarityScore.set(0);
    }

    public void calculateSimilarity(int senseId1, int senseId2) {
        double score = this.search.calculateSemanticSimilarity(senseId1, senseId2);
        this.semanticSimilarityScore.set(DoubleRounder.round(score, 2));
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
