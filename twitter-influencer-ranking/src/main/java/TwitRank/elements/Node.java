package TwitRank.elements;

public abstract class Node {
    protected int id;

    //constructor
    public Node() {
        this.id = 0;
    }
    public Node(int id) {
        this.id = id;
    }

    //getter
    public int getId() {
        return id;
    }
}