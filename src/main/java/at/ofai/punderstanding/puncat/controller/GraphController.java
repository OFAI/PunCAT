package at.ofai.punderstanding.puncat.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

import at.ofai.punderstanding.puncat.component.graph.Graph;
import at.ofai.punderstanding.puncat.model.SenseModelTarget;


public class GraphController implements Initializable {
    private final StringProperty selectedLineId = new SimpleStringProperty();
    private final StringProperty selectedNode = new SimpleStringProperty();
    @FXML
    private Button firstButton;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button lastButton;
    @FXML
    private Pane graphPane;
    private Graph graph;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var clipRect = new Rectangle();
        clipRect.widthProperty().bind(this.graphPane.widthProperty());
        clipRect.heightProperty().bind(this.graphPane.heightProperty());
        this.graphPane.setClip(clipRect);
    }

    public void updateGraphData(String selectedText, SenseModelTarget root, List<SenseModelTarget> hypernyms, List<SenseModelTarget> hyponyms) {
        this.clearContents();
        this.graph = new Graph(this, graphPane.heightProperty(), graphPane.widthProperty());
        this.graph.configButtonDisable(firstButton, prevButton, nextButton, lastButton);
        this.graphPane.getChildren().add(this.graph);

        this.graph.layoutXProperty().bind(graphPane.widthProperty().divide(2));
        this.graph.layoutYProperty().bind(graphPane.heightProperty().divide(2));
        this.graph.build(selectedText, root, hypernyms, hyponyms);
        this.selectedLineId.bind(this.graph.selectedLineIdProperty());
    }

    public void clearContents() {
        this.graphPane.getChildren().remove(this.graph);
        this.graph = null;
        this.selectedNode.set(null);
        this.firstButton.setDisable(true);
        this.prevButton.setDisable(true);
        this.nextButton.setDisable(true);
        this.lastButton.setDisable(true);
    }

    public void firstGraph() {
        this.graph.firstGraph();
    }

    public void prevGraph() {
        this.graph.prevGraph();
    }

    public void nextGraph() {
        this.graph.nextGraph();
    }

    public void lastGraph() {
        this.graph.lastGraph();
    }

    public void nodeSelected(String id) {
        this.selectedNode.set(id);
    }

    public String getSelectedLineId() {
        return selectedLineId.get();
    }

    public StringProperty selectedLineIdProperty() {
        return selectedLineId;
    }

    public String getSelectedNode() {
        return selectedNode.get();
    }

    public StringProperty selectedNodeProperty() {
        return selectedNode;
    }

    public void addLabel(Label label) {
        label.layoutYProperty().bind(this.graphPane.heightProperty().divide(2));
        label.layoutXProperty().bind(
                this.graphPane.widthProperty().divide(2)
                        .subtract(label.widthProperty().divide(2)));
        label.setTextAlignment(TextAlignment.CENTER);
        this.graphPane.getChildren().add(label);
    }
}