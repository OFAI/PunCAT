package gui.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import logic.search.Search;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SourceModel {
    private final ObservableList<String> senseList; // TODO: fill with Properties instead
    private List<Long> offsets;

    private Search search;

    public SourceModel() {
        this.senseList = FXCollections.observableArrayList();
        this.offsets = new ArrayList<>();
    }

    public void updateSenses(String word) {
        List<Synset> synsets = search.getSourceSenses(word.toLowerCase());

        this.offsets.clear();
        for (Synset s : synsets) {
            this.offsets.add(s.getOffset());
        }
        this.senseList.setAll(this.formatSenseList(synsets));
    }

    private List<String> formatSenseList(List<Synset> synsets) {
        List<String> formattedSenses = new ArrayList<>();
        for (Synset s : synsets) {
            formattedSenses.add(String.format("(%s)\n%s",
                    s.getWords().stream().map(Word::getLemma).collect(Collectors.joining(", ")),
                    s.getGloss()));
        }
        return formattedSenses;
    }

    public ObservableList<String> getSenseList() {
        return senseList;
    }

    public void setSemanticSearch(Search semanticSearch) {
        this.search = semanticSearch;
    }

    public Long getOffset(int index) {
        return this.offsets.get(index);
    }

}
