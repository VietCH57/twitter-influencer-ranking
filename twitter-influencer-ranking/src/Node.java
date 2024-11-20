public abstract class Node {
    protected int id;

    public Node() {
        this.id = 0;
    }

    public Node(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}