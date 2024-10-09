package dp.assignments.ovchip.domain;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaart {

    private int kaartNummer;
    private Date geldigTot;
    private int klasse;
    private BigDecimal saldo;
    private Reiziger reiziger;
    private List<Product> producten = new ArrayList<>();

    public OVChipkaart(int kaartNummer, Date geldigTot, int klasse, BigDecimal saldo, Reiziger reiziger) {
        this.kaartNummer = kaartNummer;
        this.geldigTot = geldigTot;
        this.klasse = klasse;
        this.saldo = saldo;
        this.reiziger = reiziger;
    }

    public OVChipkaart() {

    }

    public void addProduct(Product product) {
        if (!producten.contains(product)) {
            producten.add(product);
            product.addOVChipkaart(this);
        }
    }

    public void removeProduct(Product product) {
        if (producten.contains(product)) {
            producten.remove(product);
            product.removeOVChipkaart(this);
        }
    }

    public List<Product> getProducten() {
        return producten;
    }

    public void setProducten(List<Product> producten) {
        this.producten = producten;
    }

    public int getKaartNummer() {
        return kaartNummer;
    }

    public void setKaartNummer(int kaartNummer) {
        this.kaartNummer = kaartNummer;
    }

    public Date getGeldigTot() {
        return geldigTot;
    }

    public void setGeldigTot(Date geldigTot) {
        this.geldigTot = geldigTot;
    }

    public int getKlasse() {
        return klasse;
    }

    public void setKlasse(int klasse) {
        this.klasse = klasse;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Reiziger getReiziger() {
        return reiziger;
    }

    public void setReiziger(Reiziger reiziger) {
        this.reiziger = reiziger;
    }

    @Override
    public String toString() {
        return "OVChipkaart: (" + kaartNummer +
                ", geldig tot: " + geldigTot +
                ", klasse: " + klasse +
                ", saldo: " + saldo +
                ", reiziger: " + (reiziger != null ? reiziger.toString() : "null") +
                ')';
    }
}
