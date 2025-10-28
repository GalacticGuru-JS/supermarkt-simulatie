package com.simulatie.model.winkel;

public class Schap {
    private final Point locatie;
    private final int origineleTegelId;
    private final Product product;
    private int voorraad;

    public Schap(Point locatie, int tegelId, Product product, int startVoorraad) {
        this.locatie = locatie;
        this.origineleTegelId = tegelId;
        this.product = product;
        this.voorraad = startVoorraad;
    }

    public boolean isLeeg() { return voorraad <= 0; }
    public void pakProduct() { if (voorraad > 0) voorraad--; }
    public void vulBij(int aantal) { this.voorraad += aantal; }
    public Point getLocatie() { return locatie; }
    public int getOrigineleTegelId() { return origineleTegelId; }
    public Product getProduct() { return product; }
    public int getVoorraad() { return voorraad; }
}