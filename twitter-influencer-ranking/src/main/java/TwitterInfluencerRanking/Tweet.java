package TwitterInfluencerRanking;

public class Tweet extends Node {
    private int userId;
    private int likeCount;
    private int replyCount;
    private int retweetCount;
    private String linkToTweet;

    public Tweet() {
        super();
        this.userId = 0;
        this.likeCount = 0;
        this.replyCount = 0;
        this.retweetCount = 0;
        this.linkToTweet = null;
    }

    public Tweet(int id, int userId, int likeCount, int replyCount, int retweetCount, String linkToTweet) {
        super(id);
        this.userId = userId;
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.retweetCount = retweetCount;
        this.linkToTweet = linkToTweet;
    }

    public int getUserId() {
        return userId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public String getLinkToTweet() {
        return linkToTweet;
    }
}