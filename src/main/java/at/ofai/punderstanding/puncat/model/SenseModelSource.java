package at.ofai.punderstanding.puncat.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;


public class SenseModelSource implements SenseModel {
    private final StringProperty pronunciation = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final LongProperty synsetIdentifier = new SimpleLongProperty();
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

    public TextFlow getVisualRepr() {
        return visualRepr;
    }

    public net.sf.extjwnl.data.POS getPOS() {
        return POS;
    }
}
