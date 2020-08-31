package gui.model;

import com.google.common.collect.BiMap;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public interface SenseModel {
    StringProperty pronunciationProperty();
    String getPronunciation();
    void setPronunciation(String pronunciation);
    BiMap<Integer, String> getSynonyms();
    String getDescription();
}
