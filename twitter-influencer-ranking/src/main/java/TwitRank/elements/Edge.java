package TwitRank.elements;

public class Edge {
    private Node sourceUser;        // Source User from sheets
    private int sourceUserId;       // Source User ID from sheets
    private Node targetUser;        // Target User from sheets
    private int targetUserId;       // Target User ID from sheets
    private EdgeType type;          // Type of relationship
    private double weight;          // Weight of the relationship

    public Edge(Node sourceUser, int sourceUserId, Node targetUser, int targetUserId, EdgeType type, double weight) {
        this.sourceUser = sourceUser;
        this.sourceUserId = sourceUserId;
        this.targetUser = targetUser;
        this.targetUserId = targetUserId;
        this.type = type;
        this.weight = weight;
    }

    // Getters
    public Node getSourceUser() { return sourceUser; }
    public int getSourceUserId() { return sourceUserId; }
    public Node getTargetUser() { return targetUser; }
    public int getTargetUserId() { return targetUserId; }
    public EdgeType getType() { return type; }
    public double getWeight() { return weight; }
}