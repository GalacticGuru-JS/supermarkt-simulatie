package com.example.supermarktsimulator.oldcode;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class HelloController {
    @FXML
    private GridPane grid;

    @FXML
    public void initialize() {
        // Vul de GridPane met placeholders
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                grid.add(new Label("[" + row + "," + col + "]"), col, row);
            }
        }
    }
}
