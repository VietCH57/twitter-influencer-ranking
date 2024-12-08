package Visualizer;

import TwitRank.elements.Edge;
import TwitRank.elements.Node;
import TwitRank.elements.User;
import TwitRank.elements.KoL;
import TwitRank.graph.Graph;

public class GraphConverter {
    private final Graph sourceGraph;
    private final org.graphstream.graph.Graph targetGraph;

    public GraphConverter(Graph sourceGraph, org.graphstream.graph.Graph targetGraph) {
        this.sourceGraph = sourceGraph;
        this.targetGraph = targetGraph;
    }

    public void convert() {
        addNodes();
        addEdges();
    }

    private void addNodes() {
        for (Node node : sourceGraph.getAllNodes()) {
            if (node instanceof User) {
                String nodeId = String.valueOf(node.getId());
                org.graphstream.graph.Node gsNode = targetGraph.addNode(nodeId);
                // Store username as attribute but don't set label yet
                gsNode.setAttribute("username", ((User) node).getUsername());

                if (node instanceof KoL) {
                    gsNode.setAttribute("ui.class", "kol");
                } else {
                    gsNode.setAttribute("ui.class", "user");
                }
            }
        }
    }

    private void addEdges() {
        java.util.Set<String> addedEdges = new java.util.HashSet<>();

        for (Node sourceNode : sourceGraph.getAllNodes()) {
            for (Edge edge : sourceGraph.getOutgoingEdges(sourceNode)) {
                String sourceId = String.valueOf(edge.getSourceUser().getId());
                String targetId = String.valueOf(edge.getTargetUser().getId());

                // Create a unique identifier for this edge
                String edgeKey = sourceId + "->" + targetId;

                if (!addedEdges.contains(edgeKey)) {
                    try {
                        String edgeId = "edge" + addedEdges.size();
                        org.graphstream.graph.Edge gsEdge = targetGraph.addEdge(edgeId, sourceId, targetId, true);

                        // Set edge class based on relationship type
                        switch (edge.getType()) {
                            case FOLLOW:
                                gsEdge.setAttribute("ui.class", "follow");
                                break;
                            case RETWEET:
                                gsEdge.setAttribute("ui.class", "repost");
                                break;
                            case REPLY:
                                gsEdge.setAttribute("ui.class", "comment");
                                break;
                        }

                        addedEdges.add(edgeKey);
                    } catch (Exception e) {
                        System.out.println("Skipping duplicate edge: " + edgeKey);
                    }
                }
            }
        }
    }
}