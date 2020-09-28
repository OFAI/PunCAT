package at.ofai.punderstanding.puncat.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFontRegistry;


public class TaskController implements Initializable {
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
        var fontAwesome = GlyphFontRegistry.font("FontAwesome");
        this.setButtonLayout(firstButton, fontAwesome.create(FontAwesome.Glyph.ANGLE_DOUBLE_LEFT));
        this.setButtonLayout(prevButton, fontAwesome.create(FontAwesome.Glyph.ANGLE_LEFT));
        this.setButtonLayout(nextButton, fontAwesome.create(FontAwesome.Glyph.ANGLE_RIGHT));
        this.setButtonLayout(lastButton, fontAwesome.create(FontAwesome.Glyph.ANGLE_DOUBLE_RIGHT));

        this.keywordTextFlow.prefWidthProperty().bind(this.container.widthProperty());

        this.imageView.fitWidthProperty().bind(this.container.widthProperty());

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

    public void insertImage(Image image) {
        this.imageView.setImage(image);
    }

    public void insertQuote(TextFlow imageText) {
        this.quoteScrollPane.setContent(imageText);
    }

    public void insertKeywords(Text keywords) {
        this.keywordTextFlow.getChildren().add(keywords);
    }
}
