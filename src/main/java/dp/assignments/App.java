package dp.assignments;

import dp.assignments.ovchip.dao.AdresDAOPsql;
import dp.assignments.ovchip.dao.OVChipkaartDAOPsql;
import dp.assignments.ovchip.dao.ReizigerDAOPsql;
import dp.assignments.ovchip.domain.Adres;
import dp.assignments.ovchip.database.DB;
import dp.assignments.ovchip.domain.OVChipkaart;
import dp.assignments.ovchip.domain.Reiziger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

public class App
{
    public static void main(String[] args) throws SQLException {

        Connection conn = connect();

        ReizigerDAOPsql rdao = new ReizigerDAOPsql(conn);
        OVChipkaartDAOPsql odao = new OVChipkaartDAOPsql(conn,rdao);

        testOVChipkaartDAO(odao, rdao);

//        testAdresDAO(new AdresDAOPsql(connect()));
    }

    // Connection with JDBC
    public static Connection connect() throws SQLException {
        return DB.connect();
    }

    private static void testAdresDAO(AdresDAOPsql adao) throws SQLException {
        System.out.println("\n---------- Test AdresDAO -------------");

        // Haal alle adressen op uit de database
        List<Adres> adressen = adao.findAll();
        System.out.println("[Test] AdresDAO.findAll() geeft de volgende adressen:");
        for (Adres a : adressen) {
            System.out.println(a);
        }
        System.out.println();

        Reiziger reiziger = new Reiziger(99, "D", "", "Pijl", java.sql.Date.valueOf("2003-06-11"));
        Adres existingAdres = adao.findByReiziger(reiziger);

        Adres nieuwAdres = new Adres(100, "1234AB", "12", "Laanstraat", "Utrecht", reiziger);

        if (existingAdres != null) {
            // Update the existing address
            System.out.println("[Test] Update bestaand adres voor Reiziger met ID 99");
            existingAdres.setPostcode("1234AB");
            existingAdres.setHuisnummer("12");
            existingAdres.setStraat("Laanstraat");
            existingAdres.setWoonplaats("Utrecht");
            adao.update(existingAdres);
        } else {
            // No existing address found, create a new one
            System.out.println("[Test] Voeg nieuw adres toe voor Reiziger met ID 99");
            adao.save(nieuwAdres);  // Perform the save
        }

        // Update een bestaand adres en persisteer deze in de database
        System.out.println("[Test] Update postcode van Adres met ID 99");
        nieuwAdres.setPostcode("4321XY");
        adao.update(nieuwAdres);
        Adres updatedAdres = adao.findById(99);
        System.out.println("Postcode na update: " + updatedAdres.getPostcode());
        System.out.println();

        // Delete een bestaand adres
        System.out.println("[Test] Verwijder adres met ID 100");
        adao.delete(nieuwAdres);
        adressen = adao.findAll();
        System.out.println("Aantal adressen na AdresDAO.delete(): " + adressen.size() + "\n");

        // Vind adres aan de hand van ID
        System.out.println("[Test] Vind adres met ID 1");
        Adres adresById = adao.findById(1);
        if (adresById != null) {
            System.out.println("Adres gevonden: " + adresById);
        } else {
            System.out.println("Geen adres gevonden met ID 1");
        }
        System.out.println();

        System.out.println("[Test] Vind adres van reiziger met reiziger");
        Adres adresByReiziger = adao.findByReiziger(reiziger);
        if (adresByReiziger != null) {
            System.out.println("Adres van reiziger gevonden: " + adresByReiziger);
        } else {
            System.out.println("Geen adres gevonden voor reiziger met reiziger");
        }

        System.out.println("\n---------- Einde test AdresDAO -------------");
    }

    private static void testOVChipkaartDAO(OVChipkaartDAOPsql odao, ReizigerDAOPsql rdao) throws SQLException {
        System.out.println("\n---------- Test OVChipkaartDAO -------------");

        List<OVChipkaart> chipkaarten = odao.findAll();
        System.out.println("[Test] OVChipkaartDAO.findAll() geeft de volgende OV-chipkaarten:");
        for (OVChipkaart ov : chipkaarten) {
            System.out.println(ov);
        }
        System.out.println();

        Reiziger reiziger = new Reiziger(69, "P", "", "Vos", java.sql.Date.valueOf("2003-06-11"));
        rdao.save(reiziger);

        // Find OV-chipkaarten by reiziger
        List<OVChipkaart> chipkaartenByReiziger = odao.findByReiziger(reiziger);
        System.out.println("[Test] OVChipkaartDAO.findByReiziger() geeft de volgende OV-chipkaarten voor Reiziger met ID 69:");
        for (OVChipkaart ov : chipkaartenByReiziger) {
            System.out.println(ov);
        }
        System.out.println();

        // Create a new OVChipkaart and save it
        OVChipkaart ovChipkaart = new OVChipkaart(83892481, Date.valueOf("2029-11-02"), 2, new BigDecimal(0), reiziger);
        OVChipkaart secondOVChipkaart = new OVChipkaart(23456441, Date.valueOf("2029-11-02"), 2, new BigDecimal(0), reiziger);
        System.out.println("[Test] Voeg nieuwe OV-chipkaart toe voor Reiziger met ID 69");
        odao.save(ovChipkaart);
        odao.save(secondOVChipkaart);
        chipkaarten = odao.findAll();
        System.out.println("[Test] Aantal OV-chipkaarten na toevoegen van nieuwe kaart: " + chipkaarten.size());
        System.out.println();

        // Update an existing OV-chipkaart
        System.out.println("[Test] Update saldo van OV-chipkaart met kaartnummer 83892481");
        ovChipkaart.setSaldo(BigDecimal.valueOf(50.75));
        odao.update(ovChipkaart);
        OVChipkaart updatedChipkaart = odao.findByID(83892481);
        System.out.println("Saldo na update: " + updatedChipkaart.getSaldo());
        System.out.println();

        // Test findByID()
        System.out.println("[Test] Zoek OV-chipkaart met kaartnummer 83892481");
        OVChipkaart chipkaartById = odao.findByID(83892481);
        if (chipkaartById != null) {
            System.out.println("Gevonden OV-chipkaart: " + chipkaartById);
        } else {
            System.out.println("Geen OV-chipkaart gevonden met kaartnummer 83892481");
        }
        System.out.println();

        // Delete an OV-chipkaart
        System.out.println("[Test] Verwijder OV-chipkaart met kaartnummer 83892481 en 23456441");
        odao.delete(ovChipkaart);
        odao.delete(secondOVChipkaart);
        chipkaarten = odao.findAll();
        System.out.println("Aantal OV-chipkaarten na OVChipkaartDAO.delete(): " + chipkaarten.size());
        System.out.println();

        // Clean up: delete test reiziger
        rdao.delete(reiziger);
        System.out.println("\n---------- Einde test OVChipkaartDAO -------------");
    }


}