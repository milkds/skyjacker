package skyjacker;

import org.hibernate.Session;
import org.openqa.selenium.WebDriver;
import skyjacker.entities.Fitment;
import skyjacker.entities.FitmentNote;
import skyjacker.entities.SkyShock;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;

public class TestClass {

    public static void testItemBuild(){
        WebDriver driver = SileniumUtil.initDriver();
        String link = "https://skyjacker.com/shop/shocks/H7015/";
        SkyShock shock = new ShockBuilder().buildShock(driver, link);
        driver.quit();
    }

    public static void getFitAtts(){
        Set<String> attNames = new HashSet<>();
        Session session = HibernateUtil.getSession();
        List<String> resultList = new ArrayList<>();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<String> crQ = builder.createQuery(String.class);
        Root<FitmentNote> root = crQ.from(FitmentNote.class);
        crQ.select(root.get("fitNote")).distinct(true);
        Query q = session.createQuery(crQ);
        resultList = q.getResultList();
        Set<String> result = new TreeSet<>(resultList);
        result.forEach(s->{
            if (s.contains("Extended")||s.contains("Collapsed")){
                System.out.println(s);
            }
        });
        HibernateUtil.shutdown();
    }
}
