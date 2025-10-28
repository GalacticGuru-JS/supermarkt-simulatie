package com.simulatie.model.personen;

import com.simulatie.model.Supermarkt;
import com.simulatie.model.pathfinding.Pathfinder;
import com.simulatie.model.winkel.KassaRij;
import com.simulatie.model.winkel.Point;
import com.simulatie.model.winkel.Schap;

import java.util.Queue;

public class Klant extends Persoon {
    // AANPASSING: Nieuwe status toegevoegd
    private enum Status { WINKELEN, PRODUCT_PAKKEN, NAAR_KASSA, WACHTEN_IN_RIJ, WINKEL_VERLATEN }
    private Status status = Status.WINKELEN;
    private int itemsTePakken;
    private int wachtTimer = 0; // AANPASSING: Timer voor wachten bij schap

    public Klant(Point startPositie) {
        super(startPositie);
        this.itemsTePakken = new java.util.Random().nextInt(3) + 1;
    }

    public void setPad(Queue<Point> nieuwPad) {
        this.pad = nieuwPad;
    }

    @Override
    public void update(Supermarkt supermarkt) {
        if (pad != null && !pad.isEmpty()) {
            beweeg();
            return;
        }

        // Als we hier zijn, zijn we aangekomen op onze bestemming.
        switch (status) {
            case WINKELEN:
                if (itemsTePakken > 0) {
                    pakNieuwProduct(supermarkt);
                } else {
                    status = Status.NAAR_KASSA;
                }
                break;

            // AANPASSING: Nieuwe case voor het wachten bij een schap
            case PRODUCT_PAKKEN:
                wachtTimer++;
                if (wachtTimer > 60) { // Wacht 60 ticks (ca. 1 seconde)
                    wachtTimer = 0;
                    itemsTePakken--;
                    status = Status.WINKELEN; // Ga op zoek naar het volgende product
                }
                break;

            case NAAR_KASSA:
                vindPlekInRij(supermarkt);
                break;

            case WACHTEN_IN_RIJ:
            case WINKEL_VERLATEN:
                // Doe niets, wachten tot een andere actie ons een nieuw pad geeft.
                break;
        }
    }

    private void pakNieuwProduct(Supermarkt supermarkt) {
        Schap doelSchap = supermarkt.getWillekeurigSchap();
        // AANPASSING: We 'pakken' het product nog niet, we gaan er alleen naartoe.
        if (doelSchap != null) {
            doelSchap.pakProduct(); // Reserveren zodat niet 2 klanten hetzelfde lege vak kiezen
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

    public void magAfrekenen(Supermarkt supermarkt) {
        pad = Pathfinder.vindPad(getHuidigeTegel(), supermarkt.getUitgangLocatie(), supermarkt);
        status = Status.WINKEL_VERLATEN;
    }

    public boolean wachtOpManager() {
        return status == Status.WACHTEN_IN_RIJ && (pad == null || pad.isEmpty());
    }

    public boolean isKlaar() {
        return status == Status.WINKEL_VERLATEN && (pad == null || pad.isEmpty());
    }
}