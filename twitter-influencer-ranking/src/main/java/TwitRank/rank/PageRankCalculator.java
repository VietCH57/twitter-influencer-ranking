package TwitRank.rank;

import TwitRank.graph.Graph;
import TwitRank.elements.Node;

import java.util.*;

public class PageRankCalculator {

    public Map<Node, Double> computePageRank(Graph graph, double dampingFactor, int iterations) {
        // Validate input parameters
        if (graph == null || graph.getAllNodes().isEmpty()) {
            throw new IllegalArgumentException("Graph cannot be null or empty");
        }
        if (dampingFactor <= 0 || dampingFactor >= 1) {
            throw new IllegalArgumentException("Damping factor must be between 0 and 1");
        }
        if (iterations <= 0) {
            throw new IllegalArgumentException("Number of iterations must be positive");
        }

        PageRank pageRank = new PageRank(graph, dampingFactor, iterations);
        return pageRank.computePageRank();
    }
}