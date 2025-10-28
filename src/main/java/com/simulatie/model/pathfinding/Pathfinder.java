package com.simulatie.model.pathfinding;

import com.simulatie.model.Supermarkt;
import com.simulatie.model.winkel.Point;
import java.util.*;

public class Pathfinder {
    public static Queue<Point> vindPad(Point start, Point eind, Supermarkt supermarkt) {
        Queue<Point> teBezoeken = new LinkedList<>();
        Map<Point, Point> vorigeTegelMap = new HashMap<>();
        Set<Point> bezocht = new HashSet<>();

        teBezoeken.add(start);
        bezocht.add(start);

        while (!teBezoeken.isEmpty()) {
            Point huidig = teBezoeken.poll();
            if (huidig.equals(eind)) {
                return reconstrueerPad(start, eind, vorigeTegelMap);
            }
            for (Point buur : getBeloopbareBuren(huidig, supermarkt)) {
                if (!bezocht.contains(buur)) {
                    bezocht.add(buur);
                    vorigeTegelMap.put(buur, huidig);
                    teBezoeken.add(buur);
                }
            }
        }
        return new LinkedList<>(); // Geen pad gevonden
    }

    private static List<Point> getBeloopbareBuren(Point p, Supermarkt supermarkt) {
        List<Point> buren = new ArrayList<>();
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};
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
        while (stap != null && !stap.equals(start)) {
            pad.addFirst(stap);
            stap = vorigeTegelMap.get(stap);
        }
        return pad;
    }
}