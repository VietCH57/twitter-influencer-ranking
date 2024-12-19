package TwitRank.rank;

import TwitRank.elements.Edge;
import TwitRank.elements.Node;
import TwitRank.elements.User;
import TwitRank.graph.Graph;
import java.util.*;

public class PageRank {
    private final Graph graph;
    private final double dampingFactor;
    private final int maxIterations;
    private final double convergenceThreshold;
    private final Map<Node, Double> scores;

    public PageRank(Graph graph, double dampingFactor, int maxIterations, double convergenceThreshold) {
        this.graph = graph;
        this.dampingFactor = dampingFactor;
        this.maxIterations = maxIterations;
        this.convergenceThreshold = convergenceThreshold;
        this.scores = new HashMap<>();
    }

    public Map<Node, Double> computePageRank() {
        initializeScoresWithFollowers();

        Map<Node, Double> newScores = new HashMap<>();
        int iteration = 0;
        double maxDifference;

        do {
            maxDifference = 0.0;

            for (Node node : graph.getAllNodes()) {
                double newScore = calculateCombinedScore(node);
                newScores.put(node, newScore);
                maxDifference = Math.max(maxDifference,
                        Math.abs(newScore - scores.getOrDefault(node, 0.0)));
            }

            scores.clear();
            scores.putAll(newScores);
            iteration++;

        } while (iteration < maxIterations && maxDifference > convergenceThreshold);

        normalizeScores();
        System.out.println("Converged after " + iteration + " iterations");

        return scores;
    }

    private void initializeScoresWithFollowers() {
        double totalFollowers = graph.getAllNodes().stream()
                .filter(n -> n instanceof User)
                .mapToDouble(n -> ((User) n).getFollowerCount())
                .sum();

        for (Node node : graph.getAllNodes()) {
            if (node instanceof User) {
                User user = (User) node;
                scores.put(node, user.getFollowerCount() / totalFollowers);
            }
        }
    }

    private double calculateCombinedScore(Node node) {
        if (!(node instanceof User)) {
            return 0.0;
        }
        return (dampingFactor * calculateNetworkScore(node)) +
                ((1 - dampingFactor) * calculateMetricScore((User) node));
    }

    private double calculateNetworkScore(Node node) {
        double incomingScoreSum = 0.0;

        for (Node sourceNode : graph.getAllNodes()) {
            List<Edge> outgoingEdges = graph.getOutgoingEdges(sourceNode);
            double totalOutWeight = getTotalOutgoingWeight(outgoingEdges);

            if (totalOutWeight > 0) {
                double sourceScore = scores.get(sourceNode);
                for (Edge edge : outgoingEdges) {
                    if (edge.getTargetUser().equals(node)) {
                        incomingScoreSum += (sourceScore * edge.getWeight()) / totalOutWeight;
                    }
                }
            }
        }

        return incomingScoreSum;
    }

    private double calculateMetricScore(User user) {
        double followerScore = user.getFollowerCount() * 1.0;
        double interactionScore = (user.getReacts() * 2.0) +
                (user.getComments() * 3.0) +
                (user.getReposts() * 2.0);
        return (followerScore + interactionScore) / 1000000.0;
    }

    private double getTotalOutgoingWeight(List<Edge> edges) {
        return edges.stream()
                .mapToDouble(Edge::getWeight)
                .sum();
    }

    private void normalizeScores() {
        double sum = scores.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        if (sum > 0) {
            scores.replaceAll((node, score) -> score / sum);
        }
    }
}