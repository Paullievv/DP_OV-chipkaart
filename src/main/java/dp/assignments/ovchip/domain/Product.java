package dp.assignments.ovchip.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Product {
    private int productNummer;
    private String naam;
    private String beschrijving;
    private BigDecimal prijs;
    private List<OVChipkaart> ovChipkaarten = new ArrayList<>();

    public Product(int productNummer, String naam, String beschrijving, BigDecimal prijs) {
        this.productNummer = productNummer;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.prijs = prijs;
    }

    public Product() {

    }

    public void addOVChipkaart(OVChipkaart ovChipkaart) {
        if (!ovChipkaarten.contains(ovChipkaart)) {
            ovChipkaarten.add(ovChipkaart);
            ovChipkaart.addProduct(this);
        }
    }

    public void removeOVChipkaart(OVChipkaart ovChipkaart) {
        if (ovChipkaarten.contains(ovChipkaart)) {
            ovChipkaarten.remove(ovChipkaart);
            ovChipkaart.removeProduct(this);
        }
    }

    public List<OVChipkaart> getOvChipkaarten() {
        return ovChipkaarten;
    }

    public int getProductNummer() {
        return productNummer;
    }

    public void setProductNummer(int productNummer) {
        this.productNummer = productNummer;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public BigDecimal getPrijs() {
        return prijs;
    }

    public void setPrijs(BigDecimal prijs) {
        this.prijs = prijs;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productNummer=" + productNummer +
                ", naam='" + naam + '\'' +
                ", beschrijving='" + beschrijving + '\'' +
                ", prijs=" + prijs +
                '}';
    }
}
