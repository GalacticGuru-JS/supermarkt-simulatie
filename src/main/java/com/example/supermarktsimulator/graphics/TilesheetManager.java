package com.example.supermarktsimulator.graphics;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

/**
 * Beheert de tilesheet en het uitsnijden van individuele tiles
 */
public class TilesheetManager {
    private final Image tilesheet;
    private final int tileSize;

    public TilesheetManager(String tilesheetPath, int tileSize) {
        this.tilesheet = new Image(getClass().getResourceAsStream(tilesheetPath));
        this.tileSize = tileSize;
    }

    /**
     * Haalt een specifieke tile uit de tilesheet op basis van index
     *
     * @param tileIndex De index van de gewenste tile
     * @return Een Image object met alleen die specifieke tile
     */
    public Image getTileImage(int tileIndex) {
        int tilesPerRow = (int)(tilesheet.getWidth() / tileSize);

        int tileX = (tileIndex % tilesPerRow) * tileSize;
        int tileY = (tileIndex / tilesPerRow) * tileSize;

        return new WritableImage(
                tilesheet.getPixelReader(),
                tileX, tileY,
                tileSize, tileSize
        );
    }

    public int getTileSize() {
        return tileSize;
    }
}