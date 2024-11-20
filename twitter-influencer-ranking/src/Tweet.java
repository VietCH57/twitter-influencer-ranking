import java.util.ArrayList;
import java.util.List;

public class Tweet extends Node {
    private String content;
    private List<User> interactions;

    public Tweet(int id, String content) {
        super(id);
        this.content = content;
        this.interactions = new ArrayList<>();
    }

    public void addInteraction(User user) {
        interactions.add(user);
    }

    public List<User> getInteractions() {
        return interactions;
    }

    @Override
    public String toString() {
        return "Tweet{" + "id=" + id + ", content='" + content + '\'' + '}';
    }
}
