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

package at.ofai.punderstanding.puncat.component;

import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class LauncherWindow {
    private static final StringProperty userName = new SimpleStringProperty("");
    private static final StringProperty savePath = new SimpleStringProperty(
            Paths.get(System.getProperty("user.home"), "PunCAT").toFile().toString()
    );

    public static Stage buildUsernameWindow() {
        var rootPane = new VBox();
        rootPane.setPadding(new Insets(5));
        rootPane.setAlignment(Pos.CENTER);

        var rootScene = new Scene(rootPane);

        var rootStage = new Stage(StageStyle.UTILITY);
        rootStage.setTitle("PunCAT Launcher");
        rootStage.setScene(rootScene);
        rootStage.setOnCloseRequest(event -> Platform.exit());

        var usernamePane = buildUsernamePane(rootStage);
        rootPane.getChildren().add(usernamePane);

        var savepathPane = buildSavepathPane(rootStage);
        rootPane.getChildren().add(savepathPane);

        return rootStage;
    }

    private static VBox buildUsernamePane(Stage rootStage) {
        Label label = new Label("Enter your name below");
        label.setPadding(new Insets(0, 0, 5, 0));

        var usernameTextField = new TextField();
        userName.bind(usernameTextField.textProperty());
        usernameTextField.setPromptText("your name here");
        usernameTextField.setAlignment(Pos.CENTER);
        usernameTextField.setOnAction(event -> maybeClose(rootStage));

        Button okButton = new Button("OK");
        okButton.setOnAction(event -> maybeClose(rootStage));

        VBox usernamePane = new VBox();
        usernamePane.setAlignment(Pos.CENTER);
        usernamePane.setSpacing(5);
        usernamePane.getChildren().addAll(label, usernameTextField, okButton);

        return usernamePane;
    }

    private static TitledPane buildSavepathPane(Stage rootStage) {
        var savepathTextField = new TextField(savePath.get());
        savePath.bind(savepathTextField.textProperty());

        var browseSavepathButton = new Button("…");
        browseSavepathButton.setOnAction(actionEvent -> {
            var chooser = new DirectoryChooser();
            chooser.setTitle("Choose where to save logs and results");
            var savePath = chooser.showDialog(rootStage);
            savepathTextField.setText(savePath.getAbsolutePath());
        });

        var savepathControls = new HBox();
        savepathControls.getChildren().addAll(savepathTextField, browseSavepathButton);

        var savepathPane = new TitledPane("Edit save path for logs", savepathControls);
        savepathPane.setPadding(new Insets(15, 0, 0, 0));
        savepathPane.setExpanded(false);
        savepathPane.setAnimated(false);
        savepathPane.expandedProperty().addListener((observableValue, aBoolean, t1) ->
                Platform.runLater(rootStage::sizeToScene));

        return savepathPane;
    }

    private static void maybeClose(Stage rootStage) {
        if (validUserName() && validSavePath()) {
            rootStage.hide();
        } else if (!validSavePath()) {
            showPathError();
        }
    }

    private static void showPathError() {
        var alert = new Alert(Alert.AlertType.ERROR, "The provided save path is invalid.");
        alert.showAndWait();
    }

    private static boolean validUserName() {
        return !userName.get().isEmpty() && !userName.get().isBlank();
    }

    private static boolean validSavePath() {
        return Files.isWritable(Paths.get(savePath.get()));
    }

    public static String getUserName() {
        return userName.get();
    }

    public static StringProperty userNameProperty() {
        return userName;
    }

    public static String getSavePath() {
        return savePath.get();
    }

    public static StringProperty savePathProperty() {
        return savePath;
    }
}
