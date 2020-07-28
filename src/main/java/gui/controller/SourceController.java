package gui.controller;

import gui.component.WrappingCell;
import gui.model.SourceModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import logic.search.Search;

import java.net.URL;
import java.util.ResourceBundle;

public class SourceController implements Initializable {
    @FXML
    public TextField wordInput;
    @FXML
    public ListView<String> senseList;

    private MainController mainController;
    private SourceModel sourceModel;

    public void setReferences(MainController mc) {
        this.mainController = mc;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.sourceModel = new SourceModel();

        WrappingCell.configureWrappingCell(this.senseList);

        senseList.setItems(sourceModel.getSenseList());
    }

    public void wordInputChanged(ActionEvent actionEvent) {
        sourceModel.updateSenses(wordInput.getText());
    }

    public void senseSelected(MouseEvent mouseEvent) {
        this.mainController.sourceSelected(this.sourceModel.
                        getOffset(this.senseList.getSelectionModel().getSelectedIndex()),
                this);
    }

    public void setSemanticSearch(Search search) {
        this.sourceModel.setSemanticSearch(search);
    }

    public Long getSelectedWordOffset() {
        return this.sourceModel.getOffset(this.senseList.getSelectionModel().getSelectedIndex());
    }

    public SourceModel getSourceModel() {
        return sourceModel;
    }

    public void setSourceModel(SourceModel sourceModel) {
        this.sourceModel = sourceModel;
    }
}
