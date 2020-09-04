package at.ofai.punderstanding.puncat.gui;

import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import at.ofai.punderstanding.puncat.gui.component.SplashStage;
import at.ofai.punderstanding.puncat.gui.controller.MainController;
import at.ofai.punderstanding.puncat.gui.model.CorpusInstance.Corpus;
import at.ofai.punderstanding.puncat.gui.model.CorpusInstance.CorpusInstance;
import at.ofai.punderstanding.puncat.logic.search.Search;


public class Main extends Application {
    GridPane activePane = null;
    Stage mainStage;
    StackPane rootPane = new StackPane();
    ArrayList<GridPane> mainPanes = new ArrayList<>();
    private Search search;

    @Override
    public void start(Stage stage) {
        mainStage = stage;
        var splashStage = new SplashStage();
        splashStage.show();

        var loader = new LoaderClass();
        loader.setOnSucceeded(t -> {
            search = (Search) t.getSource().getValue();
            buildMainStage(search);
            this.mainPanes.get(0).setVisible(true);
            this.activePane = this.mainPanes.get(0);
            mainStage.show();
            splashStage.hide();
        });
        loader.start();
    }

    private void buildMainStage(Search search) {
        Scene scene = new Scene(rootPane);
        scene.getStylesheets().add("/styles.css");

        mainStage.setTitle("PunCAT");
        mainStage.setScene(scene);
        mainStage.setMaximized(true);
        mainStage.getIcons().add(new Image(getClass().getResourceAsStream("/img/message-square.png")));


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

        Corpus corpus = Corpus.parseCorpus();
        for (int i = 0; i < corpus.size(); i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainView.fxml"));
            GridPane mainPane;
            try {
                mainPane = loader.load();
            } catch (IOException e) {
                throw new RuntimeException();
            }
            var mc = ((MainController) loader.getController());
            mc.setPageButtons(btnBox);
            mc.setSearch(search);
            //mc.loadCorpusInstance(corpus.getModel(i));

            rootPane.getChildren().add(mainPane);
            this.mainPanes.add(mainPane);
        }
    }

    static class LoaderClass extends Service<Search> {
        @Override
        protected Task<Search> createTask() {
            return new Task<>() {
                @Override
                protected Search call() {
                    return new Search();
                }
            };
        }
    }

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
        this.activePane = this.mainPanes.get(idx - 1);
        this.activePane.setVisible(true);
    }

    private void nextPane() {
        int idx = this.mainPanes.indexOf(this.activePane);
        if (idx == this.mainPanes.size() - 1) {
            return;
        }
        this.activePane.setVisible(false);
        this.activePane = this.mainPanes.get(idx + 1);
        this.activePane.setVisible(true);
    }

    private void lastPane() {
        int idx = this.mainPanes.indexOf(this.activePane);
        if (idx == this.mainPanes.size() - 1) {
            return;
        }
        this.activePane.setVisible(false);
        this.activePane = this.mainPanes.get(this.mainPanes.size() - 1);
        this.activePane.setVisible(true);
    }

    public static void main(String[] args) {
        launch();
    }
}
