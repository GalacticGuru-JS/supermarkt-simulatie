package com.simulatie.model.personen;

import com.simulatie.model.Supermarkt;
import com.simulatie.model.pathfinding.Pathfinder;
import com.simulatie.model.winkel.KassaRij;
import com.simulatie.model.winkel.Point;
import com.simulatie.model.winkel.Schap;

import java.util.Queue;

/**
 * HET DOEL:
 * Representeert een klant in de supermarkt. Deze klasse definieert het gedrag van een klant,
 * zoals producten pakken en in de rij gaan staan bij de kassa.
 */
public class Klant extends Persoon {
    /**
     * DE KEUZE: Een 'enum' (enumeration) is een speciaal type dat een variabele beperkt tot een
     * vooraf gedefinieerde set van waarden. Dit maakt de code veel leesbaarder en veiliger dan
     * het gebruik van getallen (bv. status=1) of strings (bv. status="winkelen").
     * Een tikfout zoals "WINKELEN" vs "WINKELNN" wordt door de compiler gedetecteerd.
     */
    private enum Status { WINKELEN, PRODUCT_PAKKEN, NAAR_KASSA, WACHTEN_IN_RIJ, WINKEL_VERLATEN }
    private Status status = Status.WINKELEN;
    private int itemsTePakken;
    private int wachtTimer = 0; // Timer om even te wachten bij een schap.

    public Klant(Point startPositie) {
        super(startPositie);
        // Een nieuwe klant wil een willekeurig aantal (1, 2 of 3) producten pakken.
        this.itemsTePakken = new java.util.Random().nextInt(3) + 1;
    }

    // Deze methode is nodig zodat de KassaRij de klant kan vertellen om op te schuiven.
    public void setPad(Queue<Point> nieuwPad) {
        this.pad = nieuwPad;
    }

    /**
     * De 'denk'-methode van de klant. Wordt elke tick aangeroepen.
     * De klant beslist wat hij moet doen op basis van zijn huidige status.
     */
    @Override
    public void update(Supermarkt supermarkt) {
        // Als we nog aan het lopen zijn, doe dan niets anders dan bewegen.
        if (pad != null && !pad.isEmpty()) {
            beweeg();
            return;
        }

        // Als we hier zijn, is het pad leeg. We zijn dus aangekomen op onze bestemming
        // en moeten een nieuwe beslissing nemen.
        switch (status) {
            case WINKELEN:
                if (itemsTePakken > 0) {
                    // Nog niet klaar met winkelen, zoek een nieuw product.
                    zoekNieuwProduct(supermarkt);
                } else {
                    // Klaar met winkelen, ga naar de kassa.
                    status = Status.NAAR_KASSA;
                }
                break;

            case PRODUCT_PAKKEN:
                // We zijn aangekomen bij een schap, wacht even.
                wachtTimer++;
                if (wachtTimer > 60) { // Wacht 60 ticks (ca. 1 seconde bij 60 FPS).
                    wachtTimer = 0;
                    itemsTePakken--; // Nu hebben we het product "gepakt".
                    status = Status.WINKELEN; // Ga terug naar de winkel-status om te beslissen wat nu.
                }
                break;

            case NAAR_KASSA:
                vindPlekInRij(supermarkt);
                break;

            case WACHTEN_IN_RIJ:
            case WINKEL_VERLATEN:
                // Doe niets, we wachten op een externe actie (manager die ons helpt, of de rij die opschuift).
                break;
        }
    }

    /**
     * Zoekt een willekeurig schap, vindt een pad ernaartoe en verandert de status.
     */
    private void zoekNieuwProduct(Supermarkt supermarkt) {
        Schap doelSchap = supermarkt.getWillekeurigSchap();
        if (doelSchap != null) {
            doelSchap.pakProduct(); // "Reserveer" een item zodat niet twee klanten hetzelfde lege vak kiezen.
            Point bestemming = supermarkt.vindBeloopbareBuur(doelSchap.getLocatie());
            pad = Pathfinder.vindPad(getHuidigeTegel(), bestemming, supermarkt);
            status = Status.PRODUCT_PAKKEN; // Na aankomst gaan we wachten.
        }
    }

    private void vindPlekInRij(Supermarkt supermarkt) {
        KassaRij besteRij = supermarkt.vindBesteKassaRij();
        if (besteRij != null) {
            Point kassaPlek = besteRij.voegKlantToeAchteraan(this);
            if (kassaPlek != null) {
                pad = Pathfinder.vindPad(getHuidigeTegel(), kassaPlek, supermarkt);
                status = Status.WACHTEN_IN_RIJ;
            }
        }
    }

    /**
     * Wordt aangeroepen door de Supermarkt/Manager als de klant mag afrekenen.
     */
    public void magAfrekenen(Supermarkt supermarkt) {
        pad = Pathfinder.vindPad(getHuidigeTegel(), supermarkt.getUitgangLocatie(), supermarkt);
        status = Status.WINKEL_VERLATEN;
    }

    /**
     * Geeft aan of de manager geroepen moet worden.
     * @return true als de klant in de rij staat EN is aangekomen op zijn plek (pad is leeg).
     */
    public boolean wachtOpManager() {
        return status == Status.WACHTEN_IN_RIJ && (pad == null || pad.isEmpty());
    }

    /**
     * Geeft aan of de klant de winkel heeft verlaten en uit de simulatie verwijderd mag worden.
     */
    public boolean isKlaar() {
        return status == Status.WINKEL_VERLATEN && (pad == null || pad.isEmpty());
    }
}