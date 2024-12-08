package Visualizer;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;

public class GraphVisualizer {
    private final Graph visualGraph;
    private static final double INITIAL_VIEW_PERCENT = 1.0;

    public GraphVisualizer() {
        System.setProperty("org.graphstream.ui", "swing");
        this.visualGraph = new SingleGraph("Twitter Network");

        // Apply styles
        visualGraph.setAttribute("ui.stylesheet", StyleSheet.DEFAULT_STYLE);

        // Enable high-quality rendering
        visualGraph.setAttribute("ui.quality");
        visualGraph.setAttribute("ui.antialias");
    }

    public Viewer display() {
        // Create and configure the layout
        SpringBox layout = new SpringBox(false);
        layout.setForce(0.3);
        layout.setQuality(0.9);

        setupInitialPositions();

        // Display with the configured layout
        Viewer viewer = this.visualGraph.display();
        viewer.enableAutoLayout(layout);

        // Install zoom and pan handlers in the correct order
        View view = viewer.getDefaultView();
        ZoomHandler.install(viewer, visualGraph);
        PanHandler.install(view);  // Pass zoom handler to pan handler

        viewer.getDefaultView().getCamera().setViewPercent(INITIAL_VIEW_PERCENT);

        return viewer;
    }

    private void setupInitialPositions() {
        int nodeCount = visualGraph.getNodeCount();
        if (nodeCount == 0) return;

        // Calculate optimal spacing based on golden ratio
        double phi = (1 + Math.sqrt(5)) / 2;
        double goldenAngle = 2 * Math.PI * (1 - 1/phi);

        // Position nodes in a spiral pattern
        int i = 0;
        for (Node node : visualGraph) {
            double radius = 50 * Math.sqrt(i + 1);
            double theta = i * goldenAngle;

            double x = radius * Math.cos(theta);
            double y = radius * Math.sin(theta);

            node.setAttribute("xyz", x, y, 0);

            // Only freeze KOL positions
            if ("kol".equals(node.getAttribute("ui.class"))) {
                node.setAttribute("layout.frozen");
            }

            i++;
        }
    }

    public Graph getVisualGraph() {
        return visualGraph;
    }
}