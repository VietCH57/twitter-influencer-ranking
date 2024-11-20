import java.util.Map;

public class PageRankCalculator {
    private static final double DAMPING_FACTOR = 0.85;
    private static final int MAX_ITERATIONS = 100;
    private static final double CONVERGENCE_THRESHOLD = 1e-6;

    public static void calculate(Graph graph) {
        Map<Integer, Node> nodes = graph.getNodes();
        int totalNodes = nodes.size();
        double initialRank = 1.0 / totalNodes;

        // Initialize all nodes with equal rank
        for (Node node : nodes.values()) {
            node.setPageRank(initialRank);
        }

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            double totalDifference = 0;

            for (Node node : nodes.values()) {
                double rankSum = 0;

                if (node instanceof User) {
                    User user = (User) node;
                    for (User follower : user.getFollowers()) {
                        rankSum += follower.getPageRank() / follower.getFollowing().size();
                    }
                }

                // Apply damping factor
                double newRank = (1 - DAMPING_FACTOR) / totalNodes + DAMPING_FACTOR * rankSum;
                totalDifference += Math.abs(newRank - node.getPageRank());
                node.setPageRank(newRank);
            }

            if (totalDifference < CONVERGENCE_THRESHOLD) {
                break;
            }
        }
    }
}
