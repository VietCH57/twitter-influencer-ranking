package CrawlData;

import java.util.ArrayList;
import java.util.List;

public class Page {
    private String Name;
    private String userName;
    private String numOfFollower;
    private String numOfFollowing;
    private String linkPage;
    private String linkHotTweet;
    private String numOfViewInHotTweet;
    private String numOfReactInHotTweet;
    private String numOfCommentInHotTweet;
    private String numOfRepostInHotTweet;
    List<String> listFollower = new ArrayList<String>();
    List<String> listFollowing = new ArrayList<>();
    List<String> listUserNameRepost = new ArrayList<>();
    List<String> listUserNameComment = new ArrayList<>();
    private static int i = 0;
    private int id;
    ExcelFileWriter excelFileWriter = new ExcelFileWriter();

    public Page(){
        this.Name = null;
        this.userName = null;
        this.numOfFollower = null;
        this.numOfFollowing = null;
        this.linkPage = null;
        this.linkHotTweet = null;
        this.numOfViewInHotTweet = null;
        this.numOfReactInHotTweet = null;
        this.numOfCommentInHotTweet = null;
        this.numOfRepostInHotTweet = null;
        this.listFollower = null;
        this.listFollowing = null;
        this.listUserNameRepost = null;
        this.listUserNameComment = null;
        id = i;
    }

    public Page(String Name, String userName, String numOfFollower, String numOfFollowing, String linkPage, String linkHotTweet, String numOfViewInHotTweet, String numOfReactInHotTweet, String numOfCommentInHotTweet, String numOfRepostInHotTweet){
        this.Name = Name;
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
    public void setlistFollower(List<String> userNameFollower){
        listFollower.addAll(userNameFollower);
    }
    public void setlistFollowing(List<String> userNameFollowing){
        listFollowing.addAll(userNameFollowing);
    }
    public void setlistUserNameRepost(List<String> userNameRepost){
        listUserNameRepost.addAll(userNameRepost);
    }

    public void setlistUserNameComment(List<String> userNameComment){
        listUserNameComment.addAll(userNameComment);
    }

    public int getID(){
        return id;
    }

    public String getName(){
        return Name;
    }

    public String getUserName(){
        return userName;
    }

    public String getNumOfFollower(){
        return numOfFollower;
    }

    public String getNumOfFollowing(){
        return numOfFollowing;
    }

    public String getLinkPage(){
        return linkPage;
    }

    public String getLinkHotTweet(){
        return linkHotTweet;
    }

    public String getNumOfViewInHotTweet(){
        return numOfViewInHotTweet;
    }

    public String getNumOfReactInHotTweet(){
        return numOfReactInHotTweet;
    }

    public String getNumOfCommentInHotTweet(){
        return numOfCommentInHotTweet;
    }

    public String getNumOfRepostInHotTweet(){
        return numOfRepostInHotTweet;
    }

    public void writeToExcel(){
        excelFileWriter.writeRowData(this);
    }
}
