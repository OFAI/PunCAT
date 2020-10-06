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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;


public class SenseModelSource implements SenseModel {
    private final StringProperty pronunciation = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final LongProperty synsetIdentifier = new SimpleLongProperty();
    private final BooleanProperty hasGermanetEquivalent = new SimpleBooleanProperty();
    private final net.sf.extjwnl.data.POS POS;
    private final BiMap<Integer, String> synonyms = HashBiMap.create();
    private final TextFlow visualRepr = new TextFlow();

    public SenseModelSource(Synset synset) {
        this.setPronunciation("-");
        this.setDescription(synset.getGloss());
        this.setSynsetIdentifier(synset.getOffset());
        this.POS = synset.getPOS();

        for (Word w : synset.getWords()) {
            this.synonyms.put(w.getLexId(), w.getLemma());
        }


        Text pronunciation = new Text();
        Text description = new Text();
        Text synonyms = new Text();
        synonyms.setStyle("-fx-font-weight: bold");

        pronunciation.textProperty().bind(
                new SimpleStringProperty("/").concat(this.pronunciationProperty()).concat("/"));
        synonyms.setText(" (" + String.join(", ", this.getSynonyms().values()) + ") ");
        description.setText(this.getDescription());
        this.visualRepr.getChildren().addAll(pronunciation, synonyms, description);
    }

    public BiMap<Integer, String> getSynonyms() {
        return synonyms;
    }

    public String getPronunciation() {
        return pronunciation.get();
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation.set(pronunciation);
    }

    public StringProperty pronunciationProperty() {
        return pronunciation;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public Long getSynsetIdentifier() {
        return synsetIdentifier.get();
    }

    public void setSynsetIdentifier(long synsetIdentifier) {
        this.synsetIdentifier.set(synsetIdentifier);
    }

    public boolean hasGermanetEquivalent() {
        return hasGermanetEquivalent.get();
    }

    public BooleanProperty hasGermanetEquivalentProperty() {
        return hasGermanetEquivalent;
    }

    public net.sf.extjwnl.data.POS getPOS() {
        return POS;
    }
}
