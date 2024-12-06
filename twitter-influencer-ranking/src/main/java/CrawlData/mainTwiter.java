package CrawlData;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;


import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class mainTwiter {
    public static void main(String[] args) {
        //Duong dan toi GeckoDriver
        System.setProperty("webdriver.gecko.driver", "D:\\Project OOP\\Gecko\\geckodriver.exe");
        //Cau hinh cho Firefox
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("dom.webdriver.enabled", false); // Ẩn navigator.webdriver
        options.addPreference("general.useragent.override",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:91.0) Gecko/20100101 Firefox/91.0");
        options.setBinary("C:\\Users\\Admin\\AppData\\Local\\Mozilla Firefox\\firefox.exe");
        //Khoi tao WebDriver
        WebDriver driver = new FirefoxDriver(options);
        //Khoi tao doi tuong cua lop XScraper de quan ly qua trinh crawl du lieu
        XScraper scraper = new XScraper();
        //Khoi tao doi tuong cua lop ExcelFileWriter de quan ly qua trinh ghi du lieu ra file
        ExcelFileWriter excelWriter = new ExcelFileWriter();
        //Khoi tao doi tuong cua lop Scoller de quan ly cuon trang
        Scoller sc = new Scoller();
        //Khoi tao doi tuong cua lop ReadKeyWord de quan ly qua trinh doc tu khoa
        ReadKeyWord rkw = new ReadKeyWord();
        rkw.setfilePath("C:\\Users\\Admin\\IdeaProjects\\twitter-influencer-ranking\\twitter-influencer-ranking\\src\\main\\java\\CrawlData\\keyWordSearch.txt");
        rkw.setLinks();
        rkw.getLinksSize();
        //Khoi tao doi tuong cua lop XLoginAndClose de quan ly dang nhap va dong
        XLoginAndClose lg = new XLoginAndClose();
        do { //Neu nhu dang nhap that bai thi dong trinh duyet roi mo lai de dang nhap
            //Mo trinh duyet va dang nhap
            try {
                lg.loginAction(driver);
                Thread.sleep(25000);
            } catch (Exception e){
                System.out.println("Co loi trong dang nhap o ham Main");
            }
            //Kiem tra trang thai dang nhap
        } while (!lg.checkLoginStatus(driver));
        //Khoi tao danh sach tim kiem
        List<String> links = new ArrayList<>(rkw.getLinks());

        //Tim kiem tu khoa
        for (String link : links) {
            try{
                driver.navigate().to(link);
                //Doi ket qua hien thi
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                //Lay danh sach Tweet va duyet Tweet
                for (int i = 1; i <= 2; i++){
                    Thread.sleep(5000);
                    List<WebElement> tweets = new ArrayList<>(scraper.Tweet(driver));
                    scraper.User(tweets, driver, excelWriter);
                    excelWriter.saveToFile("D:\\Documents D\\TestCrawlData.xlsx");
                    sc.scoller(6000, driver);
                }
                excelWriter.saveToFile("D:\\Documents D\\TestCrawlData.xlsx");
            } catch (TimeoutException e) {
                System.out.println("Hanh dong bi qua thoi gian:" + e.getMessage());
                excelWriter.saveToFile("D:\\Documents D\\TestCrawlData.xlsx");
                continue;
            } catch (Exception e) {
                e.printStackTrace();
                excelWriter.saveToFile("D:\\Documents D\\TestCrawlData.xlsx");
                continue;
            }
        }
        lg.closeBrowser(driver);
        excelWriter.close();
    }
}