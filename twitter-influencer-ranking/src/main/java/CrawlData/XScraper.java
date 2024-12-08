package CrawlData;

import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class XScraper {

    private List<String> detectNames = new ArrayList<>();
    private String nameIndex = ".//div[2]/div[1]/div/div[1]/div/div/div[1]/div/a/div/div[1]/span/span";
    private String userNameIndex = ".//div[2]/div[1]/div[1]/div[1]/div/div/div[2]/div/div[1]";
    private String linkPageIndex = ".//div[2]/div[1]/div/div[1]/div/div/div[1]/div/a";
    private String linkTweetIndex = ".//div[2]/div[1]/div/div[1]/div/div/div[2]/div/div[3]/a";
    private String numOfViewInHotTweetIndex = ".//div[2]/div[4]/div/div/div[4]/a/div/div[2]";
    private String numOfReactInHotTweetIndex = ".//div[2]/div[4]/div/div/div[3]/button/div/div[2]";
    private String numOfCommentInHotTweetIndex = ".//div[2]/div[4]/div/div/div[1]/button/div/div[2]";
    private String numOfRepostInHotTweetIndex = ".//div[2]/div[4]/div/div/div[2]/button/div/div[2]";
    private String numOfFollowingIndex1 = "/html/body/div[1]/div/div/div[2]/main/div/div/div/div[1]/div/div[3]/div/div/div/div/div[6]/div[1]/a/span[1]";
    private String numOfFollowingIndex2 = "/html/body/div[1]/div/div/div[2]/main/div/div/div/div[1]/div/div[3]/div/div/div/div/div[5]/div[1]/a/span[1]";
    private String numOfFollowerIndex1 = "/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[3]/div/div/div/div/div[6]/div[2]/a/span[1]";
    private String numOfFollowerIndex2 = "/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[3]/div/div/div/div/div[5]/div[2]/a/span[1]";

    private List<String> scraper1Index = Arrays.asList(nameIndex, userNameIndex, numOfViewInHotTweetIndex, numOfReactInHotTweetIndex, numOfCommentInHotTweetIndex, numOfRepostInHotTweetIndex);
    private List<String> scraper2Index = Arrays.asList(linkPageIndex, linkTweetIndex);
    //Khoi tao doi tuong cua lop TabManager de quan ly qua trinh chuyen tab
    TabManager tm = new TabManager();
    //Khoi tao doi tuong cua lop Scoller de quan ly cuon trang
    Scoller sc1 = new Scoller();

    public XScraper() {}

    public void setDetecNames (List<String> detectNames) {
        for (String detectName : detectNames) {
            this.detectNames.add(detectName);
        }
    }

    public List<WebElement> Tweet(WebDriver driver){
        List<WebElement> Tweets = driver.findElements(By.xpath("//section/div/div/div/div/div/article/div/div/div[2]"));
        return Tweets;
    }

    public void User (List<WebElement> Tweets, WebDriver driver, ExcelFileWriter excelFileWriter){
        for (WebElement xPathTweet : Tweets) {
            try {
                String detectName = xPathTweet.findElement(By.xpath(".//div[2]/div[1]/div[1]/div[1]/div/div/div[2]/div/div[1]")).getText();
                if (!this.detectNames.contains(detectName)) {
                    this.detectNames.add(detectName);
                } else {
                    continue;
                }
                if (checkToContinue(driver, xPathTweet)){
                    continue;
                }
                //Goi toi phuong thuc scrapUser de lay thong tin
                scrapeUser(driver, xPathTweet, excelFileWriter);

            } catch (TimeoutException e){
                System.out.println("Loi do bi treo khi xu ly Tweet: " + e.getMessage());
                continue;
            } catch (Exception e){
                System.out.println("Loi khi thuc hien xu ly Tweet: " + e.getMessage());
                continue;
            }
        }
    }

    public void scrapeUser(WebDriver driver, WebElement xPathTweet, ExcelFileWriter excelFileWriter) {
        try {

            String name = scraper1(nameIndex, xPathTweet);
            String username = scraper1(userNameIndex, xPathTweet);
            String linkpage = scraper2(linkPageIndex, xPathTweet);
            String linkhottweet = scraper2(linkTweetIndex, xPathTweet);

            String numofviewinhottweet = scraper1(numOfViewInHotTweetIndex, xPathTweet);
            String numofreactinhottweet = scraper1(numOfReactInHotTweetIndex, xPathTweet);
            String numofcommentinhottweet = scraper1(numOfCommentInHotTweetIndex, xPathTweet);
            String numofrepostinhottweet = scraper1(numOfRepostInHotTweetIndex, xPathTweet);

            tm.newTab(driver, linkpage);
            tm.switchTab(driver);
            String numoffollowing = scraper1(numOfFollowingIndex1, numOfFollowingIndex2, driver);
            String numoffollower = scraper1(numOfFollowerIndex1, numOfFollowerIndex2, driver);

            System.out.println(linkpage);

            //Khoi tao doi tuong cua lop Page de luu du lieu
            Page page = new Page(name, username, numoffollower, numoffollowing, linkpage, linkhottweet, numofviewinhottweet, numofreactinhottweet, numofcommentinhottweet, numofrepostinhottweet);

            driver.navigate().to(linkpage + "/following");
            Thread.sleep(5000);
            crawlUserNameFollowingFollowers(driver, page, "Following");
            driver.navigate().to(linkpage + "/followers");
            Thread.sleep(5000);
            crawlUserNameFollowingFollowers(driver, page, "Follower");
            driver.navigate().to(linkhottweet + "/retweets");
            Thread.sleep(5000);
            crawlUserNameRepost(driver, page, "Repost");
            driver.navigate().to(linkhottweet);
            Thread.sleep(5000);
            crawlUserNameComment(driver, page, "Comment");
            excelFileWriter.writePageData(page);
        } catch (TimeoutException e) {
            System.out.println("Loi do bi treo khi thuc hien scrapeUser: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Loi khi thuc hien scrapeUser: " + e.getMessage());
        } finally {
            List<String> tabs = new ArrayList<>(driver.getWindowHandles());
            String currentHandle = driver.getWindowHandle();
            if (!currentHandle.equals(tabs.get(0))){
                tm.closeTab(driver);
            }
        }

    }

    //Phuong thuc lay name va num
    public String scraper1 (String xPath, WebElement xPathTweet){
        try {
            WebElement M = xPathTweet.findElement(By.xpath(xPath));
            String m = M.getText();
            if (m.equals("")) {
                m = "0";
            }
            return m;
        } catch (NoSuchElementException e){
            System.out.println("Loi khong tim thay phan tu o scraper1: " + e.getMessage());
            return "";
        } catch (TimeoutException e){
            System.out.println("Loi do bi treo khi thuc hien scraper1: " + e.getMessage());
            return "";
        } catch (Exception e){
            System.out.println("Loi qq gi y em meo biet: " + e.getMessage());
            return "";
        }
    }
    //Phuong thuc lay name va num
    public String scraper1 (String xPath1, String xPath2, WebDriver driver){
        try {
            WebElement M = driver.findElement(By.xpath(xPath1));
            String m = M.getText();
            if (m.equals("")){
                m = "0";
            }
            return m;
        } catch (NoSuchElementException e) {
            WebElement M = driver.findElement(By.xpath(xPath2));
            String m = M.getText();
            if (m.equals("")){
                m = "0";
            }
            return m;
        } catch (TimeoutException e){
            System.out.println("Loi do bi treo khi thuc hien scraper1: " + e.getMessage());
            return "";
        } catch (Exception e){
            System.out.println("Loi qq gi y em meo biet: " + e.getMessage());
            return "";
        }
    }

    //Phuong thuc lay link
    public String scraper2 (String xPath, WebElement xPathTweet){
        try {
            WebElement M = xPathTweet.findElement(By.xpath(xPath));
            String m = M.getAttribute("href");
            return m;
        } catch (NoSuchElementException e){
            System.out.println("Crawl phai Ads: " + e.getMessage()); //Do Ads meo co link dan den bai viet T_T
            return "";
        } catch (TimeoutException e){
            System.out.println("Loi do bi treo khi thuc hien scraper2: " + e.getMessage());
            return "";
        } catch (Exception e){
            System.out.println("Loi qq gi y em meo biet: " + e.getMessage());
            return "";
        }
    }

    public void crawlUserNameRepost (WebDriver driver, Page page, String listType){
        //Tao danh sach de luu tru tranh trung lap
        List<String> userretweetdetects  = new ArrayList<>();
        int lastCount = 0;
        int currentCount = 0;

        try {
            // Tìm các phần tử user
            for (int i = 1; i <=5 ; i++) {
                List<WebElement> userretweets = driver.findElements(By.xpath("//section/div/div/div/div/div/button/div/div[2]/div[1]/div[1]/div/div[2]/div/a/div/div/span"));
                for (WebElement userretweet : userretweets) {
                    try {
                        // Lấy tên người dùng
                        String userretweetdetect = userretweet.getText();
                        if (!userretweetdetects.contains(userretweetdetect)) {
                            userretweetdetects.add(userretweetdetect);
                            page.addUserToList(listType, userretweetdetect);
                        }
                    } catch (Exception e) {
                        System.out.println("Co loi khi lay du lieu tu 1 Repost: " + e.getMessage());
                        continue;
                    }
                }
                currentCount = userretweetdetects.size();
                if (currentCount == lastCount) {
                    break;
                } else {
                    lastCount = currentCount;
                }
                sc1.scoller(4000, driver);
            }
        } catch (TimeoutException e) {
            System.out.println("Loi bi treo khi lay danh sach Repost: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Loi khi lay danh sach Repost: " + e.getMessage());
        }
        System.out.println("So userrepost lay duoc la: " + userretweetdetects.size());
    }

    public void crawlUserNameComment (WebDriver driver, Page page, String listType){
        //Tao danh sach de luu tru tranh trung lap
        List<String> usercommentdetects  = new ArrayList<>();
        int lastCount = 0;
        int currentCount = 0;

        try {
            // Tìm các phần tử user
            for (int i = 1; i <=5 ; i++) {
                List<WebElement> usercomments = driver.findElements(By.xpath("//section/div/div/div/div/div/article/div/div/div[2]/div[2]/div[1]/div/div[1]/div/div/div[2]/div/div[1]/a/div/span"));
                for (WebElement usercomment : usercomments) {
                    try {
                        // Lấy tên người dùng
                        String usercommentdetect = usercomment.getText();
                        if (!usercommentdetects.contains(usercommentdetect)) {
                            usercommentdetects.add(usercommentdetect);
                            page.addUserToList(listType, usercommentdetect);
                        }
                    } catch (Exception e) {
                        System.out.println("Co loi khi lay du lieu tu 1 Comment: " + e.getMessage());
                        continue;
                    }
                }
                currentCount = usercommentdetects.size();
                if (currentCount == lastCount) {
                    break;
                } else {
                    lastCount = currentCount;
                }
                sc1.scoller(4000, driver);
            }
            page.FixComment();

        } catch (TimeoutException e) {
            System.out.println("Loi bi treo khi lay danh sach Comment: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Loi khi lay danh sach Comment: " + e.getMessage());
        }
        System.out.println("So usercomment lay duoc la: " + (usercommentdetects.size() - 1));
    }

    public void crawlUserNameFollowingFollowers(WebDriver driver, Page page, String listType){
        List<String> usernames = new ArrayList<>();

        try {
            long lastHeight = sc1.getHiger(driver);

            while (true) {
                // Lấy danh sách phần tử hiện tại
                List<WebElement> userElements = driver.findElements(By.xpath("//section/div/div/div/div/div/button/div/div[2]/div[1]/div[1]/div/div[2]/div/a/div/div/span"));
                for (WebElement userElement : userElements) {
                    String username = userElement.getText();
                    if (!usernames.contains(username)) {
                        usernames.add(username);
                        page.addUserToList(listType, username);
                    }
                }
                // Cuộn xuống cuối trang
                sc1.scollerToEnd(driver);
                Thread.sleep(2000); // Đợi nội dung tải thêm
                // Kiểm tra nếu đã cuộn đến cuối trang
                long newHeight = sc1.getHiger(driver);
                if (newHeight == lastHeight) {
                    break;
                }
                lastHeight = newHeight;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkToContinue(WebDriver driver, WebElement xPathTweet){
        for (String index : scraper2Index){
            String check = scraper2(index, xPathTweet);
            if (check.equals("")){
                return true;
            }
        }
        for (String index : scraper1Index){
            String check = scraper1(index, xPathTweet);
            if (check.equals("")){
                return true;
            }
        }
        return false;
    }

    public WebElement FilterTweet (WebDriver driver, List<WebElement> Tweets, String link){
        try {
            for (WebElement Tweet : Tweets) {
                String m = scraper2(linkPageIndex, Tweet);
                if (m.equals(link)){
                    return Tweet;
                }
            }
        } catch (Exception e) {
            System.out.println("Co loi trong qua trinh loc Tweet: " + e.getMessage());
        }
        return Tweets.getFirst();
    }

}
