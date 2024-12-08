package TwitterInfluencerRanking;

public class KoL extends User {
    private static final int MIN_FOLLOWER_COUNT = 500000;
    private static final int MIN_REACTS = 2000;
    private static final int MIN_COMMENTS = 100000;
    private static final int MIN_REPOSTS = 500;

    public KoL() {
        super();
    }

    public KoL(int id, String name, String username, int followerCount, int followingCount, String linkToProfile, int views, int reacts, int comments, int reposts) {
        super(id, name, username, followerCount, followingCount, linkToProfile, views, reacts, comments, reposts);
        if (followerCount < MIN_FOLLOWER_COUNT || reacts < MIN_REACTS || comments < MIN_COMMENTS || reposts < MIN_REPOSTS) {
            throw new IllegalArgumentException("Metrics too low for KOL classification");
        }
    }

    public static int getMinFollowerCount() { return MIN_FOLLOWER_COUNT; }
    public static int getMinReacts() { return MIN_REACTS; }
    public static int getMinComments() { return MIN_COMMENTS; }
    public static int getMinReposts() { return MIN_REPOSTS; }
}