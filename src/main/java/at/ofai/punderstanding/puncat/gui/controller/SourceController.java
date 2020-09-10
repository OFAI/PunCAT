package at.ofai.punderstanding.puncat.gui.controller;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import net.sf.extjwnl.data.Synset;

import at.ofai.punderstanding.puncat.gui.component.SenseCell;
import at.ofai.punderstanding.puncat.gui.logger.InteractionLogger;
import at.ofai.punderstanding.puncat.gui.logger.LoggerValues;
import at.ofai.punderstanding.puncat.gui.model.SenseModel;
import at.ofai.punderstanding.puncat.gui.model.SenseModelSource;
import at.ofai.punderstanding.puncat.logic.search.Search;


public class SourceController implements Initializable {
    @FXML
    public TextField wordInput;
    @FXML
    public ListView<SenseModel> senseList;

    private ObservableList<SenseModel> sources;
    private MainController mainController;
    private Search search;
    private int identifier;
    private final InteractionLogger interactionLogger;

    public void setReferences(MainController mc, int identifier) {
        this.mainController = mc;
        this.identifier = identifier;
    }

    public SourceController() {
        this.interactionLogger = new InteractionLogger();
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

    public void wordInputChanged() {
        String word = wordInput.getText();
        List<Synset> synsets = search.getSourceSenses(word.toLowerCase());
        this.sources.setAll(synsets.stream().map(SenseModelSource::new).collect(Collectors.toList()));
        this.setPronunciations();
        this.senseList.getSelectionModel().select(0);

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.SOURCE_KEYWORD_CHANGED_EVENT,
                LoggerValues.NEW_VALUE, wordInput.getText(),
                LoggerValues.PANEL_ID, this.identifier,
                LoggerValues.AUTO_SELECTED_SYNSET_ID, this.senseList.getSelectionModel().getSelectedItem().getSynsetIdentifier()));
    }

    public void senseSelected() {
        SenseModelSource selection = (SenseModelSource) this.senseList.getSelectionModel().getSelectedItem();
        if (selection != null) {
            this.mainController.sourceSelected(selection.getSynsetIdentifier(), this);
        } else {
            this.mainController.sourceSelected(null, this);
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
        return selection.getSynsetIdentifier();
    }

    public void setContentsByCorpusInstance(String lemma, Long sense) {
        this.wordInput.setText(lemma);
        this.wordInputChanged();
        var toSelect = this.sources.stream().filter(s -> s.getSynsetIdentifier().equals(sense)).findFirst();
        if (toSelect.isPresent()) {
            this.senseList.getSelectionModel().select(toSelect.get());
        } else {
            throw new RuntimeException();
        }
    }

    public void logSelection(MouseEvent mouseEvent) {
        var cls = mouseEvent.getTarget().getClass();

        // if this was a click on an empty area
        // TODO: nicer solution?
        if (cls == ListView.class || cls == Group.class ||
                (cls == SenseCell.class && !((SenseCell) mouseEvent.getTarget()).hasContent())) {
            return;
        }
        var idx = this.senseList.getSelectionModel().getSelectedIndex();
        var selection = this.senseList.getSelectionModel().getSelectedItem();

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.SOURCE_SENSE_SELECTED_EVENT,
                LoggerValues.PANEL_ID, this.identifier,
                LoggerValues.SELECTION_INDEX, idx,
                LoggerValues.SELECTED_SYNSET_ID, selection.getSynsetIdentifier())
        );
    }
}
