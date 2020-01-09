package skyjacker;

import org.openqa.selenium.WebDriver;
import skyjacker.entities.CarMergeEntity;
import skyjacker.entities.SkyShock;

import java.util.Set;

public class Controller {

    public static void main(String[] args) {
      //  TestClass.getFitNotesDuplicates();
        //new Controller().parseItemsFromFile();
        //CarBuildTester.printCarMakes();
      //  CarBuildTester.printCarModels();
        new Controller().fillMergingTable();



    }

    private void fillMergingTable() {
        String filePath = "C:\\Users\\Jakson\\Desktop\\skyModelsDB.xlsx";
        Set<CarMergeEntity> entities = ExcelUtil.getMergeInfoFromFile(filePath);
        ShockDAO.saveEntities(entities);
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
