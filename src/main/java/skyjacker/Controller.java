package skyjacker;

import org.openqa.selenium.WebDriver;
import skyjacker.entities.SkyShock;

import java.util.Set;

public class Controller {

    public static void main(String[] args) {
      //  TestClass.getFitAtts();
     //   new Controller().parseItemsFromFile();
        //CarBuildTester.printCarMakes();
      //  CarBuildTester.printCarModels();

        String s = "30.46";
        System.out.println(Double.parseDouble(s));
    }
    
    public void parseItemsFromFile(){
        Set<String> linksToParse = new JobDispatcher().getLinksToParseFromFile();
        parseLinks(linksToParse);
        HibernateUtil.shutdown();
    }

    private void parseLinks(Set<String> linksToParse) {
        WebDriver driver = SileniumUtil.initDriver();
        linksToParse.forEach(link->{
            SkyShock shock = new ShockBuilder().buildShock(driver, link);
            ShockDAO.saveShock(shock);
        });
        driver.quit();
    }
}
