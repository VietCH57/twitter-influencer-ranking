package TwitterInfluencerRanking;

import org.apache.commons.math3.linear.*;

import java.util.*;

public class PageRank {
    private final double dampingFactor;
    private final int maxIterations;
    private Graph graph;

    public PageRank(Graph graph, double dampingFactor, int maxIterations) {
        this.graph = graph;
        this.dampingFactor = dampingFactor;
        this.maxIterations = maxIterations;
    }

    public Map<Node, Double> computePageRank() {
        Set<Node> nodes = graph.getAllNodes();
        int size = nodes.size();
        RealMatrix matrix = new Array2DRowRealMatrix(size, size);

        List<Node> nodeList = new ArrayList<>(nodes);
        Map<Node, Integer> nodeIndexMap = new HashMap<>();
        for (int i = 0; i < nodeList.size(); i++) {
            nodeIndexMap.put(nodeList.get(i), i);
        }

        for (Node node : nodes) {
            List<Edge> outgoingEdges = graph.getOutgoingEdges(node);
            int fromIndex = nodeIndexMap.get(node);
            for (Edge edge : outgoingEdges) {
                int toIndex = nodeIndexMap.get(edge.getTarget());
                matrix.addToEntry(toIndex, fromIndex, 1.0 / outgoingEdges.size());
            }
        }

        RealVector rankVector = new ArrayRealVector(size, 1.0 / size);
        RealVector teleportVector = new ArrayRealVector(size, 1.0 / size);

        for (int i = 0; i < maxIterations; i++) {
            rankVector = matrix.operate(rankVector).mapMultiply(dampingFactor).add(teleportVector.mapMultiply(1 - dampingFactor));
        }

        Map<Node, Double> pageRankScores = new HashMap<>();
        for (int i = 0; i < nodeList.size(); i++) {
            pageRankScores.put(nodeList.get(i), rankVector.getEntry(i));
        }

        return pageRankScores;
    }
}