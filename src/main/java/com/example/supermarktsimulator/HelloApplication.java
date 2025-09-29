package com.example.supermarktsimulator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        // Maak een GridPane
        GridPane grid = new GridPane();
        grid.setHgap(5); // horizontale ruimte tussen cellen
        grid.setVgap(5); // verticale ruimte tussen cellen

        // Voorbeeld: 10x10 "supermarkt"
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Label tile = new Label("[" + row + "," + col + "]");
                tile.setStyle("-fx-border-color: black; -fx-padding: 10;");
                grid.add(tile, col, row);
            }
        }

        // Maak de Scene en zet deze op het Stage
        Scene scene = new Scene(grid, 600, 600);
        stage.setTitle("Supermarkt Simulatie");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
