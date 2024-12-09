package TwitRank.rank;

import TwitRank.elements.Node;
import TwitRank.graph.Graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageRank {
    private final Graph graph;
    private final double dampingFactor;
    private final int maxIterations;
    private final double epsilon;

    public PageRank(Graph graph, double dampingFactor, int maxIterations, double epsilon) {
        this.graph = graph;
        this.dampingFactor = dampingFactor;
        this.maxIterations = maxIterations;
        this.epsilon = epsilon;
    }

    public Map<Node, Double> computePageRank() {
        Map<Node, Double> pageRankScores = new HashMap<>();
        Map<Node, Double> newPageRankScores = new HashMap<>();

        // Initialize PageRank scores to 1/N for all nodes
        double initialRank = 1.0 / graph.getAllNodes().size();
        for (Node node : graph.getAllNodes()) {
            pageRankScores.put(node, initialRank);
        }
        System.out.println("Initialized PageRank scores for all nodes.");

        // Precompute the sum of PageRank scores for dangling nodes
        double danglingSum;

        for (int i = 0; i < maxIterations; i++) {
            danglingSum = 0.0;

            for (Node node : graph.getAllNodes()) {
                if (graph.getOutgoingEdges(node).isEmpty()) {
                    danglingSum += pageRankScores.get(node);
                }
            }

            boolean converged = true;

            for (Node node : graph.getAllNodes()) {
                double rankSum = 0.0;
                List<Node> incomingNodes = getIncomingNodes(node);

                for (Node incomingNode : incomingNodes) {
                    int outDegree = graph.getOutgoingEdges(incomingNode).size();
                    rankSum += pageRankScores.get(incomingNode) / outDegree;
                }

                double newRank = (1 - dampingFactor) / graph.getAllNodes().size() + dampingFactor * (rankSum + danglingSum / graph.getAllNodes().size());
                newPageRankScores.put(node, newRank);

                if (Math.abs(newRank - pageRankScores.get(node)) > epsilon) {
                    converged = false;
                }
            }

            pageRankScores.putAll(newPageRankScores);

            if (converged) {
                System.out.println("Converged after " + (i + 1) + " iterations.");
                break;
            }
        }

        System.out.println("Final PageRank scores computed.");
        return pageRankScores;
    }

    private List<Node> getIncomingNodes(Node node) {
        return graph.getAllNodes().stream()
                .filter(n -> graph.getOutgoingEdges(n).stream().anyMatch(edge -> edge.getTargetUser().equals(node)))
                .toList();
    }
}