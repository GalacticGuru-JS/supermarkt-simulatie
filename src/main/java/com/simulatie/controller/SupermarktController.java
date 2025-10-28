package com.simulatie.controller;

import com.simulatie.model.Supermarkt;

/**
 * HET DOEL:
 * De Controller is de tussenpersoon tussen de View (de knoppen) en het Model (de simulatie-logica).
 * DE KEUZE:
 * We houden deze klasse bewust heel 'dom' en klein. Zijn enige taak is het doorgeven van commando's.
 * Hij weet niet HOE de simulatie versnelt, alleen DAT hij het commando moet doorgeven aan het model.
 * Dit is een kernprincipe van het MVC-ontwerppatroon.
 */
public class SupermarktController {
    // Een 'final' variabele moet direct bij het aanmaken een waarde krijgen en kan daarna nooit meer veranderen.
    // Dit zorgt voor stabiliteit: we weten zeker dat de controller altijd met hetzelfde model praat.
    private final Supermarkt model;

    public SupermarktController(Supermarkt model) {
        this.model = model;
    }

    /**
     * Geeft het commando om de simulatie te updaten door aan het model.
     */
    public void updateSimulatie() {
        model.update();
    }

    /**
     * Geeft het commando om de simulatie te versnellen door aan het model.
     */
    public void versnelSimulatie() {
        model.pasSnelheidAan(1.5);
    }

    /**
     * Geeft het commando om de simulatie te vertragen door aan het model.
     */
    public void vertraagSimulatie() {
        model.pasSnelheidAan(1.0 / 1.5);
    }
}