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
