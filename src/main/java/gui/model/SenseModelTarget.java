package gui.model;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.tuebingen.uni.sfs.germanet.api.LexUnit;
import de.tuebingen.uni.sfs.germanet.api.Synset;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SenseModelTarget implements SenseModel {
    private final StringProperty pronunciation = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final BiMap<Integer, String> synonyms = HashBiMap.create();

    public SenseModelTarget(Synset synset) {
        this.setPronunciation("-");
        this.setDescription(String.join("; ", synset.getParaphrases()));
        this.setId(synset.getId());

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

    public StringProperty pronunciationProperty() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation.set(pronunciation);
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
