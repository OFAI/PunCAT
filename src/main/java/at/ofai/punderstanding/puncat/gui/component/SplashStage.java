package at.ofai.punderstanding.puncat.gui.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
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
        int splashWidth = 500;
        int splashHeight = 300;

        this.setWidth(splashWidth);
        this.setHeight(splashHeight);
        this.initStyle(StageStyle.UNDECORATED);

        ImageView puncatView = new ImageView(new Image(String.valueOf(getClass().getResource(Consts.splashImg))));
        puncatView.setPreserveRatio(true);
        puncatView.setFitWidth(splashWidth);
        puncatView.setFitHeight(splashHeight);
        StackPane.setAlignment(puncatView, Pos.BOTTOM_CENTER);

        var loadingLabel = new Label("Loading…");
        loadingLabel.setFont(new Font(13));
        StackPane.setMargin(loadingLabel, new Insets(0, 75, 0, 0));
        StackPane.setAlignment(loadingLabel, Pos.CENTER_RIGHT);

        StackPane puncatPane = new StackPane();
        puncatPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        puncatPane.getChildren().addAll(puncatView, loadingLabel);

        var scene = new Scene(puncatPane);
        this.setScene(scene);
    }
}
