package com.example.supermarktsimulator.model;

/**
 * Representeert een product in de supermarkt
 */
public class Product {
    private final int tileIndex;
    private final String name;
    private final ProductType type;

    public Product(int tileIndex, String name, ProductType type) {
        this.tileIndex = tileIndex;
        this.name = name;
        this.type = type;
    }

    public int getTileIndex() {
        return tileIndex;
    }

    public String getName() {
        return name;
    }

    public ProductType getType() {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return tileIndex == product.tileIndex;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(tileIndex);
    }
}