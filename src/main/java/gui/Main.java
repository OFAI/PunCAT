package gui;

import gui.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import logic.search.Search;

public class Main extends Application {
    // TODO: background threads for searching?
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainView.fxml"));
        GridPane root = loader.load();

        Search search = new Search();
        MainController mainController = loader.getController();
        mainController.setSearch(search);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles.css");
        primaryStage.setTitle("PunCAT");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
