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

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.json.JSONArray;

import at.ofai.punderstanding.puncat.logging.InteractionLogger;
import at.ofai.punderstanding.puncat.logging.LoggerValues;
import at.ofai.punderstanding.puncat.logic.similarity.PhoneticSimilarity;
import at.ofai.punderstanding.puncat.logic.similarity.SemanticSimilarity;
import at.ofai.punderstanding.puncat.model.CandidateModel;


public class CandidateController implements Initializable {
    private static final BiMap<SemanticSimilarity.algs, String> semAlgDisplayNames = HashBiMap.create();
    private static final BiMap<PhoneticSimilarity.algs, String> phonAlgDisplayNames = HashBiMap.create();

    static {
        semAlgDisplayNames.put(SemanticSimilarity.algs.JiangAndConrath, "Jiang and Conrath");
        semAlgDisplayNames.put(SemanticSimilarity.algs.LeacockAndChodorow, "Leacock and Chodorow");
        semAlgDisplayNames.put(SemanticSimilarity.algs.Lin, "Lin");
        semAlgDisplayNames.put(SemanticSimilarity.algs.Resnik, "Resnik");
        semAlgDisplayNames.put(SemanticSimilarity.algs.WuAndPalmer, "Wu and Palmer");
        semAlgDisplayNames.put(SemanticSimilarity.algs.SimplePath, "Simple path");

        phonAlgDisplayNames.put(PhoneticSimilarity.algs.ALINE, "ALINE");
    }

    private final ObservableList<CandidateModel> candidateTableContents = FXCollections.observableArrayList();
    private final StringProperty punCandidate = new SimpleStringProperty();
    private final StringProperty targetCandidate = new SimpleStringProperty();
    private final StringProperty semanticScore = new SimpleStringProperty();
    private final StringProperty phoneticScore = new SimpleStringProperty();
    private final InteractionLogger interactionLogger;
    private final ObservableList<String> semAlgs = FXCollections.observableArrayList();
    private final ObservableList<String> phonAlgs = FXCollections.observableArrayList();
    private final ObjectProperty<SemanticSimilarity.algs> selectedSemAlg = new SimpleObjectProperty<>();
    private final ObjectProperty<PhoneticSimilarity.algs> selectedPhonAlg = new SimpleObjectProperty<>();
    @FXML
    private ChoiceBox<String> phonChoiceBox;
    @FXML
    private ChoiceBox<String> semChoiceBox;
    @FXML
    private TableView<CandidateModel> candidateTable;

    public CandidateController() {
        this.interactionLogger = new InteractionLogger();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.candidateTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        var punColumn = new TableColumn<CandidateModel, String>("Pun");
        var targetColumn = new TableColumn<CandidateModel, String>("Target");
        var semColumn = new TableColumn<CandidateModel, String>();
        var phonColumn = new TableColumn<CandidateModel, String>();
        var buttonColumn = new TableColumn<CandidateModel, Void>();

        buttonColumn.setSortable(false);

        semColumn.setMaxWidth(60);
        semColumn.setMinWidth(60);
        phonColumn.setMaxWidth(60);
        phonColumn.setMinWidth(60);
        buttonColumn.setMaxWidth(35);
        buttonColumn.setMinWidth(35);

        semColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        phonColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

        this.setButtonCellValueFactory(buttonColumn);

        punColumn.setCellValueFactory(new PropertyValueFactory<>("pun"));
        targetColumn.setCellValueFactory(new PropertyValueFactory<>("target"));
        semColumn.setCellValueFactory(p -> new StringBinding() {

            {
                super.bind(p.getValue().semProperty());
            }

            @Override
            protected String computeValue() {
                var semValue = p.getValue().getSem();
                if (!semValue.isEmpty()) {
                    return new DecimalFormat("#").format(Double.parseDouble(semValue) * 100);
                } else {
                    return "";
                }
            }
        });
        phonColumn.setCellValueFactory(p -> new StringBinding() {

            {
                super.bind(p.getValue().phonProperty());
            }

            @Override
            protected String computeValue() {
                var phonValue = p.getValue().getPhon();
                if (!phonValue.isEmpty()) {
                    return new DecimalFormat("#").format(Double.parseDouble(phonValue) * 100);
                } else {
                    return "";
                }
            }
        });

        this.setContentStyle(punColumn, false);
        this.setContentStyle(targetColumn, false);
        this.setContentStyle(semColumn, true);
        this.setContentStyle(phonColumn, true);

        semColumn.setGraphic(new Label("sem %") {{
            prefWidthProperty().bind(semColumn.widthProperty());
            setTooltip(new Tooltip("Semantic similarity score") {{
                setShowDelay(Duration.millis(500));
            }});
        }});
        phonColumn.setGraphic(new Label("phon %") {{
            prefWidthProperty().bind(phonColumn.widthProperty());
            setTooltip(new Tooltip("Phonetic similarity score") {{
                setShowDelay(Duration.millis(500));
            }});
        }});

        this.candidateTable.getColumns().add(punColumn);
        this.candidateTable.getColumns().add(targetColumn);
        this.candidateTable.getColumns().add(semColumn);
        this.candidateTable.getColumns().add(phonColumn);
        this.candidateTable.getColumns().add(buttonColumn);

        this.candidateTable.sortPolicyProperty().set(param -> {
            // this policy ensures that the first line in the table is
            // not considered for sorting (i.e. its contents always remain on the first line)
            Comparator<CandidateModel> comparator = (r1, r2) -> {
                if (r1.isCurrentCandidate()) {
                    return -1;
                } else if (r2.isCurrentCandidate()) {
                    return 1;
                } else if (param.getComparator() == null) {
                    return 0;
                } else {
                    return param.getComparator().compare(r1, r2);
                }
            };
            FXCollections.sort(this.candidateTable.getItems(), comparator);
            return true;
        });

        var c = new CandidateModel(this.punCandidate, this.targetCandidate, this.semanticScore, this.phoneticScore);
        this.candidateTableContents.add(c);
        this.candidateTable.setItems(this.candidateTableContents);

        this.semAlgs.setAll(semAlgDisplayNames.values());
        this.semChoiceBox.setItems(this.semAlgs);
        this.semChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.selectedSemAlg.set(null);
            } else {
                this.selectedSemAlg.set(semAlgDisplayNames.inverse().get(newValue));

                interactionLogger.logThis(Map.of(
                        LoggerValues.EVENT, LoggerValues.SEM_ALG_CHANGED_EVENT,
                        LoggerValues.NEW_ALG, semAlgDisplayNames.inverse().get(newValue)
                ));
            }
        });
        this.semChoiceBox.getSelectionModel().select(0);

        this.phonAlgs.setAll(phonAlgDisplayNames.values());
        this.phonChoiceBox.setItems(this.phonAlgs);
        this.phonChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.selectedPhonAlg.set(null);
            } else {
                this.selectedPhonAlg.set(phonAlgDisplayNames.inverse().get(newValue));

                interactionLogger.logThis(Map.of(
                        LoggerValues.EVENT, LoggerValues.PHON_ALG_CHANGED_EVENT,
                        LoggerValues.NEW_ALG, phonAlgDisplayNames.inverse().get(newValue)
                ));
            }
        });
        this.phonChoiceBox.getSelectionModel().select(0);


    }

    private void setButtonCellValueFactory(TableColumn<CandidateModel, Void> buttonColumn) {
        buttonColumn.setCellFactory(col -> new TableCell<>() {
            private final Button button = new Button();
            private final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
            private final Tooltip addTooltip = new Tooltip("Add to candidates");
            private final Tooltip removeTooltip = new Tooltip("Remove from candidates");

            {
                this.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    this.setGraphic(null);
                } else {
                    var candidate = getTableView().getItems().get(getIndex());
                    if (candidate.isCurrentCandidate()) {
                        button.setGraphic(fontAwesome.create(FontAwesome.Glyph.PLUS));
                        button.setTooltip(addTooltip);
                        button.setOnAction(event -> newCandidate());
                        button.disableProperty().bind(candidate.hasEmptyValuesProperty());
                    } else {
                        button.setGraphic(fontAwesome.create(FontAwesome.Glyph.MINUS));
                        button.setTooltip(removeTooltip);
                        button.setOnAction(event -> candidateTableContents.remove(candidate));
                        button.disableProperty().unbind();
                        button.setDisable(false);
                    }
                    this.setGraphic(button);
                }
            }
        });
    }

    private void setContentStyle(TableColumn<CandidateModel, String> tableColumn, boolean numberColumn) {
        tableColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    this.setText(null);
                    this.setGraphic(null);
                } else {
                    this.setText(item);
                    var candidate = getTableView().getItems().get(getIndex());
                    if (candidate.isCurrentCandidate()) {
                        this.setAlignment(numberColumn ? Pos.CENTER_RIGHT : Pos.CENTER);
                        this.setStyle("-fx-font-weight: bold;");
                    } else {
                        this.setAlignment(numberColumn ? Pos.CENTER_RIGHT : Pos.CENTER);
                        this.setStyle("-fx-font-weight: normal;");
                    }
                }
            }
        });
    }

    public void newCandidate() {
        var newCandidate = new CandidateModel(
                this.punCandidate.getValue(),
                this.targetCandidate.getValue(),
                this.semanticScore.getValue(),
                this.phoneticScore.getValue());
        this.candidateTableContents.add(newCandidate);

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.NEW_CANDIDATE_ADDED_EVENT,
                LoggerValues.CANDIDATE_PUN, newCandidate.getPun(),
                LoggerValues.CANDIDATE_TARGET, newCandidate.getTarget(),
                LoggerValues.CANDIDATE_SEM, Double.parseDouble(newCandidate.getSem()),
                LoggerValues.CANDIDATE_PHON, Double.parseDouble(newCandidate.getPhon())
        ));
    }

    public JSONArray candidatesToJsonArray() {
        var candidates = new ArrayList<Map<String, Object>>();
        for (var candidate : this.candidateTableContents) {
            if (candidate.isCurrentCandidate()) {
                continue;
            }
            candidates.add(Map.of(
                    LoggerValues.CANDIDATE_PUN, candidate.getPun(),
                    LoggerValues.CANDIDATE_TARGET, candidate.getTarget(),
                    LoggerValues.CANDIDATE_SEM, Double.parseDouble(candidate.getSem()),
                    LoggerValues.CANDIDATE_PHON, Double.parseDouble(candidate.getPhon())
            ));
        }
        return new JSONArray(candidates);
    }

    public StringProperty punCandidateProperty() {
        return punCandidate;
    }

    public StringProperty targetCandidateProperty() {
        return targetCandidate;
    }

    public StringProperty semanticScoreProperty() {
        return semanticScore;
    }

    public StringProperty phoneticScoreProperty() {
        return phoneticScore;
    }

    public SemanticSimilarity.algs getSelectedSemAlg() {
        return selectedSemAlg.get();
    }

    public ObjectProperty<SemanticSimilarity.algs> selectedSemAlgProperty() {
        return selectedSemAlg;
    }

    public PhoneticSimilarity.algs getSelectedPhonAlg() {
        return selectedPhonAlg.get();
    }

    public ObjectProperty<PhoneticSimilarity.algs> selectedPhonAlgProperty() {
        return selectedPhonAlg;
    }
}
