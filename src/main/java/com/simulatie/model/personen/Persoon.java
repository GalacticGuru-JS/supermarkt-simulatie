package com.simulatie.model.personen;

import com.simulatie.model.Supermarkt;
import com.simulatie.model.winkel.Point;
import com.simulatie.view.SupermarktView;

import java.util.Queue;

public abstract class Persoon {
    protected double x;
    protected double y;

    // Snelheid staat op 4.0 voor de 4x versnelling
    private static final double BASIS_SNELHEID = 4.0;
    protected final double snelheid = BASIS_SNELHEID;
    protected Queue<Point> pad;

    public Persoon(Point startPositie) {
        // Converteer de start tegel-positie naar pixel-positie
        this.x = startPositie.x * SupermarktView.TEGEL_GROOTTE;
        this.y = startPositie.y * SupermarktView.TEGEL_GROOTTE;
    }

    public abstract void update(Supermarkt supermarkt);

    protected void beweeg() {
        if (pad == null || pad.isEmpty()) return;

        Point volgendPunt = pad.peek();
        double doelX = volgendPunt.x * SupermarktView.TEGEL_GROOTTE;
        double doelY = volgendPunt.y * SupermarktView.TEGEL_GROOTTE;

        double dx = doelX - x;
        double dy = doelY - y;
        double afstand = Math.sqrt(dx * dx + dy * dy);

        if (afstand < snelheid) {
            x = doelX;
            y = doelY;
            pad.poll(); // We zijn er, haal het volgende punt uit de wachtrij
        } else {
            // Beweeg een stapje in de juiste richting
            x += (dx / afstand) * snelheid;
            y += (dy / afstand) * snelheid;
        }
    }

    /**
     * Berekent op welke tegel (grid coördinaat) de persoon zich momenteel bevindt.
     * Dit wordt gedaan door de pixel-coördinaten (x, y) te delen door de grootte van een tegel.
     * Math.round() wordt gebruikt om netjes af te ronden naar de dichtstbijzijnde tegel.
     *
     * @return Een Point object met de x en y coördinaten van de tegel.
     */
    public Point getHuidigeTegel() {
        return new Point(
                (int) Math.round(x / SupermarktView.TEGEL_GROOTTE),
                (int) Math.round(y / SupermarktView.TEGEL_GROOTTE)
        );
    }

    // Getters voor de pixel-coördinaten, gebruikt voor het tekenen en de muis-detectie.
    public double getX() { return x; }
    public double getY() { return y; }
}