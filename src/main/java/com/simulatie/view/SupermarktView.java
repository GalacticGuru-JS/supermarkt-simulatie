package com.simulatie.view;

import com.simulatie.model.Supermarkt;
import com.simulatie.model.personen.Persoon;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class SupermarktView {
    public static final int TEGEL_GROOTTE = 32;
    private final GraphicsContext gc;
    private final Map<String, Image> sprites = new HashMap<>();

    public SupermarktView(GraphicsContext gc) {
        this.gc = gc;
        laadAfbeeldingen();
    }

    public void render(Supermarkt model) {
        tekenKaart(model.getKaartData());
        tekenPersonen(model.getPersonen());
    }

    private void tekenKaart(int[][] kaartData) {
        Image tileset = sprites.get("tileset");
        if (tileset == null || kaartData == null) return;

        for (int rij = 0; rij < kaartData.length; rij++) {
            for (int kolom = 0; kolom < kaartData[rij].length; kolom++) {
                // Teken een achtergrond voor het geval een tegel niet getekend wordt
                gc.setFill(Color.DARKSLATEGRAY);
                gc.fillRect(kolom * TEGEL_GROOTTE, rij * TEGEL_GROOTTE, TEGEL_GROOTTE, TEGEL_GROOTTE);

                int tegelId = kaartData[rij][kolom];
                if (tegelId == 0) continue; // Lege vloer niet tekenen

                // Bron-X op de tileset afbeelding
                double sx = tegelId * TEGEL_GROOTTE;
                gc.drawImage(tileset, sx, 0, TEGEL_GROOTTE, TEGEL_GROOTTE,
                        kolom * TEGEL_GROOTTE, rij * TEGEL_GROOTTE, TEGEL_GROOTTE, TEGEL_GROOTTE);
            }
        }
    }

    private void tekenPersonen(java.util.List<Persoon> personen) {
        for (Persoon p : personen) {
            Image sprite = sprites.get(p.getClass().getSimpleName());
            if (sprite != null) {
                gc.drawImage(sprite, p.getX(), p.getY(), TEGEL_GROOTTE, TEGEL_GROOTTE);
            }
        }
    }

    private void laadAfbeeldingen() {
        try {
            sprites.put("tileset", new Image(getClass().getResourceAsStream("/tileset.png")));
            sprites.put("Klant", new Image(getClass().getResourceAsStream("/klant.png")));
            sprites.put("Vakkenvuller", new Image(getClass().getResourceAsStream("/vakkenvuller.png")));
            sprites.put("SupermarktManager", new Image(getClass().getResourceAsStream("/manager.png")));
        } catch (Exception e) {
            System.err.println("Kon een of meerdere afbeeldingen niet laden!");
            e.printStackTrace();
        }
    }
}