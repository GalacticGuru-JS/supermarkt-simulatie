package com.simulatie.model.winkel;

/**
 * HET DOEL: Representeert het magazijn. Beheert de centrale voorraad van de winkel.
 */
public class Magazijn {
    private int voorraad = 60; // Start met een kleine voorraad

    public int getVoorraad() {
        return voorraad;
    }

    public void ontvangLevering(int aantal) {
        this.voorraad += aantal;
        System.out.println("Magazijn ontvangt " + aantal + " items. Nieuwe voorraad: " + this.voorraad);
    }

    /**
     * Probeert een bepaalde hoeveelheid voorraad uit het magazijn te pakken.
     * @param gewensteHoeveelheid De hoeveelheid die gepakt wordt.
     * @return De daadwerkelijk gepakte hoeveelheid (kan minder zijn als de voorraad op is).
     */
    public int pakVoorraad(int gewensteHoeveelheid) {
        int tePakken = Math.min(gewensteHoeveelheid, this.voorraad);
        this.voorraad -= tePakken;
        return tePakken;
    }
}