package gui.controller;

import de.tuebingen.uni.sfs.germanet.api.Synset;
import gui.component.SenseCell;
import gui.model.SenseModel;
import gui.model.SenseModelTarget;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
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
    public Pane graphPane;
    @FXML
    private GraphController graphPaneController;  // TODO: graph should observe senseList?

    private ObservableList<SenseModel> targets;
    private final StringProperty selectedWord = new SimpleStringProperty();
    private MainController mainController;
    private Search search;
    private final Label noResultLabel = new Label("No known equivalent in GermaNet.\nTry searching manually.");
    private BooleanBinding targetsEmptyProperty;
    private final BooleanProperty noEquivalentInGermanet = new SimpleBooleanProperty(false);

    public void setReferences(MainController mc) {
        this.mainController = mc;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.noResultLabel.setTextAlignment(TextAlignment.CENTER);

        this.selectedWord.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("")) {
                this.mainController.maybeCalculateSimilarity();
            }
        });

        this.graphPaneController.setReferences(this);
        this.targets = FXCollections.observableArrayList();
        IntegerBinding targetsSizeProperty = Bindings.size(targets);
        this.targetsEmptyProperty = targetsSizeProperty.isEqualTo(0);

        this.setupNoResultLabel();

        this.senseList.getSelectionModel().selectedItemProperty().addListener((observableValue, senseModel, t1) ->
            this.senseSelected()
        );
        this.senseList.setCellFactory(sl -> new SenseCell());
        this.senseList.setItems(this.targets);
    }

    private void setupNoResultLabel() {
        this.graphPane.getChildren().add(noResultLabel);

        this.noResultLabel.layoutYProperty().bind(this.graphPane.heightProperty().divide(2));
        this.noResultLabel.layoutXProperty().bind(
                this.graphPane.widthProperty().divide(2)
                        .subtract(noResultLabel.widthProperty().divide(2)));

        this.noResultLabel.visibleProperty().bind(targetsEmptyProperty.and(this.noEquivalentInGermanet));
    }

    public void sourceSelected(Long wordnetOffset) {
        Synset synset = search.mapToGermanet(wordnetOffset);
        if (synset != null) {
            this.noEquivalentInGermanet.setValue(false);
            var word = synset.getLexUnits().get(0).getOrthForm();
            this.selectedWord.setValue(word);
            this.wordInput.setText(word);
            this.populateSynsetList(word);
            this.setSelectionBySynset(synset);
            this.setPronunciations();
        } else {
            this.noEquivalentInGermanet.setValue(true);
            this.clearGraph();
            this.selectedWord.setValue(null);
            this.populateSynsetList(null);
            this.wordInput.setText("");
            this.setSelectionBySynset(null);
        }
    }

    public void populateSynsetList(String word) {
        if (word == null) {
            this.targets.setAll(new ArrayList<>());
            this.clearGraph();
            return;
        }

        List<Synset> synsets = search.getTargetSenses(word);
        if (synsets.isEmpty()) {
            this.clearGraph();
            this.targets.setAll(new ArrayList<>());
        } else {
            this.targets.setAll(synsets.stream().map(SenseModelTarget::new).collect(Collectors.toList()));
            this.senseList.getSelectionModel().select(0);
        }
    }

    private void setSelectionBySynset(Synset synset) {
        if (synset == null) {
            this.senseList.getSelectionModel().clearSelection();
            return;
        }
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
        this.setWordInputText(synset.getLexUnits().get(0).getOrthForm());
        this.wordInputChanged(null);

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

        if (selection == null) {
            // This happens when the user is manually searching for a word
            selection = (SenseModelTarget) this.senseList.getItems().get(0);
        }

        graphPaneController.updateGraphData(
                this.wordInput.getText(),
                selection,
                this.getHypernyms(selection),
                this.getHyponyms(selection)
        );
    }

    private void clearGraph() {
        graphPaneController.clearContents();
    }

    public void prevGraph(ActionEvent actionEvent) {
        this.graphPaneController.prevGraph();
    }

    public void nextGraph(ActionEvent actionEvent) {
        this.graphPaneController.nextGraph();
    }

    public void selectedLineChanged(int lexUnitId) {
        this.selectedWord.setValue(this.search.getLexUnitById(lexUnitId));
    }

    public String getSelectedWord() {
        return selectedWord.get();
    }

    public StringProperty selectedWordProperty() {
        return selectedWord;
    }
}
