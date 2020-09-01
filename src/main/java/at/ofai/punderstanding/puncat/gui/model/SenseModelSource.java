package at.ofai.punderstanding.puncat.gui.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;

public class SenseModelSource implements SenseModel {
    private final StringProperty pronunciation = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final LongProperty synsetIdentifier = new SimpleLongProperty();
    private final BiMap<Integer, String> synonyms = HashBiMap.create();

    public SenseModelSource(Synset synset) {
        this.setPronunciation("-");
        this.setDescription(synset.getGloss());
        this.setSynsetIdentifier(synset.getOffset());
        
        for (Word w : synset.getWords()) {
            this.synonyms.put(w.getLexId(), w.getLemma());
        }
    }

    public BiMap<Integer, String> getSynonyms() {
        return synonyms;
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

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public Long getSynsetIdentifier() {
        return synsetIdentifier.get();
    }

    public LongProperty synsetIdentifierProperty() {
        return synsetIdentifier;
    }

    public void setSynsetIdentifier(long synsetIdentifier) {
        this.synsetIdentifier.set(synsetIdentifier);
    }
}
