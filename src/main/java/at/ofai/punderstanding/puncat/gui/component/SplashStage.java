package at.ofai.punderstanding.puncat.gui.component;

import java.io.InputStream;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import afester.javafx.svg.SvgLoader;

import at.ofai.punderstanding.puncat.logic.util.Consts;


public class SplashStage extends Stage {
    public SplashStage() {
        super();
        int splashWidth = 500;
        int splashHeight = 300;

        this.setWidth(splashWidth);
        this.setHeight(splashHeight);
        this.initStyle(StageStyle.UNDECORATED);

        var loadingLabel = new Label("Loading…");
        loadingLabel.setFont(new Font(22));
        StackPane.setMargin(loadingLabel, new Insets(splashHeight/3., splashWidth/5., 0, 0));
        StackPane.setAlignment(loadingLabel, Pos.TOP_RIGHT);

        InputStream svgFile = getClass().getResourceAsStream(Consts.splashImg);
        SvgLoader loader = new SvgLoader();
        Group svgImage = loader.loadSvg(svgFile);
        svgImage.setScaleX((splashWidth-5)/svgImage.getBoundsInParent().getWidth());
        svgImage.setScaleY((splashHeight-5)/svgImage.getBoundsInParent().getHeight());

        Group puncat = new Group(svgImage);
        StackPane.setAlignment(puncat, Pos.BOTTOM_CENTER);

        StackPane puncatPane = new StackPane();
        puncatPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        puncatPane.getChildren().addAll(loadingLabel, puncat);

        var scene = new Scene(puncatPane);
        this.setScene(scene);
    }
}
