package com.simulatie.model.personen;

import com.simulatie.model.Supermarkt;
import com.simulatie.model.pathfinding.Pathfinder;
import com.simulatie.model.winkel.Point;

/**
 * HET DOEL:
 * Representeert de manager. De manager heeft twee hoofdtaken: leveringen verwerken
 * en klanten helpen bij de kassa. Deze klasse definieert de logica en prioriteiten voor die taken.
 */
public class SupermarktManager extends Persoon {
    private enum Status { PATROUILLEREN, NAAR_LEVERING, NAAR_KASSA }
    private Status status = Status.PATROUILLEREN;
    private int wachtTimer = 0;
    private Klant klantInBehandeling = null; // Houdt bij welke klant geholpen wordt.

    public SupermarktManager(Point startPositie) { super(startPositie); }

    /**
     * De 'denk'-methode van de manager.
     */
    @Override
    public void update(Supermarkt supermarkt) {
        // Als we nog onderweg zijn, beweeg en doe verder niets.
        if (pad != null && !pad.isEmpty()) {
            beweeg();
            return;
        }

        // DE KEUZE: De vorige versie gebruikte een algemene 'AFHANDELEN' status, wat voor bugs zorgde.
        // Deze nieuwe, robuustere aanpak controleert de status direct wanneer de manager aankomt.
        // Dit is duidelijker en voorkomt verwarring tussen taken.
        switch (status) {
            case PATROUILLEREN:
                patrouilleer(supermarkt);
                break;

            case NAAR_LEVERING: // We zijn aangekomen bij de levering.
                wachtTimer++;
                if (wachtTimer > 60 / snelheid) { // Korte wachttijd voor het verwerken van de levering.
                    wachtTimer = 0;
                    supermarkt.verwerkLevering();
                    status = Status.PATROUILLEREN; // Ga terug naar de basis-status.
                }
                break;

            case NAAR_KASSA: // We zijn aangekomen bij de kassa.
                wachtTimer++;
                if (wachtTimer > 120 / snelheid) { // Langere wachttijd om een klant te helpen.
                    wachtTimer = 0;
                    if (klantInBehandeling != null) {
                        supermarkt.handelKassaAf(klantInBehandeling);
                        klantInBehandeling = null;
                    }
                    status = Status.PATROUILLEREN;
                }
                break;
        }
    }

    /**
     * De standaardtaak: loop heen en weer tussen kantoor en magazijn.
     */
    private void patrouilleer(Supermarkt supermarkt) {
        if (getHuidigeTegel().equals(supermarkt.getKantoorLocatie())) {
            pad = Pathfinder.vindPad(getHuidigeTegel(), supermarkt.getMagazijnDeur(), supermarkt);
        } else {
            pad = Pathfinder.vindPad(getHuidigeTegel(), supermarkt.getKantoorLocatie(), supermarkt);
        }
    }

    /**
     * Wordt aangeroepen door de Supermarkt als er een levering is.
     * Deze taak heeft een hoge prioriteit.
     */
    public void roepVoorLevering(Supermarkt supermarkt) {
        // Levering kan een kassa-taak onderbreken.
        if (status == Status.PATROUILLEREN || status == Status.NAAR_KASSA) {
            status = Status.NAAR_LEVERING;
            klantInBehandeling = null; // Cruciaal: vergeet de klant, levering is nu belangrijker.
            pad = Pathfinder.vindPad(getHuidigeTegel(), supermarkt.getManagerLeveringPlek(), supermarkt);
        }
    }

    /**
     * Wordt aangeroepen door de Supermarkt als een klant hulp nodig heeft.
     * Deze taak heeft een lage prioriteit.
     */
    public void roepNaarKassa(Supermarkt supermarkt, Point managerDoel) {
        // Alleen reageren als we aan het patrouilleren zijn (en dus geen belangrijkere taak hebben).
        if (status == Status.PATROUILLEREN) {
            status = Status.NAAR_KASSA;
            // Vind de specifieke klant die geholpen moet worden.
            supermarkt.vindRijVanKlant(
                    supermarkt.getPersonen().stream()
                            .filter(Klant.class::isInstance)
                            .map(Klant.class::cast)
                            .filter(Klant::wachtOpManager)
                            .findFirst().orElse(null)
            ).ifPresent(rij -> klantInBehandeling = rij.getKlantVooraan());

            pad = Pathfinder.vindPad(getHuidigeTegel(), managerDoel, supermarkt);
        }
    }
}