package com.simulatie.model.winkel;

import com.simulatie.model.personen.Klant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class KassaRij {
    private final Point managerPlek;
    // Fysieke locaties op de kaart, van voor naar achter.
    private final List<Point> wachtPlekken = new ArrayList<>();
    // De daadwerkelijke wachtrij van klanten, van voor naar achter.
    private final LinkedList<Klant> klanten = new LinkedList<>();

    public KassaRij(Point kassaLocatie) {
        // Fysieke plekken in de rij, van voor naar achter.
        this.wachtPlekken.add(new Point(kassaLocatie.x, kassaLocatie.y - 1)); // Vooraan
        this.wachtPlekken.add(new Point(kassaLocatie.x, kassaLocatie.y - 2)); // Midden
        this.wachtPlekken.add(new Point(kassaLocatie.x, kassaLocatie.y - 3)); // Achteraan

        this.managerPlek = new Point(kassaLocatie.x, kassaLocatie.y + 1);
    }

    public int getAantalKlanten() { return klanten.size(); }
    public boolean isVol() { return klanten.size() >= wachtPlekken.size(); }
    public boolean bevatKlant(Klant klant) { return klanten.contains(klant); }

    public Point voegKlantToeAchteraan(Klant klant) {
        if (isVol()) {
            return null;
        }
        klanten.add(klant);
        // De nieuwe klant krijgt de laatste beschikbare fysieke plek toegewezen.
        return wachtPlekken.get(klanten.size() - 1);
    }

    public void verwijderKlantEnSchuifDoor(Klant klant) {
        if (!klanten.contains(klant)) {
            return;
        }
        klanten.remove(klant);

        // Geef alle overgebleven klanten een nieuwe bestemming.
        for (int i = 0; i < klanten.size(); i++) {
            Klant teVerplaatsenKlant = klanten.get(i);
            Point nieuwePlek = wachtPlekken.get(i);

            // Stuur de klant naar de nieuwe plek toe.
            Queue<Point> padNaarNieuwePlek = new LinkedList<>();
            padNaarNieuwePlek.add(nieuwePlek);
            teVerplaatsenKlant.setPad(padNaarNieuwePlek);
        }
    }

    public Klant getKlantVooraan() {
        return klanten.isEmpty() ? null : klanten.getFirst();
    }

    public Point getManagerPlek() {
        return managerPlek;
    }
}