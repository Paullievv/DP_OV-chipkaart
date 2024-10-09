package dp.assignments.ovchip.interfaces;

import dp.assignments.ovchip.domain.OVChipkaart;
import dp.assignments.ovchip.domain.Product;

import java.sql.SQLException;
import java.util.List;

public interface ProductDAO {
    public boolean save(Product product) throws SQLException;
    public boolean update(Product product) throws SQLException;
    public boolean delete(Product product) throws SQLException;
    public List<Product> findByOVChipkaart(OVChipkaart ovChipkaart) throws SQLException;
    public List<Product> findAll() throws SQLException;
}
