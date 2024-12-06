package CrawlData;

import org.openqa.selenium.*;

public class Scoller {

    private WebDriver driver;
    private JavascriptExecutor js ;

    public Scoller() {}

    public void setDriver(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
    }

    public void scoller(int i, WebDriver driver) {
        try {
            setDriver(driver);
            js.executeScript("window.scrollBy(0, " + i/2 + ");");
            Thread.sleep(2000);
            js.executeScript("window.scrollBy(0, " + i/2 + ");");
            Thread.sleep(2000);
            js.executeScript("window.scrollBy(0, " + i/2 + ");");
            Thread.sleep(3000);
            js.executeScript("window.scrollBy(0, " + i/2 + ");");
            Thread.sleep(3000);
            js.executeScript("window.scrollBy(0, " + (-i) + ");");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Co loi khi cuon xuong: " + e.getMessage());
        }
    }

    public long getHiger(WebDriver driver) {
        setDriver(driver);
        return (long) js.executeScript("return document.body.scrollHeight;");
    }

    public void scollerToEnd (WebDriver driver) {
        setDriver(driver);
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }




}
