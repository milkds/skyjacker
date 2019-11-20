package skyjacker;

import org.hibernate.Session;
import skyjacker.entities.Fitment;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FitmentDAO {
    public static Set<String> getAllFitLines(Session session) {
     List<String> resultList = new ArrayList<>();
     CriteriaBuilder builder = session.getCriteriaBuilder();
     CriteriaQuery<String> crQ = builder.createQuery(String.class);
     Root<Fitment> root = crQ.from(Fitment.class);
     crQ.select(root.get("fitString")).distinct(true);
     Query q = session.createQuery(crQ);
     resultList = q.getResultList();
     Set<String> result = new HashSet<>(resultList);



     return result;
    }
}
