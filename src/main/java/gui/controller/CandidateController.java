package gui.controller;

import gui.model.CandidateModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class CandidateController implements Initializable {
    public TableView<CandidateModel> candidateTable;
    private final ObservableList<CandidateModel> candidateData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.setupTableView();
    }

    private void setupTableView() {
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

        this.candidateTable.setItems(this.candidateData);
    }

    public void newCandidate(String pun, String target, Double sem, Double phon) {
        this.candidateData.add(new CandidateModel(pun, target, sem, phon));
    }
}
