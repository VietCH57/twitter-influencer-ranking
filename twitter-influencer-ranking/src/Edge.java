public class Edge {
    private Node source;
    private Node target;
    private EdgeType type;
    private double weight;

    public Edge(Node source, Node target, EdgeType type, double weight) {
        this.source = source;
        this.target = target;
        this.type = type;
        this.weight = weight;
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    public EdgeType getType() {
        return type;
    }

    public double getWeight() {
        return weight;
    }
}
