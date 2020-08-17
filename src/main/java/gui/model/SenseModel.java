package gui.model;

import javafx.collections.ObservableList;

public interface SenseModel {
    String getPronunciation();
    void setPronunciation(String pronunciation);
    ObservableList<String> getSynonyms();
    String getDescription();
}
