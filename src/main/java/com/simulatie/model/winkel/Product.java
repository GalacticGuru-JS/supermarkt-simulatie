package com.simulatie.model.winkel;

public class Product {
    private final String naam;
    private final String soort;

    public Product(String naam, String soort) {
        this.naam = naam;
        this.soort = soort;
    }

    public String getNaam() { return naam; }
    public String getSoort() { return soort; }
}