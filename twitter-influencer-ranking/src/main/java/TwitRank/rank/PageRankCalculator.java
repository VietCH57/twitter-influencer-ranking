package TwitRank.rank;

import TwitRank.graph.Graph;
import TwitRank.elements.Node;
import java.util.*;

public class PageRankCalculator {

    public Map<Node, Double> computePageRank(Graph graph, double dampingFactor, int maxIterations) {
        validateInput(graph, dampingFactor, maxIterations);
        PageRank pageRank = new PageRank(graph, dampingFactor, maxIterations);
        return pageRank.computePageRank();
    }

    private void validateInput(Graph graph, double dampingFactor, int maxIterations) {
        if (graph == null || graph.getAllNodes().isEmpty()) {
            throw new IllegalArgumentException("Graph cannot be null or empty");
        }
        if (dampingFactor <= 0 || dampingFactor >= 1) {
            throw new IllegalArgumentException("Damping factor must be between 0 and 1");
        }
        if (maxIterations <= 0) {
            throw new IllegalArgumentException("Number of iterations must be positive");
        }
    }
}