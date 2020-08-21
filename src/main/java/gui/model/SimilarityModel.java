package gui.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import logic.search.Search;
import org.decimal4j.util.DoubleRounder;

public class SimilarityModel {
    private Search search;
    private final DoubleProperty semanticSimilarityScore = new SimpleDoubleProperty(this, "semantic_similarity");
    private final DoubleProperty phoneticSimilarityScore = new SimpleDoubleProperty(this, "phonetic_similarity");

    public void setSearch(Search semanticSearch) {
        this.search = semanticSearch;
        this.semanticSimilarityScore.set(0);
        this.phoneticSimilarityScore.set(0);
    }

    public void calculateSimilarity(long sSense1, long sSense2, int tSense1, int tSense2, String word1, String word2) {
        double s1 = this.search.calculateSemanticSimilarity(sSense1, tSense1);
        double s2 = this.search.calculateSemanticSimilarity(sSense2, tSense2);
        this.semanticSimilarityScore.set(DoubleRounder.round((s1+s2)/2, 2));

        double p = this.search.calculatePhoneticSimilarity(word1, word2);
        this.phoneticSimilarityScore.set(DoubleRounder.round(p, 2));
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

    public double getPhoneticSimilarityScore() {
        return phoneticSimilarityScore.get();
    }

    public DoubleProperty phoneticSimilarityScoreProperty() {
        return phoneticSimilarityScore;
    }

    public void setPhoneticSimilarityScore(double phoneticSimilarityScore) {
        this.phoneticSimilarityScore.set(phoneticSimilarityScore);
    }
}
