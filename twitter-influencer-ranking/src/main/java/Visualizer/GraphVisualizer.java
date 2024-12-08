package Visualizer;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

public class GraphVisualizer {
    private final Graph visualGraph;
    private static final String GRAPH_CSS = """
            node.user {
                size: 25px;
                fill-color: #6699ff;
                text-size: 12;
                text-color: #000000;
                text-style: bold;
                text-alignment: center;
            }
            node.kol {
                size: 35px;
                fill-color: #ff6666;
                text-size: 14;
                text-color: #000000;
                text-style: bold;
                text-alignment: center;
            }
            edge {
                arrow-size: 5px, 3px;
                size: 1px;
            }
            """;

    public GraphVisualizer() {
        System.setProperty("org.graphstream.ui", "swing");
        this.visualGraph = new SingleGraph("Twitter Network");
        this.visualGraph.setAttribute("ui.stylesheet", GRAPH_CSS);
    }

    public void display() {
        Viewer viewer = this.visualGraph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
    }

    public Graph getVisualGraph() {
        return visualGraph;
    }
}