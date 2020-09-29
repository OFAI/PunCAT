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

package at.ofai.punderstanding.puncat.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.text.TextFlow;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.tuebingen.uni.sfs.germanet.api.LexUnit;
import de.tuebingen.uni.sfs.germanet.api.Synset;
import de.tuebingen.uni.sfs.germanet.api.WordCategory;


public class SenseModelTarget implements SenseModel {
    private final StringProperty pronunciation = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final IntegerProperty synsetIdentifier = new SimpleIntegerProperty();
    private final WordCategory POS;
    private final BiMap<Integer, String> synonyms = HashBiMap.create();

    public SenseModelTarget(Synset synset) {
        this.setPronunciation("-");
        this.setDescription(String.join("; ", synset.getParaphrases()));
        this.setSynsetIdentifier(synset.getId());
        this.POS = synset.getWordCategory();

        for (LexUnit lu : synset.getLexUnits()) {
            synonyms.put(lu.getId(), lu.getOrthForm());
        }

    }

    public BiMap<Integer, String> getSynonyms() {
        return synonyms;
    }

    @Override
    public String getPronunciation() {
        return pronunciation.get();
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation.set(pronunciation);
    }

    public StringProperty pronunciationProperty() {
        return pronunciation;
    }

    @Override
    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public Integer getSynsetIdentifier() {
        return synsetIdentifier.get();
    }

    public void setSynsetIdentifier(int synsetIdentifier) {
        this.synsetIdentifier.set(synsetIdentifier);
    }

    public IntegerProperty synsetIdentifierProperty() {
        return synsetIdentifier;
    }

    public TextFlow getVisualRepr() {
        return new TextFlow();
    }

    public WordCategory getPOS() {
        return POS;
    }
}
