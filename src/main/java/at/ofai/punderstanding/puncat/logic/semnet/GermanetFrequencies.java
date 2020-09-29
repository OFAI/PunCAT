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

package at.ofai.punderstanding.puncat.logic.semnet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.tuebingen.uni.sfs.germanet.api.Synset;
import de.tuebingen.uni.sfs.germanet.api.WordCategory;


public class GermanetFrequencies {
    private final Map<WordCategory, Map<String, Long>> frequenciesByWordCategories;

    public GermanetFrequencies(Map<String, Long> nomenFreq, Map<String, Long> verbenFreq, Map<String, Long> adjFreq) {
        this.frequenciesByWordCategories = new HashMap<>();
        this.frequenciesByWordCategories.put(WordCategory.nomen, nomenFreq);
        this.frequenciesByWordCategories.put(WordCategory.verben, verbenFreq);
        this.frequenciesByWordCategories.put(WordCategory.adj, adjFreq);
    }

    public static GermanetFrequencies loadFrequencies(Path nomenPath, Path verbenPath, Path adjPath) {
        try {
            Map<String, Long> nomenFreq = loadFile(nomenPath);
            Map<String, Long> verbenFreq = loadFile(verbenPath);
            Map<String, Long> adjFreq = loadFile(adjPath);
            return new GermanetFrequencies(nomenFreq, verbenFreq, adjFreq);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private static Map<String, Long> loadFile(Path path) throws IOException {
        Map<String, Long> freqMap = new HashMap<>();
        Files.lines(path)
                .map(line -> line.split("\\s+"))
                .forEach(splits -> {
                    String word = splits[0];
                    Long freq = Long.valueOf(splits[1]);
                    freqMap.put(word, freq);
                });
        return freqMap;
    }

    public Long getSynsetCumulativeFrequency(Synset synset) {
        long frequency = 0L;
        var wordCat = synset.getWordCategory();
        for (String form : synset.getAllOrthForms()) {
            Long f = this.frequenciesByWordCategories.get(wordCat).get(form);
            if (f != null) {
                frequency += f;
            }
        }
        return frequency;
    }

    public String getMostFrequentOrthForm(Synset synset) {
        String mostFrequent = null;
        var wordCat = synset.getWordCategory();
        long maxFreq = -1L;
        for (String form : synset.getAllOrthForms()) {
            Long f = this.frequenciesByWordCategories.get(wordCat).get(form);
            if (f == null) {
                f = 0L;  // TODO: do something about the missing entries
            }
            if (f > maxFreq) {
                maxFreq = f;
                mostFrequent = form;
            }
        }
        if (maxFreq <= 0) {
            // if there is no frequency information for this synset, return the shortest orthForm
            var shortest = synset.getAllOrthForms().stream().min(Comparator.comparingInt(String::length));
            if (shortest.isPresent()) {
                return shortest.get();
            } else {
                throw new RuntimeException("Empty synset: " + synset.getId());
            }
        }
        return mostFrequent;
    }
}
