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

package at.ofai.punderstanding.puncat.component.graph;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Group;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import com.google.common.collect.BiMap;

import at.ofai.punderstanding.puncat.logging.InteractionLogger;
import at.ofai.punderstanding.puncat.logging.LoggerValues;


public class Node extends Group {
    private static final double minRadiusX = 25;
    private static final double minRadiusY = 17;
    private static final double labelPadding = 10;
    private final LabelWrapper labelWrapper;
    private final StringProperty selectedLineId = new SimpleStringProperty();
    private final InteractionLogger interactionLogger;
    Instant scrollStartRegistered = Instant.now();
    private final Ellipse ellipse;

    public Node(BiMap<Integer, String> text, DoubleProperty x, DoubleProperty y, boolean isRoot) {
        this.interactionLogger = new InteractionLogger();

        this.ellipse = new Ellipse(0, 0);
        ellipse.centerXProperty().bind(x);
        ellipse.centerYProperty().bind(y);


        this.labelWrapper = new LabelWrapper(text, ellipse.centerXProperty(), ellipse.centerYProperty(), isRoot);
        double radiusX = Math.max(labelWrapper.getWidth() / 2 + labelPadding+10, minRadiusX);
        double radiusY = Math.max(labelWrapper.getHeight() / 2 + labelPadding, minRadiusY);
        ellipse.setRadiusX(radiusX);
        ellipse.setRadiusY(radiusY);

        ellipse.setStroke(Color.CORNFLOWERBLUE);
        ellipse.setFill(Color.WHITE);
        ellipse.setStrokeWidth(2); // TODO: styles into stylesheet

        this.getChildren().addAll(ellipse, labelWrapper.getLabel());

        if (isRoot) {
            this.enableScrollListener();
            this.labelWrapper.setupInteractions();
            this.selectedLineId.bind(this.labelWrapper.activeLineIdProperty());
        } else {
            this.setOnMouseEntered(event -> {
                this.toFront();
                this.ellipse.setStroke(Color.web("#ebba34"));
                interactionLogger.logThis(Map.of(
                        LoggerValues.EVENT, LoggerValues.GRAPH_NODE_HOVERED_EVENT,
                        LoggerValues.NODE_SYNSET_ID, this.getId()));
            });
            this.setOnMouseExited(event -> this.ellipse.setStroke(Color.CORNFLOWERBLUE));
            this.setOnMouseClicked(event -> {
                interactionLogger.logThis(Map.of(
                        LoggerValues.EVENT, LoggerValues.GRAPH_NODE_CLICKED_EVENT,
                        LoggerValues.NODE_SYNSET_ID, this.getId()));

                // TODO: find a nicer way to do this
                ((Graph) this.getParent().getParent()).nodeSelected(this);
            });
        }
    }

    public void setSelectedLine(String text) {
        this.labelWrapper.setSelectedLine(text);
    }

    public double getCenterX() {
        return this.ellipse.getCenterX();
    }

    public double getCenterY() {
        return this.ellipse.getCenterY();
    }


    @SuppressWarnings({"unused", "RedundantSuppression"})
    private Rectangle getLabelBoundingBox() {
        Rectangle r = new Rectangle();
        r.xProperty().bind(this.labelWrapper.getLabel().layoutXProperty());
        r.yProperty().bind(this.labelWrapper.getLabel().layoutYProperty());
        r.widthProperty().bind(this.labelWrapper.getLabel().widthProperty());
        r.heightProperty().bind(this.labelWrapper.getLabel().heightProperty());

        r.setOpacity(0.5);
        r.setFill(Color.TRANSPARENT);
        r.setStroke(Color.BLACK);
        return r;
    }

    public void enableScrollListener() {
        this.setOnScroll(event -> {
            if (Instant.now().minusMillis(500L).isAfter(scrollStartRegistered)) {
                scrollStartRegistered = Instant.now();
                this.labelWrapper.scrollActiveLine(event);
            }
        });
    }

    public String getSelectedLineId() {
        return selectedLineId.get();
    }

    public StringProperty selectedLineIdProperty() {
        return selectedLineId;
    }

    static class LabelWrapper {
        private static final InteractionLogger interactionLogger = new InteractionLogger();
        private final TextFlow labelFlow = new TextFlow();
        private final StringProperty activeLineId = new SimpleStringProperty();
        private ArrayList<Text> labelParts = new ArrayList<>();
        private Text activeLine = null;

        LabelWrapper(BiMap<Integer, String> text, DoubleProperty x, DoubleProperty y, boolean isRoot) {
            var normalFont = Font.font(Font.getDefault().getName(), FontWeight.NORMAL, Font.getDefault().getSize());
            var boldFont = Font.font(Font.getDefault().getName(), FontWeight.BOLD, Font.getDefault().getSize());
            for (Integer key : text.keySet()) {
                Text line = new Text(text.get(key));
                line.setId(key.toString());
                line.setStyle("-fx-font-weight: bold");
                line.setFont(boldFont);
                line.setTextAlignment(TextAlignment.CENTER);
                labelParts.add(line);
            }
            Text firstBeforeSort = this.labelParts.get(0);
            this.labelParts = LabelWrapper.sortLines(this.labelParts);
            for (int i = 0; i < this.labelParts.size() - 1; i++) {
                this.labelFlow.getChildren().addAll(this.labelParts.get(i), new Text(System.lineSeparator()));
            }
            this.labelFlow.getChildren().add(this.labelParts.get(this.labelParts.size() - 1));
            this.labelFlow.setTextAlignment(TextAlignment.CENTER);

            Text widestText = Collections.max(this.labelParts, Comparator.comparing(l -> l.getLayoutBounds().getWidth()));
            this.labelFlow.setPrefWidth(widestText.getLayoutBounds().getWidth());
            for (Text t : this.labelParts) {
                t.setFont(normalFont);
                t.setStyle("-fx-font-weight: normal");
            }

            if (isRoot) {
                this.setActiveLine(firstBeforeSort);
            }

            this.labelFlow.layoutXProperty().bind(this.labelFlow.widthProperty()
                    .divide(2)
                    .multiply(-1)
                    .add(x));
            this.labelFlow.layoutYProperty().bind(this.labelFlow.heightProperty()
                    .divide(2)
                    .multiply(-1)
                    .add(y));
        }

        private static ArrayList<Text> sortLines(ArrayList<Text> labelParts) {
            labelParts.sort(Comparator.comparing(t -> t.getLayoutBounds().getWidth()));
            Collections.reverse(labelParts);

            var sortedList = new ArrayList<Text>();
            for (int i = 0; i < labelParts.size(); i++) {
                if (i % 2 == 0) {
                    sortedList.add(0, labelParts.get(i));
                } else {
                    sortedList.add(labelParts.get(i));
                }
            }
            return sortedList;
        }

        void removeBold() {
            this.activeLine.setStyle("-fx-font-weight: normal");
        }

        void scrollActiveLine(ScrollEvent event) {
            removeBold();
            int i = this.labelParts.indexOf(this.activeLine);
            if (event.getDeltaY() > 0) {
                this.setActiveLine(this.labelParts.get(Math.floorMod(i - 1, this.labelParts.size())));
            } else {
                this.setActiveLine(this.activeLine = this.labelParts.get(Math.floorMod(i + 1, this.labelParts.size())));
            }
            this.activeLine.setStyle("-fx-font-weight: bold");

            interactionLogger.logThis(Map.of(
                    LoggerValues.EVENT, LoggerValues.SELECTION_ON_ROOT_NODE,
                    LoggerValues.PREV_ORTH_FORM, this.labelParts.get(i).getText(),
                    LoggerValues.NEW_ORTH_FORM, this.activeLine.getText()
            ));
        }

        void setupInteractions() {
            if (this.labelParts.size() == 1) return;

            for (Text t : this.labelParts) {
                t.setOnMouseClicked(event -> {
                    interactionLogger.logThis(Map.of(
                            LoggerValues.EVENT, LoggerValues.SELECTION_ON_ROOT_NODE,
                            LoggerValues.PREV_ORTH_FORM, this.activeLine.getText(),
                            LoggerValues.NEW_ORTH_FORM, t.getText()
                    ));

                    this.setActiveLine(t);
                    t.setUnderline(false);
                });
                t.setOnMouseEntered(event -> {
                    if (t != this.activeLine) {
                        t.setUnderline(true);
                    }
                });
                t.setOnMouseExited(event -> t.setUnderline(false));
            }
        }

        void setActiveLine(Text t) {
            if (this.activeLine != null) {
                this.activeLine.setStyle("-fx-font-weight: normal");
            }
            this.activeLine = t;
            this.activeLine.setStyle("-fx-font-weight: bold");
            this.activeLineId.setValue(this.activeLine.getId());
        }

        TextFlow getLabel() {
            return this.labelFlow;
        }

        double getWidth() {
            return this.labelFlow.getBoundsInParent().getWidth();
        }

        double getHeight() {
            return this.labelFlow.getBoundsInParent().getHeight();
        }

        public String getActiveLineId() {
            return activeLineId.get();
        }

        public StringProperty activeLineIdProperty() {
            return activeLineId;
        }

        public void setSelectedLine(String textToSelect) {
            var line = this.labelParts.stream()
                    .filter(t -> t.getText().toLowerCase().equals(textToSelect.toLowerCase()))
                    .findFirst();
            line.ifPresent(this::setActiveLine);
        }
    }
}
