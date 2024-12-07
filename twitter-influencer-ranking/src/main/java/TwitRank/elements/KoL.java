package TwitRank.elements;

public class KoL extends User {
    private static final int MIN_FOLLOWER_COUNT = 100000;
    private static final int MIN_REACTS = 1000;
    private static final int MIN_COMMENTS = 100;
    private static final int MIN_REPOSTS = 300;

    public KoL(int id, String name, String username, int followerCount, int followingCount,
               String linkToProfile, String linkToTweet, int views, int reacts,
               int comments, int reposts) {
        super(id, name, username, followerCount, followingCount, linkToProfile,
                linkToTweet, views, reacts, comments, reposts);

        if (followerCount < MIN_FOLLOWER_COUNT ||
                reacts < MIN_REACTS ||
                comments < MIN_COMMENTS ||
                reposts < MIN_REPOSTS) {
            throw new IllegalArgumentException("Metrics too low for KOL classification: " +
                    "followerCount=" + followerCount + " (min " + MIN_FOLLOWER_COUNT + "), " +
                    "reacts=" + reacts + " (min " + MIN_REACTS + "), " +
                    "comments=" + comments + " (min " + MIN_COMMENTS + "), " +
                    "reposts=" + reposts + " (min " + MIN_REPOSTS + ")");
        }
    }

    // Static getters for minimum thresholds
    public static int getMinFollowerCount() { return MIN_FOLLOWER_COUNT; }
    public static int getMinReacts() { return MIN_REACTS; }
    public static int getMinComments() { return MIN_COMMENTS; }
    public static int getMinReposts() { return MIN_REPOSTS; }
}