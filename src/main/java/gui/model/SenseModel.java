package gui.model;

import javafx.collections.ObservableList;

public interface SenseModel {
    String getPronunciation();
    ObservableList<String> getSynonyms();
    String getDescription();
}
