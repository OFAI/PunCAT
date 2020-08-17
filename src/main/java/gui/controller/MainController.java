package gui.controller;

import gui.model.CandidateModel;
import gui.model.SenseModelTarget;
import gui.model.SimilarityModel;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import logic.search.Search;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public GridPane source1;
    @FXML
    public GridPane target1;
    @FXML
    public GridPane target2;
    @FXML
    public GridPane source2;

    @FXML
    public Label semanticScore;
    @FXML
    public Label phoneticScore;
    @FXML
    public TableView<CandidateModel> candidate;
    @FXML
    public Button addCandidateButton;
    @FXML
    public StackPane graph;

    private SimilarityModel similarityModel;

    @FXML
    private SourceController source1Controller;
    @FXML
    private SourceController source2Controller;
    @FXML
    private TargetController target1Controller;
    @FXML
    private TargetController target2Controller;
    @FXML
    private CandidateController candidateController;
    @FXML
    private GraphController graphController;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.source1Controller.setReferences(this);
        this.source2Controller.setReferences(this);
        this.target1Controller.setReferences(this);
        this.target2Controller.setReferences(this);

        this.similarityModel = new SimilarityModel();

        this.semanticScore
                .textProperty()
                .bind(Bindings.convert(this.similarityModel.semanticSimilarityScoreProperty()));
        this.phoneticScore
                .textProperty()
                .bind(Bindings.convert(this.similarityModel.phoneticSimilarityScoreProperty()));
    }

    public void setSearch(Search search) {
        this.source1Controller.setSearch(search);
        this.source2Controller.setSearch(search);
        this.target1Controller.setSearch(search);
        this.target2Controller.setSearch(search);
        this.similarityModel.setSearch(search);
    }

    public void sourceSelected(Long offset, SourceController sourceController) {
        // TODO: there has to be a better way to do this
        TargetController targetController;
        if (sourceController == this.source1Controller) {
            targetController = this.target1Controller;
        } else {
            targetController = this.target2Controller;
        }

        targetController.sourceSelected(offset);
        SenseModelTarget selection = (SenseModelTarget) targetController
                .senseList
                .getSelectionModel()
                .getSelectedItem();
        this.graphController.updateGraphData(
                selection,
                targetController.getHypernyms(selection),
                targetController.getHyponyms(selection)
        );
    }

    public void maybeCalculateSimilarity() {
        if (this.allSenseFieldsHaveSelection()) {
            long sSense1 = this.source1Controller.getSelectedId();
            long sSense2 = this.source2Controller.getSelectedId();
            int tSense1 = this.target1Controller.getSelectedId();
            int tSense2 = this.target2Controller.getSelectedId();
            String word1 = this.target1Controller.wordInput.getText();
            String word2 = this.target2Controller.wordInput.getText();

            this.similarityModel.calculateSimilarity(sSense1, sSense2, tSense1, tSense2, word1, word2);
        }
    }

    private boolean allSenseFieldsHaveSelection() {
        return this.source1Controller.hasSelection() &&
                this.source2Controller.hasSelection() &&
                this.target1Controller.hasSelection() &&
                this.target2Controller.hasSelection();
    }

    public void addToCandidates(ActionEvent actionEvent) {
        if (target1Controller.hasSelection() && target2Controller.hasSelection()) {
            candidateController.newCandidate(
                    this.target1Controller.getWordInputText(),
                    this.target2Controller.getWordInputText(),
                    Double.parseDouble(this.semanticScore.getText()),
                    Double.parseDouble(this.phoneticScore.getText())
            );
        }
    }
}
