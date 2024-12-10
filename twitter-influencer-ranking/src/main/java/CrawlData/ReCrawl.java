package CrawlData;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ReCrawl {

    public ReCrawl() {}

    public void ControlMainCrawl (String filePath1, String filePath2, ExcelFileWriter excelWriter) {
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
        //ExcelFileWriter excelWriter = new ExcelFileWriter(filePath2);
        //Khoi tao doi tuong cua lop ReadUserName de quan ly qua trinh doc tu khoa
        ReadUserName run = new ReadUserName();
        run.setfilePath(filePath1);
        run.setLinks();
        run.getLinksSize();
        //Khoi tao doi tuong cua lop XLoginAndClose de quan ly dang nhap va dong
        XLoginAndClose lg = new XLoginAndClose();
        do { //Neu nhu dang nhap that bai thi dong trinh duyet roi mo lai de dang nhap
            //Mo trinh duyet va dang nhap
            try {
                lg.loginAction(driver);
                Thread.sleep(40000);
            } catch (Exception e){
                System.out.println("Co loi trong dang nhap o ham Main");
            }
            //Kiem tra trang thai dang nhap
        } while (!lg.checkLoginStatus(driver));
        //Khoi tao danh sach tim kiem
        List<String> links = new ArrayList<>(run.getLinks());

        //Tim kiem user
        for (String link : links) {
            try{
                driver.navigate().to(link);
                //Doi ket qua hien thi
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                Thread.sleep(5000);
                List<WebElement> Tweets = driver.findElements(By.xpath("//section/div/div/div/div/div/article/div/div/div[2]"));
                WebElement Tweet = scraper.FilterTweet(driver, Tweets, link);
                Thread.sleep(3000);
                if (scraper.checkToContinue(driver, Tweet)){
                    continue;
                }
                scraper.scrapeUser(driver, Tweet, excelWriter);
                excelWriter.saveToFile();
            } catch (TimeoutException e) {
                System.out.println("Hanh dong bi qua thoi gian:" + e.getMessage());
                excelWriter.saveToFile();
                continue;
            } catch (NoSuchElementException e){
                continue;
            } catch (Exception e) {
                e.getMessage();
                excelWriter.saveToFile();
                continue;
            }
        }
        lg.closeBrowser(driver);
    }
}