package Visualizer;

import TwitRank.graph.Graph;
import TwitRank.graph.GraphLoader;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.Viewer.CloseFramePolicy;
import org.graphstream.ui.view.View;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.io.File;

public class VisualizationApp {
    public static void main(String[] args) {
        // Create graph loader and load the graph
        GraphLoader loader = new GraphLoader();
        Graph graph = loader.loadGraphFromExcel(new File("cleaned_data.xlsx"));

        if (graph == null) {
            System.err.println("Failed to load graph from Excel file");
            return;
        }

        // Create visualizer
        GraphVisualizer visualizer = new GraphVisualizer();

        // Convert your graph to GraphStream format
        GraphConverter converter = new GraphConverter(graph, visualizer.getVisualGraph());
        converter.convert();

        // Display the graph and get the viewer
        Viewer viewer = visualizer.display();
        View view = viewer.getDefaultView();

        // Get the JFrame using SwingUtilities
        SwingUtilities.invokeLater(() -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(((javax.swing.JPanel)view));
            if (frame != null) {
                LegendDisplay.install(frame);
            }
        });

        // Set close frame policy to exit the application
        viewer.setCloseFramePolicy(CloseFramePolicy.EXIT);
    }
}