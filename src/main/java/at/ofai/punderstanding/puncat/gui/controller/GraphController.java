package at.ofai.punderstanding.puncat.gui.controller;

import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import at.ofai.punderstanding.puncat.gui.component.graph.Graph;
import at.ofai.punderstanding.puncat.gui.model.SenseModelTarget;

public class GraphController {
    @FXML
    Pane graphPane;

    private Graph graph;
    private final StringProperty selectedLineId = new SimpleStringProperty();
    private TargetController targetController;

    public void setReferences(TargetController tc) {
        this.targetController = tc;
    }

    public void updateGraphData(String selectedText, SenseModelTarget root, List<SenseModelTarget> hypernyms, List<SenseModelTarget> hyponyms) {
        this.clearContents();
        this.graph = new Graph(this, graphPane.getHeight(), graphPane.getWidth());
        this.graphPane.getChildren().add(this.graph);
        this.graph.layoutXProperty().bind(graphPane.widthProperty().divide(2));
        this.graph.layoutYProperty().bind(graphPane.heightProperty().divide(2));
        this.graph.build(selectedText, root, hypernyms, hyponyms);
        this.selectedLineId.bind(this.graph.selectedLineIdProperty());
        this.selectedLineId.addListener((observable, oldValue, newValue) -> this.selectedLineChanged());
    }

    private void selectedLineChanged() {
        this.targetController.selectedLineChanged(Integer.parseInt(this.selectedLineId.getValue()));
    }

    public void clearContents() {
        this.graphPane.getChildren().remove(this.graph);
        this.graph = null;
    }

    public void prevGraph() {
        this.graph.prevGraph();
    }

    public void nextGraph() {
        this.graph.nextGraph();
    }

    public void nodeSelected(String id) {
        this.targetController.setWordInputFromNode(id);
    }
}