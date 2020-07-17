package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import logic.search.Search;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public GridPane source;
    @FXML
    public GridPane target;
    public GridPane target2;

    @FXML
    private SourceController sourceController;
    @FXML
    private TargetController targetController;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.sourceController.setReferences(this);
        this.targetController.setReferences(this);
    }

    public void setSemanticSearch(Search semanticSearch) {
        sourceController.getSourceModel().setSemanticSearch(semanticSearch);
        targetController.getTargetModel().setSemanticSearch(semanticSearch);
    }

    public void sourceOneSelected(Long offset) {
        this.targetController.sourceWordChanged(offset);
    }

    /*
    public void srcPunTextChanged(ActionEvent actionEvent) {
        updateSrcPunList(this.srcPunText.getText());
    }

    public void srcPunSelected(MouseEvent mouseEvent) {
        int selection = this.srcPunList.getSelectionModel().getSelectedIndex();
        if (selection != -1) {
            de.tuebingen.uni.sfs.germanet.api.Synset synset = search.mapToGermanet(this.srcPunOffsets.get(selection));
            if (synset != null) {
                this.updateTrgPunText(synset.getLexUnits().get(0).getOrthForm());
            } else {
                this.updateTrgPunText("");
            }
        }
    }

    public void updateSrcPunList(String word) {
        List<String> desc = new ArrayList<>();
        List<Long> offsets = new ArrayList<>();

        if (!word.equals("")) {
            List<Synset> synsets = search.getSourceSenses(word);

            synsets.forEach(synset -> {
                desc.add(synset.getGloss());
                offsets.add(synset.getOffset());
            });
        }

        this.srcPuns.setAll(desc);
        this.srcPunOffsets = offsets;
    }

    public void updateTrgPunList(String word) {
        List<String> desc = new ArrayList<>();
        List<Integer> offsets = new ArrayList<>();

        if (!word.equals("")) {
            List<de.tuebingen.uni.sfs.germanet.api.Synset> synsets = search.getTargetSenses(word);

            synsets.forEach(synset -> {
                String wp = "";
                for (LexUnit lu : synset.getLexUnits()) {
                    if (lu.getWiktionaryParaphrases().size() > 0) {
                        wp = lu.getWiktionaryParaphrases().get(0).getWiktionarySense();
                        break;
                    }
                }
                desc.add(wp);
                offsets.add(synset.getId());
            });
        }

        this.trgPuns.setAll(desc);
        this.trgPunOffsets = offsets;
    }

    public void updateTrgPunText(String word) {
        this.trgPunText.setText(word);
        this.updateTrgPunList(word);
    }

 */
}
