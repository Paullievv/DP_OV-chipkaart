package dp.assignments.ovchip.dao;

import dp.assignments.ovchip.domain.Adres;
import dp.assignments.ovchip.domain.Reiziger;
import dp.assignments.ovchip.interfaces.AdresDAO;

import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdresDAOPsql implements AdresDAO {

    private final Connection conn;

    public AdresDAOPsql(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean save(Adres adres) throws SQLException {
        String sql = "INSERT INTO adres (adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adres.getId());
            stmt.setString(2, adres.getPostcode());
            stmt.setString(3, adres.getHuisnummer());
            stmt.setString(4, adres.getStraat());
            stmt.setString(5, adres.getWoonplaats());
            stmt.setInt(6, adres.getReiziger().getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean update(Adres adres) throws SQLException {
        String sql = "UPDATE adres SET postcode = ?, huisnummer = ?, straat = ?, woonplaats = ? WHERE adres_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, adres.getPostcode());
            pstmt.setString(2, adres.getHuisnummer());
            pstmt.setString(3, adres.getStraat());
            pstmt.setString(4, adres.getWoonplaats());
            pstmt.setInt(5, adres.getId());

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        }
    }

    @Override
    public boolean delete(Adres adres) throws SQLException {
        String sql = "DELETE FROM adres WHERE adres_id=?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, adres.getId());

            int rowsDeleted = pstmt.executeUpdate();

            return rowsDeleted>0;
        }
    }

    @Override
    public Adres findById(int id) throws SQLException {
        String sql = "SELECT * FROM adres WHERE adres_id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {

                int reizigerId = resultSet.getInt(6);
                ReizigerDAOPsql rdao = new ReizigerDAOPsql(conn);
                Reiziger reiziger = rdao.findById(reizigerId);

                return new Adres(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        reiziger
                );
            }
        }
        return null;
    }

    @Override
    public Adres findByReiziger(Reiziger reiziger) throws SQLException {
        String sql = "SELECT * FROM adres WHERE reiziger_id=?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reiziger.getId());
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                return new Adres(
                        resultSet.getInt("adres_id"),
                        resultSet.getString("postcode"),
                        resultSet.getString("huisnummer"),
                        resultSet.getString("straat"),
                        resultSet.getString("woonplaats"),
                        reiziger
                );
            }
        }
        return null;
    }

    @Override
    public List<Adres> findAll() throws SQLException {
        List<Adres> adressen = new ArrayList<>();

        String query = "SELECT adres.*, reiziger.reiziger_id, reiziger.voorletters, reiziger.tussenvoegsel, reiziger.achternaam, reiziger.geboortedatum " +
                "FROM adres " +
                "LEFT JOIN reiziger ON adres.reiziger_id = reiziger.reiziger_id";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet result = stmt.executeQuery();

        while(result.next()) {
            Reiziger reiziger = new Reiziger(result.getInt("reiziger_id"),
                    result.getString("voorletters"),
                    result.getString("tussenvoegsel"),
                    result.getString("achternaam"),
                    result.getDate("geboortedatum"));

            Adres adres = new Adres(result.getInt("adres_id"),
                    result.getString("postcode"),
                    result.getString("huisnummer"),
                    result.getString("straat"),
                    result.getString("woonplaats"),
                    reiziger);

            adressen.add(adres);
        }

        return adressen;
    }
}
