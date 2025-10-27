package com.example.supermarktsimulator.oldcode;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class TileMapRenderer extends JPanel {

    private BufferedImage tileset;
    private int[][] map;
//    private Tile[] tileDefinitions; // Metadata per tile type
    private final int tileSize = 32; // ✅ Elke tile is 32x32 pixels

    public TileMapRenderer() throws IOException {
        // 1️⃣ Tilesheet laden
        tileset = ImageIO.read(getClass().getResource("/FlorisIsCool.png"));
        map = loadMap("/tilemap.txt");
    }

    /**
     * Leest een tekstbestand met spaties gescheiden nummers en maakt daar een 2D-array van
     */
    private int[][] loadMap(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(path)))) {

            return reader.lines()
                    .map(line -> line.trim().split(" +"))
                    .map(arr -> java.util.Arrays.stream(arr)
                            .mapToInt(Integer::parseInt)
                            .toArray())
                    .toArray(int[][]::new);
        }
    }

    /**
     * Tekent de tilemap op het scherm
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int tilesPerRow = tileset.getWidth() / tileSize;

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                int tileIndex = map[y][x];

                // Bereken positie in tilesheet
                int tileX = (tileIndex % tilesPerRow) * tileSize;
                int tileY = (tileIndex / tilesPerRow) * tileSize;

                BufferedImage tile = tileset.getSubimage(tileX, tileY, tileSize, tileSize);

                // Teken tile op juiste positie
                g.drawImage(tile, x * tileSize, y * tileSize, null);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("🛒 Supermarkt Tilemap");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        TileMapRenderer panel = new TileMapRenderer();
        frame.add(panel);

        frame.setSize(1600, 1200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
