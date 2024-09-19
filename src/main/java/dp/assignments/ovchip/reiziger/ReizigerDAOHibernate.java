package dp.assignments.ovchip.reiziger;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.Date;
import java.util.List;
import java.util.function.Consumer;

public class ReizigerDAOHibernate implements ReizigerDAO {

    private final Session session;

    public ReizigerDAOHibernate(Session session) {
        this.session = session;
    }

    // Utility method to handle transaction management for operations that modify data (save, update, delete)
    private boolean executeTransaction(Consumer<Session> operation) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            operation.accept(session);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error during transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean save(Reiziger reiziger) {
        return executeTransaction(session -> session.save(reiziger));
    }

    @Override
    public boolean update(Reiziger reiziger) {
        return executeTransaction(session -> session.update(reiziger));
    }

    @Override
    public boolean delete(Reiziger reiziger) {
        return executeTransaction(session -> session.delete(reiziger));
    }

    @Override
    public Reiziger findById(int id) {
        try {
            return session.get(Reiziger.class, id);
        } catch (Exception e) {
            System.err.println("Failed to find Reiziger by id: " + id + ". Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Reiziger> findByGbdatum(Date date) {
        try {
            String hql = "FROM Reiziger WHERE geboortedatum = :date";
            Query<Reiziger> query = session.createQuery(hql, Reiziger.class);
            query.setParameter("date", date);
            return query.list();
        } catch (Exception e) {
            System.err.println("Failed to find Reizigers by geboortedatum: " + date + ". Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Reiziger> findAll() {
        try {
            String hql = "FROM Reiziger";
            Query<Reiziger> query = session.createQuery(hql, Reiziger.class);
            return query.list();
        } catch (Exception e) {
            System.err.println("Failed to retrieve all Reizigers. Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
