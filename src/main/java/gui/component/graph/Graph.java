package gui.component.graph;

import gui.controller.GraphController;
import gui.model.SenseModelTarget;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Graph extends Group {
    public static final int maxNodesPerHalf = 6;

    private final GraphController controller;
    private final int distance = 100;
    private double rootX;
    private double rootY;

    public Graph(GraphController controller) {
        this.controller = controller;
    }

    public void buildGraph(SenseModelTarget root, List<SenseModelTarget> hypernyms, List<SenseModelTarget> hyponyms) {
        this.removeNodes();
        this.addRootNode(String.join("\n", root.getSynonyms()));
        this.addNodes(hypernyms, true);
        this.addNodes(hyponyms, false);
    }

    private void addNodes(List<SenseModelTarget> senses, boolean above) {
        List<String> labels;
        labels = senses.stream().map(s -> String.join("\n", s.getSynonyms())).collect(Collectors.toList());

        if (labels.isEmpty()) return;

        if (labels.size() > maxNodesPerHalf) {
            throw new UnsupportedOperationException("Got more nodes than I can chew!");
        }
        double offset = above ? 10 + 180 : 10;
        double t = 160d / maxNodesPerHalf;
        var degrees = new ArrayList<Double>();
        for (int i = 0; i < 8; i++) {
            degrees.add(offset + t * i);
        }

        var degreesFirstHalf = new ArrayList<Double>();
        for (int i = 0; i < degrees.size()/2; i++) {
            degreesFirstHalf.add(degrees.get(i));
        }
        Collections.reverse(degreesFirstHalf);

        var degreesSecondHalf = new ArrayList<Double>(degrees.subList(labels.size()/2, labels.size()).size());
        for (int i = degrees.size()/2; i < degrees.size(); i++) {
            degreesSecondHalf.add(degrees.get(i));
        }

        degrees.clear();
        degrees.addAll(degreesFirstHalf);
        for (int i = 1; i <= degreesSecondHalf.size(); i++) {
            var item = degreesSecondHalf.get(i-1);
            if (degrees.size() > i*2) {
                degrees.add(i*2, item);
            } else {
                degrees.add(item);
            }
        }

        for (int i = 0; i < labels.size(); i++) {
            var x = rootX + distance * Math.cos(Math.toRadians(25/2.0 + degrees.get(i)));
            var y = rootY + distance * Math.sin(Math.toRadians(25/2.0 + degrees.get(i)));

            var node = new Node(labels.get(i), x, y);
            node.setId(String.valueOf(senses.get(i).getId()));
            var edge = new Line(rootX, rootY, x, y);
            this.getChildren().addAll(node, edge);
            edge.toBack();
            if (above) {
                edge.setStroke(Color.CRIMSON);
            } else {
                edge.setStroke(Color.LIMEGREEN);
            }
            edge.setStrokeWidth(1.5);
        }
    }

    public void addRootNode(String rootLabel) {
        this.rootX = this.getBoundsInParent().getCenterX();
        this.rootY = this.getBoundsInParent().getCenterY();
        Node root = new Node(rootLabel, rootX, rootY);
        root.setRootStyle();
        this.getChildren().add(root);
    }

    private void removeNodes() {
        this.getChildren().clear();
    }

    public void nodeSelected(String id) {
        this.controller.nodeSelected(id);
    }
}
