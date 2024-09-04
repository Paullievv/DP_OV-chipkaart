package org.example;

import org.example.ovchip.database.DB;

import java.sql.*;

public class App
{
    public static void main(String[] args) {

        try {
            Connection connection = connect();
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
        }
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