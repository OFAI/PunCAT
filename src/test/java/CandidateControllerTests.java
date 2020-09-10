import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import at.ofai.punderstanding.puncat.gui.controller.CandidateController;
import at.ofai.punderstanding.puncat.gui.logging.LoggerValues;


public class CandidateControllerTests {
    private static CandidateController candidateController;
    private static StringProperty punProperty;
    private static StringProperty targetProperty;
    private static StringProperty semanticSimilarityScoreProperty;
    private static StringProperty phoneticSimilarityScoreProperty;

    @BeforeAll
    static void configureEnvironment() {
        SharedConfig.configureEnvironment();
    }

    @BeforeEach
    void init() {
        candidateController = new CandidateController();
        punProperty = new SimpleStringProperty();
        targetProperty = new SimpleStringProperty();
        semanticSimilarityScoreProperty = new SimpleStringProperty();
        phoneticSimilarityScoreProperty = new SimpleStringProperty();
        candidateController.setReferences(
                punProperty,
                targetProperty,
                semanticSimilarityScoreProperty,
                phoneticSimilarityScoreProperty
        );
    }

    @Test
    void testCandidatesSavedCorretly() {
        punProperty.setValue("this is the pun");
        targetProperty.setValue("this is the target");
        semanticSimilarityScoreProperty.setValue("0.9");
        phoneticSimilarityScoreProperty.setValue("0.5");
        candidateController.newCandidate();

        String prevPun = punProperty.getValue();
        String prevTarget = targetProperty.getValue();
        String prevSem = semanticSimilarityScoreProperty.getValue();
        String prevPhon = phoneticSimilarityScoreProperty.getValue();

        punProperty.setValue("another pun");
        targetProperty.setValue("another target");
        phoneticSimilarityScoreProperty.setValue("0.1");
        semanticSimilarityScoreProperty.setValue("0.3");
        candidateController.newCandidate();

        var candidates = candidateController.candidatesToJsonArray();
        assertEquals(
                prevPun,
                candidates.getJSONObject(0).get(LoggerValues.CANDIDATE_PUN)
        );
        assertEquals(
                prevTarget,
                candidates.getJSONObject(0).get(LoggerValues.CANDIDATE_TARGET)
        );
        assertEquals(
                Double.parseDouble(prevSem),
                candidates.getJSONObject(0).get(LoggerValues.CANDIDATE_SEM)
        );
        assertEquals(
                Double.parseDouble(prevPhon),
                candidates.getJSONObject(0).get(LoggerValues.CANDIDATE_PHON)
        );

        assertEquals(
                punProperty.getValue(),
                candidates.getJSONObject(1).get(LoggerValues.CANDIDATE_PUN)
        );
        assertEquals(
                targetProperty.getValue(),
                candidates.getJSONObject(1).get(LoggerValues.CANDIDATE_TARGET)
        );
        assertEquals(
                Double.parseDouble(semanticSimilarityScoreProperty.getValue()),
                candidates.getJSONObject(1).get(LoggerValues.CANDIDATE_SEM)
        );
        assertEquals(
                Double.parseDouble(phoneticSimilarityScoreProperty.getValue()),
                candidates.getJSONObject(1).get(LoggerValues.CANDIDATE_PHON)
        );
    }
}
