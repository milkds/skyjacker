package skyjacker;

import org.hibernate.Session;
import org.hibernate.Transaction;
import skyjacker.entities.Category;
import skyjacker.entities.SkyShock;
import skyjacker.entities.SpecAndKitNote;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;

public class ShockDAO {
    public static Set<String> getParsedLinks() {
        Session session = HibernateUtil.getSession();
        List<String> parsedShockList = new ArrayList<>();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<String> crQ = builder.createQuery(String.class);
        Root<SkyShock> root = crQ.from(SkyShock.class);
        crQ.select(root.get("webLink"));
        Query q = session.createQuery(crQ);
        parsedShockList = q.getResultList();
        session.close();
        Set<String> parsedShockSet = new HashSet<>(parsedShockList);

        return parsedShockSet;
    }

    public static void saveShock(SkyShock shock) {
        Session session = HibernateUtil.getSession();
        prepareShockToSave(shock, session);
        Transaction transaction = null;
        try {
            transaction = session.getTransaction();
            transaction.begin();
            session.persist(shock);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    private static void prepareShockToSave(SkyShock shock, Session session) {
        checkCategories(shock, session);
        checkSKnotes(shock, session);
    }

    private static void checkSKnotes(SkyShock shock, Session session) {
        Set<SpecAndKitNote> checkedNotes = new HashSet<>();
        Set<SpecAndKitNote> rawNotes = shock.getNotes();
        rawNotes.forEach(note->{
            SpecAndKitNote checkedNote = checkNoteExistence(session, note);
            checkedNotes.add(Objects.requireNonNullElse(checkedNote, note));
        });
        shock.setNotes(checkedNotes);
    }

    private static SpecAndKitNote checkNoteExistence(Session session, SpecAndKitNote note) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<SpecAndKitNote> crQ = builder.createQuery(SpecAndKitNote.class);
        Root<SpecAndKitNote> root = crQ.from(SpecAndKitNote.class);
        crQ.where(builder.and(builder.equal(root.get("name"), note.getName()),
                builder.equal(root.get("value"), note.getValue())));
        Query q = session.createQuery(crQ);
        SpecAndKitNote testNote = null;
        try {
            testNote = (SpecAndKitNote) q.getSingleResult();

        } catch (NoResultException ignored) {
        }
        return testNote;
    }

    private static void checkCategories(SkyShock shock, Session session) {
        Set<Category> checkedCats = new HashSet<>();
        Set<Category> rawCats = shock.getCategories();
        rawCats.forEach(category->{
            Category checkedCat = checkCatExistence(session, category);
            checkedCats.add(Objects.requireNonNullElse(checkedCat, category));
        });
        shock.setCategories(checkedCats);
    }

    private static Category checkCatExistence(Session session, Category category) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Category> crQ = builder.createQuery(Category.class);
        Root<Category> root = crQ.from(Category.class);
        crQ.where(builder.equal(root.get("name"), category.getName()));
        Query q = session.createQuery(crQ);
        Category testCat = null;
        try {
            testCat = (Category) q.getSingleResult();

        } catch (NoResultException ignored) {
        }
        return testCat;
    }
}
