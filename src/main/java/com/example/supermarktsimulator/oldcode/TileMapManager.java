package com.example.supermarktsimulator.oldcode;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TileMapManager {
    // De tilesheet bevat alle tiles in één afbeelding
    private static Image tilesheet;

    // 2D array die de layout van de supermarkt bevat
    // Elk nummer verwijst naar een tile in de tilesheet
    private int[][] map;

    // Grootte van elke individuele tile in pixels
    private final int TILE_SIZE = 32;

    /**
     * Constructor: laadt de tilesheet en tilemap
     */
    public TileMapManager() throws IOException {
        // Laad de tilesheet (één grote afbeelding met alle tiles)
        tilesheet = new Image(getClass().getResourceAsStream("/FlorisIsCool.png"));

        // Laad de tilemap vanuit het tekstbestand
        map = loadMap("/tilemap.txt");
    }

    /**
     * Leest een tekstbestand met spaties gescheiden nummers en maakt daar een 2D-array van
     * Bijvoorbeeld: "1 0 3 5" wordt [1, 0, 3, 5]
     *
     * @param path Pad naar het tilemap tekstbestand
     * @return 2D array met tile indices
     */
    private int[][] loadMap(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(path)))) {

            // Stream API om elke regel te verwerken:
            return reader.lines()
                    // Splits elke regel op spaties
                    .map(line -> line.trim().split(" +"))
                    // Converteer strings naar integers
                    .map(arr -> java.util.Arrays.stream(arr)
                            .mapToInt(Integer::parseInt)
                            .toArray())
                    // Maak er een 2D array van
                    .toArray(int[][]::new);
        }
    }

    /**
     * Haalt een specifieke tile uit de tilesheet op basis van de tile index.
     * De tilesheet is een grid van tiles die van links naar rechts worden genummerd.
     *
     * Bijvoorbeeld bij een tilesheet van 10 tiles breed:
     * Index 0 = positie (0,0), Index 1 = positie (1,0), Index 10 = positie (0,1)
     *
     * @param tileIndex De index van de gewenste tile
     * @return Een Image object met alleen die specifieke tile
     */
    public Image getTileImage(int tileIndex) {
        // Bereken hoeveel tiles er in één rij van de tilesheet passen
        int tilesPerRow = (int)(tilesheet.getWidth() / TILE_SIZE);

        // Bereken de X-positie in de tilesheet (rest van de deling)
        int tileX = (tileIndex % tilesPerRow) * TILE_SIZE;

        // Bereken de Y-positie in de tilesheet (gehele deling)
        int tileY = (tileIndex / tilesPerRow) * TILE_SIZE;

        // Knip het juiste stukje uit de tilesheet
        return new WritableImage(
                tilesheet.getPixelReader(),
                tileX, tileY,              // Start positie
                TILE_SIZE, TILE_SIZE       // Breedte en hoogte
        );
    }

    /**
     * Geeft de tile index op een specifieke positie in de map
     *
     * @param row Rij nummer (Y-coördinaat)
     * @param col Kolom nummer (X-coördinaat)
     * @return De tile index, of -1 als de positie buiten de map valt
     */
    public int getTileAt(int row, int col) {
        // Controleer of de positie binnen de grenzen van de map ligt
        if (row < 0 || row >= map.length || col < 0 || col >= map[0].length) {
            return -1; // Buiten de map
        }
        return map[row][col];
    }

    /**
     * Vervangt een tile op een specifieke positie met een andere tile
     *
     * @param row Rij nummer
     * @param col Kolom nummer
     * @param newTileIndex De nieuwe tile index die op deze positie moet komen
     */
    public void setTileAt(int row, int col, int newTileIndex) {
        // Controleer of de positie geldig is
        if (row >= 0 && row < map.length && col >= 0 && col < map[0].length) {
            map[row][col] = newTileIndex;
        }
    }

    /**
     * Controleert of een tile index een product representeert
     * In deze simulatie zijn tiles 3, 4 en 5 producten
     *
     * @param tileIndex De te controleren tile index
     * @return true als het een product is, anders false
     */
    public boolean isProduct(int tileIndex) {
        return tileIndex == 3 || tileIndex == 4 || tileIndex == 5;
    }

    /**
     * Pakt een product op van een specifieke positie.
     * Als er een product ligt, wordt het vervangen door een lege vloer (tile 0)
     *
     * @param row Rij waar het product ligt
     * @param col Kolom waar het product ligt
     * @return De tile index van het opgepakte product, of -1 als er geen product was
     */
    public int pickupProduct(int row, int col) {
        // Haal de huidige tile op deze positie op
        int tileIndex = getTileAt(row, col);

        // Controleer of het een product is
        if (isProduct(tileIndex)) {
            // Vervang het product met een lege vloer (tile 0)
            setTileAt(row, col, 7);
            // Retourneer welk product het was
            return tileIndex;
        }

        // Geen product gevonden op deze positie
        return -1;
    }

    /**
     * Geeft een leesbare naam voor een product op basis van de tile index
     *
     * @param tileIndex De tile index van het product
     * @return De naam van het product
     */
    public String getProductName(int tileIndex) {
        switch (tileIndex) {
            case 3: return "Vlees";
            case 4: return "Frisdrank";
            case 5: return "Groente / Fruit";
            default: return "Onbekend";
        }
    }

    /**
     * @return De breedte van de map (aantal kolommen)
     */
    public int getMapWidth() {
        return map[0].length;
    }

    /**
     * @return De hoogte van de map (aantal rijen)
     */
    public int getMapHeight() {
        return map.length;
    }

    /**
     * @return De grootte van één tile in pixels
     */
    public int getTileSize() {
        return TILE_SIZE;
    }

    /**
     * @return De volledige 2D map array
     */
    public int[][] getMap() {
        return map;
    }
}