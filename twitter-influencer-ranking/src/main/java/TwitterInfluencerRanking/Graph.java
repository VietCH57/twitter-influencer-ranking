package TwitterInfluencerRanking;

import java.util.*;


public class Graph {
    private Map<Node, List<Edge>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    public void addNode(Node node) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
    }

    public void addEdge(Node source, Node target, EdgeType type, double weight) {
        Edge edge = new Edge(source, target, type, weight);
        adjacencyList.get(source).add(edge);
    }

    public List<Edge> getOutgoingEdges(Node node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    public Set<Node> getAllNodes() {
        return adjacencyList.keySet();
    }
}