package CrawlData;

import java.util.*;

public class Page {
    private String name;
    private String userName;
    private String numOfFollower;
    private String numOfFollowing;
    private String linkPage;
    private String linkHotTweet;
    private String numOfViewInHotTweet;
    private String numOfReactInHotTweet;
    private String numOfCommentInHotTweet;
    private String numOfRepostInHotTweet;
    private Map<String, List<String>> userLists;// Follower, Following, Repost, Comment
    private static int i = 0;
    private int id;

    public Page() {
        this.userLists = new HashMap<>();
        this.userLists.put("Follower", new ArrayList<>());
        this.userLists.put("Following", new ArrayList<>());
        this.userLists.put("Repost", new ArrayList<>());
        this.userLists.put("Comment", new ArrayList<>());
    }



    // Constructor voi thong tin co ban
    public Page(String name, String userName, String numOfFollower, String numOfFollowing, String linkPage, String linkHotTweet, String numOfViewInHotTweet, String numOfReactInHotTweet, String numOfCommentInHotTweet, String numOfRepostInHotTweet) {
        this();
        this.name = name;
        this.userName = userName;
        this.numOfFollower = numOfFollower;
        this.numOfFollowing = numOfFollowing;
        this.linkPage = linkPage;
        this.linkHotTweet = linkHotTweet;
        this.numOfViewInHotTweet = numOfViewInHotTweet;
        this.numOfReactInHotTweet = numOfReactInHotTweet;
        this.numOfCommentInHotTweet = numOfCommentInHotTweet;
        this.numOfRepostInHotTweet = numOfRepostInHotTweet;
        id = i;
        i++;
    }

    // Them vao danh sach
    public void addUserToList(String listType, String userName) {
        userLists.computeIfAbsent(listType, k -> new ArrayList<>()).add(userName);
    }
    //Lay ra khoi danh sach
    public List<String> getList(String listType) {
        return userLists.getOrDefault(listType, new ArrayList<>());
    }

    public String getName() {
        return name;
    }

    public String getUserName() {
        return userName;
    }

    public String getNumOfFollower() {
        return numOfFollower;
    }

    public String getNumOfFollowing() {
        return numOfFollowing;
    }

    public String getLinkPage() {
        return linkPage;
    }

    public String getLinkHotTweet() {
        return linkHotTweet;
    }

    public String getNumOfViewInHotTweet() {
        return numOfViewInHotTweet;
    }

    public String getNumOfReactInHotTweet() {
        return numOfReactInHotTweet;
    }

    public String getNumOfCommentInHotTweet() {
        return numOfCommentInHotTweet;
    }

    public String getNumOfRepostInHotTweet() {
        return numOfRepostInHotTweet;
    }

    public void FixComment (){
        if(!userLists.get("Comment").isEmpty()){
            userLists.get("Comment").removeFirst();
            System.out.println("Đã xóa phần tử đầu tiên");
        }
    }

    public int getId() {
        return id;
    }
}
