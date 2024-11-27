package CrawlData;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class mainTwiter {
    public static List<String> getLinkSearchPage() {
        String filePath = "C:/Users/Admin/IdeaProjects/TestSetup/keyWordSearch.txt";  //Đường dẫn tới file chứa từ khóa
        List<String> link = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                link.add("https://x.com/search?q=" + line + "&src=typed_query");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return link;
    }

    public static void Login(WebDriver driver) {
        try {
            // Mở Twitter
            driver.get("https://x.com/login");
            // Đợi tối đa 10 giây để trang tải
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            // Điền tên người dùng hoặc email
            WebElement usernameInput = driver.findElement(By.name("text"));
            usernameInput.sendKeys("nkhuy05@gmail.com");
            //Nhấn nút tiếp tục (Next)
            WebElement nextButton = driver.findElement(By.xpath("/html/body/div/div/div/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div/div/div/button[2]/div"));
            nextButton.click();
            // Đợi một chút để vươtj qua xác thực
            Thread.sleep(25000);
            // Đợi để đảm bảo đăng nhập thành công
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            // Kiểm tra xem có đăng nhập thành công không
            if (driver.getCurrentUrl().contains("home")) {
                System.out.println("Đăng nhập thành công!");
            } else {
                System.out.println("Đăng nhập thất bại.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void searchLink(String link, WebDriver driver) {
        driver.navigate().to(link);
    }

    public static void browserTweet(WebDriver driver, JavascriptExecutor js, WebElement xPathTweet) {

        //Khai báo các thuộc tính có trong tweet
        String name;
        String username;
        String numoffollower;
        String numoffollowing;
        String linkpage;
        String linkhottweet;
        String numofviewinhottweet;
        String numofreactinhottweet;
        String numofcommentinhottweet;
        String numofrepostinhottweet;
        List<String> listfollower = new ArrayList<String>();
        List<String> listfollowing = new ArrayList<>();
        List<String> listusernamerepost = new ArrayList<>();
        List<String> listusernamecomment = new ArrayList<>();


        try {
            // Lấy name người dùng
            WebElement Name = xPathTweet.findElement(By.xpath("//main//section/div/div/div/div/div/article/div/div/div[2]/div[2]/div[1]/div/div[1]/div/div/div[1]/div/a/div/div[1]/span/span"));
            name = Name.getText();
            // Lấy username người dùng
            WebElement userName = xPathTweet.findElement(By.xpath("//main//section/div/div/div/div/div/article/div/div/div[2]/div[2]/div[1]/div[1]/div[1]/div/div/div[2]/div/div[1]"));
            username = userName.getText();
            // Lấy link user
            WebElement linkPage = xPathTweet.findElement(By.xpath("//main//section/div/div/div/div/div/article/div/div/div[2]/div[2]/div[1]/div/div[1]/div/div/div[1]/div/a"));
            linkpage = "https://x.com" + linkPage.getAttribute("href");
            // Lấy link Tweet
            WebElement linkTweet = xPathTweet.findElement(By.xpath("//main//section/div/div/div/div/div/article/div/div/div[2]/div[2]/div[1]/div/div[1]/a"));
            linkhottweet = "https://x.com" + linkTweet.getAttribute("href");
            // Lấy số view
            WebElement numOfViewInHotTweet = xPathTweet.findElement(By.xpath("//main//section/div/div/div/div/div/article/div/div/div[2]/div[2]/div[4]/div/div/div[4]/a/div/div[2]"));
            numofviewinhottweet = numOfViewInHotTweet.getText();
            // Lấy số react
            WebElement numOfReactInHotTweet = xPathTweet.findElement(By.xpath("//main//section/div/div/div/div/div/article/div/div/div[2]/div[2]/div[4]/div/div/div[3]/button/div/div[2]"));
            numofreactinhottweet = numOfReactInHotTweet.getText();
            //Lấy số cmt
            WebElement numOfCommentInHotTweet = xPathTweet.findElement(By.xpath("//main//section/div/div/div/div/div/article/div/div/div[2]/div[2]/div[4]/div/div/div[1]/button/div/div[2]"));
            numofcommentinhottweet = numOfCommentInHotTweet.getText();
            // Lấy số repost
            WebElement numOfRepostInHotTweet = xPathTweet.findElement(By.xpath("//main//section/div/div/div/div/div/article/div/div/div[2]/div[2]/div[4]/div/div/div[2]/button/div/div[2]"));
            numofrepostinhottweet = numOfRepostInHotTweet.getText();
            // Mở trang cá nhân và tweet để lấy dữ liệu
            ((JavascriptExecutor) driver).executeScript("window.open('" + linkpage + "', '_blank');");
            ((JavascriptExecutor) driver).executeScript("window.open('" + linkhottweet + "', '_blank');");
            Thread.sleep(6500);
            List<String> tabs = new ArrayList<>(driver.getWindowHandles());
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.switchTo().window(tabs.get(1));
            try {
                WebElement numOfFollowing = driver.findElement(By.xpath("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[3]/div/div/div/div/div[6]/div[1]/a/span[1]"));
                numoffollowing = numOfFollowing.getText();
            } catch (NoSuchElementException e) {
                WebElement numOfFollowing = driver.findElement(By.xpath("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[3]/div/div/div/div/div[5]/div[1]/a/span[1]"));
                numoffollowing = numOfFollowing.getText();
            }
            try {
                WebElement numOfFollower = driver.findElement(By.xpath("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[3]/div/div/div/div/div[6]/div[2]/a/span[1]"));
                numoffollower = numOfFollower.getText();
            } catch (NoSuchElementException e) {
                WebElement numOfFollower = driver.findElement(By.xpath("/html/body/div[1]/div/div/div[2]/main/div/div/div/div/div/div[3]/div/div/div/div/div[5]/div[2]/a/span[1]"));
                numoffollower = numOfFollower.getText();
            }

            Page page = new Page(name, username, numoffollower, numoffollowing, linkpage, linkhottweet, numofviewinhottweet, numofreactinhottweet, numofcommentinhottweet, numofrepostinhottweet);

            driver.navigate().to(linkpage + "/following");
            Thread.sleep(6000);
            page.setlistFollowing(crawlUserNameFollowingFollowers(driver, js));
            driver.navigate().to(linkpage + "/followers");
            Thread.sleep(6000);
            page.setlistFollower(crawlUserNameFollowingFollowers(driver, js));
        } catch (Exception e) {
            System.out.println("Có lỗi xảy ra khi lấy thông tin từ một tweet: " + e.getMessage());
            Page nullpage = new Page();
        }
    }

    public static List<String> crawlUserNameFollowingFollowers(WebDriver driver, JavascriptExecutor js) {
        List<String> usernames = new ArrayList<>();

        try {
            long lastHeight = (long) js.executeScript("return document.body.scrollHeight;");

            while (true) {
                // Lấy danh sách phần tử hiện tại
                List<WebElement> userElements = driver.findElements(By.xpath("//section/div/div/div/div/div/button/div/div[2]/div[1]/div[1]/div/div[2]/div/a/div/div/span"));
                for (WebElement userElement : userElements) {
                    String username = userElement.getText();
                    if (!usernames.contains(username)) {
                        usernames.add(username);
                    }
                }
                // Cuộn xuống cuối trang
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep(2000); // Đợi nội dung tải thêm
                // Kiểm tra nếu đã cuộn đến cuối trang
                long newHeight = (long) js.executeScript("return document.body.scrollHeight;");
                if (newHeight == lastHeight) {
                    break;
                }
                lastHeight = newHeight;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return usernames;
    }


    public static void main(String[] args) {
        // Đường dẫn tới geckodriver
        System.setProperty("webdriver.gecko.driver", "D:\\Project OOP\\Gecko\\geckodriver.exe");

        // Cấu hình cho Firefox
        FirefoxOptions options = new FirefoxOptions();
        options.setBinary("C:\\Users\\Admin\\AppData\\Local\\Mozilla Firefox\\firefox.exe");

        // Khởi tạo WebDriver
        WebDriver driver = new FirefoxDriver(options);

        //Khởi tạo danh sách tìm kiếm
        List<String> links = new ArrayList<>();
        links = getLinkSearchPage();

        //Khởi tạo danh sách tweets tìm kiếm được
        List<String> tweets = new ArrayList<>();

        JavascriptExecutor js = (JavascriptExecutor) driver;

        Login(driver);

        // Đợi tối đa 10 giây để trang tải
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Tìm kiếm từ khóa liên quan đến blockchain
        for (String link : links) {
            searchLink(link, driver);

            // Đợi kết quả hiển thị

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));


            // Lấy danh sách kết quả tweet
            for (int i = 1; i < 21; i++) {
                List<WebElement> xPathTweets = driver.findElements(By.xpath("//main//section/div/div/div/div/div/article/div"));
                for(WebElement xPathTweet : xPathTweets) {
                    browserTweet(driver, js, xPathTweet);

                }


                System.out.println("Danh sách người dùng đã được ghi vào file twitter_results.xlxs");
            }
        }
    }
}
