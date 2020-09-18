package at.ofai.punderstanding.puncat.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFontRegistry;


public class TaskController implements Initializable {
    @FXML
    public Button firstButton;
    @FXML
    public Button prevButton;
    @FXML
    public Button nextButton;
    @FXML
    public Button lastButton;
    @FXML
    public GridPane container;
    @FXML
    private ImageView imageView;
    @FXML
    private ScrollPane quotePane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GridPane.setValignment(this.container, VPos.BOTTOM);
        var fontAwesome = GlyphFontRegistry.font("FontAwesome");
        this.setButtonLayout(firstButton, fontAwesome.create(FontAwesome.Glyph.ANGLE_DOUBLE_LEFT));
        this.setButtonLayout(prevButton, fontAwesome.create(FontAwesome.Glyph.ANGLE_LEFT));
        this.setButtonLayout(nextButton, fontAwesome.create(FontAwesome.Glyph.ANGLE_RIGHT));
        this.setButtonLayout(lastButton, fontAwesome.create(FontAwesome.Glyph.ANGLE_DOUBLE_RIGHT));
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

    public void insertImage(Image image) {
        this.imageView.fitWidthProperty().bind(this.container.widthProperty());
        this.imageView.setImage(image);
    }

    public void insertQuote(TextFlow imageText) {
        // TODO: proper centering
        var imgBox = new VBox();
        imgBox.setAlignment(Pos.CENTER);
        imgBox.prefHeightProperty().bind(this.quotePane.heightProperty().subtract(5));
        imgBox.getChildren().add(imageText);
        this.quotePane.setContent(imgBox);
        this.quotePane.setFitToWidth(true);
        this.quotePane.maxHeightProperty().bind(imgBox.heightProperty().add(5));
    }

    public void insertKeywords(TextFlow keywordFlow) {
        this.container.add(keywordFlow, 0, 2);
    }
}
