package gui.controller;

import gui.graph.Graph;
import gui.model.SenseModelTarget;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

import javax.swing.SwingUtilities;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GraphController implements Initializable {
    @FXML
    public StackPane graphPane;

    //private SenseGraph senseGraph;
    private Graph graph;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*
        this.senseGraph = new SenseGraph();
        this.graphPane.getChildren().add(this.senseGraph.getPanel());
         */
        this.graph = new Graph();
        SwingNode swingNode = new SwingNode();
        this.createSwingContent(swingNode, this.graph);

        this.graphPane.getChildren().add(swingNode);
        /*
        this.graphPane.widthProperty().addListener((observableValue, number, t1) -> {
            swingNode.requestFocus();
        });
        this.graphPane.heightProperty().addListener((observableValue, number, t1) -> {
            swingNode.requestFocus();
        });

         */
    }

    public void updateGraphData(SenseModelTarget root, List<SenseModelTarget> hypernyms, List<SenseModelTarget> hyponyms) {
        /*
        this.senseGraph.setRootNodeData(root);
        this.senseGraph.setHypernymNodeData(hypernyms);
        this.senseGraph.setHyponymNodeData(hyponyms);
        this.senseGraph.updateGraph();
         */
        try {
            SwingUtilities.invokeAndWait(() -> this.graph.buildGraph(root, hypernyms, hyponyms));
        } catch (Exception ignored) {}

    }

    private void createSwingContent(SwingNode swingNode, Graph graph) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                graph.init();
                swingNode.setContent(graph.getGraphComponent());
                swingNode.requestFocus();
                swingNode.autosize();
            });
        } catch (Exception ignored) {}
    }
}