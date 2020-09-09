package at.ofai.punderstanding.puncat.gui.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import de.tuebingen.uni.sfs.germanet.api.Synset;

import at.ofai.punderstanding.puncat.gui.component.SenseCell;
import at.ofai.punderstanding.puncat.gui.logger.InteractionLogger;
import at.ofai.punderstanding.puncat.gui.logger.LoggerValues;
import at.ofai.punderstanding.puncat.gui.model.SenseModel;
import at.ofai.punderstanding.puncat.gui.model.SenseModelTarget;
import at.ofai.punderstanding.puncat.logic.search.Search;


// TODO: base class or interface for source/target controllers
public class TargetController implements Initializable {
    private final StringProperty selectedWord = new SimpleStringProperty();
    private final Label noResultLabel = new Label("No known equivalent in GermaNet.\nTry searching manually.");
    private final BooleanProperty noEquivalentInGermanet = new SimpleBooleanProperty(false);
    @FXML
    public TextField wordInput;
    @FXML
    public ListView<SenseModel> senseList;
    @FXML
    public Pane graphPane;
    @FXML
    private GraphController graphPaneController;  // TODO: graph should observe senseList?
    private ObservableList<SenseModel> targets;
    private MainController mainController;
    private Search search;
    private BooleanBinding targetsEmptyProperty;
    private int identifier;

    public void setReferences(MainController mc, int identifier) {
        this.mainController = mc;
        this.identifier = identifier;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.noResultLabel.setTextAlignment(TextAlignment.CENTER);
        this.graphPaneController.setReferences(this);
        this.targets = FXCollections.observableArrayList();
        IntegerBinding targetsSizeProperty = Bindings.size(targets);
        this.targetsEmptyProperty = targetsSizeProperty.isEqualTo(0);

        this.setupNoResultLabel();

        this.senseList.getSelectionModel().selectedItemProperty().addListener((observableValue, senseModel, t1) ->
                this.senseSelected()
        );

        this.senseList.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            var trgt = event.getTarget();
            if (trgt.getClass() == Text.class || trgt.getClass() == TextFlow.class) {
                this.logSelection();
            } else if (trgt.getClass() == SenseCell.class && ((SenseCell) trgt).hasContent()) {
                this.logSelection();
            }
        });

        this.senseList.setCellFactory(sl -> new SenseCell());
        this.senseList.setItems(this.targets);
    }

    private void setupNoResultLabel() {
        this.graphPane.getChildren().add(noResultLabel);

        this.noResultLabel.layoutYProperty().bind(this.graphPane.heightProperty().divide(2));
        this.noResultLabel.layoutXProperty().bind(
                this.graphPane.widthProperty().divide(2)
                        .subtract(noResultLabel.widthProperty().divide(2)));

        this.noResultLabel.visibleProperty().bind(targetsEmptyProperty.and(this.noEquivalentInGermanet));
    }

    public void sourceSelected(Long wordnetOffset) {
        Synset synset;
        if (wordnetOffset != null) {
            synset = search.mapToGermanet(wordnetOffset);
        } else {
            synset = null;
        }
        if (synset != null) {
            this.noEquivalentInGermanet.setValue(false);
            String word = this.search.getGermanetMostFrequentOrthForm(synset);
            this.wordInput.setText(word);
            this.populateSynsetList(word);
            this.setPronunciations();
            this.selectedWord.setValue(word);
            this.setSelectionBySynset(synset);
        } else {
            this.noEquivalentInGermanet.setValue(true);
            this.selectedWord.setValue(null);
            this.populateSynsetList(null);
            this.wordInput.setText(null);
            this.setSelectionBySynset(null);
        }
    }

    public void populateSynsetList(String word) {
        if (word == null) {
            this.targets.setAll(new ArrayList<>());
            this.clearGraph();
            return;
        }

        List<Synset> synsets = search.getTargetSenses(word);
        if (synsets.isEmpty()) {
            this.clearGraph();
            this.targets.setAll(new ArrayList<>());
        } else {
            synsets.sort(Comparator.comparingLong(o -> this.search.getGermanetSynsetCumulativeFrequency(o)));
            this.targets.setAll(synsets.stream().map(SenseModelTarget::new).collect(Collectors.toList()));
            this.senseList.getSelectionModel().select(0);
        }
    }

    private void setSelectionBySynset(Synset synset) {
        if (synset == null) {
            this.senseList.getSelectionModel().clearSelection();
            return;
        }
        var t = this.targets
                .stream()
                .filter(senseModel -> ((SenseModelTarget) senseModel).getSynsetIdentifier() == synset.getId())
                .findFirst()
                .orElse(null);
        if (t != null) {
            this.senseList.getSelectionModel().select(t);
        } else {
            throw new RuntimeException();
        }
    }

    public void wordInputChangedByUser() {
        InteractionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.TARGET_KEYWORD_CHANGED_EVENT,
                LoggerValues.NEW_KEYWORD, wordInput.getText(),
                LoggerValues.PANEL_ID, this.identifier,
                LoggerValues.AUTO_SELECTED_SYNSET_ID, this.senseList.getSelectionModel().getSelectedItem().getSynsetIdentifier()));

        this.wordInputChanged();
    }

    public void wordInputChanged() {
        this.populateSynsetList(wordInput.getText());
        this.setPronunciations();
        this.senseList.getSelectionModel().select(0);
    }

    private void setPronunciations() {
        String ipa = this.search.getIpaTranscription(this.wordInput.getText().toLowerCase(), "de");
        for (SenseModel sm : this.senseList.getItems()) {
            sm.setPronunciation(ipa);
        }
    }

    public void senseSelected() {
        var selection = (SenseModelTarget) this.senseList.getSelectionModel().getSelectedItem();
        if (selection != null) {
            var selectedSynset = this.search.getTargetSynsetById(selection.getSynsetIdentifier());
            String word = this.search.getGermanetMostFrequentOrthForm(selectedSynset);
            this.selectedWord.setValue(word);
        } else {
            this.selectedWord.setValue("");
        }
        this.mainController.maybeCalculateSimilarity();
        this.updateGraph();
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public int getSelectionIndex() {
        return this.senseList.getSelectionModel().getSelectedIndex();
    }

    public int getSelectedId() {
        SenseModelTarget selection = (SenseModelTarget) this.senseList.getSelectionModel().getSelectedItem();
        return selection.getSynsetIdentifier();
    }

    public boolean hasSelection() {
        return !this.senseList.getSelectionModel().selectedItemProperty().isNull().get();
    }

    public String getWordInputText() {
        return this.wordInput.getText();
    }

    public void setWordInputText(String text) {
        this.wordInput.setText(text);
    }

    public List<SenseModelTarget> getHypernyms(SenseModelTarget selection) {
        List<Synset> hypernyms = this.search.getTargetHypernyms(selection.getSynsetIdentifier());
        return hypernyms.stream().map(SenseModelTarget::new).collect(Collectors.toList());
    }

    public List<SenseModelTarget> getHyponyms(SenseModelTarget selection) {
        List<Synset> hyponyms = this.search.getTargetHyponyms(selection.getSynsetIdentifier());
        return hyponyms.stream().map(SenseModelTarget::new).collect(Collectors.toList());
    }

    public void setWordInputFromNode(String stringId) {
        int id = Integer.parseInt(stringId);
        var synset = this.search.getTargetSynsetById(id);
        this.setWordInputText(synset.getLexUnits().get(0).getOrthForm());
        this.wordInputChanged();

        SenseModel s = this.targets.stream().filter(t -> id == ((SenseModelTarget) t).getSynsetIdentifier()).findAny().orElse(null);

        if (s == null) {
            // TODO
            throw new RuntimeException();
        }

        this.senseList.getSelectionModel().select(s);
    }

    public void updateGraph() {
        if (this.selectedWord.get() == null || this.selectedWord.get().equals("")) {
            this.clearGraph();
            return;
        }
        SenseModelTarget selection = (SenseModelTarget) this.senseList
                .getSelectionModel()
                .getSelectedItem();

        graphPaneController.updateGraphData(
                this.selectedWord.get(),
                selection,
                this.getHypernyms(selection),
                this.getHyponyms(selection)
        );
    }

    private void clearGraph() {
        graphPaneController.clearContents();
    }

    public void prevGraph() {
        this.graphPaneController.prevGraph();
    }

    public void nextGraph() {
        this.graphPaneController.nextGraph();
    }

    public void selectedLineChanged(int lexUnitId) {
        this.selectedWord.setValue(this.search.getGermanetOrthFormByLexUnitId(lexUnitId));
        this.mainController.maybeCalculateSimilarity();
    }

    public String getSelectedWord() {
        return selectedWord.get();
    }

    public StringProperty selectedWordProperty() {
        return selectedWord;
    }

    public void logSelection() {
        int idx = this.senseList.getSelectionModel().getSelectedIndex();
        var selection = this.senseList.getSelectionModel().getSelectedItem();
        InteractionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.TARGET_SENSE_SELECTED_EVENT,
                LoggerValues.PANEL_ID, this.identifier,
                LoggerValues.SELECTION_INDEX, idx,
                LoggerValues.SELECTED_SYNSET_ID, selection.getSynsetIdentifier())
        );
    }
}
