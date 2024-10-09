package dp.assignments.ovchip.dao;

import dp.assignments.ovchip.domain.OVChipkaart;
import dp.assignments.ovchip.domain.Product;
import dp.assignments.ovchip.domain.Reiziger;
import dp.assignments.ovchip.interfaces.ProductDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOPsql implements ProductDAO {
    private final Connection conn;
    private final ReizigerDAOPsql rdao;

    public ProductDAOPsql(Connection conn, ReizigerDAOPsql rdao) {
        this.conn = conn;
        this.rdao = rdao;
    }

    @Override
    public boolean save(Product product) throws SQLException {
        String sql = "INSERT INTO product (product_nummer, naam, beschrijving, prijs) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, product.getProductNummer());
            stmt.setString(2, product.getNaam());
            stmt.setString(3, product.getBeschrijving());
            stmt.setBigDecimal(4, product.getPrijs());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                for (OVChipkaart ovChipkaart : product.getOvChipkaarten()) {
                    String joinSql = "INSERT INTO ov_chipkaart_product (kaart_nummer, product_nummer) VALUES (?, ?)";
                    try (PreparedStatement joinStmt = conn.prepareStatement(joinSql)) {
                        joinStmt.setInt(1, ovChipkaart.getKaartNummer());
                        joinStmt.setInt(2, product.getProductNummer());
                        joinStmt.executeUpdate();
                    }
                }
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean update(Product product) throws SQLException {
        String sql = "UPDATE product SET naam = ?, beschrijving = ?, prijs = ? WHERE product_nummer = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getNaam());
            stmt.setString(2, product.getBeschrijving());
            stmt.setBigDecimal(3, product.getPrijs());
            stmt.setInt(4, product.getProductNummer());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                String deleteJoinSql = "DELETE FROM ov_chipkaart_product WHERE product_nummer = ?";
                try (PreparedStatement deleteJoinStmt = conn.prepareStatement(deleteJoinSql)) {
                    deleteJoinStmt.setInt(1, product.getProductNummer());
                    deleteJoinStmt.executeUpdate();
                }

                for (OVChipkaart ovChipkaart : product.getOvChipkaarten()) {
                    String insertJoinSql = "INSERT INTO ov_chipkaart_product (kaart_nummer, product_nummer) VALUES (?, ?)";
                    try (PreparedStatement insertJoinStmt = conn.prepareStatement(insertJoinSql)) {
                        insertJoinStmt.setInt(1, ovChipkaart.getKaartNummer());
                        insertJoinStmt.setInt(2, product.getProductNummer());
                        insertJoinStmt.executeUpdate();
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(Product product) throws SQLException {
        String joinSql = "DELETE FROM ov_chipkaart_product WHERE product_nummer = ?";
        try (PreparedStatement joinStmt = conn.prepareStatement(joinSql)) {
            joinStmt.setInt(1, product.getProductNummer());
            joinStmt.executeUpdate();
        }

        String sql = "DELETE FROM product WHERE product_nummer = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, product.getProductNummer());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Product> findByOVChipkaart(OVChipkaart ovChipkaart) throws SQLException {
        String sql = "SELECT p.product_nummer, p.naam, p.beschrijving, p.prijs " +
                "FROM product p " +
                "JOIN ov_chipkaart_product ocp ON p.product_nummer = ocp.product_nummer " +
                "WHERE ocp.kaart_nummer = ?";

        List<Product> producten = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ovChipkaart.getKaartNummer());
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


    @Override
    public List<Product> findAll() throws SQLException {
        String sql = "SELECT * FROM product";
        List<Product> producten = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setProductNummer(rs.getInt("product_nummer"));
                product.setNaam(rs.getString("naam"));
                product.setBeschrijving(rs.getString("beschrijving"));
                product.setPrijs(rs.getBigDecimal("prijs"));

                List<OVChipkaart> ovChipkaarten = findOVChipkaartenByProduct(product.getProductNummer());
                for (OVChipkaart ovChipkaart : ovChipkaarten) {
                    product.addOVChipkaart(ovChipkaart);
                }

                producten.add(product);
            }
        }
        return producten;
    }


    private List<OVChipkaart> findOVChipkaartenByProduct(int productNummer) throws SQLException {
        String sql = "SELECT oc.kaart_nummer, oc.geldig_tot, oc.klasse, oc.saldo, oc.reiziger_id " +
                "FROM ov_chipkaart oc " +
                "JOIN ov_chipkaart_product ocp ON oc.kaart_nummer = ocp.kaart_nummer " +
                "WHERE ocp.product_nummer = ?";

        List<OVChipkaart> ovChipkaarten = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productNummer);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OVChipkaart ovChipkaart = new OVChipkaart();
                ovChipkaart.setKaartNummer(rs.getInt("kaart_nummer"));
                ovChipkaart.setGeldigTot(rs.getDate("geldig_tot"));
                ovChipkaart.setKlasse(rs.getInt("klasse"));
                ovChipkaart.setSaldo(rs.getBigDecimal("saldo"));

                Reiziger reiziger = rdao.findById(rs.getInt("reiziger_id"));
                ovChipkaart.setReiziger(reiziger);

                ovChipkaarten.add(ovChipkaart);
            }
        }
        return ovChipkaarten;
    }

}
