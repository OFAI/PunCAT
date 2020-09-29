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

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class UsernameWindow {
    private static String userName = "";

    public static Stage buildUsernameWindow() {
        BorderPane userNamePane = new BorderPane();
        userNamePane.setPadding(new Insets(5));

        Scene userNameScene = new Scene(userNamePane, 200, 100);

        Stage userNameStage = new Stage(StageStyle.UTILITY);
        userNameStage.setTitle("PunCAT");
        userNameStage.setAlwaysOnTop(true);
        userNameStage.setScene(userNameScene);
        userNameStage.setOnCloseRequest(event -> Platform.exit());

        Label label = new Label("Enter your name below.");
        label.setPadding(new Insets(0, 0, 5, 0));

        TextField textField = new TextField();
        textField.setPromptText("your name here");
        textField.setAlignment(Pos.CENTER);
        textField.setOnAction(event -> {
            if (!textField.getText().equals("")) {
                userName = textField.getText();
                userNameStage.hide();
            }
        });

        Button okButton = new Button("OK");
        okButton.setOnAction(event -> {
            if (!textField.getText().equals("")) {
                userName = textField.getText();
                userNameStage.hide();
            }
        });
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setSpacing(5);
        container.getChildren().addAll(label, textField, okButton);
        userNamePane.setCenter(container);

        return userNameStage;
    }

    public static String getUserName() {
        return userName;
    }
}
