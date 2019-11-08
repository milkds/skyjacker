package skyjacker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class JobDispatcher {
    public Set<String> getLinksToParseFromFile() {
        Set<String> linksToParse = getAllLinks();
        Set<String> parsedLinks = ShockDAO.getParsedLinks();
        linksToParse.removeAll(parsedLinks);

        return linksToParse;
    }

    private Set<String> getAllLinks() {
        Set<String> linksToParse = new HashSet<>();
        Scanner s = null;
        try {
            s = new Scanner(new File("src\\main\\resources\\links.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (s != null) {
            while (s.hasNext()){
                linksToParse.add(s.next());
            }
            s.close();
        }

        return linksToParse;
    }
}
