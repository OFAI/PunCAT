package gui.controller;

import de.tuebingen.uni.sfs.germanet.api.Synset;
import gui.component.SenseCell;
import gui.model.SenseModel;
import gui.model.SenseModelTarget;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import logic.search.Search;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

// TODO: base class or interface for source/target controllers
public class TargetController implements Initializable {
    @FXML
    public TextField wordInput;
    @FXML
    public ListView<SenseModel> senseList;

    private ObservableList<SenseModel> targets;
    private MainController mainController;
    private Search search;

    public void setReferences(MainController mc) {
        this.mainController = mc;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.targets = FXCollections.observableArrayList();

        this.senseList.getSelectionModel().selectedItemProperty().addListener((observableValue, senseModel, t1) ->
            this.senseSelected()
        );
        this.senseList.setCellFactory(sl -> new SenseCell());
        this.senseList.setItems(this.targets);
    }

    public void sourceSelected(Long offset) {
        Synset synset = search.mapToGermanet(offset);
        if (synset != null) {
            this.populateSynsetList(synset.getAllOrthForms().get(0));
            this.wordInput.setText(synset.getAllOrthForms().get(0));
            this.setSelectionBySynset(synset);
        } else {
            this.populateSynsetList(null);
        }
    }

    public void populateSynsetList(String word) {
        if (word == null) {
            this.targets.setAll(new ArrayList<>());
            return;
        }

        List<Synset> synsets = search.getTargetSenses(word);
        this.targets.setAll(synsets.stream().map(SenseModelTarget::new).collect(Collectors.toList()));
    }

    private void setSelectionBySynset(Synset synset) {
        // TODO: not very nice
        for (int i = 0; i < this.targets.size(); i++) {
            if (((SenseModelTarget) this.targets.get(i)).getId() == synset.getId()) {
                this.senseList.getSelectionModel().select(i);
                break;
            }
        }
    }

    public void wordInputChanged(ActionEvent actionEvent) {
        this.populateSynsetList(wordInput.getText());
    }

    public void senseSelected() {
        if (this.senseList.getSelectionModel().getSelectedItem() != null) {
            this.mainController.maybeCalculateSimilarity();
        }
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public int getSelectionIndex() {
        return this.senseList.getSelectionModel().getSelectedIndex();
    }

    public int getSelectedId() {
        SenseModelTarget selection = (SenseModelTarget) this.senseList.getSelectionModel().getSelectedItem();
        return selection.getId();
    }

    public boolean hasSelection() {
        return !this.senseList.getSelectionModel().selectedItemProperty().isNull().get();
    }

    public String getSearchWord() {
        return this.wordInput.getText();
    }
}
