package at.ofai.punderstanding.puncat.gui.model;

import javafx.beans.property.StringProperty;

import com.google.common.collect.BiMap;


public interface SenseModel {
    StringProperty pronunciationProperty();

    String getPronunciation();

    void setPronunciation(String pronunciation);

    BiMap<Integer, String> getSynonyms();

    String getDescription();

    Number getSynsetIdentifier();  // TODO: this always requires typecasting
}
