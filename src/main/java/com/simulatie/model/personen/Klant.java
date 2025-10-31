package com.simulatie.model.personen;

import com.simulatie.model.Supermarkt;
import com.simulatie.model.pathfinding.Pathfinder;
import com.simulatie.model.winkel.KassaRij;
import com.simulatie.model.winkel.Point;
import com.simulatie.model.winkel.Product;
import com.simulatie.model.winkel.Schap;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Klant extends Persoon {
    private enum Status { WINKELEN, PRODUCT_PAKKEN, NAAR_KASSA, WACHTEN_IN_RIJ, WINKEL_VERLATEN }
    private Status status = Status.WINKELEN;
    private int itemsTePakken;
    private int wachtTimer = 0;

    private final List<Product> winkelmandje = new ArrayList<>();

    public Klant(Point startPositie) {
        super(startPositie);

        // --- DE AANPASSING STAAT HIER ---
        // Voorheen: nextInt(3) + 1; (resulteerde in 1 tot 3 items)
        // Nu: Een klant pakt willekeurig tussen de 3 en 7 producten.
        // nextInt(5) geeft een getal van 0-4. Door er 3 bij op te tellen, krijgen we een range van 3-7.
        this.itemsTePakken = new Random().nextInt(5) + 1;
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

        switch (status) {
            case WINKELEN:
                if (itemsTePakken > 0) {
                    zoekNieuwProduct(supermarkt);
                } else {
                    status = Status.NAAR_KASSA;
                }
                break;

            case PRODUCT_PAKKEN:
                wachtTimer++;
                if (wachtTimer > 15) {
                    wachtTimer = 0;
                    itemsTePakken--;
                    status = Status.WINKELEN;
                }
                break;

            case NAAR_KASSA:
                vindPlekInRij(supermarkt);
                break;

            case WACHTEN_IN_RIJ:
            case WINKEL_VERLATEN:
                // Doe niets
                break;
        }
    }

    private void zoekNieuwProduct(Supermarkt supermarkt) {
        Schap doelSchap = supermarkt.getWillekeurigSchap();
        if (doelSchap != null) {
            doelSchap.pakProduct();
            winkelmandje.add(doelSchap.getProduct());

            Point bestemming = supermarkt.vindBeloopbareBuur(doelSchap.getLocatie());
            pad = Pathfinder.vindPad(getHuidigeTegel(), bestemming, supermarkt);
            status = Status.PRODUCT_PAKKEN;
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

    public List<Product> getWinkelmandje() {
        return winkelmandje;
    }
}