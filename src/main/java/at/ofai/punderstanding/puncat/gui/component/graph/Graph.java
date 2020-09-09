package at.ofai.punderstanding.puncat.gui.component.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import com.google.common.collect.BiMap;

import at.ofai.punderstanding.puncat.gui.controller.GraphController;
import at.ofai.punderstanding.puncat.gui.logger.InteractionLogger;
import at.ofai.punderstanding.puncat.gui.logger.LoggerValues;
import at.ofai.punderstanding.puncat.gui.model.SenseModelTarget;


public class Graph extends Group {
    public static final int maxNodesPerHalf = 6;

    private final GraphController controller;
    private final DoubleProperty childNodeDistanceX = new SimpleDoubleProperty();
    private final DoubleProperty childNodeDistanceY = new SimpleDoubleProperty();

    private final StringProperty selectedLineId = new SimpleStringProperty();
    private final ArrayList<Group> childNodePages = new ArrayList<>();
    private Group activeChildren;

    public Graph(GraphController controller, ReadOnlyDoubleProperty slotHeight, ReadOnlyDoubleProperty slotWidth) {
        this.controller = controller;

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
        this.activeChildren = this.childNodePages.get(0);
        this.activeChildren.setVisible(true);

        Node rootNode = new Node(root.getSynonyms(), new SimpleDoubleProperty(0), new SimpleDoubleProperty(0), true);
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
            y.bind(childNodeDistanceY.multiply( Math.sin(Math.toRadians(25 / 2.0 + degrees.get(i)))));

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

    public void nodeSelected(String id) {
        this.controller.nodeSelected(id);
    }

    private void setVisibleChildren(Group children) {
        this.activeChildren.setVisible(false);
        this.activeChildren = children;
        this.activeChildren.setVisible(true);
    }

    public void prevGraph() {
        int idx = this.childNodePages.indexOf(this.activeChildren);

        InteractionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.PREV_GRAPH_BUTTON_CLICKED_EVENT,
                LoggerValues.PREV_GRAPH_IDX, idx,
                LoggerValues.NEXT_GRAPH_IDX, idx == 0 ? 0 : idx - 1));

        if (idx != 0) {
            this.setVisibleChildren(this.childNodePages.get(idx - 1));
        }
    }

    public void nextGraph() {
        int idx = this.childNodePages.indexOf(this.activeChildren);

        InteractionLogger.logThis(Map.of(
                LoggerValues.EVENT, LoggerValues.NEXT_GRAPH_BUTTON_CLICKED_EVENT,
                LoggerValues.PREV_GRAPH_IDX, idx,
                LoggerValues.NEXT_GRAPH_IDX, idx == this.childNodePages.size()-1 ? this.childNodePages.size()-1 : idx + 1));

        if (idx != this.childNodePages.size() - 1) {
            this.setVisibleChildren(this.childNodePages.get(idx + 1));
        }
    }

    public StringProperty selectedLineIdProperty() {
        return selectedLineId;
    }
}
