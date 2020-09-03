package at.ofai.punderstanding.puncat.gui.component;

import java.util.Collections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import at.ofai.punderstanding.puncat.logic.util.Consts;


public class SplashStage extends Stage {
    public SplashStage() {
        super();
        int splashWidth = 750;
        int splashHeight = 440;

        this.setWidth(splashWidth);
        this.setHeight(splashHeight);
        this.initStyle(StageStyle.UNDECORATED);

        var loadingLabel = new Label("Loading…");
        loadingLabel.setFont(new Font(18));
        StackPane.setMargin(loadingLabel, new Insets(0, 200, 0, 0));
        StackPane.setAlignment(loadingLabel, Pos.CENTER_RIGHT);

        var puncatImg = new Image(String.valueOf(getClass().getResource(Consts.splashImg)));
        var puncatBackground = new BackgroundImage(puncatImg,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(0, 0, false, false, true, true));

        StackPane puncatPane = new StackPane();
        puncatPane.setBackground(new Background(
                Collections.singletonList(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)),
                Collections.singletonList(puncatBackground)));
        puncatPane.getChildren().addAll(loadingLabel);

        var scene = new Scene(puncatPane);
        this.setScene(scene);
    }
}
