package gui.controller;

import gui.model.CandidateModel;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;

public class CandidateController implements Initializable {
    public TableView<CandidateModel> candidateTable;
    private final ObservableList<CandidateModel> candidateData = FXCollections.observableArrayList();
    private final StringProperty punCandidate = new SimpleStringProperty();
    private final StringProperty targetCandidate = new SimpleStringProperty();
    private final DoubleProperty semanticScore = new SimpleDoubleProperty();
    private final DoubleProperty phoneticScore = new SimpleDoubleProperty();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.setupTableView();
    }

    public void setReferences(StringProperty punProperty, StringProperty targetProperty,
                              DoubleProperty semanticSimilarityScoreProperty, DoubleProperty phoneticSimilarityScoreProperty) {
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
        //punColumn.prefWidthProperty().bind(this.candidateTable.widthProperty().multiply(0.4).subtract(2));

        TableColumn<CandidateModel, String> targetColumn = new TableColumn<>("Target");
        targetColumn.setCellValueFactory(new PropertyValueFactory<>("target"));
        this.candidateTable.getColumns().add(targetColumn);
        //targetColumn.prefWidthProperty().bind(this.candidateTable.widthProperty().multiply(0.4).subtract(2));

        TableColumn<CandidateModel, String> semColumn = new TableColumn<>("~sem");
        semColumn.setCellValueFactory(new PropertyValueFactory<>("sem"));
        this.candidateTable.getColumns().add(semColumn);
        //semColumn.prefWidthProperty().bind(this.candidateTable.widthProperty().multiply(0.1).subtract(2));

        TableColumn<CandidateModel, String> phonColumn = new TableColumn<>("~phon");
        phonColumn.setCellValueFactory(new PropertyValueFactory<>("phon"));
        this.candidateTable.getColumns().add(phonColumn);
        //phonColumn.prefWidthProperty().bind(this.candidateTable.widthProperty().multiply(0.1).subtract(2));

        this.candidateData.add(new CandidateModel(this.punCandidate, this.targetCandidate, this.semanticScore, this.phoneticScore));
        this.candidateTable.setItems(this.candidateData);
        this.candidateTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void newCandidate() {
        this.candidateData.add(new CandidateModel(
                this.punCandidate.getValue(),
                this.targetCandidate.getValue(),
                this.semanticScore.getValue(),
                this.phoneticScore.getValue()));
    }
}
