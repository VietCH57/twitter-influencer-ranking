import java.util.ArrayList;
import java.util.List;

public class User extends Node {
    private String name;
    private String username;
    private List<User> followers;
    private List<User> following;

    public User(int id, String name, String username) {
        super(id);
        this.name = name;
        this.username = username;
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
    }

    public void addFollower(User follower) {
        followers.add(follower);
    }

    public void addFollowing(User followee) {
        following.add(followee);
    }

    public List<User> getFollowers() {
        return followers;
    }

    public List<User> getFollowing() {
        return following;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name='" + name + '\'' + ", username='" + username + '\'' + '}';
    }
}
