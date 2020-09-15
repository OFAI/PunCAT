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
    @FXML
    private TableView<CandidateModel> candidateTable;
    private final ObservableList<CandidateModel> candidateTableContents = FXCollections.observableArrayList();
    private final StringProperty punCandidate = new SimpleStringProperty();
    private final StringProperty targetCandidate = new SimpleStringProperty();
    private final StringProperty semanticScore = new SimpleStringProperty();
    private final StringProperty phoneticScore = new SimpleStringProperty();
    private final InteractionLogger interactionLogger;

    public CandidateController() {
        this.interactionLogger = new InteractionLogger();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var punColumn = new TableColumn<CandidateModel, String>("Pun");
        punColumn.setCellValueFactory(new PropertyValueFactory<>("pun"));
        this.candidateTable.getColumns().add(punColumn);

        var targetColumn = new TableColumn<CandidateModel, String>("Target");
        targetColumn.setCellValueFactory(new PropertyValueFactory<>("target"));
        this.candidateTable.getColumns().add(targetColumn);

        var phonColumn = new TableColumn<CandidateModel, String>("~phon");
        phonColumn.setCellValueFactory(new PropertyValueFactory<>("phon"));
        this.candidateTable.getColumns().add(phonColumn);

        var semColumn = new TableColumn<CandidateModel, String>("~sem");
        semColumn.setCellValueFactory(new PropertyValueFactory<>("sem"));
        this.candidateTable.getColumns().add(semColumn);

        var buttonColumn = new TableColumn<CandidateModel, Void>();
        buttonColumn.setCellFactory(col -> new TableCell<>() {
            private final Button button = new Button();
            {
                button.prefWidthProperty().bind(this.widthProperty());
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    var candidate = getTableView().getItems().get(getIndex());
                    if (candidate.isCurrentCandidate()) {
                        button.setText("+");
                        setGraphic(button);
                        button.setOnAction(event -> newCandidate());
                        button.disableProperty().bind(candidate.hasEmptyValuesProperty());
                    } else {
                        button.setText("-");
                        setGraphic(button);
                        button.setOnAction(event -> candidateTableContents.remove(candidate));
                    }
                }
            }
        });
        this.candidateTable.getColumns().add(buttonColumn);
        this.candidateTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        var c = new CandidateModel(this.punCandidate, this.targetCandidate, this.semanticScore, this.phoneticScore);
        this.candidateTableContents.add(c);
        this.candidateTable.setItems(this.candidateTableContents);
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
