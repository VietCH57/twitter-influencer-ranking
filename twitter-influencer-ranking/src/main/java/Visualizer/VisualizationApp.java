package Visualizer;

import TwitRank.graph.Graph;
import TwitRank.graph.GraphLoader;

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

        // Display the graph
        visualizer.display();
    }
}