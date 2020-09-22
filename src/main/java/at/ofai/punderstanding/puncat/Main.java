package at.ofai.punderstanding.puncat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.json.JSONArray;

import at.ofai.punderstanding.puncat.component.SplashStage;
import at.ofai.punderstanding.puncat.component.UsernameWindow;
import at.ofai.punderstanding.puncat.controller.MainController;
import at.ofai.punderstanding.puncat.logging.InteractionLogger;
import at.ofai.punderstanding.puncat.logging.LoggerValues;
import at.ofai.punderstanding.puncat.logic.Search;
import at.ofai.punderstanding.puncat.model.corpus.Corpus;
import at.ofai.punderstanding.puncat.model.corpus.CorpusInstance;


public class Main extends Application {
    private static final String icon = "/img/Computational_Pun-derstanding_head.png";
    //GridPane activePane = null;
    ObjectProperty<GridPane> activePane = new SimpleObjectProperty<>();
    Stage stage;
    BorderPane rootPane = new BorderPane();
    StackPane mainViewContainer = new StackPane();
    ArrayList<GridPane> mainPaneList = new ArrayList<>();
    ArrayList<MainController> mainControllers = new ArrayList<>();
    String userName = "";
    Long startupInstant = 0L;
    InteractionLogger interactionLogger;
    private Search search;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.stage.setOnCloseRequest(event -> {
            this.saveCandidates();
            interactionLogger.logThis(Map.of(LoggerValues.EVENT, LoggerValues.PUNCAT_CLOSED_EVENT));
        });

        var userNameStage = UsernameWindow.buildUsernameWindow();
        userNameStage.setOnHidden(event -> {
            userName = UsernameWindow.getUserName();
            this.build();
        });
        userNameStage.show();
    }

    private void build() {
        this.startupInstant = Instant.now().toEpochMilli();
        System.setProperty("puncatlogfilename",
                "log_" + this.userName + "_" + this.startupInstant);

        var splashStage = new SplashStage();
        splashStage.show();

        var loader = new LoaderClass();
        loader.setOnSucceeded(t -> {
            interactionLogger = new InteractionLogger();
            interactionLogger.logThis(Map.of(LoggerValues.EVENT, LoggerValues.PUNCAT_STARTED_EVENT));

            this.search = (Search) t.getSource().getValue();

            this.buildRootStage();

            this.buildMainPane(null);
            this.activePane.set(this.mainPaneList.get(0));
            this.activePane.get().setVisible(true);

            this.stage.show();

            splashStage.hide();
        });
        loader.start();
    }

    private void buildRootStage() {
        this.rootPane.setTop(createMenubar());
        this.rootPane.setCenter(mainViewContainer);
        Scene scene = new Scene(this.rootPane);
        scene.getStylesheets().add("/styles.css");

        this.stage.setTitle("PunCAT");
        this.stage.setScene(scene);
        this.stage.setMaximized(true);
        this.stage.getIcons().add(new Image(getClass().getResourceAsStream(icon)));
    }

    private void buildMainPane(CorpusInstance corpusInstance) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainView.fxml"));
        GridPane mainPane;
        try {
            mainPane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mainPane.setVisible(false);

        var mc = (MainController) loader.getController();
        mc.setSearch(this.search);

        this.mainPaneList.add(mainPane);
        this.mainViewContainer.getChildren().add(mainPane);
        this.mainControllers.add(mc);

        if (corpusInstance != null) {
            mc.loadCorpusInstance(corpusInstance);
            var buttons = mc.getButtons();
            buttons.get(0).setOnAction(event -> this.firstPane());
            buttons.get(1).setOnAction(event -> this.prevPane());
            buttons.get(2).setOnAction(event -> this.nextPane());
            buttons.get(3).setOnAction(event -> this.lastPane());
            this.activePane.addListener((observable, oldValue, newValue) -> {
                buttons.get(0).setDisable(this.mainPaneList.indexOf(newValue) == 0);
                buttons.get(1).setDisable(this.mainPaneList.indexOf(newValue) == 0);
                buttons.get(2).setDisable(this.mainPaneList.indexOf(newValue) == this.mainPaneList.size() - 1);
                buttons.get(3).setDisable(this.mainPaneList.indexOf(newValue) == this.mainPaneList.size() - 1);
            });
        }
    }

    private void parseXml(File file) {
        Corpus corpus = Corpus.parseCorpus(file);
        if (corpus.size() == 0) {
            return;
        }
        this.rootPane.getChildren().removeAll(this.mainPaneList);
        this.mainPaneList.clear();
        this.mainControllers.clear();
        for (CorpusInstance ci : corpus.getInstances()) {
            buildMainPane(ci);
        }

        this.setActivePane(this.mainPaneList.get(0));
    }

    private void setActivePane(GridPane mainPane) {
        this.activePane.get().setVisible(false);
        this.activePane.set(mainPane);
        this.activePane.get().setVisible(true);
    }

    private MenuBar createMenubar() {
        var menuBar = new MenuBar();
        var fileMenu = new Menu("File");
        var openItem = new MenuItem("Open corpus");
        menuBar.getMenus().add(fileMenu);
        fileMenu.getItems().add(openItem);

        openItem.setOnAction(event -> {
            File file = new FileChooser().showOpenDialog(stage);
            if (file != null) {
                parseXml(file);
            }
        });
        return menuBar;
    }

    private void firstPane() {
        int idx = this.mainPaneList.indexOf(this.activePane.get());

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.FIRST_TASK_BUTTON_CLICKED_EVENT,
                LoggerValues.PREV_TASK_IDX, idx,
                LoggerValues.NEXT_TASK_IDX, idx == 0 ? 0 : idx - 1));

        if (idx == 0) {
            return;
        }
        this.activePane.get().setVisible(false);
        this.activePane.set(this.mainPaneList.get(0));
        this.activePane.get().setVisible(true);
    }

    private void prevPane() {
        int idx = this.mainPaneList.indexOf(this.activePane.get());

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.PREVIOUS_TASK_BUTTON_CLICKED_EVENT,
                LoggerValues.PREV_TASK_IDX, idx,
                LoggerValues.NEXT_TASK_IDX, idx == 0 ? 0 : idx - 1));

        if (idx == 0) {
            return;
        }
        this.activePane.get().setVisible(false);
        this.activePane.set(this.mainPaneList.get(idx - 1));
        this.activePane.get().setVisible(true);
    }

    private void nextPane() {
        int idx = this.mainPaneList.indexOf(this.activePane.get());

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.NEXT_TASK_BUTTON_CLICKED_EVENT,
                LoggerValues.PREV_TASK_IDX, idx,
                LoggerValues.NEXT_TASK_IDX, idx == this.mainPaneList.size() - 1 ? this.mainPaneList.size() - 1 : idx + 1));

        if (idx == this.mainPaneList.size() - 1) {
            return;
        }
        this.activePane.get().setVisible(false);
        this.activePane.set(this.mainPaneList.get(idx + 1));
        this.activePane.get().setVisible(true);
    }

    private void lastPane() {
        int idx = this.mainPaneList.indexOf(this.activePane.get());

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.LAST_TASK_BUTTON_CLICKED_EVENT,
                LoggerValues.PREV_TASK_IDX, idx,
                LoggerValues.NEXT_TASK_IDX, idx == this.mainPaneList.size() - 1 ? this.mainPaneList.size() - 1 : idx + 1));

        if (idx == this.mainPaneList.size() - 1) {
            return;
        }
        this.activePane.get().setVisible(false);
        this.activePane.set(this.mainPaneList.get(this.mainPaneList.size() - 1));
        this.activePane.get().setVisible(true);
    }

    void saveCandidates() {
        JSONArray candidateList = new JSONArray();
        for (MainController mc : this.mainControllers) {
            var candidates = mc.saveCandidates();
            candidateList.put(Map.of(mc.getCorpusInstanceId(), candidates));
        }

        String fileName = "results_" + this.userName + "_" + this.startupInstant + ".json";
        File file = new File(System.getProperty("user.dir"), "results");
        if (!file.exists() && !file.mkdir()) {
            throw new RuntimeException("could not create results folder");
        }
        try {
            Files.writeString(file.toPath().resolve(Paths.get(fileName)), candidateList.toString(4), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("could not write results file");
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
}
