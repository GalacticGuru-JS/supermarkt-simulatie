package com.simulatie.controller;

import com.simulatie.model.Supermarkt;

public class SupermarktController {
    private final Supermarkt model;

    public SupermarktController(Supermarkt model) {
        this.model = model;
    }

    public void updateSimulatie() {
        model.update();
    }

    public void versnelSimulatie() {
        model.pasSnelheidAan(1.5);
    }

    public void vertraagSimulatie() {
        model.pasSnelheidAan(1.0 / 1.5);
    }
}