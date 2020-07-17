package gui;

import gui.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import logic.search.Search;

public class Main extends Application {
    // TODO: background threads

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainView.fxml"));
        GridPane root = loader.load();

        // TODO: no need for duplicate Source/Target classes if controllers are instantiated and assigned manually?

        Search semanticSearch = new Search();
        MainController mainController = loader.getController();
        mainController.setSemanticSearch(semanticSearch);

        Scene scene = new Scene(root);
        primaryStage.setTitle("PunCat");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
