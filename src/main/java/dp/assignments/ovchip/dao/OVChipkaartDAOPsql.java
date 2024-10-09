package dp.assignments.ovchip.dao;

import dp.assignments.ovchip.domain.OVChipkaart;
import dp.assignments.ovchip.domain.Reiziger;
import dp.assignments.ovchip.interfaces.OVChipkaartDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {

    private final Connection conn;
    private ReizigerDAOPsql rdao;

    public OVChipkaartDAOPsql(Connection conn, ReizigerDAOPsql rdao) {
        this.conn = conn;
        this.rdao = rdao;
    }

    @Override
    public boolean save(OVChipkaart ovChipkaart) throws SQLException {
        String sql = "INSERT INTO ov_chipkaart (kaart_nummer, geldig_tot, klasse, saldo, reiziger_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ovChipkaart.getKaartNummer());
            stmt.setDate(2, ovChipkaart.getGeldigTot());
            stmt.setInt(3, ovChipkaart.getKlasse());
            stmt.setBigDecimal(4, ovChipkaart.getSaldo());
            stmt.setInt(5, ovChipkaart.getReiziger().getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean update(OVChipkaart ovChipkaart) throws SQLException {
        String sql = "UPDATE ov_chipkaart SET geldig_tot = ?, klasse = ?, saldo = ?, reiziger_id = ? WHERE kaart_nummer = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, ovChipkaart.getGeldigTot());
            stmt.setInt(2, ovChipkaart.getKlasse());
            stmt.setBigDecimal(3, ovChipkaart.getSaldo());
            stmt.setInt(4, ovChipkaart.getReiziger().getId());
            stmt.setInt(5, ovChipkaart.getKaartNummer());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean delete(OVChipkaart ovChipkaart) throws SQLException {
        String sql = "DELETE FROM ov_chipkaart WHERE kaart_nummer = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ovChipkaart.getKaartNummer());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public OVChipkaart findByID(int kaartNummer) throws SQLException {
        String sql = "SELECT * FROM ov_chipkaart WHERE kaart_nummer = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, kaartNummer);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                OVChipkaart ovChipkaart = new OVChipkaart();
                ovChipkaart.setKaartNummer(rs.getInt("kaart_nummer"));
                ovChipkaart.setGeldigTot(rs.getDate("geldig_tot"));
                ovChipkaart.setKlasse(rs.getInt("klasse"));
                ovChipkaart.setSaldo(rs.getBigDecimal("saldo"));

                int reizigerId = rs.getInt("reiziger_id");
                Reiziger reiziger = rdao.findById(reizigerId);
                ovChipkaart.setReiziger(reiziger);

                return ovChipkaart;
            }
        }
        return null;
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) throws SQLException {
        String sql = "SELECT * FROM ov_chipkaart WHERE reiziger_id = ?";
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reiziger.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OVChipkaart ovChipkaart = new OVChipkaart();
                ovChipkaart.setKaartNummer(rs.getInt("kaart_nummer"));
                ovChipkaart.setGeldigTot(rs.getDate("geldig_tot"));
                ovChipkaart.setKlasse(rs.getInt("klasse"));
                ovChipkaart.setSaldo(rs.getBigDecimal("saldo"));
                ovChipkaart.setReiziger(reiziger);

                ovChipkaarten.add(ovChipkaart);
            }
        }
        return ovChipkaarten;
    }

    @Override
    public List<OVChipkaart> findAll() throws SQLException {
        String sql = "SELECT * FROM ov_chipkaart";
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OVChipkaart ovChipkaart = new OVChipkaart();
                ovChipkaart.setKaartNummer(rs.getInt("kaart_nummer"));
                ovChipkaart.setGeldigTot(rs.getDate("geldig_tot"));
                ovChipkaart.setKlasse(rs.getInt("klasse"));
                ovChipkaart.setSaldo(rs.getBigDecimal("saldo"));

                int reizigerId = rs.getInt("reiziger_id");
                Reiziger reiziger = rdao.findById(reizigerId);
                ovChipkaart.setReiziger(reiziger);

                ovChipkaarten.add(ovChipkaart);
            }
        }
        return ovChipkaarten;
    }
}
