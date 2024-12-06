package TwitterInfluencerRanking;

import org.apache.commons.math3.linear.*;

import java.util.*;

public class PageRank {
    private final double DAMPING_FACTOR;
    private final int MAX_ITERATIONS;
    private Graph graph;

    public PageRank(Graph graph, double DAMPING_FACTOR, int MAX_ITERATIONS) {
        this.graph = graph;
        this.DAMPING_FACTOR = DAMPING_FACTOR;
        this.MAX_ITERATIONS = MAX_ITERATIONS;
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

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            rankVector = matrix.operate(rankVector).mapMultiply(DAMPING_FACTOR).add(teleportVector.mapMultiply(1 - DAMPING_FACTOR));
        }

        Map<Node, Double> pageRankScores = new HashMap<>();
        for (int i = 0; i < nodeList.size(); i++) {
            pageRankScores.put(nodeList.get(i), rankVector.getEntry(i));
        }

        return pageRankScores;
    }
}