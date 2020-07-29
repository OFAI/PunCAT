package gui.model;

import de.tuebingen.uni.sfs.germanet.api.Synset;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SenseModelTarget implements SenseModel {
    private final StringProperty pronunciation = new SimpleStringProperty();
    private final ListProperty<String> synonyms = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final StringProperty description = new SimpleStringProperty();
    private final IntegerProperty id = new SimpleIntegerProperty();

    public SenseModelTarget(Synset synset) {
        this.setPronunciation("-");
        this.setDescription(String.join("; ", synset.getParaphrases()));
        this.setId(synset.getId());

        ObservableList<String> synonyms = FXCollections.observableArrayList();
        synonyms.setAll(synset.getAllOrthForms());
        this.setSynonyms(synonyms);
    }

    @Override
    public String getPronunciation() {
        return pronunciation.get();
    }

    public StringProperty pronunciationProperty() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation.set(pronunciation);
    }

    @Override
    public ObservableList<String> getSynonyms() {
        return synonyms.get();
    }

    public ListProperty<String> synonymsProperty() {
        return synonyms;
    }

    public void setSynonyms(ObservableList<String> synonyms) {
        this.synonyms.set(synonyms);
    }

    @Override
    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }
}
