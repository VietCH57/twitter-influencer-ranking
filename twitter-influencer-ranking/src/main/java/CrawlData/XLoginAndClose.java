package CrawlData;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;


public class XLoginAndClose {

    public XLoginAndClose() {}

    public void loginAction(WebDriver driver) {
        try {
            // Mở Twitter
            driver.get("https://x.com/login");
            // Doi toi da de trang tai trong 10s
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            // Dien ten nguoi dung hoac Email
            //WebElement usernameInput = driver.findElement(By.name("text"));
            //usernameInput.sendKeys("nkhuy05@gmail.com"); //Nhap Email cua ban vao trong ""
            //Nhan nut tiep tuc (Next)
            //WebElement nextButton = driver.findElement(By.xpath("/html/body/div/div/div/div[1]/div/div/div/div/div/div/div[2]/div[2]/div/div/div[2]/div[2]/div/div/div/button[2]/div"));
            //nextButton.click();
            // Doi de dang nhap thanh cong
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));


        } catch (Exception e){
            System.out.println("Co loi trong qua trinh dang nhap: " + e.getMessage());
        }
    }

    public boolean checkLoginStatus(WebDriver driver) {
        try{
            if (driver.getCurrentUrl().contains("home")){
                System.out.println("Dang nhap thanh cong!");
                return true;
            } else {
                System.out.println("Dang nhap that bai!--Hay thu lai!");
                return false;
            }
        } catch (Exception e){
            System.out.println("Co loi trong qua trinh kiem tra dang nhap: " + e.getMessage());
            return false;
        }
    }

    public void closeBrowser(WebDriver driver) {
        driver.quit();
    }
}