package com.simulatie.view;

import com.simulatie.model.Supermarkt;
import com.simulatie.model.personen.Persoon;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * HET DOEL:
 * De View is verantwoordelijk voor alles wat met tekenen te maken heeft.
 * Hij krijgt de huidige staat van het Model (`Supermarkt`) en tekent deze op het `Canvas`.
 * DE KEUZE:
 * De View is, net als de Controller, 'dom'. Hij bevat geen enkele simulatielogica.
 * Hij weet niet waarom een persoon beweegt, alleen DAT hij op een bepaalde x,y-coördinaat getekend moet worden.
 * Deze scheiding maakt het mogelijk om de hele grafische weergave te veranderen zonder de simulatielogica aan te passen.
 */
public class SupermarktView {
    // Een constante ('static final') is een waarde die gedeeld wordt door alle objecten van deze klasse en nooit verandert.
    // Dit is de grootte van één tegel in pixels. Door dit hier te definiëren, kunnen we het overal in de code
    // hergebruiken en hoeven we het maar op één plek aan te passen als we de grootte willen veranderen.
    public static final int TEGEL_GROOTTE = 32;

    private final GraphicsContext gc; // De "pen" om op het canvas te tekenen.
    private final Map<String, Image> sprites; // Een 'Map' om afbeeldingen efficiënt op te slaan en op te zoeken op naam.

    public SupermarktView(GraphicsContext gc) {
        this.gc = gc;
        this.sprites = new HashMap<>();
        laadAfbeeldingen();
    }

    /**
     * De hoofd-tekenmethode. Roept de andere tekenmethodes aan in de juiste volgorde.
     * Eerst de achtergrond (de kaart), dan de voorgrond (de personen).
     * @param model Het supermarktmodel dat de data bevat om te tekenen.
     */
    public void render(Supermarkt model) {
        tekenKaart(model.getKaartData());
        tekenPersonen(model.getPersonen());
    }

    /**
     * Tekent de tegelkaart (de vloer, muren, schappen).
     * @param kaartData Een 2D-array van getallen die de kaart representeert.
     */
    private void tekenKaart(int[][] kaartData) {
        Image tileset = sprites.get("tileset");
        if (tileset == null || kaartData == null) return;

        // Loop door elke tegel in de 2D-array. 'rij' is de y-as, 'kolom' is de x-as.
        for (int rij = 0; rij < kaartData.length; rij++) {
            for (int kolom = 0; kolom < kaartData[rij].length; kolom++) {
                // Teken eerst een donkere achtergrond, voor het geval er geen tegel is.
                gc.setFill(Color.DARKSLATEGRAY);
                gc.fillRect(kolom * TEGEL_GROOTTE, rij * TEGEL_GROOTTE, TEGEL_GROOTTE, TEGEL_GROOTTE);

                int tegelId = kaartData[rij][kolom];
                if (tegelId == 0) continue; // Een ID van 0 is een lege vloer, die slaan we over.

                // 'sx' is de x-positie op de BRONafbeelding (tileset.png).
                // Als tegelId 3 is, pakken we het 3e vierkantje van 32x32 pixels uit de afbeelding.
                double sx = tegelId * TEGEL_GROOTTE;

                // De drawImage-methode met 9 argumenten knipt een stukje uit de bronafbeelding (sx, sy, sw, sh)
                // en plakt het op het canvas op de doelpositie (dx, dy, dw, dh).
                gc.drawImage(tileset, sx, 0, TEGEL_GROOTTE, TEGEL_GROOTTE,
                        kolom * TEGEL_GROOTTE, rij * TEGEL_GROOTTE, TEGEL_GROOTTE, TEGEL_GROOTTE);
            }
        }
    }

    /**
     * Tekent alle personen (klanten, personeel) op het canvas.
     * @param personen De lijst met alle personen uit het model.
     */
    private void tekenPersonen(java.util.List<Persoon> personen) {
        for (Persoon p : personen) {
            // DE KEUZE: We gebruiken de naam van de klasse (bv. "Klant", "Vakkenvuller") om de juiste afbeelding op te zoeken.
            // Dit is een slimme truc om een lange reeks `if/else` statements te vermijden.
            Image sprite = sprites.get(p.getClass().getSimpleName());
            if (sprite != null) {
                // Teken de gevonden afbeelding op de x,y-pixelcoördinaten van de persoon.
                gc.drawImage(sprite, p.getX(), p.getY(), TEGEL_GROOTTE, TEGEL_GROOTTE);
            }
        }
    }

    /**
     * Laadt alle benodigde afbeeldingen in het geheugen bij de start van de applicatie.
     * DE KEUZE: We laden de afbeeldingen maar één keer en slaan ze op in de 'sprites' Map.
     * Dit is veel efficiënter dan de afbeelding elke keer opnieuw van de harde schijf te laden
     * in de 'render'-methode (die 60x per seconde draait).
     */
    private void laadAfbeeldingen() {
        try {
            // De afbeeldingen worden geladen uit de 'resources' map van het project.
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