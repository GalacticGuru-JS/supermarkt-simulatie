package com.simulatie.model.personen;

import com.simulatie.model.Supermarkt;
import com.simulatie.model.pathfinding.Pathfinder;
import com.simulatie.model.winkel.Point;
import com.simulatie.model.winkel.Schap;

public class Vakkenvuller extends Persoon {

    private enum Status { RONDLOPEN, VOORRAAD_HALEN, VAKKEN_VULLEN }
    private Status status = Status.RONDLOPEN;

    private int handVoorraad = 0;
    private static final int MAX_HANDVOORRAAD = 20;
    private static final int VUL_HOEVEELHEID = 10;

    private Schap doelSchap = null;

    public Vakkenvuller(Point startPositie) { super(startPositie); }

    public int getHandVoorraad() {
        return handVoorraad;
    }

    @Override
    public void update(Supermarkt supermarkt) {
        if (pad != null && !pad.isEmpty()) {
            beweeg();
            return;
        }

        switch (status) {
            case RONDLOPEN:
                beslisVolgendeActie(supermarkt);
                break;

            case VOORRAAD_HALEN:
                // We zijn aangekomen bij de magazijndeur
                int gehaaldeVoorraad = supermarkt.getMagazijn().pakVoorraad(MAX_HANDVOORRAAD - handVoorraad);
                handVoorraad += gehaaldeVoorraad;
                System.out.println("Vakkenvuller heeft " + gehaaldeVoorraad + " items gehaald. Totaal: " + handVoorraad);
                status = Status.RONDLOPEN;
                break;

            case VAKKEN_VULLEN:
                // We zijn aangekomen bij het lege schap
                if (doelSchap != null) {
                    doelSchap.vulBij(VUL_HOEVEELHEID);
                    handVoorraad -= VUL_HOEVEELHEID;
                    doelSchap = null;
                }
                status = Status.RONDLOPEN;
                break;
        }
    }

    private void beslisVolgendeActie(Supermarkt supermarkt) {
        if (handVoorraad <= 0) {
            // Geen voorraad meer, ga nieuwe halen.
            status = Status.VOORRAAD_HALEN;
            pad = Pathfinder.vindPad(getHuidigeTegel(), supermarkt.getMagazijnDeur(), supermarkt);
            System.out.println("Vakkenvuller gaat voorraad halen.");
        } else {
            // We hebben voorraad, zoek een leeg schap.
            Schap leegSchap = supermarkt.vindLeegSchap();
            if (leegSchap != null) {
                status = Status.VAKKEN_VULLEN;
                doelSchap = leegSchap;
                Point bestemming = supermarkt.vindBeloopbareBuur(leegSchap.getLocatie());
                pad = Pathfinder.vindPad(getHuidigeTegel(), bestemming, supermarkt);
                System.out.println("Vakkenvuller gaat naar leeg schap op " + leegSchap.getLocatie());
            } else {
                // Geen lege schappen, loop maar een rondje.
                pad = Pathfinder.vindPad(getHuidigeTegel(), supermarkt.getWillekeurigeBeloopbareTegel(), supermarkt);
            }
        }
    }
}