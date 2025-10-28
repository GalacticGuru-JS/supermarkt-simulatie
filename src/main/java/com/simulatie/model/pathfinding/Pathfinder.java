package com.simulatie.model.pathfinding;

import com.simulatie.model.Supermarkt;
import com.simulatie.model.winkel.Point;
import java.util.*;

/**
 * HET DOEL:
 * Een 'utility' klasse voor het vinden van paden. Het gebruikt een Breadth-First Search (BFS) algoritme.
 * DE KEUZE:
 * Alle methodes zijn 'static'. Dit betekent dat we geen object van Pathfinder hoeven te maken
 * om de methodes te gebruiken (we kunnen direct `Pathfinder.vindPad(...)` aanroepen). Dit is
 * logisch voor een klasse die geen eigen staat (variabelen) hoeft bij te houden.
 */
public class Pathfinder {
    /**
     * Vindt het kortste pad van A naar B op de kaart.
     * @param start Het startpunt.
     * @param eind Het eindpunt.
     * @param supermarkt Het model, nodig om te controleren welke tegels beloopbaar zijn.
     * @return Een Queue (wachtrij) van punten die het pad vormen.
     */
    public static Queue<Point> vindPad(Point start, Point eind, Supermarkt supermarkt) {
        Queue<Point> teBezoeken = new LinkedList<>(); // Punten die we nog moeten onderzoeken.
        Map<Point, Point> vorigeTegelMap = new HashMap<>(); // Houdt bij via welk punt we een ander punt hebben bereikt.
        Set<Point> bezocht = new HashSet<>(); // Punten die we al hebben bezocht, om oneindige lussen te voorkomen.

        teBezoeken.add(start);
        bezocht.add(start);

        while (!teBezoeken.isEmpty()) {
            Point huidig = teBezoeken.poll();
            if (huidig.equals(eind)) {
                // Doel gevonden! Construeer het pad terug naar het begin.
                return reconstrueerPad(start, eind, vorigeTegelMap);
            }
            // Bekijk alle buren van het huidige punt.
            for (Point buur : getBeloopbareBuren(huidig, supermarkt)) {
                if (!bezocht.contains(buur)) {
                    bezocht.add(buur);
                    vorigeTegelMap.put(buur, huidig); // Onthoud: "Ik heb 'buur' bereikt via 'huidig'".
                    teBezoeken.add(buur);
                }
            }
        }
        return new LinkedList<>(); // Geen pad gevonden.
    }

    private static List<Point> getBeloopbareBuren(Point p, Supermarkt supermarkt) {
        List<Point> buren = new ArrayList<>();
        int[] dx = {0, 0, 1, -1}; // Veranderingen in x (rechts, links)
        int[] dy = {1, -1, 0, 0}; // Veranderingen in y (onder, boven)
        for (int i = 0; i < 4; i++) {
            Point buur = new Point(p.x + dx[i], p.y + dy[i]);
            if (supermarkt.isBeloopbaar(buur)) {
                buren.add(buur);
            }
        }
        return buren;
    }

    private static Queue<Point> reconstrueerPad(Point start, Point eind, Map<Point, Point> vorigeTegelMap) {
        LinkedList<Point> pad = new LinkedList<>();
        Point stap = eind;
        // Werk terug van het einde naar het begin via de 'vorigeTegelMap'.
        while (stap != null && !stap.equals(start)) {
            pad.addFirst(stap); // Voeg elke stap vooraan de lijst toe.
            stap = vorigeTegelMap.get(stap);
        }
        return pad;
    }
}