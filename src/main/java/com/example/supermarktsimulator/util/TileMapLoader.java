package com.example.supermarktsimulator.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility klasse voor het laden van tilemaps uit bestanden
 */
public class TileMapLoader {

    /**
     * Leest een tekstbestand met spaties gescheiden nummers en maakt daar een 2D-array van
     *
     * @param path Pad naar het tilemap tekstbestand
     * @return 2D array met tile indices
     * @throws IOException Als het bestand niet geladen kan worden
     */
    public static int[][] loadMap(String path) throws IOException {
        InputStream inputStream = TileMapLoader.class.getResourceAsStream(path);

        if (inputStream == null) {
            throw new IOException("Kan tilemap niet vinden: " + path);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines()
                    .map(line -> line.trim().split(" +"))
                    .map(arr -> java.util.Arrays.stream(arr)
                            .mapToInt(Integer::parseInt)
                            .toArray())
                    .toArray(int[][]::new);
        }
    }
}