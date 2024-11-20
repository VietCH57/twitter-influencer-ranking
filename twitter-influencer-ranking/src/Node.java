public abstract class Node {
    protected int id;
    protected double pageRank;

    public Node() {
        this.id = -1;
        this.pageRank = 0.0;
    }

    public Node(int id) {
        this.id = id;
        this.pageRank = 0.0;
    }

    public int getId() {
        return id;
    }

    public double getPageRank() {
        return pageRank;
    }

    public void setPageRank(double pageRank) {
        this.pageRank = pageRank;
    }
}
