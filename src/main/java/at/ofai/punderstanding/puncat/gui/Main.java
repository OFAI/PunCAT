package at.ofai.punderstanding.puncat.gui;

import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import at.ofai.punderstanding.puncat.gui.component.SplashStage;
import at.ofai.punderstanding.puncat.gui.controller.MainController;
import at.ofai.punderstanding.puncat.logic.search.Search;


public class Main extends Application {
    GridPane activePane = null;
    Stage mainStage;
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
        StackPane rootPane = new StackPane();
        Scene scene = new Scene(rootPane);
        scene.getStylesheets().add("/styles.css");

        mainStage.setTitle("PunCAT");
        mainStage.setScene(scene);
        mainStage.setMaximized(true);
        mainStage.getIcons().add(new Image(getClass().getResourceAsStream("/img/message-square.png")));

        for (int i = 0; i < 2; i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainView.fxml"));
            GridPane mainPane;
            try {
                mainPane = loader.load();
            } catch (IOException e) {
                throw new RuntimeException();
            }
            ((MainController) loader.getController()).setSearch(search);
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
