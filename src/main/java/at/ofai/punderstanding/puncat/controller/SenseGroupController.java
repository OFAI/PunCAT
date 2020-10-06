/*
  Copyright 2020 Máté Lajkó

  This file is part of PunCAT.

  PunCAT is free software: you can redistribute it and/or modify it
  under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PunCAT is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with PunCAT.  If not, see <https://www.gnu.org/licenses/>.
 */

package at.ofai.punderstanding.puncat.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;

import at.ofai.punderstanding.puncat.component.SenseCell;
import at.ofai.punderstanding.puncat.logging.InteractionLogger;
import at.ofai.punderstanding.puncat.logging.LoggerValues;
import at.ofai.punderstanding.puncat.logic.Search;
import at.ofai.punderstanding.puncat.model.SenseModel;
import at.ofai.punderstanding.puncat.model.SenseModelSource;
import at.ofai.punderstanding.puncat.model.SenseModelTarget;


public class SenseGroupController implements Initializable {
    private final ObservableList<SenseModel> sourceList = FXCollections.observableArrayList();
    private final ObservableList<SenseModel> targetList = FXCollections.observableArrayList();
    private final StringProperty selectedOrthForm = new SimpleStringProperty();
    private final BooleanProperty dontHandleNextSelectionEvent = new SimpleBooleanProperty(false);
    private final BooleanProperty readyForSimilarityCalculations = new SimpleBooleanProperty(false);
    private final BooleanProperty noEquivalentInGermanetProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty unknownSourceProperty = new SimpleBooleanProperty(false);
    private final Label noEquivalentLabel = new Label("No known equivalent in GermaNet!");
    private final Label unknownSourceLabel = new Label("Source language word unknown.\nPlease enter a new source word.");
    private final InteractionLogger interactionLogger = new InteractionLogger();
    @FXML
    private TabPane tabpane;
    @FXML
    private Tab targetTab;
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
                this.onGraphNodeClicked(newValue);
            }
        });
        this.graphController.selectedLineIdProperty()
                .addListener((observable, oldValue, newValue) -> this.onGraphRootScrolled(newValue));

        this.graphController.addLabel(this.noEquivalentLabel);
        this.noEquivalentLabel.visibleProperty().bind(
                Bindings.size(targetList).isEqualTo(0)
                        .and(Bindings.size(sourceList).isNotEqualTo(0))
                        .and(this.noEquivalentInGermanetProperty)
        );
        this.graphController.addLabel(this.unknownSourceLabel);
        this.unknownSourceLabel.visibleProperty().bind(this.unknownSourceProperty);

        this.sourceListView.setCellFactory(SenseCell::new);
        this.targetListView.setCellFactory(SenseCell::new);
        this.sourceListView.setItems(this.sourceList);
        this.targetListView.setItems(this.targetList);

        // Disable the Target tab if there is no selection in the Source tab
        this.targetTab.disableProperty().bind(this.sourceListView.getSelectionModel().selectedItemProperty().isNull());

        this.sourceKeyword.setOnAction(e -> {
            this.onSearchForSourceKeyword();
            // The current input is invalid if sourceKeyword has text, but sourceList is empty.
            // In this case apply relevant CSS and show info label. Othervise remove this CSS and hide info label.
            if (!this.sourceKeyword.getText().isEmpty() && this.sourceList.isEmpty()) {
                this.sourceKeyword.getStyleClass().add("invalid-input");
                this.unknownSourceProperty.set(true);
            } else {
                this.sourceKeyword.getStyleClass().remove("invalid-input");
                this.unknownSourceProperty.set(false);
            }
        });
        this.targetKeyword.setOnAction(e -> this.onSearchForTargetKeyword());

        this.sourceListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!this.dontHandleNextSelectionEvent.get() && newValue != null) {
                this.onSourceSenseSelected();
            }
        });
        this.targetListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!this.dontHandleNextSelectionEvent.get() && newValue != null) {
                this.onTargetSenseSelected();
            }
        });

        this.tabpane.prefHeightProperty().bind(this.container.heightProperty());
        this.sourceListView.prefHeightProperty().bind(this.tabpane.heightProperty());
        this.targetListView.prefHeightProperty().bind(this.tabpane.heightProperty());
    }

    private void onSearchForSourceKeyword() {
        this.readyForSimilarityCalculations.set(false);
        var wnSynsets = this.search.wordnetGetSenses(this.sourceKeyword.getText());
        if (!wnSynsets.isEmpty()) {
            this.fillSourceList(wnSynsets);
            this.selectInSourceList(wnSynsets.get(0));

            var equivalentGermanetSynset = this.wordnetSynsetToGermanetOrNull(
                    wnSynsets.get(0).getPOS(), wnSynsets.get(0).getOffset());

            if (equivalentGermanetSynset != null) {
                this.noEquivalentInGermanetProperty.set(false);
                this.setTargetContents(equivalentGermanetSynset);
            } else {
                // if there is no equivalent synset in GermaNet
                this.noEquivalentInGermanetProperty.set(true);
                this.clearTarget();
            }

            interactionLogger.logThis(Map.of(
                    LoggerValues.EVENT, LoggerValues.SOURCE_KEYWORD_CHANGED_EVENT,
                    LoggerValues.NEW_VALUE, this.sourceKeyword.getText(),
                    LoggerValues.PANEL_ID, this.identifier,
                    LoggerValues.AUTO_SELECTED_SYNSET_ID, this.getSelectedSourceId()));
        } else {
            // if no synset matches the keyword
            this.clearSource();

            interactionLogger.logThis(Map.of(
                    LoggerValues.EVENT, LoggerValues.SOURCE_KEYWORD_CHANGED_EVENT,
                    LoggerValues.NEW_VALUE, this.sourceKeyword.getText(),
                    LoggerValues.PANEL_ID, this.identifier,
                    LoggerValues.AUTO_SELECTED_SYNSET_ID, "no_result"));
        }
        this.readyForSimilarityCalculations.set(true);
    }

    private void onSearchForTargetKeyword() {
        this.readyForSimilarityCalculations.set(false);
        var gnSynsets = this.search.germanetGetSenses(this.targetKeyword.getText());
        if (!gnSynsets.isEmpty()) {
            this.selectedOrthForm.set(this.targetKeyword.getText());
            this.fillTargetList(gnSynsets, this.targetKeyword.getText());
            this.selectInTargetList(gnSynsets.get(0));
            this.buildGraph();

            interactionLogger.logThis(Map.of(
                    LoggerValues.EVENT, LoggerValues.TARGET_KEYWORD_CHANGED_EVENT,
                    LoggerValues.NEW_KEYWORD, this.targetKeyword.getText(),
                    LoggerValues.PANEL_ID, this.identifier,
                    LoggerValues.AUTO_SELECTED_SYNSET_ID, this.getSelectedTargetId()));
        } else {
            // if no synset matches the keyword
            this.targetList.clear();
            this.selectedOrthForm.set(null);
            this.clearGraph();

            interactionLogger.logThis(Map.of(
                    LoggerValues.EVENT, LoggerValues.TARGET_KEYWORD_CHANGED_EVENT,
                    LoggerValues.NEW_KEYWORD, this.targetKeyword.getText(),
                    LoggerValues.PANEL_ID, this.identifier,
                    LoggerValues.AUTO_SELECTED_SYNSET_ID, "no_result"));
        }
        this.readyForSimilarityCalculations.set(true);
    }

    private void onSourceSenseSelected() {
        this.readyForSimilarityCalculations.set(false);
        var selectedSource = (SenseModelSource) this.sourceListView.getSelectionModel().getSelectedItem();
        var equivalentGermanetSynset = this.wordnetSynsetToGermanetOrNull(
                selectedSource.getPOS(), selectedSource.getSynsetIdentifier());
        if (equivalentGermanetSynset != null) {
            this.noEquivalentInGermanetProperty.set(false);
            this.setTargetContents(equivalentGermanetSynset);
        } else {
            // if there is no equivalent synset in GermaNet
            this.noEquivalentInGermanetProperty.set(true);
            this.clearTarget();
        }
        this.readyForSimilarityCalculations.set(true);

        if (!this.dontHandleNextSelectionEvent.get()) {
            interactionLogger.logThis(Map.of(
                    LoggerValues.EVENT, LoggerValues.SOURCE_SENSE_SELECTED_EVENT,
                    LoggerValues.PANEL_ID, this.identifier,
                    LoggerValues.SELECTION_INDEX, this.sourceListView.getSelectionModel().getSelectedIndex(),
                    LoggerValues.SELECTED_SYNSET_ID, this.getSelectedSourceId())
            );
        }
    }

    private void onTargetSenseSelected() {
        this.readyForSimilarityCalculations.set(false);
        this.buildGraph();
        this.readyForSimilarityCalculations.set(true);

        if (!this.dontHandleNextSelectionEvent.get()) {
            interactionLogger.logThis(Map.of(
                    LoggerValues.EVENT, LoggerValues.TARGET_SENSE_SELECTED_EVENT,
                    LoggerValues.PANEL_ID, this.identifier,
                    LoggerValues.SELECTION_INDEX, this.targetListView.getSelectionModel().getSelectedIndex(),
                    LoggerValues.SELECTED_SYNSET_ID, this.getSelectedTargetId()
            ));
        }
    }

    private void onGraphNodeClicked(String synsetIdString) {
        this.readyForSimilarityCalculations.set(false);

        var synset = this.search.germanetGetSynsetById(Integer.parseInt(synsetIdString));
        this.setTargetContents(synset);

        this.readyForSimilarityCalculations.set(true);
    }

    private void setTargetContents(de.tuebingen.uni.sfs.germanet.api.Synset synset) {
        var mostFrequentOrthForm = this.search.germanetGetMostFrequentOrthForm(synset);
        this.targetKeyword.setText(mostFrequentOrthForm);
        this.selectedOrthForm.set(mostFrequentOrthForm);
        var listContents = this.search.germanetGetSenses(mostFrequentOrthForm);
        this.fillTargetList(listContents, mostFrequentOrthForm);
        this.selectInTargetList(synset);
        this.buildGraph();
    }

    private void onGraphRootScrolled(String lexUnitId) {
        this.readyForSimilarityCalculations.set(false);

        this.selectedOrthForm.set(this.search.germanetGetOrthFormByLexUnitId(Integer.parseInt(lexUnitId)));

        this.readyForSimilarityCalculations.set(true);
    }

    private void fillSourceList(List<Synset> wnSynsets) {
        var senseModels = wnSynsets.stream().map(SenseModelSource::new).collect(Collectors.toList());
        for (var sm : senseModels) {
            sm.hasGermanetEquivalentProperty().set(
                    null != this.search.wordnetSynsetToGermanetOrNull(sm.getPOS(), sm.getSynsetIdentifier())
            );
        }
        for (int i = 0; i < wnSynsets.size(); i++) {
            var ipa = this.search.getIpaTranscriptionEnglish(this.sourceKeyword.getText(), wnSynsets.get(i));
            senseModels.get(i).setPronunciation(ipa);
        }
        this.sourceList.setAll(senseModels);
        sourceListView.getItems().sort((o1, o2) -> {
            if (((SenseModelSource) o1).hasGermanetEquivalent() && !((SenseModelSource) o2).hasGermanetEquivalent())
                return -1;
            else if (((SenseModelSource) o2).hasGermanetEquivalent() && !((SenseModelSource) o1).hasGermanetEquivalent())
                return 1;
            else return 0;
        });
    }

    private void fillTargetList(List<de.tuebingen.uni.sfs.germanet.api.Synset> gnSynsets, String keyword) {
        var senseModels = gnSynsets.stream().map(SenseModelTarget::new).collect(Collectors.toList());
        var ipa = this.search.getIpaTranscriptionGerman(keyword);
        senseModels.forEach(s -> s.setPronunciation(ipa));
        this.targetList.setAll(senseModels);
    }

    private void selectInSourceList(Synset synset) {
        this.dontHandleNextSelectionEvent.set(true);
        this.sourceListView.getSelectionModel().select(
                this.sourceList
                        .stream()
                        .filter(s -> s.getSynsetIdentifier().equals(synset.getOffset()))
                        .findFirst()
                        .orElseThrow(RuntimeException::new)
        );
        this.dontHandleNextSelectionEvent.set(false);
    }

    private void selectInTargetList(de.tuebingen.uni.sfs.germanet.api.Synset synset) {
        this.dontHandleNextSelectionEvent.set(true);
        this.targetListView.getSelectionModel().select(
                this.targetList
                        .stream()
                        .filter(s -> s.getSynsetIdentifier().equals(synset.getId()))
                        .findFirst()
                        .orElseThrow(RuntimeException::new)
        );
        this.dontHandleNextSelectionEvent.set(false);
    }

    private de.tuebingen.uni.sfs.germanet.api.Synset wordnetSynsetToGermanetOrNull(POS pos, long offset) {
        return this.search.wordnetSynsetToGermanetOrNull(pos, offset);
    }

    public void buildGraph() {
        graphController.updateGraphData(
                this.selectedOrthForm.get(),
                this.getSelectedTarget(),
                this.getHypernyms(this.getSelectedTarget()),
                this.getHyponyms(this.getSelectedTarget())
        );
    }

    private void clearSource() {
        this.sourceList.clear();
        this.clearTarget();
    }

    private void clearTarget() {
        this.targetList.clear();
        this.targetKeyword.setText("");
        this.selectedOrthForm.set(null);
        this.clearGraph();
    }

    public List<SenseModelTarget> getHypernyms(SenseModelTarget selection) {
        var hypernyms = this.search.germanetGetHypernymsOrderedByFrequency(selection.getSynsetIdentifier());
        return hypernyms.stream().map(SenseModelTarget::new).collect(Collectors.toList());
    }

    public List<SenseModelTarget> getHyponyms(SenseModelTarget selection) {
        var hyponyms = this.search.germanetGetHyponymsOrderedByFrequency(selection.getSynsetIdentifier());
        return hyponyms.stream().map(SenseModelTarget::new).collect(Collectors.toList());
    }

    private void clearGraph() {
        graphController.clearContents();
    }

    public void setContentsByCorpusInstance(String firstLemma, String senseKey) {
        this.readyForSimilarityCalculationsProperty().set(false);
        this.sourceKeyword.setText(firstLemma);
        var word = this.search.wordnetGetWordBySenseKey(senseKey);
        var synsets = this.search.wordnetGetSenses(word.getLemma());
        this.fillSourceList(synsets);
        this.selectInSourceList(word.getSynset());
        this.sourceListView.scrollTo(this.getSelectedSource());

        var germanetEquivalent = this.search.wordnetSynsetToGermanetOrNull(
                word.getSynset().getPOS(), word.getSynset().getOffset());
        if (germanetEquivalent != null) {
            this.setTargetContents(germanetEquivalent);
            this.targetListView.scrollTo(this.getSelectedTarget());
        } else {
            this.noEquivalentInGermanetProperty.set(true);
        }
        this.readyForSimilarityCalculationsProperty().set(true);
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public SenseModelSource getSelectedSource() {
        return (SenseModelSource) this.sourceListView.getSelectionModel().getSelectedItem();
    }

    public SenseModelTarget getSelectedTarget() {
        return (SenseModelTarget) this.targetListView.getSelectionModel().getSelectedItem();
    }

    public long getSelectedSourceId() {
        var selection = (SenseModelSource) this.sourceListView.getSelectionModel().getSelectedItem();
        return selection.getSynsetIdentifier();
    }

    public int getSelectedTargetId() {
        var selection = (SenseModelTarget) this.targetListView.getSelectionModel().getSelectedItem();
        return selection.getSynsetIdentifier();
    }

    public String getSelectedOrthForm() {
        return selectedOrthForm.get();
    }

    public StringProperty selectedOrthFormProperty() {
        return selectedOrthForm;
    }

    public boolean isReadyForSimilarityCalculations() {
        return readyForSimilarityCalculations.get();
    }

    public BooleanProperty readyForSimilarityCalculationsProperty() {
        return readyForSimilarityCalculations;
    }

    public void setIdentifier(int i) {
        this.identifier = i;
        this.graphController.setIdentifier(i);
    }

    public boolean hasSelections() {
        return this.getSelectedSource() != null && this.getSelectedTarget() != null &&
                this.selectedOrthForm.get() != null && !this.selectedOrthForm.get().equals("");
    }
}
