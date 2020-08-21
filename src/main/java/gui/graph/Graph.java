package gui.graph;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import gui.model.SenseModelTarget;

import java.awt.Color;
import java.util.List;

public class Graph {
    private mxGraph graph;
    private mxGraphComponent graphComponent;

    public Graph() {
    }

    public void init() {
        this.graph = new mxGraph();
        this.graph.getStylesheet().putCellStyle("ROUNDED", Style.style);
        this.graphComponent = new mxGraphComponent(graph);
        this.graphComponent.setGridVisible(true);
    }

    public void buildGraph(SenseModelTarget senseRoot, List<SenseModelTarget> hypernyms, List<SenseModelTarget> hyponyms) {
        graph.getModel().beginUpdate();
        try {
            var paneSize = this.graphComponent.getSize();
            String text = String.join("\n", senseRoot.getSynonyms());
            var rootCell = (mxCell) graph.insertVertex(this.graph.getDefaultParent(),
                    String.valueOf(senseRoot.getId()),
                    text, paneSize.getWidth()/2, paneSize.getHeight()/2,
                    CellSize.getX(text), CellSize.getY(text),
                    "ROUNDED");

            this.fillWithChildren(rootCell, hypernyms);
            this.fillWithChildren(rootCell, hyponyms);
            // new mxHierarchicalLayout(graph).execute(graph.getDefaultParent());
            new mxFastOrganicLayout(this.graph).execute(rootCell);

        } finally {
            this.graph.getModel().endUpdate();
        }
    }

    private void fillWithChildren(mxICell rootCell, List<SenseModelTarget> children) {
        for (SenseModelTarget child : children) {
            String text = String.join("\n", child.getSynonyms());

            var cell = (mxCell) graph.insertVertex(graph.getDefaultParent(),
                    String.valueOf(child.getId()),
                    text, 0, 0,
                    CellSize.getX(text), CellSize.getY(text),
                    "ROUNDED;");

            graph.insertEdge(graph.getDefaultParent(), null, "", rootCell, cell);
        }
    }

    public mxGraphComponent getGraphComponent() {
        return graphComponent;
    }
}
