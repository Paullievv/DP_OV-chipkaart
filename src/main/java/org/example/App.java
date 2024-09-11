package org.example;

import org.example.ovchip.database.DB;
import org.example.ovchip.reiziger.Reiziger;
import org.example.ovchip.reiziger.ReizigerDAO;
import org.example.ovchip.reiziger.ReizigerDAOPsql;

import java.sql.*;
import java.util.List;

public class App
{
    public static void main(String[] args) throws SQLException {

        try {
            Connection connection = connect();

            testReizigerDAO(new ReizigerDAOPsql(connection));

            Reiziger reiziger = new Reiziger(69, "P", "", "Vos", Date.valueOf("2003-06-11"));
            ReizigerDAO reizigerDAO = new ReizigerDAOPsql(connection);
            reizigerDAO.save(reiziger);

            Statement statement = connection.createStatement();
            String selectTravellers = "SELECT voorletters, tussenvoegsel, achternaam, geboortedatum FROM reiziger";

            ResultSet resultSet = statement.executeQuery(selectTravellers);

            System.out.println("Alle reizigers:");

            int number = 1;

            while (resultSet.next()) {
                String firstName = resultSet.getString("voorletters");
                String middleName = resultSet.getString("tussenvoegsel");
                String lastName = resultSet.getString("achternaam");
                Date dateOfBirth = resultSet.getDate("geboortedatum");

                firstName = (firstName != null) ? firstName : "";
                middleName = (middleName != null) ? middleName : "";
                lastName = (lastName != null) ? lastName : "";
                String dateOfBirthStr = (dateOfBirth != null) ? dateOfBirth.toString() : "";

                if (!middleName.isEmpty()) {
                    System.out.printf("#%d %s. %s %s (%s)%n", number, firstName, middleName, lastName, dateOfBirthStr);
                } else {
                    System.out.printf("#%d %s. %s (%s)%n", number, firstName, lastName, dateOfBirthStr);
                }

                number++;
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        } finally {
            connect().close();
        }
    }

    private static void testReizigerDAO(ReizigerDAO rdao) {
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

        // Voeg aanvullende tests van de ontbrekende CRUD-operaties in.
    }

    public static Connection connect(){
        Connection connection = null;
        try {
            connection = DB.connect();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return connection;
    }
}