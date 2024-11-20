import java.util.*;

public class PageRankCalculator {
    private static final double DAMPING_FACTOR = 0.85;
    private static final double EPSILON = 0.0001;
    private static final int MAX_ITERATIONS = 100;

    public Map<Node, Double> calculate(Graph graph) {
        Map<Node, Double> ranks = new HashMap<>();
        Set<Node> nodes = new HashSet<>(graph.getAllNodes());

        // Khởi tạo rank ban đầuE
        double initialRank = 1.0 / nodes.size();
        for (Node node : nodes) {
            ranks.put(node, initialRank);
        }

        // Lặp cho đến khi hội tụ
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            Map<Node, Double> newRanks = new HashMap<>();
            double maxDiff = 0.0;

            for (Node node : nodes) {
                double sum = 0.0;
                for (Edge edge : graph.getOutgoingEdges(node)) {
                    Node source = edge.getSource();
                    double sourceRank = ranks.get(source);
                    double weight = edge.getWeight();
                    sum += sourceRank * weight;
                }

                double newRank = (1 - DAMPING_FACTOR) / nodes.size() +
                        DAMPING_FACTOR * sum;
                newRanks.put(node, newRank);
                maxDiff = Math.max(maxDiff, Math.abs(newRank - ranks.get(node)));
            }

            ranks = newRanks;
            if (maxDiff < EPSILON) break;
        }

        return ranks;
    }
}