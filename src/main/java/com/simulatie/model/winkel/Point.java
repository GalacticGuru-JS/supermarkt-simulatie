package com.simulatie.model.winkel;

import java.util.Objects;

/**
 * HET DOEL: Een simpele dataklasse om een (x, y) coördinaat op de tegelkaart bij te houden.
 * DE KEUZE: Velden zijn 'final' gemaakt, wat betekent dat een Point onveranderlijk ('immutable') is.
 * Nadat je een Point(x,y) hebt gemaakt, kun je de x en y niet meer aanpassen. Dit voorkomt onverwachte bugs.
 * De 'equals' en 'hashCode' methodes zijn essentieel om Points correct te kunnen gebruiken in HashMaps en HashSets (zoals in de Pathfinder).
 */
public class Point {
    public final int x;
    public final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
    }
}