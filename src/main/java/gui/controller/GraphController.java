package gui.controller;

import gui.component.SenseGraph;
import gui.model.SenseModelTarget;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GraphController implements Initializable {
    @FXML
    public StackPane graphPane;

    private SenseGraph senseGraph;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.senseGraph = new SenseGraph();
        this.graphPane.getChildren().add(this.senseGraph.getPanel());
    }

    public void updateGraphData(SenseModelTarget root, List<SenseModelTarget> hypernyms, List<SenseModelTarget> hyponyms) {
        this.senseGraph.setRootNodeData(root);
        this.senseGraph.setHypernymNodeData(hypernyms);
        this.senseGraph.setHyponymNodeData(hyponyms);
        this.senseGraph.updateGraph();
    }
}