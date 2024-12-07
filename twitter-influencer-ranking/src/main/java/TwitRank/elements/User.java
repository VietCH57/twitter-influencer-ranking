package TwitRank.elements;

public class User extends Node {
    private String name;            // Name column
    private String username;        // UserName column
    private int followerCount;      // Followers column
    private int followingCount;     // Following column
    private String linkToProfile;   // LinkToProfile column
    private String linkToTweet;     // LinkToTweet column
    private int views;              // Views column
    private int reacts;             // Reacts column
    private int comments;           // Comments column
    private int reposts;            // Reposts column

    // Default constructor
    public User() {
        super();
        this.name = "";
        this.username = "";
        this.followerCount = 0;
        this.followingCount = 0;
        this.linkToProfile = "";
        this.linkToTweet = "";
        this.views = 0;
        this.reacts = 0;
        this.comments = 0;
        this.reposts = 0;
    }

    // Full constructor
    public User(int id, String name, String username, int followerCount, int followingCount,
                String linkToProfile, String linkToTweet, int views, int reacts,
                int comments, int reposts) {
        super(id);  // ID column
        this.name = name;
        this.username = username;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.linkToProfile = linkToProfile;
        this.linkToTweet = linkToTweet;
        this.views = views;
        this.reacts = reacts;
        this.comments = comments;
        this.reposts = reposts;
    }

    // Getters
    public String getName() { return name; }
    public String getUsername() { return username; }
    public int getFollowerCount() { return followerCount; }
    public int getFollowingCount() { return followingCount; }
    public String getLinkToProfile() { return linkToProfile; }
    public String getLinkToTweet() { return linkToTweet; }
    public int getViews() { return views; }
    public int getReacts() { return reacts; }
    public int getComments() { return comments; }
    public int getReposts() { return reposts; }
}