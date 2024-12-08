package TwitRank.rank;

import TwitRank.elements.Edge;
import TwitRank.elements.Node;
import TwitRank.elements.User;
import TwitRank.graph.Graph;
import org.apache.commons.math3.linear.*;

import java.util.*;

public class PageRank {
    private final double dampingFactor;
    private final int maxIterations;
    private final Graph graph;

    // Weights for user metrics
    private static final double FOLLOWER_WEIGHT = 0.3;
    private static final double VIEW_WEIGHT = 0.2;
    private static final double REACT_WEIGHT = 0.2;
    private static final double COMMENT_WEIGHT = 0.15;
    private static final double REPOST_WEIGHT = 0.15;

    public PageRank(Graph graph, double dampingFactor, int maxIterations) {
        this.graph = graph;
        this.dampingFactor = dampingFactor;
        this.maxIterations = maxIterations;
    }

    private double calculateUserImportance(User user) {
        double normalizedFollowers = Math.log1p(user.getFollowerCount());
        double normalizedViews = Math.log1p(user.getViews());
        double normalizedReacts = Math.log1p(user.getReacts());
        double normalizedComments = Math.log1p(user.getComments());
        double normalizedReposts = Math.log1p(user.getReposts());

        return (normalizedFollowers * FOLLOWER_WEIGHT +
                normalizedViews * VIEW_WEIGHT +
                normalizedReacts * REACT_WEIGHT +
                normalizedComments * COMMENT_WEIGHT +
                normalizedReposts * REPOST_WEIGHT);
    }

    public Map<Node, Double> computePageRank() {
        Set<Node> nodes = graph.getAllNodes();
        int size = nodes.size();

        System.out.println("Starting PageRank computation for " + size + " nodes");

        RealMatrix transitionMatrix = new Array2DRowRealMatrix(size, size);
        List<Node> nodeList = new ArrayList<>(nodes);
        Map<Node, Integer> nodeIndexMap = createNodeIndexMapping(nodeList);

        System.out.println("Building transition matrix...");
        buildTransitionMatrix(transitionMatrix, nodeList, nodeIndexMap);

        RealVector rankVector = initializeRankVector(nodeList);
        RealVector teleportVector = createTeleportVector(nodeList);

        System.out.println("Starting power iteration...");
        rankVector = performPowerIteration(transitionMatrix, rankVector, teleportVector);

        return createFinalResults(nodeList, rankVector);
    }

    private Map<Node, Integer> createNodeIndexMapping(List<Node> nodeList) {
        Map<Node, Integer> nodeIndexMap = new HashMap<>();
        for (int i = 0; i < nodeList.size(); i++) {
            nodeIndexMap.put(nodeList.get(i), i);
        }
        return nodeIndexMap;
    }

    private void buildTransitionMatrix(RealMatrix matrix, List<Node> nodeList,
                                       Map<Node, Integer> nodeIndexMap) {
        for (Node node : nodeList) {
            if (!(node instanceof User sourceUser)) continue;

            List<Edge> outgoingEdges = graph.getOutgoingEdges(node);
            if (outgoingEdges.isEmpty()) continue;

            int fromIndex = nodeIndexMap.get(node);
            double sourceImportance = calculateUserImportance(sourceUser);

            double totalWeight = outgoingEdges.stream()
                    .mapToDouble(edge -> edge.getWeight() * sourceImportance)
                    .sum();

            for (Edge edge : outgoingEdges) {
                int toIndex = nodeIndexMap.get(edge.getTargetUser());
                double normalizedWeight = (edge.getWeight() * sourceImportance) / totalWeight;
                matrix.setEntry(toIndex, fromIndex, normalizedWeight);
            }
        }
    }

    private RealVector initializeRankVector(List<Node> nodeList) {
        RealVector rankVector = new ArrayRealVector(nodeList.size());
        double totalImportance = 0.0;

        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            if (node instanceof User user) {
                double importance = calculateUserImportance(user);
                totalImportance += importance;
                rankVector.setEntry(i, importance);
            }
        }

        if (totalImportance > 0) {
            for (int i = 0; i < nodeList.size(); i++) {
                rankVector.setEntry(i, rankVector.getEntry(i) / totalImportance);
            }
        } else {
            double uniformValue = 1.0 / nodeList.size();
            for (int i = 0; i < nodeList.size(); i++) {
                rankVector.setEntry(i, uniformValue);
            }
        }

        return rankVector;
    }

    private RealVector createTeleportVector(List<Node> nodeList) {
        RealVector teleportVector = new ArrayRealVector(nodeList.size());
        double totalImportance = 0.0;

        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            if (node instanceof User user) {
                double importance = calculateUserImportance(user);
                totalImportance += importance;
                teleportVector.setEntry(i, importance);
            }
        }

        if (totalImportance > 0) {
            for (int i = 0; i < nodeList.size(); i++) {
                teleportVector.setEntry(i, teleportVector.getEntry(i) / totalImportance);
            }
        } else {
            double uniformValue = 1.0 / nodeList.size();
            for (int i = 0; i < nodeList.size(); i++) {
                teleportVector.setEntry(i, uniformValue);
            }
        }

        return teleportVector;
    }

    private RealVector performPowerIteration(RealMatrix transitionMatrix,
                                             RealVector rankVector,
                                             RealVector teleportVector) {
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            RealVector newRankVector = transitionMatrix.operate(rankVector)
                    .mapMultiply(dampingFactor)
                    .add(teleportVector.mapMultiply(1 - dampingFactor));

            double diff = newRankVector.subtract(rankVector).getNorm();

            if (diff < 1e-10) {
                return newRankVector;
            }

            rankVector = newRankVector;
        }

        System.out.println("Maximum iterations (" + maxIterations + ") reached");
        return rankVector;
    }

    private Map<Node, Double> createFinalResults(List<Node> nodeList, RealVector rankVector) {
        Map<Node, Double> pageRankScores = new HashMap<>();
        for (int i = 0; i < nodeList.size(); i++) {
            pageRankScores.put(nodeList.get(i), rankVector.getEntry(i));
        }
        return pageRankScores;
    }
}