package com.example.supermarktsimulator;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * SupermarktApplication is de hoofdapplicatie voor de supermarkt simulator.
 * Deze klasse maakt gebruik van JavaFX om een interactieve tilemap weer te geven
 * waarin de gebruiker producten kan oppakken door erop te klikken.
 */
public class SupermarktApplication extends Application {

    // De TileMapManager beheert de tilemap data en tilesheet
    private TileMapManager mapManager;

    // De GridPane bevat alle tiles in een grid layout
    private GridPane grid;

    // Label dat status berichten toont, bijvoorbeeld "Product opgepakt!"
    private Label statusLabel;

    // Label dat de inhoud van het winkelmandje toont
    private Label inventoryLabel;

    // List die bijhoudt welke producten de gebruiker heeft opgepakt
    private List<String> inventory = new ArrayList<>();

    /**
     * De start methode wordt aangeroepen door JavaFX om de applicatie te starten.
     * Hier wordt de hele UI opgebouwd.
     */
    @Override
    public void start(Stage stage) {
        // Probeer de TileMapManager te initialiseren
        try {
            mapManager = new TileMapManager();
        } catch (IOException e) {
            // Als het laden van de tilemap/tilesheet faalt, print de error en stop
            e.printStackTrace();
            return;
        }

        // ===== LAYOUT OPZETTEN =====

        // BorderPane als hoofdlayout (heeft center, left, right, top, bottom secties)
        BorderPane root = new BorderPane();

        // GridPane voor de tilemap (center van de BorderPane)
        grid = new GridPane();
        grid.setHgap(0);  // Geen horizontale ruimte tussen tiles
        grid.setVgap(0);  // Geen verticale ruimte tussen tiles
        grid.setPadding(new Insets(10));  // 10 pixels padding rondom de grid

        // Render de tilemap voor het eerst
        renderMap();

        // ===== INFO PANEL (rechterkant) =====

        // VBox voor het info panel (verticale container)
        VBox infoPanel = new VBox(10);  // 10 pixels spacing tussen elementen
        infoPanel.setPadding(new Insets(10));
        infoPanel.setStyle("-fx-background-color: #f0f0f0; -fx-min-width: 300;");

        // Titel label
        Label titleLabel = new Label("🛒 Supermarkt Simulator");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        // Status label (laat zien wat er gebeurt bij clicks)
        statusLabel = new Label("Klik op een product om het op te pakken!");
        statusLabel.setWrapText(true);  // Text wrapping als het te lang is

        // Inventory label (toont wat er in het winkelmandje zit)
        inventoryLabel = new Label("Winkelmandje (0):");
        inventoryLabel.setStyle("-fx-font-weight: bold;");

        // Voeg alle labels toe aan de info panel
        infoPanel.getChildren().addAll(titleLabel, statusLabel, new Label(""), inventoryLabel);

        // Plaats de grid in het midden en de info panel rechts
        root.setCenter(grid);
        root.setRight(infoPanel);

        // ===== SCENE EN STAGE CONFIGUREREN =====

        // Maak een Scene met de root layout
        Scene scene = new Scene(root, 1400, 700);
        stage.setTitle("Supermarkt Simulatie - JavaFX");
        stage.setScene(scene);
        stage.show();  // Toon het venster
    }

    /**
     * Rendert de volledige tilemap in de GridPane.
     * Deze methode wordt aangeroepen bij het opstarten en elke keer als de map verandert
     * (bijvoorbeeld wanneer een product wordt opgepakt).
     */
    private void renderMap() {
        // Verwijder alle bestaande tiles uit de grid
        grid.getChildren().clear();

        // Haal de 2D map array op uit de manager
        int[][] map = mapManager.getMap();

        // Loop door alle rijen en kolommen van de map
        for (int row = 0; row < mapManager.getMapHeight(); row++) {
            for (int col = 0; col < mapManager.getMapWidth(); col++) {

                // Haal de tile index op voor deze positie
                int tileIndex = map[row][col];

                // Maak een ImageView met de juiste tile afbeelding
                ImageView tileView = new ImageView(mapManager.getTileImage(tileIndex));
                tileView.setFitWidth(mapManager.getTileSize());
                tileView.setFitHeight(mapManager.getTileSize());

                // Bewaar de huidige positie in final variabelen voor de lambda
                // (Lambda's kunnen alleen final of effectively final variabelen gebruiken)
                final int currentRow = row;
                final int currentCol = col;

                // ===== INTERACTIVITEIT TOEVOEGEN =====

                // Als het een product is, voeg dan hover effects toe
                if (mapManager.isProduct(tileIndex)) {
                    // Verander cursor naar een handje bij hover
                    tileView.setStyle("-fx-cursor: hand;");

                    // Maak tile semi-transparant bij hover (visuele feedback)
                    tileView.setOnMouseEntered(e -> tileView.setOpacity(0.7));

                    // Zet opacity terug naar normaal als de muis weggaat
                    tileView.setOnMouseExited(e -> tileView.setOpacity(1.0));
                }

                // Voeg click handler toe: roep handleTileClick aan bij linkse muisklik
                tileView.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {  // PRIMARY = linkermuisknop
                        handleTileClick(currentRow, currentCol);
                    }
                });

                // Voeg de tile toe aan de grid op de juiste positie
                grid.add(tileView, col, row);
            }
        }
    }

    /**
     * Behandelt een klik op een tile.
     * Deze methode wordt aangeroepen wanneer de gebruiker op een tile klikt.
     * Als het een product is, wordt het opgepakt en toegevoegd aan het winkelmandje.
     *
     * @param row De rij waar op geklikt is
     * @param col De kolom waar op geklikt is
     */
    private void handleTileClick(int row, int col) {
        // Probeer een product op te pakken op deze positie
        // pickupProduct() geeft de tile index terug als het een product was, anders -1
        int pickedProduct = mapManager.pickupProduct(row, col);

        // Controleer of er daadwerkelijk een product was opgepakt
        if (pickedProduct != -1) {
            // ===== PRODUCT SUCCESVOL OPGEPAKT =====

            // Haal de naam van het product op (bijv. "Product A")
            String productName = mapManager.getProductName(pickedProduct);

            // Voeg het product toe aan de inventory list
            inventory.add(productName);

            // Update de status label met een succesbericht (groen)
            statusLabel.setText("✅ " + productName + " toegevoegd aan winkelmandje!");
            statusLabel.setStyle("-fx-text-fill: green;");

            // Update de weergave van het winkelmandje
            updateInventoryDisplay();

            // Render de map opnieuw zodat het product visueel verdwijnt
            renderMap();

        } else {
            // ===== GEEN PRODUCT OP DEZE POSITIE =====

            // Toon een foutmelding (rood)
            statusLabel.setText("❌ Hier ligt geen product!");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * Update de weergave van het winkelmandje in de info panel.
     * Telt hoeveel van elk product er in het winkelmandje zit en toont dit.
     */
    private void updateInventoryDisplay() {
        // Start met een basismelding met het totaal aantal items
        StringBuilder sb = new StringBuilder("Winkelmandje (" + inventory.size() + "):\n");

        // ===== TEL AANTAL VAN ELKE PRODUCT TYPE =====
        int countA = 0, countB = 0, countC = 0;

        // Loop door alle items in het winkelmandje
        for (String product : inventory) {
            if (product.equals("Vlees")) countA++;
            else if (product.equals("Frisdrank")) countB++;
            else if (product.equals("Groente / Fruit")) countC++;
        }

        // Voeg alleen producten toe die daadwerkelijk in het mandje zitten
        if (countA > 0) sb.append("\n📦 Vlees: ").append(countA);
        if (countB > 0) sb.append("\n📦 Frisdrank: ").append(countB);
        if (countC > 0) sb.append("\n📦 Groente / Fruit: ").append(countC);

        // Update het inventory label met de nieuwe tekst
        inventoryLabel.setText(sb.toString());
    }

    /**
     * Main methode: start de JavaFX applicatie
     */
    public static void main(String[] args) {
        launch();  // Roept automatisch start() aan
    }
}