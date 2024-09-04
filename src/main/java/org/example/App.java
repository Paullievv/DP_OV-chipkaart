package org.example;

import java.sql.*;

public class App
{
    private static final String URL = "jdbc:postgresql://localhost:5432/ovchip";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            statement = connection.createStatement();
            String selectTravellers = "SELECT voorletters, tussenvoegsel, achternaam, geboortedatum FROM reiziger";

            resultSet = statement.executeQuery(selectTravellers);

            System.out.println("Alle reizigers:");

            int number = 1;

            while (resultSet.next()) {
                String firstName = resultSet.getString("voorletters");
                String tussenvoegsel = resultSet.getString("tussenvoegsel");
                String secondName = resultSet.getString("achternaam");
                Date dateOfBirth = resultSet.getDate("geboortedatum");

                System.out.printf("#%s %s %s %s (%s)%n", number, firstName, tussenvoegsel, secondName, dateOfBirth);
                number = number + 1;
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        }
    }
}