package at.ofai.punderstanding.puncat.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.Duration;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.json.JSONArray;

import at.ofai.punderstanding.puncat.logging.InteractionLogger;
import at.ofai.punderstanding.puncat.logging.LoggerValues;
import at.ofai.punderstanding.puncat.model.CandidateModel;


public class CandidateController implements Initializable {
    private final ObservableList<CandidateModel> candidateTableContents = FXCollections.observableArrayList();
    private final StringProperty punCandidate = new SimpleStringProperty();
    private final StringProperty targetCandidate = new SimpleStringProperty();
    private final StringProperty semanticScore = new SimpleStringProperty();
    private final StringProperty phoneticScore = new SimpleStringProperty();
    private final InteractionLogger interactionLogger;
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
        var phonColumn = new TableColumn<CandidateModel, String>();
        var semColumn = new TableColumn<CandidateModel, String>();
        var buttonColumn = new TableColumn<CandidateModel, Void>();

        punColumn.setCellValueFactory(new PropertyValueFactory<>("pun"));
        targetColumn.setCellValueFactory(new PropertyValueFactory<>("target"));
        phonColumn.setCellValueFactory(new PropertyValueFactory<>("phon"));
        semColumn.setCellValueFactory(new PropertyValueFactory<>("sem"));
        this.setButtonBehavior(buttonColumn);

        this.setTextColor(punColumn);
        this.setTextColor(targetColumn);
        this.setTextColor(phonColumn);
        this.setTextColor(semColumn);

        var phonHeader = new Label("~phon");
        phonHeader.prefWidthProperty().bind(phonColumn.widthProperty());
        var phonTooltip = new Tooltip("Phonetic similarity score");
        phonTooltip.setShowDelay(Duration.millis(500));
        phonHeader.setTooltip(phonTooltip);
        phonColumn.setGraphic(phonHeader);

        var semHeader = new Label("~sem");
        semHeader.prefWidthProperty().bind(semColumn.widthProperty());
        var semTooltip = new Tooltip("Semantic similarity score");
        semTooltip.setShowDelay(Duration.millis(500));
        semHeader.setTooltip(semTooltip);
        semColumn.setGraphic(semHeader);

        this.candidateTable.getColumns().add(punColumn);
        this.candidateTable.getColumns().add(targetColumn);
        this.candidateTable.getColumns().add(phonColumn);
        this.candidateTable.getColumns().add(semColumn);
        this.candidateTable.getColumns().add(buttonColumn);

        this.candidateTable.sortPolicyProperty().set(param -> {
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
    }

    private void setButtonBehavior(TableColumn<CandidateModel, Void> buttonColumn) {
        buttonColumn.setCellFactory(col -> new TableCell<>() {
            private final Button button = new Button();
            private final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
            private final Tooltip addTooltip = new Tooltip("Add to candidates");
            private final Tooltip removeTooltip = new Tooltip("Remove from candidates");
            {
                //button.prefWidthProperty().bind(this.widthProperty());
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

    private void setTextColor(TableColumn<CandidateModel, String> tableColumn) {
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
                        this.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
                    } else {
                        this.setStyle("-fx-font-weight: normal; -fx-alignment: center;");
                    }
                }
            }
        });
    }

    public void newCandidate() {
        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.NEW_CANDIDATE_ADDED_EVENT,
                LoggerValues.CANDIDATE_PUN, this.punCandidate.getValue(),
                LoggerValues.CANDIDATE_TARGET, this.targetCandidate.getValue(),
                LoggerValues.CANDIDATE_SEM, Double.parseDouble(this.semanticScore.getValue()),
                LoggerValues.CANDIDATE_PHON, Double.parseDouble(this.phoneticScore.getValue())
        ));

        this.candidateTableContents.add(new CandidateModel(
                this.punCandidate.getValue(),
                this.targetCandidate.getValue(),
                this.semanticScore.getValue(),
                this.phoneticScore.getValue()));
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
}
