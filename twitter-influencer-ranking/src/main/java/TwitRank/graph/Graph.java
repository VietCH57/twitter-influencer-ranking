package TwitRank.graph;

import TwitRank.elements.Edge;
import TwitRank.elements.Node;
import java.util.*;

public class Graph {
    private final Map<Node, List<Edge>> adjacencyList;
    private final Map<Integer, Node> nodeById;

    public Graph() {
        this.adjacencyList = new HashMap<>();
        this.nodeById = new HashMap<>();
    }

    public void addNode(Node node) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
        nodeById.put(node.getId(), node);
    }

    public void addEdge(Edge edge) {
        adjacencyList.get(edge.getSourceUser()).add(edge);
    }

    public List<Edge> getOutgoingEdges(Node node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    public Set<Node> getAllNodes() {
        return adjacencyList.keySet();
    }

    public Node getNodeById(int id) {
        return nodeById.get(id);
    }
}