package at.ofai.punderstanding.puncat.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import at.ofai.punderstanding.puncat.component.SenseCell;
import at.ofai.punderstanding.puncat.logging.InteractionLogger;
import at.ofai.punderstanding.puncat.logging.LoggerValues;
import at.ofai.punderstanding.puncat.logic.search.Search;
import at.ofai.punderstanding.puncat.model.SenseModel;
import at.ofai.punderstanding.puncat.model.SenseModelSource;
import at.ofai.punderstanding.puncat.model.SenseModelTarget;


public class SenseGroupController implements Initializable {
    private final ObservableList<SenseModel> sourceList = FXCollections.observableArrayList();
    private final ObservableList<SenseModel> targetList = FXCollections.observableArrayList();
    private final ObjectProperty<SenseModelSource> selectedSource = new SimpleObjectProperty<>();
    private final ObjectProperty<SenseModelTarget> selectedTarget = new SimpleObjectProperty<>();
    private final StringProperty selectedOrthForm = new SimpleStringProperty();
    private final BooleanProperty noEquivalentInGermanet = new SimpleBooleanProperty(false);
    private final Label noResultLabel = new Label("No known equivalent in GermaNet!\nTry searching manually.");
    private final InteractionLogger interactionLogger = new InteractionLogger();
    @FXML
    private GridPane container;
    @FXML
    private TextField sourceKeyword;
    @FXML
    private TextField targetKeyword;
    @FXML
    private ListView<SenseModel> sourceListView;
    @FXML
    private ListView<SenseModel> targetListView;
    private GraphController graphController;
    private Search search;
    private int identifier;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var loader = new FXMLLoader(getClass().getResource("/fxml/graphView.fxml"));
        try {
            this.container.add(loader.load(), 1, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.graphController = loader.getController();
        this.graphController.selectedNodeProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.onNodeClicked(newValue);
            }
        });
        this.graphController.selectedLineIdProperty()
                .addListener((observable, oldValue, newValue) -> this.onNodeLineChanged(newValue));

        this.graphController.addLabel(this.noResultLabel);
        this.noResultLabel.visibleProperty().bind(Bindings.size(targetList).isEqualTo(0).and(this.noEquivalentInGermanet));

        this.sourceKeyword.setOnAction(event -> this.onSourceKeywordChanged());
        this.targetKeyword.setOnAction(event -> this.onTargetKeywordChanged());

        this.sourceListView.setCellFactory(SenseCell::new);
        this.targetListView.setCellFactory(SenseCell::new);
        this.sourceListView.setItems(this.sourceList);
        this.targetListView.setItems(this.targetList);

        this.sourceListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.selectedSource.set((SenseModelSource) newValue);
            this.onSourceSelection();
        });

        this.targetListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.selectedTarget.set((SenseModelTarget) newValue);
            this.onTargetSelection();
        });
    }

    private void onSourceKeywordChanged() {
        String keyword = this.sourceKeyword.getText();
        var synsets = this.search.wordnetGetSenses(keyword);
        var senseModels = synsets.stream().map(SenseModelSource::new).collect(Collectors.toList());

        var ipa = this.search.getIpaTranscriptionEnglish(keyword);  // TODO: missing ipa entries
        senseModels.forEach(s -> s.setPronunciation(ipa));

        this.sourceList.setAll(senseModels);
        this.sourceListView.getSelectionModel().select(0);  // onSourceSelection() gets called here

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.SOURCE_KEYWORD_CHANGED_EVENT,
                LoggerValues.NEW_VALUE, keyword,
                LoggerValues.PANEL_ID, this.identifier,
                LoggerValues.AUTO_SELECTED_SYNSET_ID, this.getSelectedSourceId()));
    }

    private void onSourceSelection() {
        if (this.selectedSource.get() == null) {
            this.clearTarget();
        } else {
            interactionLogger.logThis(Map.of(
                    LoggerValues.EVENT, LoggerValues.SOURCE_SENSE_SELECTED_EVENT,
                    LoggerValues.PANEL_ID, this.identifier,
                    LoggerValues.SELECTION_INDEX, this.sourceListView.getSelectionModel().getSelectedIndex(),
                    LoggerValues.SELECTED_SYNSET_ID, this.getSelectedSourceId())
            );

            this.selectTargetOfSelectedSource();
        }
    }

    private void selectTargetOfSelectedSource() {
        var synset = search.mapWordnetSynsetToGermanet(selectedSource.get().getSynsetIdentifier());
        if (synset == null) {
            this.clearTarget();
            this.noEquivalentInGermanet.setValue(true);
        } else {
            this.noEquivalentInGermanet.setValue(false);
            var text = this.search.germanetGetMostFrequentOrthForm(synset);
            this.targetKeyword.setText(text);
            this.selectedOrthForm.set(text);
            this.fillTargetList();
            this.targetListView.getSelectionModel().select(
                    this.targetList
                            .stream()
                            .filter(s -> s.getSynsetIdentifier().equals(synset.getId()))
                            .findFirst()
                            .orElse(null)
            );
        }
    }

    private void onTargetKeywordChanged() {
        this.selectedOrthForm.set(this.targetKeyword.getText());
        this.fillTargetList();
        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.TARGET_KEYWORD_CHANGED_EVENT,
                LoggerValues.NEW_KEYWORD, this.targetKeyword.getText(),
                LoggerValues.PANEL_ID, this.identifier,
                LoggerValues.AUTO_SELECTED_SYNSET_ID, this.targetList.get(0).getSynsetIdentifier()
        ));
        this.targetListView.getSelectionModel().select(0);
    }

    private void fillTargetList() {
        String keyword = this.selectedOrthForm.get();
        var synsets = this.search.germanetGetSenses(keyword);
        var senseModels = synsets.stream().map(SenseModelTarget::new).collect(Collectors.toList());
        var ipa = this.search.getIpaTranscriptionGerman(keyword);
        senseModels.forEach(s -> s.setPronunciation(ipa));

        this.targetList.setAll(senseModels);
    }

    private void onTargetSelection() {
        if (this.selectedTarget.get() == null) {
            this.clearGraph();
            this.selectedOrthForm.set(null);
        } else {
            var synset = this.search.germanetGetSynsetById(this.selectedTarget.get().getSynsetIdentifier());
            this.selectedOrthForm.set(this.search.germanetGetMostFrequentOrthForm(synset));
            this.updateGraph();
            interactionLogger.logThis(Map.of(
                    LoggerValues.EVENT, LoggerValues.TARGET_SENSE_SELECTED_EVENT,
                    LoggerValues.PANEL_ID, this.identifier,
                    LoggerValues.SELECTION_INDEX, this.targetListView.getSelectionModel().getSelectedIndex(),
                    LoggerValues.SELECTED_SYNSET_ID, this.getSelectedTargetId()
            ));
        }
    }

    public void updateGraph() {
        graphController.updateGraphData(
                this.selectedOrthForm.get(),
                this.selectedTarget.get(),
                this.getHypernyms(this.selectedTarget.get()),
                this.getHyponyms(this.selectedTarget.get()));
    }

    public List<SenseModelTarget> getHypernyms(SenseModelTarget selection) {
        var hypernyms = this.search.germanetGetHypernymsOrderedByFrequency(selection.getSynsetIdentifier());
        return hypernyms.stream().map(SenseModelTarget::new).collect(Collectors.toList());
    }

    public List<SenseModelTarget> getHyponyms(SenseModelTarget selection) {
        var hyponyms = this.search.germanetGetHyponymsOrderedByFrequency(selection.getSynsetIdentifier());
        return hyponyms.stream().map(SenseModelTarget::new).collect(Collectors.toList());
    }

    private void clearTarget() {
        this.targetList.clear();
        this.targetKeyword.setText("");
        this.selectedOrthForm.set(null);
        this.clearGraph();
    }

    private void clearGraph() {
        graphController.clearContents();
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    private void onNodeLineChanged(String lexUnitId) {
        this.selectedOrthForm.set(this.search.getGermanetOrthFormByLexUnitId(Integer.parseInt(lexUnitId)));
    }

    private void onNodeClicked(String synsetIdString) {
        var synset = this.search.germanetGetSynsetById(Integer.parseInt(synsetIdString));
        this.targetKeyword.setText(this.search.germanetGetMostFrequentOrthForm(synset));
        this.onTargetKeywordChanged();
        this.targetListView.getSelectionModel().select(
                this.targetList
                        .stream()
                        .filter(sm -> sm.getSynsetIdentifier().equals(synset.getId()))
                        .findFirst()
                        .orElse(null)
        );
    }

    public boolean hasSelections() {
        return this.selectedSource.get() != null && this.selectedTarget.get() != null &&
                this.selectedOrthForm.get() != null && !this.selectedOrthForm.get().equals("");
    }

    public long getSelectedSourceId() {
        return this.selectedSource.get().getSynsetIdentifier();
    }

    public int getSelectedTargetId() {
        return this.selectedTarget.get().getSynsetIdentifier();
    }

    public String getSelectedOrthForm() {
        return selectedOrthForm.get();
    }

    public StringProperty selectedOrthFormProperty() {
        return selectedOrthForm;
    }

    public ObjectProperty<SenseModelTarget> selectedTargetProperty() {
        return selectedTarget;
    }

    public void setContentsByCorpusInstance(String firstLemma, String senseKey) {
        this.sourceKeyword.setText(firstLemma);
        this.setSourceListBySenseKey(senseKey);
    }

    private void setSourceListBySenseKey(String senseKey) {
        var word = this.search.wordnetGetWordBySenseKey(senseKey);
        var synsets = this.search.wordnetGetSenses(word.getLemma());
        var senseModels = synsets.stream().map(SenseModelSource::new).collect(Collectors.toList());

        var ipa = this.search.getIpaTranscriptionEnglish(word.getLemma());  // TODO: missing ipa entries
        senseModels.forEach(s -> s.setPronunciation(ipa));

        this.sourceList.setAll(senseModels);
        this.sourceListView.getSelectionModel().select(
                this.sourceList.stream().filter(s -> s.getSynsetIdentifier().equals(word.getSynset().getOffset()))
                        .findFirst()
                        .orElse(null)
        );  // onSourceSelection() gets called here
    }

    public void setIdentifier(int i) {
        this.identifier = i;
    }
}
