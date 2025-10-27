package com.example.supermarktsimulator;

import com.example.supermarktsimulator.graphics.TilesheetManager;
import com.example.supermarktsimulator.model.Product;
import com.example.supermarktsimulator.game.SupermarktGame;
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
import java.util.Map;
import java.util.Optional;

/**
 * Hoofdapplicatie voor de supermarkt simulator
 * Verantwoordelijk voor UI rendering en user input
 */
public class SupermarktApplication extends Application {

    private SupermarktGame game;
    private TilesheetManager tilesheetManager;
    private GridPane grid;
    private Label statusLabel;
    private Label inventoryLabel;

    private static final int TILE_SIZE = 32;
    private static final String TILESHEET_PATH = "/FlorisIsCool.png";
    private static final String MAP_PATH = "/tilemap.txt";

    @Override
    public void start(Stage stage) {
        try {
            initializeGame();
        } catch (IOException e) {
            showError("Kan game niet laden: " + e.getMessage());
            return;
        }

        Scene scene = new Scene(createMainLayout(), 1400, 700);
        stage.setTitle("Supermarkt Simulatie - JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initialiseert de game en graphics managers
     */
    private void initializeGame() throws IOException {
        game = new SupermarktGame(MAP_PATH);
        tilesheetManager = new TilesheetManager(TILESHEET_PATH, TILE_SIZE);
    }

    /**
     * Creëert de hoofdlayout van de applicatie
     */
    private BorderPane createMainLayout() {
        BorderPane root = new BorderPane();

        grid = createTileGrid();
        renderMap();

        root.setCenter(grid);
        root.setRight(createInfoPanel());

        return root;
    }

    /**
     * Creëert de GridPane voor de tilemap
     */
    private GridPane createTileGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(0);
        gridPane.setVgap(0);
        gridPane.setPadding(new Insets(10));
        return gridPane;
    }

    /**
     * Creëert het informatie paneel aan de rechterkant
     */
    private VBox createInfoPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-min-width: 300;");

        Label title = new Label("🛒 Supermarkt Simulator");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        statusLabel = new Label("Klik op een product om het op te pakken!");
        statusLabel.setWrapText(true);

        inventoryLabel = new Label("Winkelmandje (0):");
        inventoryLabel.setStyle("-fx-font-weight: bold;");

        panel.getChildren().addAll(title, statusLabel, new Label(""), inventoryLabel);
        return panel;
    }

    /**
     * Rendert de volledige tilemap
     */
    private void renderMap() {
        grid.getChildren().clear();

        int[][] map = game.getMap();

        for (int row = 0; row < game.getMapHeight(); row++) {
            for (int col = 0; col < game.getMapWidth(); col++) {
                ImageView tileView = createTileView(row, col, map[row][col]);
                grid.add(tileView, col, row);
            }
        }
    }

    /**
     * Creëert een ImageView voor een specifieke tile
     */
    private ImageView createTileView(int row, int col, int tileIndex) {
        ImageView tileView = new ImageView(tilesheetManager.getTileImage(tileIndex));
        tileView.setFitWidth(TILE_SIZE);
        tileView.setFitHeight(TILE_SIZE);

        if (game.isProduct(tileIndex)) {
            addProductInteractivity(tileView);
        }

        tileView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                handleTileClick(row, col);
            }
        });

        return tileView;
    }

    /**
     * Voegt hover effecten toe aan product tiles
     */
    private void addProductInteractivity(ImageView tileView) {
        tileView.setStyle("-fx-cursor: hand;");
        tileView.setOnMouseEntered(e -> tileView.setOpacity(0.7));
        tileView.setOnMouseExited(e -> tileView.setOpacity(1.0));
    }

    /**
     * Behandelt een klik op een tile
     */
    private void handleTileClick(int row, int col) {
        Optional<Product> pickedProduct = game.pickupProduct(row, col);

        if (pickedProduct.isPresent()) {
            handleSuccessfulPickup(pickedProduct.get());
        } else {
            handleFailedPickup();
        }
    }

    /**
     * Behandelt een succesvol product oppakken
     */
    private void handleSuccessfulPickup(Product product) {
        statusLabel.setText("✅ " + product.getName() + " toegevoegd aan winkelmandje!");
        statusLabel.setStyle("-fx-text-fill: green;");
        updateInventoryDisplay();
        renderMap();
    }

    /**
     * Behandelt een mislukte poging om een product op te pakken
     */
    private void handleFailedPickup() {
        statusLabel.setText("❌ Hier ligt geen product!");
        statusLabel.setStyle("-fx-text-fill: red;");
    }

    /**
     * Update de weergave van het winkelmandje
     */
    private void updateInventoryDisplay() {
        int total = game.getInventory().getTotalItems();
        StringBuilder sb = new StringBuilder("Winkelmandje (" + total + "):\n");

        Map<String, Integer> counts = game.getInventory().getProductCounts();

        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            sb.append("\n📦 ").append(entry.getKey()).append(": ").append(entry.getValue());
        }

        inventoryLabel.setText(sb.toString());
    }

    /**
     * Toont een error bericht
     */
    private void showError(String message) {
        System.err.println("ERROR: " + message);
    }

    public static void main(String[] args) {
        launch();
    }
}