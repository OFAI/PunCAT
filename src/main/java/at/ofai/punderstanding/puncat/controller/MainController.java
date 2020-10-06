/*
  Copyright 2020 Máté Lajkó

  This file is part of PunCAT.

  PunCAT is free software: you can redistribute it and/or modify it
  under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PunCAT is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with PunCAT.  If not, see <https://www.gnu.org/licenses/>.
 */

package at.ofai.punderstanding.puncat.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.json.JSONArray;

import at.ofai.punderstanding.puncat.component.KeywordText;
import at.ofai.punderstanding.puncat.component.QuoteTextFlow;
import at.ofai.punderstanding.puncat.logic.Search;
import at.ofai.punderstanding.puncat.model.CandidateModel;
import at.ofai.punderstanding.puncat.model.SimilarityModel;
import at.ofai.punderstanding.puncat.model.corpus.CorpusInstance;


public class MainController implements Initializable {
    private final SimilarityModel similarityModel = new SimilarityModel();
    @FXML
    private GridPane container;
    @FXML
    private GridPane taskAndCandidateGridPane;
    @FXML
    private GridPane senseGroupsGridPane;
    private CandidateController candidateController;
    private SenseGroupController senseGroupController1;
    private SenseGroupController senseGroupController2;
    private TaskController taskController = null;
    private String corpusInstanceId = "i";
    private CorpusInstance corpusInstance = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("/fxml/senseGroupView.fxml"));
        try {
            this.senseGroupsGridPane.add(loader.load(), 0, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.senseGroupController1 = loader.getController();

        loader = new FXMLLoader(getClass().getResource("/fxml/senseGroupView.fxml"));
        try {
            this.senseGroupsGridPane.add(loader.load(), 0, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.senseGroupController2 = loader.getController();

        loader = new FXMLLoader(getClass().getResource("/fxml/candidateView.fxml"));
        try {
            this.taskAndCandidateGridPane.add(loader.load(), 0, 1);
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

        ChangeListener<Boolean> senseGroupReady = (observable, oldValue, newValue) -> {
            if (newValue) this.maybeCalculateSimilarity();
        };
        this.senseGroupController1.readyForSimilarityCalculationsProperty().addListener(senseGroupReady);
        this.senseGroupController2.readyForSimilarityCalculationsProperty().addListener(senseGroupReady);
        this.candidateController.selectedSemAlgProperty().addListener(
                (observable, oldValue, newValue) -> this.maybeCalculateSimilarity()
        );
        this.candidateController.selectedPhonAlgProperty().addListener(
                (observable, oldValue, newValue) -> this.maybeCalculateSimilarity()
        );

        this.senseGroupController1.setIdentifier(1);
        this.senseGroupController2.setIdentifier(2);
    }

    public void setSearch(Search search) {
        this.similarityModel.setSearch(search);
        this.senseGroupController1.setSearch(search);
        this.senseGroupController2.setSearch(search);
    }

    public void loadCorpusInstance(CorpusInstance corpusInstance, Stage stage) {
        this.corpusInstance = corpusInstance;
        this.corpusInstanceId = corpusInstance.getId();

        this.senseGroupController1.setContentsByCorpusInstance(
                corpusInstance.getText().getPun().getFirstLemma(),
                corpusInstance.getText().getPun().getFirstSense());
        this.senseGroupController2.setContentsByCorpusInstance(
                corpusInstance.getText().getPun().getSecondLemma(),
                corpusInstance.getText().getPun().getSecondSense());

        var loader = new FXMLLoader(getClass().getResource("/fxml/taskView.fxml"));
        try {
            this.taskAndCandidateGridPane.add(loader.load(), 0, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.taskController = loader.getController();

        var imgPath = corpusInstance.getImg().src;
        if (imgPath != null) {
            var image = new Image(corpusInstance.getImg().src, true);
            this.taskController.insertImage(image, stage);
        }

        var imageText = QuoteTextFlow.build(corpusInstance.getText());
        this.taskController.insertQuote(imageText);

        var keywords = KeywordText.build(corpusInstance.getImg().keywords);
        this.taskController.insertKeywords(keywords);
    }

    public JSONArray candidatesToJSONArray() {
        return this.candidateController.candidatesToJsonArray();
    }

    public List<CandidateModel> getCandidates() {
        return this.candidateController.getStoredCandidates();
    }

    public String getCorpusInstanceId() {
        return this.corpusInstanceId;
    }

    public void maybeCalculateSimilarity() {
        if (this.everyFieldHasSelection()) {
            var sourceSense1 = this.senseGroupController1.getSelectedSource();
            var sourceSense2 = this.senseGroupController2.getSelectedSource();
            var targetSense1 = this.senseGroupController1.getSelectedTarget();
            var targetSense2 = this.senseGroupController2.getSelectedTarget();
            var word1 = this.senseGroupController1.getSelectedOrthForm();
            var word2 = this.senseGroupController2.getSelectedOrthForm();

            this.similarityModel.calculateSemanticSimilarity(
                    sourceSense1, sourceSense2,
                    targetSense1, targetSense2,
                    this.candidateController.getSelectedSemAlg());
            this.similarityModel.calculatePhoneticSimilarity(
                    word1, word2,
                    this.candidateController.getSelectedPhonAlg());
        } else {
            this.similarityModel.clearSimilarity();
        }
    }

    private boolean everyFieldHasSelection() {
        return this.senseGroupController1.hasSelections() && this.senseGroupController1.isReadyForSimilarityCalculations()
                && this.senseGroupController2.hasSelections() && this.senseGroupController2.isReadyForSimilarityCalculations();
    }

    public List<Button> getButtons() {
        return this.taskController.getButtons();
    }

    public GridPane getContainer() {
        return container;
    }

    public CorpusInstance getCorpusInstance() {
        return corpusInstance;
    }
}
