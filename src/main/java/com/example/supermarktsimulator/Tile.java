package com.example.supermarktsimulator;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Tile {
    private String type;
    private Image image;
    private ImageView view;
    public Tile(String type, String imagePath, int size){
        this.type = type;
        this.image = new Image(getClass().getResourceAsStream(imagePath));
        this.view = new ImageView(image);
        this.view.setFitWidth(size);
        this.view.setFitHeight(size);
    }

    public String getType() {
        return type;
    }

    public ImageView getView() {
        return view;
    }
}
