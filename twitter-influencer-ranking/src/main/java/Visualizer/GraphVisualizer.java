package Visualizer;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swing_viewer.SwingViewer;  // Add this import
import javax.swing.*;
import java.awt.*;

public class GraphVisualizer {
    private final Graph visualGraph;
    private static final double INITIAL_VIEW_PERCENT = 1.0;

    public GraphVisualizer() {
        System.setProperty("org.graphstream.ui", "swing");
        this.visualGraph = new SingleGraph("Twitter Network");
        visualGraph.setAttribute("ui.stylesheet", StyleSheet.DEFAULT_STYLE);
        visualGraph.setAttribute("ui.quality");
        visualGraph.setAttribute("ui.antialias");
    }

    public Viewer display() {
        SpringBox layout = new SpringBox(false);
        layout.setForce(0.3);
        layout.setQuality(0.9);

        setupInitialPositions();

        // Create SwingViewer instead of abstract Viewer
        SwingViewer viewer = new SwingViewer(visualGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        View view = viewer.addDefaultView(false);
        viewer.enableAutoLayout(layout);

        // Create the main frame
        JFrame frame = new JFrame("Graph Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create and add search panel
        SearchDisplay searchDisplay = new SearchDisplay(visualGraph, viewer);
        mainPanel.add(searchDisplay.getSearchPanel(), BorderLayout.NORTH);

        // Add the graph view
        mainPanel.add((Component) view, BorderLayout.CENTER);

        // Set up the frame
        frame.add(mainPanel);
        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);

        // Install handlers
        ZoomHandler.install(viewer, visualGraph);
        PanHandler.install(view);

        viewer.getDefaultView().getCamera().setViewPercent(INITIAL_VIEW_PERCENT);

        frame.setVisible(true);
        return viewer;
    }

    private void setupInitialPositions() {
        int nodeCount = visualGraph.getNodeCount();
        if (nodeCount == 0) return;

        double phi = (1 + Math.sqrt(5)) / 2;
        double goldenAngle = 2 * Math.PI * (1 - 1/phi);

        int i = 0;
        for (Node node : visualGraph) {
            double radius = 50 * Math.sqrt(i + 1);
            double theta = i * goldenAngle;

            double x = radius * Math.cos(theta);
            double y = radius * Math.sin(theta);

            // Set both xyz and x,y attributes
            node.setAttribute("xyz", x, y, 0);
            node.setAttribute("x", x);
            node.setAttribute("y", y);

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