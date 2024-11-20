import java.util.HashMap;
import java.util.Map;

public class Graph {
    private Map<Integer, Node> nodes;
    private Map<Integer, User> users;

    public Graph() {
        nodes = new HashMap<>();
        users = new HashMap<>();
    }

    public void addUser(User user) {
        nodes.put(user.getId(), user);
        users.put(user.getId(), user);
    }

    public void addTweet(Tweet tweet) {
        nodes.put(tweet.getId(), tweet);
    }

    public void addEdge(int sourceId, int targetId) {
        Node source = nodes.get(sourceId);
        Node target = nodes.get(targetId);

        if (source instanceof User && target instanceof User) {
            ((User) source).addFollowing((User) target);
            ((User) target).addFollower((User) source);
        } else if (source instanceof User && target instanceof Tweet) {
            ((Tweet) target).addInteraction((User) source);
        }
    }

    public Map<Integer, Node> getNodes() {
        return nodes;
    }

    public Map<Integer, User> getUsers() {
        return users;
    }
}
