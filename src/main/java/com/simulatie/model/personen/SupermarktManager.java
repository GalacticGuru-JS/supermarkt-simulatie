package com.simulatie.model.personen;

import com.simulatie.model.Supermarkt;
import com.simulatie.model.pathfinding.Pathfinder;
import com.simulatie.model.winkel.Point;

public class SupermarktManager extends Persoon {
    private enum Status { PATROUILLEREN, NAAR_LEVERING, NAAR_KASSA }
    private Status status = Status.PATROUILLEREN;
    private int wachtTimer = 0;
    private Klant klantInBehandeling = null;

    public SupermarktManager(Point startPositie) { super(startPositie); }

    @Override
    public void update(Supermarkt supermarkt) {
        if (pad != null && !pad.isEmpty()) {
            beweeg();
            return;
        }

        switch (status) {
            case PATROUILLEREN:
                patrouilleer(supermarkt);
                break;

            case NAAR_LEVERING:
                wachtTimer++;
                // Wachttijd voor verwerken van levering is verkort van 60 naar 15 (4x sneller).
                if (wachtTimer > 30) {
                    wachtTimer = 0;
                    supermarkt.verwerkLevering();
                    status = Status.PATROUILLEREN;
                }
                break;

            case NAAR_KASSA:
                wachtTimer++;
                // Wachttijd voor helpen bij kassa is verkort van 120 naar 30 (4x sneller).
                if (wachtTimer > 30) {
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
        if (status == Status.PATROUILLEREN || status == Status.NAAR_KASSA) {
            status = Status.NAAR_LEVERING;
            klantInBehandeling = null;
            pad = Pathfinder.vindPad(getHuidigeTegel(), supermarkt.getManagerLeveringPlek(), supermarkt);
        }
    }

    public void roepNaarKassa(Supermarkt supermarkt, Point managerDoel) {
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