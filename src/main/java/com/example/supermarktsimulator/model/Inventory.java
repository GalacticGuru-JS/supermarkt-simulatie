package com.example.supermarktsimulator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Beheert het winkelmandje van de speler
 */
public class Inventory {
    private final List<Product> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    /**
     * Voegt een product toe aan het winkelmandje
     */
    public void addProduct(Product product) {
        items.add(product);
    }

    /**
     * Verwijdert een product uit het winkelmandje
     */
    public boolean removeProduct(Product product) {
        return items.remove(product);
    }

    /**
     * Leegt het hele winkelmandje
     */
    public void clear() {
        items.clear();
    }

    /**
     * Geeft het totaal aantal items terug
     */
    public int getTotalItems() {
        return items.size();
    }

    /**
     * Geeft alle items terug
     */
    public List<Product> getItems() {
        return new ArrayList<>(items); // Defensive copy
    }

    /**
     * Telt hoeveel van elk product type er in het mandje zit
     */
    public Map<String, Integer> getProductCounts() {
        Map<String, Integer> counts = new HashMap<>();
        for (Product product : items) {
            counts.merge(product.getName(), 1, Integer::sum);
        }
        return counts;
    }

    /**
     * Controleert of het winkelmandje leeg is
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
}