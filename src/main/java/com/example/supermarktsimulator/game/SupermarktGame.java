package com.example.supermarktsimulator.game;

import com.example.supermarktsimulator.model.ProductRegistry;
import com.example.supermarktsimulator.model.Inventory;
import com.example.supermarktsimulator.model.Product;
import com.example.supermarktsimulator.util.TileMapLoader;

import java.io.IOException;
import java.util.Optional;

/**
 * Beheert de game state en logica van de supermarkt simulator
 */
public class SupermarktGame {
    private int[][] map;
    private final Inventory inventory;
    private final int emptyTileIndex = 7; // Tile die verschijnt na oppakken product

    public SupermarktGame(String mapPath) throws IOException {
        this.map = TileMapLoader.loadMap(mapPath);
        this.inventory = new Inventory();
    }

    /**
     * Probeert een product op te pakken op de gegeven positie
     *
     * @param row Rij positie
     * @param col Kolom positie
     * @return Optional met Product als succesvol, anders empty
     */
    public Optional<Product> pickupProduct(int row, int col) {
        if (!isValidPosition(row, col)) {
            return Optional.empty();
        }

        int tileIndex = map[row][col];
        Optional<Product> product = ProductRegistry.getProduct(tileIndex);

        if (product.isPresent()) {
            // Vervang product met lege tile
            map[row][col] = emptyTileIndex;

            // Voeg toe aan inventory
            inventory.addProduct(product.get());
        }

        return product;
    }

    /**
     * Controleert of een positie binnen de map grenzen ligt
     */
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < map.length &&
                col >= 0 && col < map[0].length;
    }

    /**
     * Haalt de tile index op een positie op
     */
    public int getTileAt(int row, int col) {
        if (!isValidPosition(row, col)) {
            return -1;
        }
        return map[row][col];
    }

    /**
     * Controleert of een tile een product is
     */
    public boolean isProduct(int tileIndex) {
        return ProductRegistry.isProduct(tileIndex);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int[][] getMap() {
        return map;
    }

    public int getMapWidth() {
        return map[0].length;
    }

    public int getMapHeight() {
        return map.length;
    }
}