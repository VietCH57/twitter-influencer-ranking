package TwitterInfluencerRanking;

public class KoL extends User {
    public static final int MIN_FOLLOWER_COUNT = 300;

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