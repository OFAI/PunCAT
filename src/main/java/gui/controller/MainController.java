package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    }

    public void setSemanticSearch(Search semanticSearch) {
        source1Controller.getSourceModel().setSemanticSearch(semanticSearch);
        source2Controller.getSourceModel().setSemanticSearch(semanticSearch);
        target1Controller.getTargetModel().setSemanticSearch(semanticSearch);
        target2Controller.getTargetModel().setSemanticSearch(semanticSearch);
    }

    public void sourceSelected(Long offset, SourceController sourceController) {
        // TODO: there has to be a better way to do this
        if (sourceController == this.source1Controller) {
            this.target1Controller.sourceWordChanged(offset);
        } else {
            this.target2Controller.sourceWordChanged(offset);
        }
    }
}
