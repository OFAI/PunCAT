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

import java.io.InputStream;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import afester.javafx.svg.SvgLoader;


public class SplashStage extends Stage {
    private static final String splashImg = "/img/Computational_Pun-derstanding_logo.svg";
    private static final String icon = "/img/Computational_Pun-derstanding_head.png";

    public SplashStage() {
        super();
        this.getIcons().add(new Image(getClass().getResourceAsStream(icon)));
        int splashWidth = 500;
        int splashHeight = 300;

        this.setWidth(splashWidth);
        this.setHeight(splashHeight);
        this.initStyle(StageStyle.UNDECORATED);
        this.setTitle("PunCAT");

        var loadingLabel = new Label("PunCAT\nis loading…");
        loadingLabel.setTextAlignment(TextAlignment.CENTER);
        loadingLabel.setFont(new Font(22));
        StackPane.setMargin(loadingLabel, new Insets(splashHeight / 3., splashWidth / 5., 0, 0));
        StackPane.setAlignment(loadingLabel, Pos.TOP_RIGHT);

        InputStream svgFile = getClass().getResourceAsStream(splashImg);
        SvgLoader loader = new SvgLoader();
        Group svgImage = loader.loadSvg(svgFile);
        svgImage.setScaleX((splashWidth - 5) / svgImage.getBoundsInParent().getWidth());
        svgImage.setScaleY((splashHeight - 5) / svgImage.getBoundsInParent().getHeight());

        Group puncat = new Group(svgImage);
        StackPane.setAlignment(puncat, Pos.BOTTOM_CENTER);

        StackPane puncatPane = new StackPane();
        puncatPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        puncatPane.getChildren().addAll(loadingLabel, puncat);

        var scene = new Scene(puncatPane);
        this.setScene(scene);
    }
}
