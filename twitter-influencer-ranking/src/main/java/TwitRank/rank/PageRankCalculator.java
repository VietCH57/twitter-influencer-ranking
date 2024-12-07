package TwitRank.rank;

import TwitRank.graph.Graph;
import TwitRank.model.Node;

import java.util.*;

public class PageRankCalculator {

    public Map<Node, Double> computePageRank(Graph graph, double dampingFactor, int iterations) {
        PageRank pageRank = new PageRank(graph, dampingFactor, iterations);
        return pageRank.computePageRank();
    }
}