package gui.model;

import de.tuebingen.uni.sfs.germanet.api.LexUnit;
import de.tuebingen.uni.sfs.germanet.api.Synset;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import logic.search.Search;

import java.util.ArrayList;
import java.util.List;

public class TargetModel {  // TODO: base class or interface for source/target models
    private final ObservableList<String> senseList; // TODO: fill with Properties instead
    private List<Integer> ids;

    private Search search;

    public TargetModel() {
        this.senseList = FXCollections.observableArrayList();
        this.ids = new ArrayList<Integer>();
    }

    public void updateSenses(String word) {
        this.ids.clear();
        if (word == null) {
            this.senseList.setAll(new ArrayList<String>());
            return;
        }
        List<Synset> synsets = search.getTargetSenses(word.toLowerCase());

        for (Synset s : synsets) {
            this.ids.add(s.getId());
        }
        this.senseList.setAll(this.formatSenseList(synsets));
    }

    public void updateSensesBySourceOffset(Long offset) {
        Synset synset = search.mapToGermanet(offset);
        if (synset != null) {
            this.updateSenses(synset.getAllOrthForms().get(0));
        } else {
            this.updateSenses(null);
        }
    }

    private List<String> formatSenseList(List<Synset> synsets) {
        List<String> formattedSenses = new ArrayList<>();
        for (Synset s : synsets) {
            String description = String.join("; ", s.getParaphrases());

            formattedSenses.add(String.format("(%s (%s))\n%s",
                    String.join(", ", s.getAllOrthForms()),
                    s.getWordCategory().toString(),
                    description));
        }
        return formattedSenses;
    }

    public String getTextFromOffset(Long offset) {
        Synset synset = this.search.mapToGermanet(offset);
        if (synset == null) {
            return "";
        } else return synset.getAllOrthForms().get(0);
    }

    public void setSemanticSearch(Search semanticSearch) {
        this.search = semanticSearch;
    }

    public ObservableList<String> getSenseList() {
        return senseList;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }
}
