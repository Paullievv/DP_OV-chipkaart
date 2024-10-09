package dp.assignments.ovchip.dao;

import dp.assignments.ovchip.domain.OVChipkaart;
import dp.assignments.ovchip.domain.Product;
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
    public ReizigerDAOPsql rdao;
    private final ProductDAOPsql productDAO;

    public OVChipkaartDAOPsql(Connection conn, ReizigerDAOPsql rdao, ProductDAOPsql productDAO) {
        this.conn = conn;
        this.rdao = rdao;
        this.productDAO = productDAO;
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
            if (affectedRows > 0) {
                saveProducts(ovChipkaart);
                return true;
            }
            return false;
        }
    }

    private void saveProducts(OVChipkaart ovChipkaart) throws SQLException {
        String sql = "INSERT INTO ov_chipkaart_product (kaart_nummer, product_nummer) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Product product : ovChipkaart.getProducten()) {
                stmt.setInt(1, ovChipkaart.getKaartNummer());
                stmt.setInt(2, product.getProductNummer());
                stmt.addBatch();
            }
            stmt.executeBatch();
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
            if (affectedRows > 0) {
                deleteProducts(ovChipkaart);
                saveProducts(ovChipkaart);
                return true;
            }
            return false;
        }
    }

    private void deleteProducts(OVChipkaart ovChipkaart) throws SQLException {
        String sql = "DELETE FROM ov_chipkaart_product WHERE kaart_nummer = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ovChipkaart.getKaartNummer());
            stmt.executeUpdate();
        }
    }

    @Override
    public boolean delete(OVChipkaart ovChipkaart) throws SQLException {
        deleteProducts(ovChipkaart);

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

                ovChipkaart.setProducten(findProductsByOVChipkaart(ovChipkaart));

                return ovChipkaart;
            }
        }
        return null;
    }

    private List<Product> findProductsByOVChipkaart(OVChipkaart ovChipkaart) throws SQLException {
        String sql = "SELECT p.* FROM product p JOIN ov_chipkaart_product ocp ON p.product_nummer = ocp.product_nummer WHERE ocp.kaart_nummer = ?";
        List<Product> producten = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ovChipkaart.getKaartNummer());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_nummer"),
                        rs.getString("naam"),
                        rs.getString("beschrijving"),
                        rs.getBigDecimal("prijs")
                );
                producten.add(product);
            }
        }
        return producten;
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

                ovChipkaart.setProducten(findProductsByOVChipkaart(ovChipkaart));

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

                List<Product> producten = findProductsByOVChipkaart(ovChipkaart.getKaartNummer());
                for (Product product : producten) {
                    ovChipkaart.addProduct(product);
                }

                ovChipkaarten.add(ovChipkaart);
            }
        }
        return ovChipkaarten;
    }

    private List<Product> findProductsByOVChipkaart(int kaartNummer) throws SQLException {
        String sql = "SELECT p.product_nummer, p.naam, p.beschrijving, p.prijs " +
                "FROM product p " +
                "JOIN ov_chipkaart_product ocp ON p.product_nummer = ocp.product_nummer " +
                "WHERE ocp.kaart_nummer = ?";

        List<Product> producten = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, kaartNummer);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setProductNummer(rs.getInt("product_nummer"));
                product.setNaam(rs.getString("naam"));
                product.setBeschrijving(rs.getString("beschrijving"));
                product.setPrijs(rs.getBigDecimal("prijs"));

                producten.add(product);
            }
        }
        return producten;
    }


}
