package at.ofai.punderstanding.puncat.model;

import javafx.beans.property.StringProperty;
import javafx.scene.text.TextFlow;

import com.google.common.collect.BiMap;


public interface SenseModel {
    StringProperty pronunciationProperty();

    String getPronunciation();

    void setPronunciation(String pronunciation);

    BiMap<Integer, String> getSynonyms();

    String getDescription();

    Number getSynsetIdentifier();  // TODO: this always requires typecasting

    TextFlow getVisualRepr();
}
