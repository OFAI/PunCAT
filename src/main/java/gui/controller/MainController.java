package gui.controller;

import gui.model.SimilarityModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
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

    private SimilarityModel similarityModel;

    @FXML
    private SourceController source1Controller;
    @FXML
    private SourceController source2Controller;
    @FXML
    private TargetController target1Controller;
    @FXML
    private TargetController target2Controller;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.source1Controller.setReferences(this);
        this.source2Controller.setReferences(this);
        this.target1Controller.setReferences(this);
        this.target2Controller.setReferences(this);

        this.similarityModel = new SimilarityModel();

        this.semanticScore.textProperty().bind(Bindings.convert(this.similarityModel.semanticSimilarityScoreProperty()));
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
        if (sourceController == this.source1Controller) {
            this.target1Controller.sourceSelected(offset);
        } else {
            this.target2Controller.sourceSelected(offset);
        }
    }

    public void maybeCalculateSimilarity() {
        if (this.target1Controller.getSelectionIndex() != -1 && this.target2Controller.getSelectionIndex() != -1) {
            int sense1 = this.target1Controller.getSelectedId();
            int sense2 = this.target2Controller.getSelectedId();
            this.similarityModel.calculateSimilarity(sense1, sense2);
        }
    }
}
