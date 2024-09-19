package dp.assignments.ovchip.reiziger;

import java.sql.Date;
import java.util.List;

public interface ReizigerDAO {
    public boolean save(Reiziger reiziger);
    public boolean update(Reiziger reiziger);
    public boolean delete(Reiziger reiziger);
    public Reiziger findById(int id);
    public List<Reiziger> findByGbdatum(Date date);
    public List<Reiziger> findAll();
}
