package com.simulatie.model.winkel;

import com.simulatie.model.personen.Klant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * HET DOEL:
 * Beheert de logica voor één enkele kassarij.
 * DE KEUZE:
 * Door de complexe logica van een wachtrij (klanten toevoegen, verwijderen, opschuiven) in een
 * aparte klasse te stoppen, maken we de `Supermarkt`-klasse veel schoner en simpeler.
 * Dit principe heet 'Encapsulation' (inkapseling). De Supermarkt hoeft niet te weten HOE
 * een rij werkt, alleen DAT hij een rij kan vragen om een klant toe te voegen of te verwijderen.
 */
public class KassaRij {
    private final Point managerPlek;
    // Fysieke locaties op de kaart, van voor naar achter.
    private final List<Point> wachtPlekken = new ArrayList<>();
    // De daadwerkelijke, logische wachtrij van klanten.
    private final LinkedList<Klant> klanten = new LinkedList<>();

    public KassaRij(Point kassaLocatie) {
        this.wachtPlekken.add(new Point(kassaLocatie.x, kassaLocatie.y - 1)); // Vooraan
        this.wachtPlekken.add(new Point(kassaLocatie.x, kassaLocatie.y - 2)); // Midden
        this.wachtPlekken.add(new Point(kassaLocatie.x, kassaLocatie.y - 3)); // Achteraan

        this.managerPlek = new Point(kassaLocatie.x, kassaLocatie.y + 1);
    }

    public int getAantalKlanten() { return klanten.size(); }
    public boolean isVol() { return klanten.size() >= wachtPlekken.size(); }
    public boolean bevatKlant(Klant klant) { return klanten.contains(klant); }

    /**
     * Voegt een klant toe aan het einde van de logische wachtrij.
     * @return De fysieke tegel-locatie waar de klant naartoe moet lopen.
     */
    public Point voegKlantToeAchteraan(Klant klant) {
        if (isVol()) return null;
        klanten.add(klant);
        // De nieuwe klant krijgt de fysieke plek die overeenkomt met zijn nieuwe positie in de rij.
        return wachtPlekken.get(klanten.size() - 1);
    }

    /**
     * Verwijdert een klant (meestal de voorste) en laat alle andere klanten opschuiven.
     */
    public void verwijderKlantEnSchuifDoor(Klant klant) {
        if (!klanten.contains(klant)) return;
        klanten.remove(klant);

        // DE KEUZE: Deze methode is de meest robuuste manier om een rij op te schuiven.
        // In plaats van ingewikkelde logica over "wie staat achter wie", geven we simpelweg
        // elke overgebleven klant een nieuwe doel-locatie op basis van zijn nieuwe index in de lijst.
        for (int i = 0; i < klanten.size(); i++) {
            Klant teVerplaatsenKlant = klanten.get(i);
            Point nieuwePlek = wachtPlekken.get(i);

            // Geef de klant een heel kort pad (van 1 tegel) naar zijn nieuwe plek.
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