package gui.component;

import gui.model.SenseModelTarget;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.fx_viewer.util.DefaultApplication;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.view.Viewer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SenseGraph {
    private Map<String, List<SenseModelTarget>> nodeData;
    private SenseModelTarget rootNodeData;
    private static final int maxNodesPerView = 10;
    private final Graph graph;
    private FxViewPanel panel;

    private static final String stylesheet = "edge.hypernym { fill-color: red; } " +
            "edge.hyponym { fill-color: green; } " +
            "node { fill-mode: plain; fill-color: white; stroke-mode: plain; stroke-color: black; " +
            "padding: 10px; size-mode: fit; shape: circle; " +
            "text-alignment: center; text-size: 11; } " +
            "node.root { text-style: bold; } ";

    public SenseGraph() {
        this.nodeData = new HashMap<>()  {{ put("hypernym", null); put("hyponym", null); }};
        this.graph = new SingleGraph("PunCAT Graph");
        this.graph.setAttribute("ui.stylesheet", stylesheet);

        // TODO: make this work
        // this.graph.setAttribute("ui.stylesheet", getClass().getResource("/graphStyles.css"));

        FxViewer viewer = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();
        this.panel = (FxViewPanel) viewer.addView(FxViewer.DEFAULT_VIEW_ID, new FxGraphRenderer());
    }

    public void updateGraph() {
        this.graph.clear();
        this.graph.setAttribute("ui.stylesheet", stylesheet);
        this.graph.setAttribute("ui.quality");
        this.graph.setAttribute("ui.antialias");

        Node rootNode = this.createAndAddNode(this.rootNodeData);
        rootNode.setAttribute("ui.class", "root");

        for (String relation : this.nodeData.keySet()) {
            int currentNumberOfNodes = 0;
            for (SenseModelTarget sense : this.nodeData.get(relation)) {
                if (currentNumberOfNodes == maxNodesPerView) {
                    break;
                }
                Node node = this.createAndAddNode(sense);
                Edge edge = this.graph.addEdge(rootNode.getId() + "+" + node.getId(), rootNode, node);
                edge.setAttribute("ui.class", relation);
                currentNumberOfNodes++;
            }
        }
    }

    private Node createAndAddNode(SenseModelTarget sense) {
        Node node = this.graph.addNode(String.valueOf(sense.getId()));
        node.setAttribute("sense", sense);
        node.setAttribute("ui.label", String.join("\n", sense.getSynonyms()));
        return node;
    }

    public Map<String, List<SenseModelTarget>> getNodeData() {
        return nodeData;
    }

    public void setNodeData(Map<String, List<SenseModelTarget>> nodeData) {
        this.nodeData = nodeData;
    }

    public void setHyponymNodeData(List<SenseModelTarget> hyponyms) {
        this.nodeData.replace("hyponym", hyponyms);
    }

    public void setHypernymNodeData(List<SenseModelTarget> hypernyms) {
        this.nodeData.replace("hypernym", hypernyms);
    }

    public SenseModelTarget getRootNodeData() {
        return rootNodeData;
    }

    public void setRootNodeData(SenseModelTarget rootNodeData) {
        this.rootNodeData = rootNodeData;
    }

    public FxViewPanel getPanel() {
        return panel;
    }

    public void setPanel(FxViewPanel panel) {
        this.panel = panel;
    }
}
