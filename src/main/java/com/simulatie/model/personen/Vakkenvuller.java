package com.simulatie.model.personen;

import com.simulatie.model.Supermarkt;
import com.simulatie.model.pathfinding.Pathfinder;
import com.simulatie.model.winkel.Point;
import com.simulatie.model.winkel.Schap;

/**
 * HET DOEL:
 * Representeert een vakkenvuller. Zijn taak is om voorraad uit het magazijn te halen
 * en daarmee lege schappen in de winkel bij te vullen.
 */
public class Vakkenvuller extends Persoon {

    private enum Status { RONDLOPEN, VOORRAAD_HALEN, VAKKEN_VULLEN }
    private Status status = Status.RONDLOPEN;

    private int handVoorraad = 0; // Hoeveel producten de vuller momenteel bij zich draagt.
    private static final int MAX_HANDVOORRAAD = 20; // Maximaal aantal producten dat hij kan dragen.
    private static final int VUL_HOEVEELHEID = 10; // Hoeveel producten hij per keer in een schap legt.

    private Schap doelSchap = null;

    public Vakkenvuller(Point startPositie) { super(startPositie); }

    public int getHandVoorraad() {
        return handVoorraad;
    }

    /**
     * De 'denk'-methode van de vakkenvuller.
     */
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
                // We zijn aangekomen bij de magazijndeur.
                int gehaaldeVoorraad = supermarkt.getMagazijn().pakVoorraad(MAX_HANDVOORRAAD - handVoorraad);
                handVoorraad += gehaaldeVoorraad;
                status = Status.RONDLOPEN; // Terug naar basisstatus om nieuwe beslissing te maken.
                break;

            case VAKKEN_VULLEN:
                // We zijn aangekomen bij het lege schap.
                if (doelSchap != null) {
                    doelSchap.vulBij(VUL_HOEVEELHEID);
                    handVoorraad -= VUL_HOEVEELHEID;
                    doelSchap = null;
                }
                status = Status.RONDLOPEN;
                break;
        }
    }

    /**
     * De kernlogica van de vakkenvuller: wat is de volgende stap?
     */
    private void beslisVolgendeActie(Supermarkt supermarkt) {
        if (handVoorraad <= 0) {
            // Geen voorraad meer, ga nieuwe halen.
            status = Status.VOORRAAD_HALEN;
            pad = Pathfinder.vindPad(getHuidigeTegel(), supermarkt.getMagazijnDeur(), supermarkt);
        } else {
            // We hebben voorraad, zoek een leeg schap.
            Schap leegSchap = supermarkt.vindLeegSchap();
            if (leegSchap != null) {
                status = Status.VAKKEN_VULLEN;
                doelSchap = leegSchap;
                Point bestemming = supermarkt.vindBeloopbareBuur(leegSchap.getLocatie());
                pad = Pathfinder.vindPad(getHuidigeTegel(), bestemming, supermarkt);
            } else {
                // Geen lege schappen, loop maar een willekeurig rondje.
                pad = Pathfinder.vindPad(getHuidigeTegel(), supermarkt.getWillekeurigeBeloopbareTegel(), supermarkt);
            }
        }
    }
}