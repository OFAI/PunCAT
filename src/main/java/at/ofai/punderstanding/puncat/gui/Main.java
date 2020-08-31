package at.ofai.punderstanding.puncat.gui;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import at.ofai.punderstanding.puncat.gui.controller.MainController;
import at.ofai.punderstanding.puncat.logic.search.Search;

public class Main extends Application {
    GridPane activePane = null;
    List<GridPane> mainPanes = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane rootPane = new StackPane();
        Scene scene = new Scene(rootPane);
        scene.getStylesheets().add("/styles.css");
        primaryStage.setTitle("PunCAT");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/img/message-square.png")));

        Search search = new Search();
        for (int i = 0; i < 2; i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainView.fxml"));
            GridPane mainPane = loader.load();
            mainPane.setVisible(false);

            GridPane targetPane = (GridPane) mainPane.lookup("#targetPane");
            /*
            Button first = new Button("", loadButtonImage(Consts.firstBtn));
            Button prev = new Button("", loadButtonImage(Consts.prevBtn));
            Button next = new Button("", loadButtonImage(Consts.nextBtn));
            Button last = new Button("", loadButtonImage(Consts.lastBtn));
             */
            Button first = new Button("<<");
            first.setOnAction(event -> this.firstPane());
            first.setTooltip(new Tooltip("jump to the first translation task"));  // TODO: ui strings to Consts?
            first.setStyle("-fx-font-size: 15px");

            Button prev = new Button("<");
            prev.setOnAction(event -> this.prevPane());
            prev.setTooltip(new Tooltip("step to the previous translation task"));
            prev.setStyle("-fx-font-size: 15px");

            Button next = new Button(">");
            next.setOnAction(event -> this.nextPane());
            next.setTooltip(new Tooltip("step to the next translation task"));
            next.setStyle("-fx-font-size: 15px");

            Button last = new Button(">>");
            last.setOnAction(event -> this.lastPane());
            last.setTooltip(new Tooltip("jump to the last translation task"));  // TODO: tooltip size, look, delay
            last.setStyle("-fx-font-size: 15px");

            var btnBox = new HBox();
            btnBox.setSpacing(5);
            btnBox.getChildren().addAll(first, prev, next, last);
            btnBox.setAlignment(Pos.BOTTOM_RIGHT);
            targetPane.add(btnBox, 1, 2);

            ((MainController) loader.getController()) .setSearch(search);
            rootPane.getChildren().add(mainPane);
            this.mainPanes.add(mainPane);
        }
        this.mainPanes.get(0).setVisible(true);
        this.activePane = this.mainPanes.get(0);

        primaryStage.show();
    }

    /*
    private ImageView loadButtonImage(String path) {
        var img = new Image(String.valueOf(getClass().getResource(path)));
        var imgNode = new ImageView(img);
        imgNode.setFitHeight(22);
        imgNode.setPreserveRatio(true);
        imgNode.setSmooth(true);
        imgNode.setCache(true);
        return imgNode;
    }
     */

    private void firstPane() {
        int idx = this.mainPanes.indexOf(this.activePane);
        if (idx == 0) {
            return;
        }
        this.activePane.setVisible(false);
        this.activePane = this.mainPanes.get(0);
        this.activePane.setVisible(true);

    }

    private void prevPane() {
        int idx = this.mainPanes.indexOf(this.activePane);
        if (idx == 0) {
            return;
        }
        this.activePane.setVisible(false);
        this.activePane = this.mainPanes.get(idx-1);
        this.activePane.setVisible(true);
    }

    private void nextPane() {
        int idx = this.mainPanes.indexOf(this.activePane);
        if (idx == this.mainPanes.size()-1) {
            return;
        }
        this.activePane.setVisible(false);
        this.activePane = this.mainPanes.get(idx+1);
        this.activePane.setVisible(true);
    }

    private void lastPane() {
        int idx = this.mainPanes.indexOf(this.activePane);
        if (idx == this.mainPanes.size()-1) {
            return;
        }
        this.activePane.setVisible(false);
        this.activePane = this.mainPanes.get(this.mainPanes.size()-1);
        this.activePane.setVisible(true);
    }

    public static void main(String[] args) {
        launch();
    }
}
