package TwitRank.rank;

import TwitRank.elements.Edge;
import TwitRank.elements.Node;
import TwitRank.elements.User;
import TwitRank.graph.Graph;
import org.apache.commons.math3.linear.*;

import java.util.*;

public class PageRank {
    private final double DAMPING_FACTOR;
    private final int MAX_ITERATIONS;
    private final Graph graph;

    // Weights for user metrics
    private static final double FOLLOWER_WEIGHT = 0.3;
    private static final double VIEW_WEIGHT = 0.2;
    private static final double REACT_WEIGHT = 0.2;
    private static final double COMMENT_WEIGHT = 0.15;
    private static final double REPOST_WEIGHT = 0.15;

    public PageRank(Graph graph, double DAMPING_FACTOR, int MAX_ITERATIONS) {
        this.graph = graph;
        this.DAMPING_FACTOR = DAMPING_FACTOR;
        this.MAX_ITERATIONS = MAX_ITERATIONS;
    }

    private double calculateUserImportance(User user) {
        // Normalize each metric using log scale to handle large numbers
        double normalizedFollowers = Math.log1p(user.getFollowerCount());
        double normalizedViews = Math.log1p(user.getViews());
        double normalizedReacts = Math.log1p(user.getReacts());
        double normalizedComments = Math.log1p(user.getComments());
        double normalizedReposts = Math.log1p(user.getReposts());

        // Combine metrics using weights
        return (normalizedFollowers * FOLLOWER_WEIGHT +
                normalizedViews * VIEW_WEIGHT +
                normalizedReacts * REACT_WEIGHT +
                normalizedComments * COMMENT_WEIGHT +
                normalizedReposts * REPOST_WEIGHT);
    }

    public Map<Node, Double> computePageRank() {
        Set<Node> nodes = graph.getAllNodes();
        int size = nodes.size();
        RealMatrix matrix = new Array2DRowRealMatrix(size, size);

        // Create node to index mapping
        List<Node> nodeList = new ArrayList<>(nodes);
        Map<Node, Integer> nodeIndexMap = new HashMap<>();
        for (int i = 0; i < nodeList.size(); i++) {
            nodeIndexMap.put(nodeList.get(i), i);
        }

        // Calculate weighted edges considering both edge type and user importance
        for (Node node : nodes) {
            if (!(node instanceof User)) continue;
            User sourceUser = (User) node;
            List<Edge> outgoingEdges = graph.getOutgoingEdges(node);
            if (outgoingEdges.isEmpty()) continue;

            int fromIndex = nodeIndexMap.get(node);
            double sourceImportance = calculateUserImportance(sourceUser);

            // Calculate total combined weight for normalization
            double totalWeight = 0.0;
            for (Edge edge : outgoingEdges) {
                double edgeTypeWeight = edge.getWeight();
                totalWeight += edgeTypeWeight * sourceImportance;
            }

            // Add weighted edges to matrix
            for (Edge edge : outgoingEdges) {
                int toIndex = nodeIndexMap.get(edge.getTarget());
                double edgeTypeWeight = edge.getWeight();
                double normalizedWeight = (edgeTypeWeight * sourceImportance) / totalWeight;
                matrix.addToEntry(toIndex, fromIndex, normalizedWeight);
            }
        }

        // Initialize rank vector with personalized weights based on user metrics
        RealVector rankVector = new ArrayRealVector(size);
        RealVector teleportVector = new ArrayRealVector(size);
        double totalImportance = 0.0;

        // Calculate initial ranks based on user importance
        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            if (node instanceof User) {
                double importance = calculateUserImportance((User) node);
                totalImportance += importance;
                rankVector.setEntry(i, importance);
                teleportVector.setEntry(i, importance);
            }
        }

        // Normalize initial vectors
        if (totalImportance > 0) {
            for (int i = 0; i < size; i++) {
                rankVector.setEntry(i, rankVector.getEntry(i) / totalImportance);
                teleportVector.setEntry(i, teleportVector.getEntry(i) / totalImportance);
            }
        } else {
            // Fallback to uniform distribution if no importance scores
            rankVector = new ArrayRealVector(size, 1.0 / size);
            teleportVector = new ArrayRealVector(size, 1.0 / size);
        }

        // Power iteration method with damping factor
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            RealVector newRankVector = matrix.operate(rankVector)
                    .mapMultiply(DAMPING_FACTOR)
                    .add(teleportVector.mapMultiply(1 - DAMPING_FACTOR));

            // Check for convergence
            if (newRankVector.subtract(rankVector).getNorm() < 1e-10) {
                System.out.println("PageRank converged after " + (i + 1) + " iterations");
                rankVector = newRankVector;
                break;
            }

            rankVector = newRankVector;
        }

        // Create result map
        Map<Node, Double> pageRankScores = new HashMap<>();
        for (int i = 0; i < nodeList.size(); i++) {
            pageRankScores.put(nodeList.get(i), rankVector.getEntry(i));
        }

        return pageRankScores;
    }
}