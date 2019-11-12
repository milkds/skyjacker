package skyjacker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.sql.Time;
import java.time.Duration;

public class SileniumUtil {
    private static final Logger logger = LogManager.getLogger(SileniumUtil.class.getName());
    public static WebDriver initDriver() {
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("http://example.com/");
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(60))
                .pollingEvery(Duration.ofMillis(100))
                .ignoring(WebDriverException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.tagName("body"))
                );
        logger.debug("Driver initiated");
        return driver;
    }

    public static void openLink(WebDriver driver, String link) {
        WebElement body = driver.findElement(By.tagName("body"));
        driver.get(link);
        logger.debug("opening link: "+link);
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(60))
                .pollingEvery(Duration.ofMillis(100))
                .ignoring(WebDriverException.class)
                .until(ExpectedConditions.and(
                        ExpectedConditions.stalenessOf(body),
                        ExpectedConditions.presenceOfElementLocated(By.id("main-header"))
                ));
        if  (noShockFound(driver)){
            throw  new TimeoutException();
        }
        logger.debug("successfully opened " + link);
    }

    private static boolean noShockFound(WebDriver driver) {
        WebElement checkEl = null;
        try {
            checkEl = driver.findElement(By.id("left-area"));
        }
        catch (NoSuchElementException e){
            return false;
        }
        return checkEl.getText().contains("No Results Found");

    }

    public static WebElement getElementBy(WebDriver driver, By by) {
        WebElement result = null;
        try {
            result =  new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(60))
                    .pollingEvery(Duration.ofMillis(100))
                    .ignoring(WebDriverException.class)
                    .until(ExpectedConditions.presenceOfElementLocated(by)
                    );
        }
        catch (TimeoutException|NoSuchElementException ignored){
        }

        return result;
    }

    public static WebElement getElementFromElementBy(WebElement fitEl, By by) {
        WebElement result = null;
        try {
            result = fitEl.findElement(by);
        }
        catch (NoSuchElementException ignored){
        }
        return result;
    }
}
