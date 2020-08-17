package gui.controller;

import gui.component.SenseCell;
import gui.model.SenseModel;
import gui.model.SenseModelSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import logic.search.Search;
import net.sf.extjwnl.data.Synset;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SourceController implements Initializable {
    @FXML
    public TextField wordInput;
    @FXML
    public ListView<SenseModel> senseList;

    private ObservableList<SenseModel> sources;
    private MainController mainController;
    private Search search;

    public void setReferences(MainController mc) {
        this.mainController = mc;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.sources = FXCollections.observableArrayList();
        this.senseList.getSelectionModel().selectedItemProperty().addListener((observableValue, senseModel, t1) ->
            this.senseSelected()
        );

        this.senseList.setCellFactory(sl -> new SenseCell());
        this.senseList.setItems(this.sources);
    }

    public void wordInputChanged(ActionEvent actionEvent) {
        String word = wordInput.getText();
        List<Synset> synsets = search.getSourceSenses(word.toLowerCase());
        this.sources.setAll(synsets.stream().map(SenseModelSource::new).collect(Collectors.toList()));
        this.setPronunciations();
    }

    public void senseSelected() {
        SenseModelSource selection = (SenseModelSource) this.senseList.getSelectionModel().getSelectedItem();
        if (selection != null) {
            this.mainController.sourceSelected(selection.getOffset(), this);
        }
    }

    private void setPronunciations() {
        String ipa = this.search.getIpaTranscription(this.wordInput.getText().toLowerCase(), "en");
        for (SenseModel sm : this.senseList.getItems()) {
            sm.setPronunciation(ipa);
        }
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public boolean hasSelection() {
        return !this.senseList.getSelectionModel().selectedItemProperty().isNull().get();
    }

    public long getSelectedId() {
        SenseModelSource selection = (SenseModelSource) this.senseList.getSelectionModel().getSelectedItem();
        return selection.getOffset();
    }
}
