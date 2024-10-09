package dp.assignments.ovchip.dao;

import dp.assignments.ovchip.domain.Reiziger;
import dp.assignments.ovchip.interfaces.ReizigerDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOPsql implements ReizigerDAO {

    private final Connection conn;

    public ReizigerDAOPsql(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean save(Reiziger reiziger) throws SQLException {
        String sql = "INSERT INTO reiziger (reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reiziger.getId());
            stmt.setString(2, reiziger.getVoorletters());
            stmt.setString(3, reiziger.getTussenvoegsel());
            stmt.setString(4, reiziger.getAchternaam());
            stmt.setDate(5, reiziger.getGeboortedatum());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean update(Reiziger reiziger) throws SQLException {
        String sql = "UPDATE reiziger SET voorletters = ?, tussenvoegsel = ?, achternaam = ?, geboortedatum = ? WHERE reiziger_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reiziger.getId());
            pstmt.setString(2, reiziger.getVoorletters());
            pstmt.setString(3, reiziger.getTussenvoegsel());
            pstmt.setString(4, reiziger.getAchternaam());

            if (reiziger.getGeboortedatum() != null) {
                pstmt.setDate(5, reiziger.getGeboortedatum());
            } else {
                pstmt.setNull(5, Types.DATE);
            }

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        }
    }


    @Override
    public boolean delete(Reiziger reiziger) throws SQLException {
        String sql = "DELETE FROM reiziger WHERE reiziger_id=?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reiziger.getId());

            int rowsDeleted = pstmt.executeUpdate();

            return rowsDeleted>0;
        }
    }

    @Override
    public Reiziger findById(int id) throws SQLException {
        String sql = "SELECT * FROM reiziger WHERE reiziger_id=?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                return new Reiziger(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getDate(5)
                );
            }
        }
        return null;
    }

    @Override
    public List<Reiziger> findByGbdatum(Date date) {
        return null;
    }

    @Override
    public List<Reiziger> findAll() throws SQLException {
        List<Reiziger> reizigers = new ArrayList<>();

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM reiziger");
        ResultSet result = stmt.executeQuery();

        while(result.next()) {
            reizigers.add(new Reiziger(result.getInt(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getDate(5)));
        }

        return reizigers;
    }

}
