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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import at.ofai.punderstanding.puncat.component.ArrowButton;


public class TaskController implements Initializable {
    @FXML
    private StackPane imageViewContainer;
    @FXML
    private Button firstButton;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button lastButton;
    @FXML
    private GridPane container;
    @FXML
    private TextFlow keywordTextFlow;
    @FXML
    private ImageView imageView;
    @FXML
    private ScrollPane quoteScrollPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArrowButton.setArrows(firstButton, FontAwesome.Glyph.ANGLE_DOUBLE_LEFT);
        ArrowButton.setArrows(prevButton, FontAwesome.Glyph.ANGLE_LEFT);
        ArrowButton.setArrows(nextButton, FontAwesome.Glyph.ANGLE_RIGHT);
        ArrowButton.setArrows(lastButton, FontAwesome.Glyph.ANGLE_DOUBLE_RIGHT);

        this.keywordTextFlow.prefWidthProperty().bind(this.container.widthProperty());

        this.imageViewContainer.maxWidth(100);

        this.quoteScrollPane.setFitToWidth(true);
    }

    private void setButtonLayout(Button button, Glyph glyph) {
        button.setContentDisplay(ContentDisplay.CENTER);
        glyph.setFontSize(18);
        button.setGraphic(glyph);
        button.setPadding(new Insets(2));
        button.prefWidthProperty().bind(button.heightProperty().add(2));
    }

    public List<Button> getButtons() {
        return List.of(this.firstButton, this.prevButton, this.nextButton, this.lastButton);
    }

    public void insertImage(Image image, Stage stage) {
        imageViewContainer.setAlignment(Pos.CENTER);
        imageView.fitWidthProperty().bind(stage.widthProperty().multiply(0.2));
        this.imageView.setImage(image);
    }

    public void insertQuote(TextFlow imageText) {
        this.quoteScrollPane.setContent(imageText);
    }

    public void insertKeywords(Text keywords) {
        this.keywordTextFlow.getChildren().add(keywords);
    }
}
