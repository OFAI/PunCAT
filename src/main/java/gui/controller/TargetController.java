package gui.controller;

import gui.component.WrappingCell;
import gui.model.TargetModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

// TODO: base class or interface for source/target controllers
public class TargetController implements Initializable {
    @FXML
    public TextField wordInput;
    @FXML
    public ListView<String> senseList;

    private MainController mainController;
    private TargetModel targetModel;

    public void setReferences(MainController mc) {
        this.mainController = mc;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.targetModel = new TargetModel();

        WrappingCell.setWrappingCell(this.senseList);

        senseList.setItems(targetModel.getSenseList());
    }

    public void sourceWordChanged(Long offset) {
        this.wordInput.setText(this.targetModel.getTextFromOffset(offset));
        this.targetModel.updateSensesBySourceOffset(offset);
    }

    public TargetModel getTargetModel() {
        return targetModel;
    }

    public void wordInputChanged(ActionEvent actionEvent) {
        targetModel.updateSenses(wordInput.getText());
    }
}
