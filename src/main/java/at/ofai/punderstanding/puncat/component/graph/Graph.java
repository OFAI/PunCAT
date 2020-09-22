package at.ofai.punderstanding.puncat.component.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import com.google.common.collect.BiMap;

import at.ofai.punderstanding.puncat.controller.GraphController;
import at.ofai.punderstanding.puncat.logging.InteractionLogger;
import at.ofai.punderstanding.puncat.logging.LoggerValues;
import at.ofai.punderstanding.puncat.model.SenseModelTarget;


public class Graph extends Group {
    public static final int maxNodesPerHalf = 6;

    private final GraphController controller;
    private final DoubleProperty childNodeDistanceX = new SimpleDoubleProperty();
    private final DoubleProperty childNodeDistanceY = new SimpleDoubleProperty();

    private final StringProperty selectedLineId = new SimpleStringProperty();
    private final ArrayList<Group> childNodePages = new ArrayList<>();
    private final InteractionLogger interactionLogger;
    private ObjectProperty<Group> activeChildren = new SimpleObjectProperty<>();
    private Node rootNode;

    public Graph(GraphController controller, ReadOnlyDoubleProperty slotHeight, ReadOnlyDoubleProperty slotWidth) {
        this.controller = controller;
        this.interactionLogger = new InteractionLogger();

        // TODO: check this on a higher resolution
        this.childNodeDistanceX.bind(slotWidth.multiply(0.4));
        this.childNodeDistanceY.bind(slotHeight.multiply(0.4));
    }

    public void build(String selectedText, SenseModelTarget root, List<SenseModelTarget> hypernyms, List<SenseModelTarget> hyponyms) {
        var hypernymSlices = this.slice(hypernyms);
        var hyponymSlices = this.slice(hyponyms);
        int pages = Math.max(hypernymSlices.size(), hyponymSlices.size());

        for (int i = 0; i < pages; i++) {
            List<SenseModelTarget> hypernymSlice = hypernymSlices.size() > i ? hypernymSlices.get(i) : Collections.emptyList();
            List<SenseModelTarget> hyponymSlice = hyponymSlices.size() > i ? hyponymSlices.get(i) : Collections.emptyList();
            this.buildChildren(hypernymSlice, hyponymSlice);
        }
        this.activeChildren.set(this.childNodePages.get(0));
        this.activeChildren.get().setVisible(true);

        this.rootNode = new Node(root.getSynonyms(), new SimpleDoubleProperty(0), new SimpleDoubleProperty(0), true);
        this.getChildren().add(rootNode);
        rootNode.setSelectedLine(selectedText);
        // TODO: this is a long binding chain. there must be a better way
        this.selectedLineId.bind(rootNode.selectedLineIdProperty());
    }

    private void buildChildren(List<SenseModelTarget> hypernyms, List<SenseModelTarget> hyponyms) {
        Group childGroup = new Group();
        childGroup.setVisible(false);

        this.addNodes(childGroup, hypernyms, true);
        this.addNodes(childGroup, hyponyms, false);

        this.childNodePages.add(childGroup);
        this.getChildren().add(childGroup);
    }

    private ArrayList<ArrayList<SenseModelTarget>> slice(List<SenseModelTarget> senses) {
        var slices = new ArrayList<ArrayList<SenseModelTarget>>();
        for (int i = 0; i <= senses.size() / Graph.maxNodesPerHalf; i++) {
            int end = Math.min(i * Graph.maxNodesPerHalf + Graph.maxNodesPerHalf, senses.size());

            var s = new ArrayList<>(senses.subList(i * Graph.maxNodesPerHalf, end));
            slices.add(s);
        }
        return slices;
    }

    private void addNodes(Group childGroup, List<SenseModelTarget> senses, boolean above) {
        // TODO: simplify
        if (senses.isEmpty()) {
            return;
        }

        List<BiMap<Integer, String>> labelValues;
        labelValues = senses.stream().map(SenseModelTarget::getSynonyms).collect(Collectors.toList());

        double offset = above ? 10 + 180 : 10;
        double t = 160d / maxNodesPerHalf;
        var degrees = new ArrayList<Double>();
        for (int i = 0; i < 8; i++) {
            degrees.add(offset + t * i);
        }

        var degreesFirstHalf = new ArrayList<Double>();
        for (int i = 0; i < degrees.size() / 2; i++) {
            degreesFirstHalf.add(degrees.get(i));
        }
        Collections.reverse(degreesFirstHalf);

        var degreesSecondHalf = new ArrayList<Double>(degrees.subList(labelValues.size() / 2, labelValues.size()).size());
        for (int i = degrees.size() / 2; i < degrees.size(); i++) {
            degreesSecondHalf.add(degrees.get(i));
        }

        degrees.clear();
        degrees.addAll(degreesFirstHalf);
        for (int i = 1; i <= degreesSecondHalf.size(); i++) {
            var item = degreesSecondHalf.get(i - 1);
            if (degrees.size() > i * 2) {
                degrees.add(i * 2, item);
            } else {
                degrees.add(item);
            }
        }

        for (int i = 0; i < labelValues.size(); i++) {
            DoubleProperty x = new SimpleDoubleProperty();
            x.bind(childNodeDistanceX.multiply(Math.cos(Math.toRadians(25 / 2.0 + degrees.get(i)))));
            DoubleProperty y = new SimpleDoubleProperty();
            y.bind(childNodeDistanceY.multiply(Math.sin(Math.toRadians(25 / 2.0 + degrees.get(i)))));

            var node = new Node(labelValues.get(i), x, y, false);
            node.setId(String.valueOf(senses.get(i).getSynsetIdentifier()));
            var edge = new Line();
            edge.setStartX(0);
            edge.setStartY(0);
            edge.endXProperty().bind(x);
            edge.endYProperty().bind(y);

            childGroup.getChildren().addAll(node, edge);

            edge.toBack();
            if (above) {
                edge.setStroke(Color.CRIMSON);
            } else {
                edge.setStroke(Color.LIMEGREEN);
            }
            edge.setStrokeWidth(1.5);  // TODO: styles into stylesheet
        }
    }

    public void nodeSelected(Node node) {
        var fadeTransitions = new ArrayList<FadeTransition>();
        for (javafx.scene.Node n : this.activeChildren.get().getChildren()) {
            if (n != node) {
                var ft = new FadeTransition(Duration.millis(100), n);
                ft.setFromValue(1);
                ft.setToValue(0);
                fadeTransitions.add(ft);
            }
        }
        var ft = new FadeTransition(Duration.millis(100), this.rootNode);
        ft.setFromValue(1);
        ft.setToValue(0);
        fadeTransitions.add(ft);

        node.setCacheHint(CacheHint.SPEED);
        var tt = new TranslateTransition(Duration.millis(100), node);
        tt.byYProperty().set(node.getCenterY() * -1);
        tt.byXProperty().set(node.getCenterX() * -1);
        tt.setOnFinished(event -> {
            node.setCacheHint(CacheHint.DEFAULT);
            this.controller.nodeSelected(node.getId());
        });

        for (var tr : fadeTransitions) {
            tr.play();
        }
        tt.play();

    }

    private void setVisibleChildren(Group children) {
        this.activeChildren.get().setVisible(false);
        this.activeChildren.set(children);
        this.activeChildren.get().setVisible(true);
    }

    public void firstGraph() {
        int idx = this.childNodePages.indexOf(this.activeChildren.get());

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.FIRST_GRAPH_BUTTON_CLICKED_EVENT,
                LoggerValues.PREV_GRAPH_IDX, idx,
                LoggerValues.NEXT_GRAPH_IDX, idx == 0 ? 0 : idx - 1));

        if (idx != 0) {
            this.setVisibleChildren(this.childNodePages.get(0));
        }
    }

    public void prevGraph() {
        int idx = this.childNodePages.indexOf(this.activeChildren.get());

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.PREV_GRAPH_BUTTON_CLICKED_EVENT,
                LoggerValues.PREV_GRAPH_IDX, idx,
                LoggerValues.NEXT_GRAPH_IDX, idx == 0 ? 0 : idx - 1));

        if (idx != 0) {
            this.setVisibleChildren(this.childNodePages.get(idx - 1));
        }
    }

    public void nextGraph() {
        int idx = this.childNodePages.indexOf(this.activeChildren.get());

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.NEXT_GRAPH_BUTTON_CLICKED_EVENT,
                LoggerValues.PREV_GRAPH_IDX, idx,
                LoggerValues.NEXT_GRAPH_IDX, idx == this.childNodePages.size() - 1 ? this.childNodePages.size() - 1 : idx + 1));

        if (idx != this.childNodePages.size() - 1) {
            this.setVisibleChildren(this.childNodePages.get(idx + 1));
        }
    }

    public void lastGraph() {
        int idx = this.childNodePages.indexOf(this.activeChildren.get());

        interactionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.LAST_GRAPH_BUTTON_CLICKED_EVENT,
                LoggerValues.PREV_GRAPH_IDX, idx,
                LoggerValues.NEXT_GRAPH_IDX, idx == this.childNodePages.size() - 1 ? this.childNodePages.size() - 1 : idx + 1));

        if (idx != this.childNodePages.size() - 1) {
            this.setVisibleChildren(this.childNodePages.get(this.childNodePages.size() - 1));
        }
    }

    public StringProperty selectedLineIdProperty() {
        return selectedLineId;
    }

    public void configButtonDisable(Button firstButton, Button prevButton, Button nextButton, Button lastButton) {
        this.activeChildren.addListener((observable, oldValue, newValue) -> {
            var noActive = this.activeChildren.get() == null;
            var onePageOrNoPages = childNodePages.size() == 1 || childNodePages.isEmpty();
            var firstPage = childNodePages.indexOf(this.activeChildren.get()) == 0;
            var lastPage = childNodePages.indexOf(this.activeChildren.get()) == childNodePages.size() - 1;
            firstButton.setDisable(noActive || firstPage || onePageOrNoPages);
            prevButton.setDisable(noActive || firstPage || onePageOrNoPages);
            nextButton.setDisable(noActive || lastPage || onePageOrNoPages);
            lastButton.setDisable(noActive || lastPage || onePageOrNoPages);
        });
    }
}
