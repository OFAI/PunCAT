package at.ofai.punderstanding.puncat.controller;

import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
        punColumn.setCellValueFactory(new PropertyValueFactory<>("pun"));
        this.setTextColor(punColumn);
        this.candidateTable.getColumns().add(punColumn);

        var targetColumn = new TableColumn<CandidateModel, String>("Target");
        targetColumn.setCellValueFactory(new PropertyValueFactory<>("target"));
        this.setTextColor(targetColumn);
        this.candidateTable.getColumns().add(targetColumn);

        var phonColumn = new TableColumn<CandidateModel, String>("~phon");
        phonColumn.setCellValueFactory(new PropertyValueFactory<>("phon"));
        this.setTextColor(phonColumn);
        this.candidateTable.getColumns().add(phonColumn);

        var semColumn = new TableColumn<CandidateModel, String>("~sem");
        semColumn.setCellValueFactory(new PropertyValueFactory<>("sem"));
        this.setTextColor(semColumn);
        this.candidateTable.getColumns().add(semColumn);

        var buttonColumn = new TableColumn<CandidateModel, Void>();
        buttonColumn.setCellFactory(col -> new TableCell<>() {
            private final Button button = new Button();

            {
                button.prefWidthProperty().bind(this.widthProperty());
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
                        button.setText("+");
                        this.setGraphic(button);
                        button.setOnAction(event -> newCandidate());
                        button.disableProperty().bind(candidate.hasEmptyValuesProperty());
                    } else {
                        this.setId("");
                        button.setText("-");
                        button.setDisable(false);
                        button.setOnAction(event -> candidateTableContents.remove(candidate));
                        this.setGraphic(button);
                    }
                }
            }
        });
        this.candidateTable.getColumns().add(buttonColumn);

        var c = new CandidateModel(this.punCandidate, this.targetCandidate, this.semanticScore, this.phoneticScore);
        this.candidateTableContents.add(c);
        this.candidateTable.setItems(this.candidateTableContents);
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
