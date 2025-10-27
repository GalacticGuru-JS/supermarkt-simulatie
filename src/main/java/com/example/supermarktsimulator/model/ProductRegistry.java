package com.example.supermarktsimulator.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry die tile indices aan Product objecten koppelt
 */
public class ProductRegistry {
    private static final Map<Integer, Product> products = new HashMap<>();

    static {
        // Registreer alle producten met hun tile index
        products.put(3, new Product(3, "Vlees", ProductType.VLEES));
        products.put(4, new Product(4, "Frisdrank", ProductType.FRISDRANK));
        products.put(5, new Product(5, "Groente / Fruit", ProductType.GROENTE_FRUIT));
    }

    /**
     * Haalt een product op basis van tile index
     */
    public static Optional<Product> getProduct(int tileIndex) {
        return Optional.ofNullable(products.get(tileIndex));
    }

    /**
     * Controleert of een tile index een product is
     */
    public static boolean isProduct(int tileIndex) {
        return products.containsKey(tileIndex);
    }

    /**
     * Voegt een nieuw product toe aan de registry
     */
    public static void registerProduct(Product product) {
        products.put(product.getTileIndex(), product);
    }
}