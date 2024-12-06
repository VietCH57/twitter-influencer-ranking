package TwitterInfluencerRanking;

public class User extends Node {
    private String username;
    private int followerCount;
    private int followingCount;
    private String linkToProfile;

    //constructor
    public User() {
        super();
        this.username = null;
        this.followerCount = 0;
        this.followingCount = 0;
        this.linkToProfile = null;
    }

    public User(int id, String username, int followerCount, int followingCount, String linkToProfile) {
        super(id);
        this.username = username;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.linkToProfile = linkToProfile;
    }

    //getter
    public String getUsername() {
        return username;
    }
    public int getFollowerCount() {
        return followerCount;
    }
    public int getFollowingCount() {
        return followingCount;
    }
    public String getLinkToProfile() {
        return linkToProfile;
    }
}