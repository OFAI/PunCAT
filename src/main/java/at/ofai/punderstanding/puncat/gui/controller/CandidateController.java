package at.ofai.punderstanding.puncat.gui.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.json.JSONArray;

import at.ofai.punderstanding.puncat.gui.logger.InteractionLogger;
import at.ofai.punderstanding.puncat.gui.logger.LoggerValues;
import at.ofai.punderstanding.puncat.gui.model.CandidateModel;


public class CandidateController implements Initializable {
    private final ObservableList<CandidateModel> candidateData = FXCollections.observableArrayList();
    private final StringProperty punCandidate = new SimpleStringProperty();
    private final StringProperty targetCandidate = new SimpleStringProperty();
    private final StringProperty semanticScore = new SimpleStringProperty();
    private final StringProperty phoneticScore = new SimpleStringProperty();
    public TableView<CandidateModel> candidateTable;
    private final InteractionLogger interactionLogger;

    public CandidateController() {
        this.interactionLogger = new InteractionLogger();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.setupTableView();
    }

    public void setReferences(StringProperty punProperty, StringProperty targetProperty,
                              StringProperty semanticSimilarityScoreProperty, StringProperty phoneticSimilarityScoreProperty) {
        this.punCandidate.bind(punProperty);
        this.targetCandidate.bind(targetProperty);
        this.semanticScore.bind(semanticSimilarityScoreProperty);
        this.phoneticScore.bind(phoneticSimilarityScoreProperty);
    }

    private void setupTableView() {
        this.candidateTable.setId("candidate-table");
        this.candidateTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            public void updateItem(CandidateModel item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null && item.isRealTime()) {
                    setId("firstRow");
                }
            }
        });

        this.candidateTable.sortPolicyProperty().set(param -> {
            Comparator<CandidateModel> comparator = (r1, r2) -> {
                if (r1.isRealTime()) {
                    return -1;
                } else if (r2.isRealTime()) {
                    return 1;
                } else if (param.getComparator() == null) {
                    return 0;
                } else {
                    return param.getComparator().compare(r1, r2);
                }
            };
            FXCollections.sort(candidateTable.getItems(), comparator);
            return true;
        });

        TableColumn<CandidateModel, String> punColumn = new TableColumn<>("Pun");
        punColumn.setCellValueFactory(new PropertyValueFactory<>("pun"));
        this.candidateTable.getColumns().add(punColumn);

        TableColumn<CandidateModel, String> targetColumn = new TableColumn<>("Target");
        targetColumn.setCellValueFactory(new PropertyValueFactory<>("target"));
        this.candidateTable.getColumns().add(targetColumn);

        TableColumn<CandidateModel, String> semColumn = new TableColumn<>("~sem");
        semColumn.setCellValueFactory(new PropertyValueFactory<>("sem"));
        this.candidateTable.getColumns().add(semColumn);

        TableColumn<CandidateModel, String> phonColumn = new TableColumn<>("~phon");
        phonColumn.setCellValueFactory(new PropertyValueFactory<>("phon"));
        this.candidateTable.getColumns().add(phonColumn);

        this.candidateData.add(new CandidateModel(this.punCandidate, this.targetCandidate, this.semanticScore, this.phoneticScore));
        this.candidateTable.setItems(this.candidateData);
        this.candidateTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void newCandidate() {
        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.NEW_CANDIDATE_ADDED_EVENT,
                LoggerValues.CANDIDATE_PUN, this.punCandidate.getValue(),
                LoggerValues.CANDIDATE_TARGET, this.targetCandidate.getValue(),
                LoggerValues.CANDIDATE_SEM, Double.parseDouble(this.semanticScore.getValue()),
                LoggerValues.CANDIDATE_PHON, Double.parseDouble(this.phoneticScore.getValue())
        ));

        this.candidateData.add(new CandidateModel(
                this.punCandidate.getValue(),
                this.targetCandidate.getValue(),
                this.semanticScore.getValue(),
                this.phoneticScore.getValue()));
    }

    public JSONArray candidatesToJsonArray() {
        var candidates = new ArrayList<Map<String, Object>>();
        for (var candidate : this.candidateData) {
            if (candidate.isRealTime()) {
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
}
