package skyjacker;

import org.hibernate.Session;
import org.hibernate.Transaction;
import skyjacker.entities.Fitment;
import skyjacker.entities.FitmentNote;
import skyjacker.entities.SkyShock;
import skyjacker.entities.SpecAndKitNote;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;

public class FitmentDAO {
    public static Set<String> getAllFitLines(Session session) {
     List<String> resultList = new ArrayList<>();
     CriteriaBuilder builder = session.getCriteriaBuilder();
     CriteriaQuery<String> crQ = builder.createQuery(String.class);
     Root<Fitment> root = crQ.from(Fitment.class);
     crQ.select(root.get("fitString")).distinct(true);
     Query q = session.createQuery(crQ);
     resultList = q.getResultList();

     return new HashSet<>(resultList);
    }

 public static void prepareFitments(SkyShock shock, Session session) {
     Set<Fitment> fitments = shock.getFitments();
     Set<FitmentNote> newFitNotes = new HashSet<>();
     fitments.forEach(fitment -> {
          Set<FitmentNote> fitNotes = fitment.getFitNotes();
          Set<FitmentNote> checkedNotes = new HashSet<>();
          fitNotes.forEach(note -> {
             FitmentNote checkedNote = checkNoteExistence(session, note);
             if (checkedNote==null){
                 if (!newFitNotes.contains(note)){
                     newFitNotes.add(note);
                     checkedNote = note;
                 }
                 else {
                     for (FitmentNote n : newFitNotes) {
                         if (n.getFitNote().equals(note.getFitNote())) {
                             checkedNote = n;
                             break;
                         }
                     }
                 }
             }
             checkedNotes.add(checkedNote);
          });
          fitment.setFitNotes(checkedNotes);
         // saveFitment(session, fitment);
     });
 }

    private static void saveFitment(Session session, Fitment fitment) {
        Transaction transaction = null;
        try {
            transaction = session.getTransaction();
            transaction.begin();
            session.save(fitment);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    private static FitmentNote checkNoteExistence(Session session, FitmentNote note) {
     CriteriaBuilder builder = session.getCriteriaBuilder();
     CriteriaQuery<FitmentNote> crQ = builder.createQuery(FitmentNote.class);
     Root<FitmentNote> root = crQ.from(FitmentNote.class);
     crQ.where(builder.equal(root.get("fitNote"), note.getFitNote()));
     Query q = session.createQuery(crQ);
     FitmentNote testNote = null;
     try {
      testNote = (FitmentNote) q.getSingleResult();
     } catch (NoResultException ignored) {}

     return testNote;
 }
}
