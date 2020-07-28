package gui.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;

import java.util.stream.Collectors;

public class SourceModel implements SenseModel {
    private final StringProperty pronunciation = new SimpleStringProperty();
    private final ListProperty<String> synonyms = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final StringProperty description = new SimpleStringProperty();
    private final LongProperty offset = new SimpleLongProperty();

    public SourceModel(Synset synset) {
        this.setPronunciation("-");
        this.setDescription(synset.getGloss());
        this.setOffset(synset.getOffset());

        ObservableList<String> synonyms = FXCollections.observableArrayList();
        synonyms.setAll(synset.getWords().stream().map(Word::getLemma).collect(Collectors.toList()));
        this.setSynonyms(synonyms);
    }

    public String getPronunciation() {
        return pronunciation.get();
    }

    public StringProperty pronunciationProperty() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation.set(pronunciation);
    }

    public ObservableList<String> getSynonyms() {
        return synonyms.get();
    }

    public ListProperty<String> synonymsProperty() {
        return synonyms;
    }

    public void setSynonyms(ObservableList<String> synonyms) {
        this.synonyms.set(synonyms);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public long getOffset() {
        return offset.get();
    }

    public LongProperty offsetProperty() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset.set(offset);
    }
}
