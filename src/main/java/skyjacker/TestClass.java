package skyjacker;

import org.openqa.selenium.WebDriver;
import skyjacker.entities.SkyShock;

public class TestClass {

    public static void testItemBuild(){
        WebDriver driver = SileniumUtil.initDriver();
        String link = "https://skyjacker.com/shop/shocks/H7015/";
        SkyShock shock = new ShockBuilder().buildShock(driver, link);
        driver.quit();
    }
}
