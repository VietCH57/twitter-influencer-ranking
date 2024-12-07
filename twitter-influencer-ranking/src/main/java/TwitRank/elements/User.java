package TwitRank.elements;

public class User extends Node {
    private String name;
    private String username;
    private int followerCount;
    private int followingCount;
    private String linkToProfile;
    private String linkToTweet;  // Added this field
    private int views;
    private int reacts;
    private int comments;
    private int reposts;

    public User() {
        super();
        this.name = null;
        this.username = null;
        this.followerCount = 0;
        this.followingCount = 0;
        this.linkToProfile = null;
        this.linkToTweet = null;  // Initialize new field
        this.views = 0;
        this.reacts = 0;
        this.comments = 0;
        this.reposts = 0;
    }

    public User(int id, String name, String username, int followerCount, int followingCount,
                String linkToProfile, String linkToTweet, int views, int reacts,
                int comments, int reposts) {
        super(id);
        this.name = name;
        this.username = username;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.linkToProfile = linkToProfile;
        this.linkToTweet = linkToTweet;  // Add to constructor
        this.views = views;
        this.reacts = reacts;
        this.comments = comments;
        this.reposts = reposts;
    }

    // Add getter for new field
    public String getLinkToTweet() {
        return linkToTweet;
    }

    // Existing getters remain the same
    public String getName() { return name; }
    public String getUsername() { return username; }
    public int getFollowerCount() { return followerCount; }
    public int getFollowingCount() { return followingCount; }
    public String getLinkToProfile() { return linkToProfile; }
    public int getViews() { return views; }
    public int getReacts() { return reacts; }
    public int getComments() { return comments; }
    public int getReposts() { return reposts; }
}