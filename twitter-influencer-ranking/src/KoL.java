public class KoL extends User {
    private static final int MIN_FOLLOWER_COUNT = 10000;

    public KoL() {
        super();
    }

    public KoL(int id, String username, int followerCount, int followingCount, String linkToProfile) {
        super(id, username, followerCount, followingCount, linkToProfile);
        if (followerCount < MIN_FOLLOWER_COUNT) {
            throw new IllegalArgumentException("Follower count too low for KOL classification");
        }
    }
}