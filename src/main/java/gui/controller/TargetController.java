package gui.controller;

import de.tuebingen.uni.sfs.germanet.api.OrthFormVariant;
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
import javafx.scene.layout.Pane;
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
    @FXML
    public Pane graph;
    @FXML
    private GraphController graphController;

    private ObservableList<SenseModel> targets;
    private MainController mainController;
    private Search search;

    public void setReferences(MainController mc) {
        this.mainController = mc;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.graphController.setReferences(this);
        this.targets = FXCollections.observableArrayList();

        this.wordInput.textProperty().addListener((observableValue, s, t1) -> this.wordInputChanged(new ActionEvent()));

        this.senseList.getSelectionModel().selectedItemProperty().addListener((observableValue, senseModel, t1) ->
            this.senseSelected()
        );
        this.senseList.setCellFactory(sl -> new SenseCell());
        this.senseList.setItems(this.targets);
    }

    public void sourceSelected(Long offset) {
        Synset synset = search.mapToGermanet(offset);
        if (synset != null) {
            this.populateSynsetList(synset.getOrthForms(OrthFormVariant.orthForm).get(0));
            this.wordInput.setText(synset.getOrthForms(OrthFormVariant.orthForm).get(0));
            this.setSelectionBySynset(synset);
            this.setPronunciations();
            this.updateGraph();
        } else {
            this.populateSynsetList(null);
            this.wordInput.setText("");
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
        this.setPronunciations();
    }

    private void setPronunciations() {
        String ipa = this.search.getIpaTranscription(this.wordInput.getText().toLowerCase(), "de");
        for (SenseModel sm : this.senseList.getItems()) {
            sm.setPronunciation(ipa);
        }
    }

    public void senseSelected() {
        if (this.senseList.getSelectionModel().getSelectedItem() != null) {
            this.mainController.maybeCalculateSimilarity();
            this.updateGraph();
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

    public String getWordInputText() {
        return this.wordInput.getText();
    }

    public void setWordInputText(String text) {
        this.wordInput.setText(text);
    }

    public List<SenseModelTarget> getHypernyms(SenseModelTarget selection) {
        List<Synset> hypernyms = this.search.getTargetHypernyms(selection.getId());
        return hypernyms.stream().map(SenseModelTarget::new).collect(Collectors.toList());
    }

    public List<SenseModelTarget> getHyponyms(SenseModelTarget selection) {
        List<Synset> hyponyms = this.search.getTargetHyponyms(selection.getId());
        return hyponyms.stream().map(SenseModelTarget::new).collect(Collectors.toList());
    }

    public void setWordInputFromNode(String stringId) {
        int id = Integer.parseInt(stringId);
        var synset = this.search.getTargetSynsetById(id);
        this.wordInput.setText(synset.getLexUnits().get(0).getOrthForm());

        SenseModel s = this.targets.stream().filter(t -> id == ((SenseModelTarget)t).getId()).findAny().orElse(null);
        if (s == null) {
            // TODO
            throw new RuntimeException();
        }

        this.senseList.getSelectionModel().select(s);
    }

    public void updateGraph() {
        SenseModelTarget selection = (SenseModelTarget) this.senseList
                .getSelectionModel()
                .getSelectedItem();

        graphController.updateGraphData(
                selection,
                this.getHypernyms(selection),
                this.getHyponyms(selection)
        );
    }

    public void prevGraph(ActionEvent actionEvent) {
        this.graphController.prevGraph();
    }

    public void nextGraph(ActionEvent actionEvent) {
        this.graphController.nextGraph();
    }
}
