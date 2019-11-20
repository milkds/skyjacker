package skyjacker;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import skyjacker.entities.Fitment;

import java.util.HashSet;
import java.util.Set;

public class CarBuildTester {

    public static void printFitLines(){
        Session session = HibernateUtil.getSession();
        Set<String> fitLines = FitmentDAO.getAllFitLines(session);
        session.close();
        HibernateUtil.shutdown();
        fitLines.forEach(System.out::println);
    }
    public static void printCarMakes(){
        Session session = HibernateUtil.getSession();
        Set<String> fitLines = FitmentDAO.getAllFitLines(session);
        session.close();
        HibernateUtil.shutdown();

        Set<String> makes = new HashSet<>();
        fitLines.forEach(fitLine->{
            String[] split = fitLine.split(" ");
            makes.add(split[1]);
        });
        makes.forEach(System.out::println);

    }
    public static void printCarDrives(){

        Set<String> fitLines = getFitLines();
        Set<String> drives = new HashSet<>();
        fitLines.forEach(fitLine->{
            String[] split = fitLine.split(" ");
            int length = split.length;
            String drive = split[length-1];
            if (drive.equals("GAS")||drive.equals("DIESEL")){
                drive = split[length-2];
            }
            drives.add(drive);
        });
        drives.forEach(System.out::println);

    }
    public static void printCarModels(){

        Set<String> fitLines = getFitLines();
        Set<String> carModels = new HashSet<>();
        fitLines.forEach(fitLine->{
           fitLine = fitLine.substring(5);
           fitLine = removeDrive(fitLine);
           fitLine = removeMake(fitLine);
           fitLine = fitLine.trim();
           carModels.add(fitLine);
        });
        carModels.forEach(System.out::println);

    }

    private static String removeMake(String fitLine) {
        return StringUtils.substringAfter(fitLine, " ");
    }

    private static String removeDrive(String fitLine) {
        String drive = "";
        if (fitLine.contains("2WD/4WD")){
            drive = "2WD/4WD";
        }
        else if (fitLine.contains("AWD")){
            drive = "AWD";
        }
        else if (fitLine.contains("4WD")){
            drive = "4WD";
        }
        else {
            drive = "2WD";
        }

        String result = StringUtils.substringBefore(fitLine, drive);

            return result;
    }

    private static Set<String> getFitLines(){
        Session session = HibernateUtil.getSession();
        Set<String> fitLines = FitmentDAO.getAllFitLines(session);
        session.close();
        HibernateUtil.shutdown();
        return fitLines;
    }
}
