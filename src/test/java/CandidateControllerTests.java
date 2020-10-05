/*
  Copyright 2020 Máté Lajkó

  This file is part of PunCAT.

  PunCAT is free software: you can redistribute it and/or modify it
  under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PunCAT is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with PunCAT.  If not, see <https://www.gnu.org/licenses/>.
 */

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import at.ofai.punderstanding.puncat.controller.CandidateController;
import at.ofai.punderstanding.puncat.logging.LoggerValues;
import at.ofai.punderstanding.puncat.logic.similarity.PhoneticSimilarity;
import at.ofai.punderstanding.puncat.logic.similarity.SemanticSimilarity;


public class CandidateControllerTests {
    private static CandidateController candidateController;
    private static StringProperty punProperty;
    private static StringProperty targetProperty;
    private static StringProperty semanticSimilarityScoreProperty;
    private static StringProperty phoneticSimilarityScoreProperty;
    private static ObjectProperty<SemanticSimilarity.algs> semanticAlgorithmProperty;
    private static ObjectProperty<PhoneticSimilarity.algs> phoneticAlgorithmProperty;

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
        semanticAlgorithmProperty = new SimpleObjectProperty<>();
        phoneticAlgorithmProperty = new SimpleObjectProperty<>();
        candidateController.punCandidateProperty().bind(punProperty);
        candidateController.targetCandidateProperty().bind(targetProperty);
        candidateController.semanticScoreProperty().bind(semanticSimilarityScoreProperty);
        candidateController.phoneticScoreProperty().bind(phoneticSimilarityScoreProperty);
        candidateController.selectedSemAlgProperty().bind(semanticAlgorithmProperty);
        candidateController.selectedPhonAlgProperty().bind(phoneticAlgorithmProperty);
    }

    @Test
    void testCandidatesSavedCorretly() {
        punProperty.setValue("this is the pun");
        targetProperty.setValue("this is the target");
        semanticSimilarityScoreProperty.setValue("0.9");
        phoneticSimilarityScoreProperty.setValue("0.5");
        semanticAlgorithmProperty.set(SemanticSimilarity.algs.JiangAndConrath);
        phoneticAlgorithmProperty.set(PhoneticSimilarity.algs.ALINE);
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
        assertEquals(
                "Jiang and Conrath",
                candidates.getJSONObject(1).get(LoggerValues.CANDIDATE_SEM_ALG)
        );
        assertEquals(
                "ALINE",
                candidates.getJSONObject(1).get(LoggerValues.CANDIDATE_PHON_ALG)
        );
    }

}
