package Visualizer;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swing_viewer.SwingViewer;

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
        visualGraph.setAttribute("layout.stabilization-limit", 0.9);
    }

    public Viewer display() {
        // Set up layout
        SpringBox layout = new SpringBox(false);
        layout.setForce(0.5);
        layout.setQuality(0.5);

        setupInitialPositions();

        // Create viewer and view
        SwingViewer viewer = new SwingViewer(visualGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        View view = viewer.addDefaultView(false);
        viewer.enableAutoLayout(layout);

        // Get screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.8);
        int height = (int) (screenSize.height * 0.8);

        // Create main frame
        JFrame frame = new JFrame("Twitter Network Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);

        // Create layered pane for z-ordering
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(width, height));

        // Add the graph view
        Component graphView = (Component) view;
        graphView.setBounds(0, 0, width, height);
        ((JPanel) graphView).setBackground(new Color(27, 32, 43));
        layeredPane.add(graphView, JLayeredPane.DEFAULT_LAYER);

        // Create and add search panel
        SearchDisplay searchDisplay = new SearchDisplay(visualGraph, viewer);
        JPanel searchPanel = searchDisplay.getSearchPanel();
        int searchPanelX = (width - SearchDisplay.PANEL_WIDTH) / 2; // Center horizontally
        searchPanel.setBounds(searchPanelX, 10, SearchDisplay.PANEL_WIDTH, SearchDisplay.PANEL_HEIGHT);
        layeredPane.add(searchPanel, JLayeredPane.POPUP_LAYER);

        // Add legend display
        LegendDisplay.install(frame);

        // Install handlers
        ZoomHandler.install(viewer, visualGraph);
        PanHandler.install(view);

        // Set initial view settings
        viewer.getDefaultView().getCamera().setViewPercent(INITIAL_VIEW_PERCENT);

        // Add layered pane to frame
        frame.setContentPane(layeredPane);
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