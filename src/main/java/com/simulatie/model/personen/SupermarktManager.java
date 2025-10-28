package com.simulatie.model.personen;

import com.simulatie.model.Supermarkt;
import com.simulatie.model.pathfinding.Pathfinder;
import com.simulatie.model.winkel.Point;

public class SupermarktManager extends Persoon {
    // AANPASSING: AFHANDELEN is verwijderd, de logica zit nu in NAAR_...
    private enum Status { PATROUILLEREN, NAAR_LEVERING, NAAR_KASSA }
    private Status status = Status.PATROUILLEREN;
    private int wachtTimer = 0;
    private Klant klantInBehandeling = null;

    public SupermarktManager(Point startPositie) { super(startPositie); }

    @Override
    public void update(Supermarkt supermarkt) {
        // Als we nog onderweg zijn, beweeg en doe verder niets.
        if (pad != null && !pad.isEmpty()) {
            beweeg();
            return;
        }

        // Als we hier zijn, is het pad leeg. We zijn dus aangekomen of hebben geen taak.
        switch (status) {
            case PATROUILLEREN:
                patrouilleer(supermarkt);
                break;

            case NAAR_LEVERING: // Aangekomen bij de levering
                wachtTimer++;
                if (wachtTimer > 60 / snelheid) { // Korte wachttijd voor levering
                    wachtTimer = 0;
                    supermarkt.verwerkLevering();
                    status = Status.PATROUILLEREN;
                }
                break;

            case NAAR_KASSA: // Aangekomen bij de kassa
                wachtTimer++;
                if (wachtTimer > 120 / snelheid) { // Langere wachttijd voor klant
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

    private void patrouilleer(Supermarkt supermarkt) {
        if (getHuidigeTegel().equals(supermarkt.getKantoorLocatie())) {
            pad = Pathfinder.vindPad(getHuidigeTegel(), supermarkt.getMagazijnDeur(), supermarkt);
        } else {
            pad = Pathfinder.vindPad(getHuidigeTegel(), supermarkt.getKantoorLocatie(), supermarkt);
        }
    }

    public void roepVoorLevering(Supermarkt supermarkt) {
        // Levering heeft hoge prioriteit en kan een kassa-taak onderbreken.
        if (status == Status.PATROUILLEREN || status == Status.NAAR_KASSA) {
            status = Status.NAAR_LEVERING;
            klantInBehandeling = null; // Vergeet de klant, levering is belangrijker.
            pad = Pathfinder.vindPad(getHuidigeTegel(), supermarkt.getManagerLeveringPlek(), supermarkt);
        }
    }

    public void roepNaarKassa(Supermarkt supermarkt, Point managerDoel) {
        // Kassa heeft lage prioriteit, alleen als we patrouilleren.
        if (status == Status.PATROUILLEREN) {
            status = Status.NAAR_KASSA;
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