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

package at.ofai.punderstanding.puncat.logic.aline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class ALINEconsts {
    public static final List<String> consonants = new ArrayList<>();
    public static final Map<String, Double> similarityMatrix = new HashMap<>();
    public static final HashSet<String> R_v = new HashSet<>();
    public static final HashSet<String> R_c = new HashSet<>();
    public static final Map<String, Integer> salience = new HashMap<>();
    public static final Map<String, Map<String, String>> featureMatrix = new HashMap<>();

    static {
        consonants.add("B");
        consonants.add("N");
        consonants.add("R");
        consonants.add("b");
        consonants.add("c");
        consonants.add("d");
        consonants.add("f");
        consonants.add("g");
        consonants.add("h");
        consonants.add("j");
        consonants.add("k");
        consonants.add("l");
        consonants.add("m");
        consonants.add("n");
        consonants.add("p");
        consonants.add("q");
        consonants.add("r");
        consonants.add("s");
        consonants.add("t");
        consonants.add("v");
        consonants.add("x");
        consonants.add("z");
        consonants.add("ç");
        consonants.add("ð");
        consonants.add("ħ");
        consonants.add("ŋ");
        consonants.add("ɖ");
        consonants.add("ɟ");
        consonants.add("ɢ");
        consonants.add("ɣ");
        consonants.add("ɦ");
        consonants.add("ɬ");
        consonants.add("ɮ");
        consonants.add("ɰ");
        consonants.add("ɱ");
        consonants.add("ɲ");
        consonants.add("ɳ");
        consonants.add("ɴ");
        consonants.add("ɸ");
        consonants.add("ɹ");
        consonants.add("ɻ");
        consonants.add("ɽ");
        consonants.add("ɾ");
        consonants.add("ʀ");
        consonants.add("ʁ");
        consonants.add("ʂ");
        consonants.add("ʃ");
        consonants.add("ʈ");
        consonants.add("ʋ");
        consonants.add("ʐ");
        consonants.add("ʒ");
        consonants.add("ʔ");
        consonants.add("ʕ");
        consonants.add("ʙ");
        consonants.add("ʝ");
        consonants.add("β");
        consonants.add("θ");
        consonants.add("χ");
        consonants.add("ʐ");
        consonants.add("w");
    }

    static {
        similarityMatrix.put("bilabial", 1.0);
        similarityMatrix.put("labiodental", 0.95);
        similarityMatrix.put("dental", 0.9);
        similarityMatrix.put("alveolar", 0.85);
        similarityMatrix.put("retroflex", 0.8);
        similarityMatrix.put("palato-alveolar", 0.75);
        similarityMatrix.put("palatal", 0.7);
        similarityMatrix.put("velar", 0.6);
        similarityMatrix.put("uvular", 0.5);
        similarityMatrix.put("pharyngeal", 0.3);
        similarityMatrix.put("glottal", 0.1);
        similarityMatrix.put("labiovelar", 1.0);
        similarityMatrix.put("vowel", -1.0);
        similarityMatrix.put("stop", 1.0);
        similarityMatrix.put("affricate", 0.9);
        similarityMatrix.put("fricative", 0.85);
        similarityMatrix.put("trill", 0.7);
        similarityMatrix.put("tap", 0.65);
        similarityMatrix.put("approximant", 0.6);
        similarityMatrix.put("high vowel", 0.4);
        similarityMatrix.put("mid vowel", 0.2);
        similarityMatrix.put("low vowel", 0.0);
        similarityMatrix.put("vowel2", 0.5);
        similarityMatrix.put("high", 1.0);
        similarityMatrix.put("mid", 0.5);
        similarityMatrix.put("low", 0.0);
        similarityMatrix.put("front", 1.0);
        similarityMatrix.put("central", 0.5);
        similarityMatrix.put("back", 0.0);
        similarityMatrix.put("plus", 1.0);
        similarityMatrix.put("minus", 0.0);
    }

    static {
        R_v.add("syllabic");
        R_v.add("nasal");
        R_v.add("retroflex");
        R_v.add("high");
        R_v.add("back");
        R_v.add("round");
        R_v.add("long");
    }

    static {
        R_c.add("syllabic");
        R_c.add("manner");
        R_c.add("voice");
        R_c.add("nasal");
        R_c.add("retroflex");
        R_c.add("lateral");
        R_c.add("aspirated");
        R_c.add("place");
    }

    static {
        salience.put("syllabic", 5);
        salience.put("voice", 10);
        salience.put("lateral", 10);
        salience.put("high", 5);
        salience.put("manner", 50);
        salience.put("long", 1);
        salience.put("place", 40);
        salience.put("nasal", 10);
        salience.put("aspirated", 5);
        salience.put("back", 5);
        salience.put("retroflex", 10);
        salience.put("round", 5);
    }

    static {
        Map<String, String> m = new HashMap<>();
        m.put("place", "bilabial");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("p", m);


        m = new HashMap<>();
        m.put("place", "bilabial");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("b", m);

        m = new HashMap<>();
        m.put("place", "alveolar");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("t", m);

        m = new HashMap<>();
        m.put("place", "alveolar");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("d", m);

        m = new HashMap<>();
        m.put("place", "retroflex");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "plus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ʈ", m);

        m = new HashMap<>();
        m.put("place", "retroflex");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "plus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɖ", m);

        m = new HashMap<>();
        m.put("place", "palatal");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("c", m);

        m = new HashMap<>();
        m.put("place", "palatal");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɟ", m);

        m = new HashMap<>();
        m.put("place", "velar");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("k", m);

        m = new HashMap<>();
        m.put("place", "velar");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("g", m);

        m = new HashMap<>();
        m.put("place", "uvular");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("q", m);

        m = new HashMap<>();
        m.put("place", "uvular");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɢ", m);

        m = new HashMap<>();
        m.put("place", "glottal");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ʔ", m);

        m = new HashMap<>();
        m.put("place", "bilabial");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "plus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("m", m);

        m = new HashMap<>();
        m.put("place", "labiodental");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "plus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɱ", m);

        m = new HashMap<>();
        m.put("place", "alveolar");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "plus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("n", m);

        m = new HashMap<>();
        m.put("place", "retroflex");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "plus");
        m.put("retroflex", "plus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɳ", m);

        m = new HashMap<>();
        m.put("place", "palatal");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "plus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɲ", m);

        m = new HashMap<>();
        m.put("place", "velar");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "plus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ŋ", m);

        m = new HashMap<>();
        m.put("place", "uvular");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "plus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɴ", m);

        m = new HashMap<>();
        m.put("place", "uvular");
        m.put("manner", "stop");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "plus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("N", m);

        m = new HashMap<>();
        m.put("place", "bilabial");
        m.put("manner", "trill");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ʙ", m);

        m = new HashMap<>();
        m.put("place", "bilabial");
        m.put("manner", "trill");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("B", m);

        m = new HashMap<>();
        m.put("place", "alveolar");
        m.put("manner", "trill");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "plus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("r", m);

        m = new HashMap<>();
        m.put("place", "uvular");
        m.put("manner", "trill");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ʀ", m);

        m = new HashMap<>();
        m.put("place", "uvular");
        m.put("manner", "trill");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("R", m);

        m = new HashMap<>();
        m.put("place", "alveolar");
        m.put("manner", "tap");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɾ", m);

        m = new HashMap<>();
        m.put("place", "retroflex");
        m.put("manner", "tap");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "plus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɽ", m);

        m = new HashMap<>();
        m.put("place", "bilabial");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɸ", m);

        m = new HashMap<>();
        m.put("place", "bilabial");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("β", m);

        m = new HashMap<>();
        m.put("place", "labiodental");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("f", m);

        m = new HashMap<>();
        m.put("place", "labiodental");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("v", m);

        m = new HashMap<>();
        m.put("place", "dental");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("θ", m);

        m = new HashMap<>();
        m.put("place", "dental");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ð", m);

        m = new HashMap<>();
        m.put("place", "alveolar");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("s", m);

        m = new HashMap<>();
        m.put("place", "alveolar");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("z", m);

        m = new HashMap<>();
        m.put("place", "palato-alveolar");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ʃ", m);

        m = new HashMap<>();
        m.put("place", "palato-alveolar");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ʒ", m);

        m = new HashMap<>();
        m.put("place", "retroflex");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "plus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ʂ", m);

        m = new HashMap<>();
        m.put("place", "retroflex");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "plus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ʐ", m);

        m = new HashMap<>();
        m.put("place", "palatal");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ç", m);

        m = new HashMap<>();
        m.put("place", "palatal");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ʝ", m);

        m = new HashMap<>();
        m.put("place", "velar");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("x", m);

        m = new HashMap<>();
        m.put("place", "velar");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɣ", m);

        m = new HashMap<>();
        m.put("place", "uvular");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("χ", m);

        m = new HashMap<>();
        m.put("place", "uvular");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ʁ", m);

        m = new HashMap<>();
        m.put("place", "pharyngeal");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ħ", m);

        m = new HashMap<>();
        m.put("place", "pharyngeal");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ʕ", m);

        m = new HashMap<>();
        m.put("place", "glottal");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("h", m);

        m = new HashMap<>();
        m.put("place", "glottal");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɦ", m);

        m = new HashMap<>();
        m.put("place", "alveolar");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "minus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "plus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɬ", m);

        m = new HashMap<>();
        m.put("place", "alveolar");
        m.put("manner", "fricative");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "plus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɮ", m);

        m = new HashMap<>();
        m.put("place", "labiodental");
        m.put("manner", "approximant");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ʋ", m);

        m = new HashMap<>();
        m.put("place", "alveolar");
        m.put("manner", "approximant");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɹ", m);

        m = new HashMap<>();
        m.put("place", "retroflex");
        m.put("manner", "approximant");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "plus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɻ", m);

        m = new HashMap<>();
        m.put("place", "palatal");
        m.put("manner", "approximant");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("j", m);

        m = new HashMap<>();
        m.put("place", "velar");
        m.put("manner", "approximant");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɰ", m);

        m = new HashMap<>();
        m.put("place", "alveolar");
        m.put("manner", "approximant");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "plus");
        m.put("aspirated", "minus");
        featureMatrix.put("l", m);

        m = new HashMap<>();
        m.put("place", "labiovelar");
        m.put("manner", "approximant");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("w", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "high");
        m.put("back", "front");
        m.put("round", "minus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("i", m);


        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "high");
        m.put("back", "front");
        m.put("round", "plus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("y", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "mid");
        m.put("back", "front");
        m.put("round", "minus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("e", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "mid");
        m.put("back", "front");
        m.put("round", "minus");
        m.put("long", "plus");
        m.put("aspirated", "minus");
        featureMatrix.put("E", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "mid");
        m.put("back", "front");
        m.put("round", "plus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ø", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "mid");
        m.put("back", "front");
        m.put("round", "minus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɛ", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "mid");
        m.put("back", "front");
        m.put("round", "plus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("œ", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "low");
        m.put("back", "front");
        m.put("round", "minus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("æ", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "low");
        m.put("back", "front");
        m.put("round", "minus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("a", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "low");
        m.put("back", "front");
        m.put("round", "minus");
        m.put("long", "plus");
        m.put("aspirated", "minus");
        featureMatrix.put("A", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "high");
        m.put("back", "central");
        m.put("round", "minus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɨ", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "high");
        m.put("back", "central");
        m.put("round", "plus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ʉ", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "mid");
        m.put("back", "central");
        m.put("round", "minus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ə", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "high");
        m.put("back", "back");
        m.put("round", "plus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("u", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "high");
        m.put("back", "back");
        m.put("round", "plus");
        m.put("long", "plus");
        m.put("aspirated", "minus");
        featureMatrix.put("U", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "mid");
        m.put("back", "back");
        m.put("round", "plus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("o", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "mid");
        m.put("back", "back");
        m.put("round", "plus");
        m.put("long", "plus");
        m.put("aspirated", "minus");
        featureMatrix.put("O", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "mid");
        m.put("back", "back");
        m.put("round", "plus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɔ", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "low");
        m.put("back", "back");
        m.put("round", "minus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɒ", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "high");
        m.put("back", "front");
        m.put("round", "minus");
        m.put("long", "plus");
        m.put("aspirated", "minus");
        featureMatrix.put("I", m);

        // added manually
        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "low");
        m.put("back", "central");
        m.put("round", "minus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɐ", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "mid");
        m.put("back", "central");
        m.put("round", "minus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɜ", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "high");
        m.put("back", "back");
        m.put("round", "minus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ʊ", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "low");
        m.put("back", "back");
        m.put("round", "minus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɑ", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "mid");
        m.put("back", "front");
        m.put("round", "plus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ʏ", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "mid");
        m.put("back", "back");
        m.put("round", "minus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ʌ", m);

        m = new HashMap<>();
        m.put("place", "vowel");
        m.put("manner", "vowel2");
        m.put("syllabic", "plus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("high", "high");
        m.put("back", "front");
        m.put("round", "minus");
        m.put("long", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɪ", m);

        m = new HashMap<>();
        m.put("place", "palatal");
        m.put("manner", "approximant");
        m.put("syllabic", "minus");
        m.put("voice", "plus");
        m.put("nasal", "minus");
        m.put("retroflex", "minus");
        m.put("lateral", "minus");
        m.put("aspirated", "minus");
        featureMatrix.put("ɥ", m);
    }
}
