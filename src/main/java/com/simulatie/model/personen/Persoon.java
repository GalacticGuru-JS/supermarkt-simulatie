package com.simulatie.model.personen;

import com.simulatie.model.Supermarkt;
import com.simulatie.model.winkel.Point;
import com.simulatie.view.SupermarktView;

import java.util.Queue;

public abstract class Persoon {
    private double x; // Pixel X-coordinaat
    private double y; // Pixel Y-coordinaat
    private static final double BASIS_SNELHEID = 1.0;
    protected double snelheid = BASIS_SNELHEID;
    protected Queue<Point> pad;

    public Persoon(Point startPositie) {
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
            pad.poll();
        } else {
            x += (dx / afstand) * snelheid;
            y += (dy / afstand) * snelheid;
        }
    }

    public void setSnelheidMultiplier(double multiplier) {
        this.snelheid = BASIS_SNELHEID * multiplier;
    }

    public Point getHuidigeTegel() {
        return new Point(
                (int) Math.round(x / SupermarktView.TEGEL_GROOTTE),
                (int) Math.round(y / SupermarktView.TEGEL_GROOTTE)
        );
    }

    public double getX() { return x; }
    public double getY() { return y; }
}