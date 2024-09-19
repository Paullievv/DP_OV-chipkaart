package dp.assignments;

import dp.assignments.ovchip.database.HibernateUtil;
import dp.assignments.ovchip.reiziger.ReizigerDAOHibernate;
import dp.assignments.ovchip.database.DB;
import dp.assignments.ovchip.reiziger.Reiziger;
import dp.assignments.ovchip.reiziger.ReizigerDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.sql.*;
import java.util.List;

public class App
{
    public static void main(String[] args) throws SQLException {

        // Save reiziger with the Hibernate session given as parameter
        saveReiziger(openSession());

        // Uncomment below to test ReizigerDAOHibernate
//      testReizigerDAO(new ReizigerDAOHibernate(openSession()));

        // Shutdown Hibernate when program is done
        HibernateUtil.shutdown();
    }

    public static void saveReiziger(Session session) {
        ReizigerDAOHibernate dao = new ReizigerDAOHibernate(session);

        Reiziger Pijltje = new Reiziger(99, "D", "", "Pijl", java.sql.Date.valueOf("2003-06-11"));
        dao.delete(Pijltje);
        dao.save(Pijltje);

        // Closing session after all operations, new session will be given as parameter to the method
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    // Connection with Hibernate
    public static Session openSession() throws SQLException {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        return sessionFactory.openSession();
    }

    // Connection with JDBC
    public static Connection connect() throws SQLException {
        return DB.connect();
    }

    private static void testReizigerDAO(ReizigerDAOHibernate rdao) {
        System.out.println("\n---------- Test ReizigerDAO -------------");

        // Haal alle reizigers op uit de database
        List<Reiziger> reizigers = rdao.findAll();
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println();

        // Maak een nieuwe reiziger aan en persisteer deze in de database
        String gbdatum = "1981-03-14";
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", java.sql.Date.valueOf(gbdatum));
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");

        // Update een bestaande reiziger en persisteer deze in de database
        System.out.println("[Test] Update de geboortedatum van Reiziger met ID 77");
        sietske.setGeboortedatum(java.sql.Date.valueOf("1985-05-25"));
        rdao.update(sietske);
        Reiziger updatedReiziger = rdao.findById(77);
        System.out.println("Geboortedatum na update: " + updatedReiziger.getGeboortedatum());
        System.out.println();

        // Delete een bestaande reiziger
        System.out.println("[Test] Verwijder reiziger met ID 77");
        rdao.delete(sietske);
        reizigers = rdao.findAll();
        System.out.println("Aantal reizigers na ReizigerDAO.delete(): " + reizigers.size() + "\n");

        // Vind reiziger aan de hand van ID
        System.out.println("[Test] Vind reiziger met ID 1");
        Reiziger reizigerById = rdao.findById(1);
        if (reizigerById != null) {
            System.out.println("Reiziger gevonden: " + reizigerById);
        } else {
            System.out.println("Geen reiziger gevonden met ID 1");
        }
        System.out.println();

        // Vind reiziger aan de hand van geboortedatum
        System.out.println("[Test] Vind reizigers met geboortedatum '2003-06-11'");
        List<Reiziger> reizigersByDate = rdao.findByGbdatum(java.sql.Date.valueOf("2003-06-11"));
        if (reizigersByDate.isEmpty()) {
            System.out.println("Geen reizigers gevonden met geboortedatum '2003-06-11'");
        } else {
            System.out.println("Gevonden reizigers:");
            for (Reiziger r : reizigersByDate) {
                System.out.println(r);
            }
        }

        System.out.println("\n---------- Einde test ReizigerDAO -------------");
    }
}