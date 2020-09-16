package at.ofai.punderstanding.puncat.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import org.json.JSONArray;

import at.ofai.punderstanding.puncat.component.CorpusInstanceKeywordTextFlow;
import at.ofai.punderstanding.puncat.component.CorpusInstanceQuoteTextFlow;
import at.ofai.punderstanding.puncat.logic.search.Search;
import at.ofai.punderstanding.puncat.model.SimilarityModel;
import at.ofai.punderstanding.puncat.model.corpus.CorpusInstance;


public class MainController implements Initializable {
    private final SimilarityModel similarityModel = new SimilarityModel();
    @FXML
    public BorderPane container;
    @FXML
    private ImageView imageView;
    @FXML
    private VBox imageContainer;
    @FXML
    private GridPane rootGridPane;
    @FXML
    private Button firstButton;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button lastButton;
    private CandidateController candidateController;
    private SenseGroupController senseGroupController1;
    private SenseGroupController senseGroupController2;
    private String corpusInstanceId = "none";


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/fxml/senseGroupView.fxml"));
        try {
            this.rootGridPane.add(loader.load(), 1, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.senseGroupController1 = loader.getController();

        loader = new FXMLLoader(getClass().getResource("/fxml/senseGroupView.fxml"));
        try {
            this.rootGridPane.add(loader.load(), 1, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.senseGroupController2 = loader.getController();

        loader = new FXMLLoader(getClass().getResource("/fxml/candidateView.fxml"));
        try {
            this.rootGridPane.add(loader.load(), 0, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.candidateController = loader.getController();

        this.candidateController
                .punCandidateProperty().bind(this.senseGroupController1.selectedOrthFormProperty());
        this.candidateController
                .targetCandidateProperty().bind(this.senseGroupController2.selectedOrthFormProperty());
        this.candidateController
                .semanticScoreProperty().bind(this.similarityModel.semanticSimilarityScoreProperty());
        this.candidateController
                .phoneticScoreProperty().bind(this.similarityModel.phoneticSimilarityScoreProperty());

        this.senseGroupController1
                .selectedTargetProperty()
                .addListener((observable, oldValue, newValue) -> this.maybeCalculateSimilarity());
        this.senseGroupController1
                .selectedOrthFormProperty()
                .addListener((observable, oldValue, newValue) -> this.maybeCalculateSimilarity());
        this.senseGroupController2
                .selectedTargetProperty()
                .addListener((observable, oldValue, newValue) -> this.maybeCalculateSimilarity());
        this.senseGroupController2
                .selectedOrthFormProperty()
                .addListener((observable, oldValue, newValue) -> this.maybeCalculateSimilarity());

        this.senseGroupController1.setIdentifier(1);
        this.senseGroupController2.setIdentifier(2);
    }

    public void setSearch(Search search) {
        this.similarityModel.setSearch(search);
        this.senseGroupController1.setSearch(search);
        this.senseGroupController2.setSearch(search);
    }

    public void loadCorpusInstance(CorpusInstance corpusInstance) {
        this.corpusInstanceId = corpusInstance.getId();

        this.senseGroupController1.setContentsByCorpusInstance(
                corpusInstance.getText().getPun().getFirstLemma(),
                corpusInstance.getText().getPun().getFirstSense());
        this.senseGroupController2.setContentsByCorpusInstance(
                corpusInstance.getText().getPun().getSecondLemma(),
                corpusInstance.getText().getPun().getSecondSense());

        this.imageView.fitWidthProperty().bind(this.imageContainer.widthProperty());
        var image = new Image(corpusInstance.getImg().src, true);
        this.imageView.setImage(image);

        var imageText = CorpusInstanceQuoteTextFlow.build(corpusInstance.getText());
        this.imageContainer.getChildren().add(0, imageText);
        this.imageContainer.setSpacing(10);

        var keywordLabel = CorpusInstanceKeywordTextFlow.build(corpusInstance.getImg().keywords);
        this.imageContainer.getChildren().add(keywordLabel);

        for (var btn : this.getButtons()) {
            btn.setVisible(true);
        }
    }

    public JSONArray saveCandidates() {
        return this.candidateController.candidatesToJsonArray();
    }

    public String getCorpusInstanceId() {
        return this.corpusInstanceId;
    }

    public void maybeCalculateSimilarity() {
        if (this.everyFieldHasSelection()) {
            long sourceSense1 = this.senseGroupController1.getSelectedSourceId();
            long sourceSense2 = this.senseGroupController2.getSelectedSourceId();
            int targetSense1 = this.senseGroupController1.getSelectedTargetId();
            int targetSense2 = this.senseGroupController2.getSelectedTargetId();
            String word1 = this.senseGroupController1.getSelectedOrthForm();
            String word2 = this.senseGroupController2.getSelectedOrthForm();

            this.similarityModel.calculateSemanticSimilarity(sourceSense1, sourceSense2, targetSense1, targetSense2);
            this.similarityModel.calculatePhoneticSimilarity(word1, word2);
        } else {
            this.similarityModel.clearSimilarity();
        }
    }

    private boolean everyFieldHasSelection() {
        return this.senseGroupController1.hasSelections() && this.senseGroupController2.hasSelections();
    }

    public List<Button> getButtons() {
        return List.of(this.firstButton, this.prevButton, this.nextButton, this.lastButton);
    }

}
