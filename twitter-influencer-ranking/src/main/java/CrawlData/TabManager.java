package CrawlData;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.ArrayList;
import java.util.List;

public class TabManager {

    public TabManager(){}

    public void newTab(WebDriver driver, String link){
        ((JavascriptExecutor)driver).executeScript("window.open('" + link + "');");
    }

    public void switchTab (WebDriver driver){
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.getLast());
    }

    public void closeTab (WebDriver driver){
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.close();
        driver.switchTo().window(tabs.getFirst());
    }
}
