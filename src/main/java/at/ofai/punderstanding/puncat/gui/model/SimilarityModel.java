package at.ofai.punderstanding.puncat.gui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.decimal4j.util.DoubleRounder;

import at.ofai.punderstanding.puncat.logic.search.Search;


public class SimilarityModel {
    private final StringProperty semanticSimilarityScore = new SimpleStringProperty();
    private final StringProperty phoneticSimilarityScore = new SimpleStringProperty();
    private Search search;

    public void setSearch(Search semanticSearch) {
        this.search = semanticSearch;
        this.semanticSimilarityScore.set("");
        this.phoneticSimilarityScore.set("");
    }

    public void calculateSemanticSimilarity(long sSense1, long sSense2, int tSense1, int tSense2) {
        double s1 = this.search.calculateSemanticSimilarity(sSense1, tSense1);
        double s2 = this.search.calculateSemanticSimilarity(sSense2, tSense2);
        double avg = DoubleRounder.round((s1 + s2) / 2, 2);
        this.semanticSimilarityScore.set(String.valueOf(avg));
    }

    public void calculatePhoneticSimilarity(String word1, String word2) {
        double p = this.search.calculatePhoneticSimilarity(word1, word2);
        p = DoubleRounder.round(p, 2);
        this.phoneticSimilarityScore.set(String.valueOf(p));
    }

    public void clearSimilarity() {
        this.phoneticSimilarityScore.setValue("");
        this.semanticSimilarityScore.setValue("");
    }

    public String getSemanticSimilarityScore() {
        return semanticSimilarityScore.get();
    }

    public void setSemanticSimilarityScore(String semanticSimilarityScore) {
        this.semanticSimilarityScore.set(semanticSimilarityScore);
    }

    public StringProperty semanticSimilarityScoreProperty() {
        return semanticSimilarityScore;
    }

    public String getPhoneticSimilarityScore() {
        return phoneticSimilarityScore.get();
    }

    public void setPhoneticSimilarityScore(String phoneticSimilarityScore) {
        this.phoneticSimilarityScore.set(phoneticSimilarityScore);
    }

    public StringProperty phoneticSimilarityScoreProperty() {
        return phoneticSimilarityScore;
    }
}
