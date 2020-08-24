package gui.controller;

import gui.component.graph.Graph;
import gui.model.SenseModelTarget;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class GraphController implements Initializable {
    @FXML
    public GridPane graphPane;

    private MainController mainController;
    private final List<Graph> graphs = new ArrayList<>();
    private Graph activeGraph;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setReferences(MainController mc) {
        this.mainController = mc;
    }

    public void updateGraphData(SenseModelTarget root, List<SenseModelTarget> hypernyms, List<SenseModelTarget> hyponyms) {
        this.graphPane.getChildren().clear();
        this.graphs.clear();
        var paneWidth = graphPane.getCellBounds(0, 0).getWidth();
        var paneHeight = graphPane.getCellBounds(0, 0).getHeight();

        var hypernymSlices = this.slice(hypernyms);
        var hyponymSlices = this.slice(hyponyms);

        int graphCount = Math.max(hypernymSlices.size(), hyponymSlices.size());
        for (int i = 0; i < graphCount; i++) {
            Graph g = new Graph(this, paneWidth, paneHeight);
            this.graphs.add(g);
            // this.graphPane.add(g, 0, 0);
            g.setVisible(false);
        }

        for (int i = 0; i < graphCount; i++) {
            List<SenseModelTarget> hypernymSlice = hypernymSlices.size() > i ? hypernymSlices.get(i) : Collections.emptyList();
            List<SenseModelTarget> hyponymSlice = hyponymSlices.size() > i ? hyponymSlices.get(i) : Collections.emptyList();
            this.graphs.get(i).buildGraph(root, hypernymSlice, hyponymSlice);
        }
        this.setVisibleGraph(this.graphs.get(0));

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

    private void setVisibleGraph(Graph graph) {
        if (this.activeGraph != null) {
            this.activeGraph.setVisible(false);
            this.graphPane.getChildren().remove(this.activeGraph);
        }
        this.activeGraph = graph;
        graph.setVisible(true);
        this.graphPane.add(graph, 0, 0);
    }

    public void prevGraph() {
        int idx = this.graphs.indexOf(this.activeGraph);
        if (idx == 0) return;
        this.setVisibleGraph(this.graphs.get(idx-1));
    }

    public void nextGraph() {
        int idx = this.graphs.indexOf(this.activeGraph);
        if (idx == this.graphs.size()-1) return;
        this.setVisibleGraph(this.graphs.get(idx+1));
    }

    public void nodeSelected(String id) {
        this.mainController.nodeSelected(id, this);
    }
}