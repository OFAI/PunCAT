/*
  Copyright 2020 Máté Lajkó

  This file is part of PunCAT.

  PunCAT is free software: you can redistribute it and/or modify it
  under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PunCAT is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with PunCAT.  If not, see <https://www.gnu.org/licenses/>.
 */

package at.ofai.punderstanding.puncat;

import java.io.File;
import java.io.FileWriter;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.opencsv.CSVWriter;
import org.json.JSONArray;

import at.ofai.punderstanding.puncat.component.LauncherWindow;
import at.ofai.punderstanding.puncat.component.SplashStage;
import at.ofai.punderstanding.puncat.controller.MainController;
import at.ofai.punderstanding.puncat.logging.InteractionLogger;
import at.ofai.punderstanding.puncat.logging.LoggerValues;
import at.ofai.punderstanding.puncat.logic.ResourcePaths;
import at.ofai.punderstanding.puncat.logic.Search;
import at.ofai.punderstanding.puncat.model.corpus.Corpus;
import at.ofai.punderstanding.puncat.model.corpus.CorpusInstance;


public class Main extends Application {
    private static final String icon = "/img/Computational_Pun-derstanding_head.png";
    private final ObjectProperty<GridPane> activePane = new SimpleObjectProperty<>();
    private final BorderPane rootPane = new BorderPane();
    private final StackPane mainViewContainer = new StackPane();
    private final ArrayList<GridPane> mainPaneList = new ArrayList<>();
    private final ArrayList<MainController> mainControllers = new ArrayList<>();
    private String savePath;
    private InteractionLogger interactionLogger;
    private Stage stage;
    private String userName = "";
    private Long startupInstant;
    private Search search;

    public static void main(String[] args) {
        if (args.length == 1) {
            ResourcePaths.resourcePath = args[0];
        } else if (args.length > 1) {
            throw new RuntimeException("Unexpected number of program arguments");
        }
        ResourcePaths.init();
        launch();
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("PunCAT");
        this.stage.setMaximized(true);
        this.stage.getIcons().add(new Image(getClass().getResourceAsStream(icon)));
        this.stage.setOnCloseRequest(event -> {
            this.saveCandidatesToJson();
            interactionLogger.logThis(Map.of(LoggerValues.EVENT, LoggerValues.PUNCAT_CLOSED_EVENT));
        });

        var launcherStage = LauncherWindow.buildLauncherWindow();
        launcherStage.setOnHidden(event -> {
            this.userName = LauncherWindow.getUserName();
            this.savePath = LauncherWindow.getSavePath();
            this.build();
        });
        launcherStage.show();
        launcherStage.requestFocus();
    }

    private void build() {
        this.startupInstant = Instant.now().toEpochMilli();
        var logFilesPath = new File(savePath, "logs");
        System.setProperty("puncatlogfilename",
                new File(logFilesPath, "log_" + this.userName + "_" + this.startupInstant).toString());

        var splashStage = new SplashStage();
        splashStage.show();

        var loader = new SearchLoader();
        loader.setOnFailed(workerStateEvent -> {
            throw new RuntimeException(loader.getException());
        });
        loader.setOnSucceeded(t -> {
            this.interactionLogger = new InteractionLogger();
            this.interactionLogger.logThis(Map.of(LoggerValues.EVENT, LoggerValues.PUNCAT_STARTED_EVENT));

            this.search = (Search) t.getSource().getValue();

            this.buildRootStage();

            this.buildMainPane(null);
            this.setActivePane(this.mainPaneList.get(0));

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
        this.stage.setScene(scene);
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
            mc.loadCorpusInstance(corpusInstance, this.stage);
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
        Corpus corpus = null;
        try {
            corpus = Corpus.parseCorpus(file);
        } catch (Exception e) {
            // This handles unrecoverable problems with the xml,
            // like when it is not well-formed or there is no text element.
            e.printStackTrace();
            var alert = new Alert(Alert.AlertType.ERROR, "The provided XML file seems invalid.");
            var result = alert.showAndWait();
            if (result.isPresent()) {
                return;
            }
        }

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.CORPUS_OPENED_EVENT,
                LoggerValues.CORPUS_FILE, file.getAbsolutePath())
        );

        assert corpus != null;
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
        if (this.activePane.get() != null) {
            this.activePane.get().setVisible(false);
        }
        this.activePane.set(mainPane);
        this.activePane.get().setVisible(true);
    }

    private MenuBar createMenubar() {
        var menuBar = new MenuBar();
        var fileMenu = new Menu("File");
        var openItem = new MenuItem("Open corpus");
        var exitItem = new MenuItem("Exit");
        var exportItem = new MenuItem("Export candidates");
        menuBar.getMenus().add(fileMenu);
        fileMenu.getItems().addAll(openItem, exportItem, exitItem);

        openItem.setOnAction(event -> {
            var fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
            fileChooser.setTitle("Select resource file");
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                parseXml(file);
            }
        });
        exportItem.setOnAction(event -> {
            var fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
            fileChooser.setTitle("Select export location");
            fileChooser.setInitialFileName("puncat_export.csv");
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                this.saveCandidatesToCsv(file);
            }
        });
        exitItem.setOnAction(event ->
                stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST))
        );
        return menuBar;
    }

    private MainController getMainPaneController(GridPane pane) {
        return this.mainControllers.get(this.mainPaneList.indexOf(pane));
    }

    private void saveCandidatesToJson() {
        JSONArray candidateList = new JSONArray();
        for (MainController mc : this.mainControllers) {
            var candidates = mc.candidatesToJSONArray();
            candidateList.put(Map.of(mc.getCorpusInstanceId(), candidates));
        }

        String fileName = "results_" + this.userName + "_" + this.startupInstant + ".json";
        File file = new File(savePath, "results");
        if (!file.exists() && !file.mkdir()) {
            throw new RuntimeException("could not create results folder");
        }
        try {
            var savePath = file.toPath().resolve(Paths.get(fileName));
            Files.writeString(savePath, candidateList.toString(4), StandardCharsets.UTF_8);
            System.out.println("results saved to: " + savePath);
        } catch (IOException e) {
            throw new RuntimeException("could not write results file", e);
        }
    }

    private void saveCandidatesToCsv(File path) {
        CSVWriter writer;
        try {
            writer = new CSVWriter(new FileWriter(path));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't open file for writing: " + path, e);
        }
        writer.writeNext(new String[]{"instanceId", "instanceText", "pun", "target", "phon", "sem", "phonAlg", "semAlg"});
        for (MainController mc : this.mainControllers) {
            var instanceId = mc.getCorpusInstanceId();
            var candidates = mc.getCandidates();
            for (var candidate : candidates) {
                var corpusInstance = mc.getCorpusInstance();
                String instanceText = "";
                if (corpusInstance != null) {
                    instanceText = corpusInstance.getText().toString();
                }
                String[] values = new String[]{
                        instanceId, instanceText,
                        candidate.getPun(), candidate.getTarget(),
                        candidate.getPhon(), candidate.getSem(),
                        candidate.getPhonAlg(), candidate.getSemAlg()
                };
                writer.writeNext(values);
            }

        }

        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.CSV_EXPORT_EVENT,
                LoggerValues.CSV_PATH, path.toPath().toString().replace("\\", "/")
        ));
    }

    private void firstPane() {
        int idx = this.mainPaneList.indexOf(this.activePane.get());

        if (idx == 0) {
            return;
        }
        this.activePane.get().setVisible(false);
        this.activePane.set(this.mainPaneList.get(0));
        this.activePane.get().setVisible(true);

        var prevController = this.mainControllers.get(idx);
        var currentController = this.getMainPaneController(this.activePane.get());

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.FIRST_TASK_BUTTON_CLICKED_EVENT,
                LoggerValues.PREV_INSTANCE_ID, prevController.getCorpusInstanceId(),
                LoggerValues.CURRENT_INSTANCE_ID, currentController.getCorpusInstanceId()));
    }

    private void prevPane() {
        int idx = this.mainPaneList.indexOf(this.activePane.get());

        if (idx == 0) {
            return;
        }
        this.activePane.get().setVisible(false);
        this.activePane.set(this.mainPaneList.get(idx - 1));
        this.activePane.get().setVisible(true);

        var prevController = this.mainControllers.get(idx);
        var currentController = this.getMainPaneController(this.activePane.get());

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.PREVIOUS_TASK_BUTTON_CLICKED_EVENT,
                LoggerValues.PREV_INSTANCE_ID, prevController.getCorpusInstanceId(),
                LoggerValues.CURRENT_INSTANCE_ID, currentController.getCorpusInstanceId()));
    }

    private void nextPane() {
        int idx = this.mainPaneList.indexOf(this.activePane.get());

        if (idx == this.mainPaneList.size() - 1) {
            return;
        }
        this.activePane.get().setVisible(false);
        this.activePane.set(this.mainPaneList.get(idx + 1));
        this.activePane.get().setVisible(true);

        var prevController = this.mainControllers.get(idx);
        var currentController = this.getMainPaneController(this.activePane.get());

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.NEXT_TASK_BUTTON_CLICKED_EVENT,
                LoggerValues.PREV_INSTANCE_ID, prevController.getCorpusInstanceId(),
                LoggerValues.CURRENT_INSTANCE_ID, currentController.getCorpusInstanceId()));
    }

    private void lastPane() {
        int idx = this.mainPaneList.indexOf(this.activePane.get());

        if (idx == this.mainPaneList.size() - 1) {
            return;
        }
        this.activePane.get().setVisible(false);
        this.activePane.set(this.mainPaneList.get(this.mainPaneList.size() - 1));
        this.activePane.get().setVisible(true);

        var prevController = this.mainControllers.get(idx);
        var currentController = this.getMainPaneController(this.activePane.get());

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.LAST_TASK_BUTTON_CLICKED_EVENT,
                LoggerValues.PREV_INSTANCE_ID, prevController.getCorpusInstanceId(),
                LoggerValues.CURRENT_INSTANCE_ID, currentController.getCorpusInstanceId()));
    }

    static class SearchLoader extends Service<Search> {
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
