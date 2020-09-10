package at.ofai.punderstanding.puncat.gui;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.json.JSONArray;

import at.ofai.punderstanding.puncat.gui.component.SplashStage;
import at.ofai.punderstanding.puncat.gui.component.UsernameWindow;
import at.ofai.punderstanding.puncat.gui.controller.MainController;
import at.ofai.punderstanding.puncat.gui.logging.InteractionLogger;
import at.ofai.punderstanding.puncat.gui.logging.LoggerValues;
import at.ofai.punderstanding.puncat.gui.model.corpus.Corpus;
import at.ofai.punderstanding.puncat.gui.model.corpus.CorpusInstance;
import at.ofai.punderstanding.puncat.logic.search.Search;
import at.ofai.punderstanding.puncat.logic.util.Consts;


public class Main extends Application {
    GridPane activePane = null;
    Stage stage;
    BorderPane rootPane = new BorderPane();
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
            this.activePane = this.mainPaneList.get(0);
            this.rootPane.setCenter(this.activePane);

            this.stage.show();

            splashStage.hide();
        });
        loader.start();
    }

    private void buildRootStage() {
        this.rootPane.setTop(createMenubar());
        Scene scene = new Scene(this.rootPane);
        scene.getStylesheets().add("/styles.css");

        this.stage.setTitle("PunCAT");
        this.stage.setScene(scene);
        this.stage.setMaximized(true);
        this.stage.getIcons().add(new Image(getClass().getResourceAsStream(Consts.icon)));
    }

    private void buildMainPane(CorpusInstance corpusInstance) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainView.fxml"));
        GridPane mainPane;
        try {
            mainPane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        mainPane.setVisible(true);

        var mc = (MainController) loader.getController();
        mc.setSearch(this.search);
        var targetPane = (GridPane) mainPane.lookup("#targetPane");
        targetPane.add(createButtonBox(), 1, 2);

        this.mainPaneList.add(mainPane);
        this.mainControllers.add(mc);

        if (corpusInstance != null) {
            mc.loadCorpusInstance(corpusInstance);
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

        this.activePane = this.mainPaneList.get(0);
        this.rootPane.setCenter(this.activePane);
    }

    private HBox createButtonBox() {
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

        return btnBox;
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
        int idx = this.mainPaneList.indexOf(this.activePane);

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.FIRST_TASK_BUTTON_CLICKED_EVENT,
                LoggerValues.PREV_TASK_IDX, idx,
                LoggerValues.NEXT_TASK_IDX, idx == 0 ? 0 : idx - 1));

        if (idx == 0) {
            return;
        }
        this.activePane = this.mainPaneList.get(0);
        this.rootPane.setCenter(this.activePane);
    }

    private void prevPane() {
        int idx = this.mainPaneList.indexOf(this.activePane);

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.PREVIOUS_TASK_BUTTON_CLICKED_EVENT,
                LoggerValues.PREV_TASK_IDX, idx,
                LoggerValues.NEXT_TASK_IDX, idx == 0 ? 0 : idx - 1));

        if (idx == 0) {
            return;
        }
        this.activePane = this.mainPaneList.get(idx - 1);
        this.rootPane.setCenter(this.activePane);
    }

    private void nextPane() {
        int idx = this.mainPaneList.indexOf(this.activePane);

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.NEXT_TASK_BUTTON_CLICKED_EVENT,
                LoggerValues.PREV_TASK_IDX, idx,
                LoggerValues.NEXT_TASK_IDX, idx == this.mainPaneList.size() - 1 ? this.mainPaneList.size() - 1 : idx + 1));

        if (idx == this.mainPaneList.size() - 1) {
            return;
        }
        this.activePane = this.mainPaneList.get(idx + 1);
        this.rootPane.setCenter(this.activePane);
    }

    private void lastPane() {
        int idx = this.mainPaneList.indexOf(this.activePane);

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.LAST_TASK_BUTTON_CLICKED_EVENT,
                LoggerValues.PREV_TASK_IDX, idx,
                LoggerValues.NEXT_TASK_IDX, idx == this.mainPaneList.size() - 1 ? this.mainPaneList.size() - 1 : idx + 1));

        if (idx == this.mainPaneList.size() - 1) {
            return;
        }
        this.activePane = this.mainPaneList.get(idx + 1);
        this.rootPane.setCenter(this.activePane);
    }

    void saveCandidates() {
        JSONArray candidateList = new JSONArray();
        for (MainController mc : this.mainControllers) {
            var candidates = mc.saveCandidates();
            candidateList.put(Map.of(
                    "task", mc.getCorpusInstanceId(),
                    "results", candidates
            ));
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
