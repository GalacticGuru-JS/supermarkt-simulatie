package com.simulatie.model.personen;

import com.simulatie.model.Supermarkt;
import com.simulatie.model.winkel.Point;
import com.simulatie.view.SupermarktView;

import java.util.Queue;

/**
 * HET DOEL:
 * Dit is een 'abstracte' basisklasse voor alle bewegende entiteiten in de simulatie.
 * Het bevat de code die voor ALLE personen hetzelfde is, zoals hun positie en bewegingslogica.
 * DE KEUZE:
 * Door een abstracte klasse te gebruiken, voorkomen we code-duplicatie. De 'beweeg'-methode hoeven
 * we maar één keer te schrijven, en `Klant`, `Vakkenvuller` en `SupermarktManager` erven deze
 * functionaliteit automatisch over (dit heet 'inheritance').
 */
public abstract class Persoon {
    // Pixelcoördinaten voor vloeiende beweging.
    private double x;
    private double y;

    private static final double BASIS_SNELHEID = 1.0;
    protected double snelheid = BASIS_SNELHEID;
    protected Queue<Point> pad; // Een 'Queue' is een wachtrij van punten waar de persoon naartoe moet lopen.

    public Persoon(Point startPositie) {
        // We slaan de positie op in pixels, niet in tegels. Dit is nodig voor vloeiende animatie.
        this.x = startPositie.x * SupermarktView.TEGEL_GROOTTE;
        this.y = startPositie.y * SupermarktView.TEGEL_GROOTTE;
    }

    /**
     * Een 'abstracte' methode heeft geen inhoud. Het dwingt elke subklasse (Klant, Vakkenvuller)
     * om zijn EIGEN versie van deze methode te implementeren. Een Klant update immers anders dan een Manager.
     * @param supermarkt Het model met alle informatie die de persoon nodig heeft om een beslissing te nemen.
     */
    public abstract void update(Supermarkt supermarkt);

    /**
     * Beweegt de persoon een kleine stap in de richting van het volgende punt in zijn pad.
     */
    protected void beweeg() {
        if (pad == null || pad.isEmpty()) return;

        // 'peek()' kijkt naar het volgende item in de wachtrij zonder het te verwijderen.
        Point volgendPunt = pad.peek();
        double doelX = volgendPunt.x * SupermarktView.TEGEL_GROOTTE;
        double doelY = volgendPunt.y * SupermarktView.TEGEL_GROOTTE;

        // Bereken de vector (richting en afstand) naar het doel.
        double dx = doelX - x;
        double dy = doelY - y;
        double afstand = Math.sqrt(dx * dx + dy * dy);

        // Als we heel dichtbij zijn, spring dan direct naar het doel en haal het punt uit de wachtrij.
        if (afstand < snelheid) {
            x = doelX;
            y = doelY;
            pad.poll(); // 'poll()' haalt het volgende item uit de wachtrij.
        } else {
            // Anders, zet een kleine stap in de juiste richting.
            // Dit is vector-normalisatie: we delen de richtingvector (dx, dy) door de lengte (afstand)
            // en vermenigvuldigen met de gewenste stapgrootte (snelheid).
            x += (dx / afstand) * snelheid;
            y += (dy / afstand) * snelheid;
        }
    }

    public void setSnelheidMultiplier(double multiplier) {
        this.snelheid = BASIS_SNELHEID * multiplier;
    }

    /**
     * Berekent op welke tegel de persoon zich momenteel bevindt, gebaseerd op zijn pixelpositie.
     */
    public Point getHuidigeTegel() {
        return new Point(
                (int) Math.round(x / SupermarktView.TEGEL_GROOTTE),
                (int) Math.round(y / SupermarktView.TEGEL_GROOTTE)
        );
    }

    public double getX() { return x; }
    public double getY() { return y; }
}