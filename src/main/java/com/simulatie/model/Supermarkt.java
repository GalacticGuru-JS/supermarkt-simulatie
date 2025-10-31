package com.simulatie.model;

import com.simulatie.model.personen.Klant;
import com.simulatie.model.personen.Persoon;
import com.simulatie.model.personen.SupermarktManager;
import com.simulatie.model.personen.Vakkenvuller;
import com.simulatie.model.winkel.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Supermarkt {

    public static final int KAART_BREEDTE_IN_TEGELS = 32;
    public static final int KAART_HOOGTE_IN_TEGELS = 21;
    private static final Point INGANG_LOCATIE = new Point(4, 20);
    private static final Point UITGANG_LOCATIE = new Point(3, 20);
    private static final Point KANTOOR_LOCATIE = new Point(2, 2);
    private static final Point MAGAZIJN_DEUR = new Point(25, 12);
    private static final Point MANAGER_LEVERING_PLEK = new Point(30, 12);
    private static final int MAX_AANTAL_KLANTEN = 25;

    private int[][] kaartData;
    private final List<Persoon> personen = new ArrayList<>();
    private final List<Schap> schappen = new ArrayList<>();
    private final List<KassaRij> kassaRijen = new ArrayList<>();
    private SupermarktManager manager;
    private Vakkenvuller vakkenvuller;
    private final Magazijn magazijn = new Magazijn();

    private int klantSpawnTimer = 0;
    private int leveringTimer = 0;
    private boolean leveringOnderweg = false;
    private final Random random = new Random();

    public Supermarkt() {
        laadKaart();
        initialiseerWinkel();
    }

    public void update() {
        beheerLeveringen();
        werkPersonenBij();
        spawnNieuweKlant();
        werkKaartBij();
        verwijderKlantenDieKlaarZijn();
    }

    private void initialiseerWinkel() {
        Product vlees = new Product("Biefstuk", "Vleeswaren");
        Product frisdrank = new Product("Cola", "Frisdrank");
        Product groente = new Product("Broccoli", "Groente");
        Map<Integer, Product> productPerTegel = Map.of(3, vlees, 4, frisdrank, 5, groente);

        for (int r = 0; r < kaartData.length; r++) {
            for (int c = 0; c < kaartData[r].length; c++) {
                int tegelId = kaartData[r][c];
                if (productPerTegel.containsKey(tegelId)) {
                    schappen.add(new Schap(new Point(c, r), tegelId, productPerTegel.get(tegelId), 10));
                }
                if (tegelId == 6) {
                    kassaRijen.add(new KassaRij(new Point(c, r)));
                }
            }
        }

        manager = new SupermarktManager(KANTOOR_LOCATIE);
        vakkenvuller = new Vakkenvuller(MAGAZIJN_DEUR);
        personen.add(manager);
        personen.add(vakkenvuller);
    }

    private void spawnNieuweKlant() {
        klantSpawnTimer++;
        long aantalKlanten = personen.stream().filter(Klant.class::isInstance).count();
        // De wachttijd voor een nieuwe klant is verkort van 100 naar 25 (4x sneller).
        if (klantSpawnTimer > 25 && aantalKlanten < MAX_AANTAL_KLANTEN) {
            klantSpawnTimer = 0;
            Klant nieuweKlant = new Klant(INGANG_LOCATIE);
            personen.add(nieuweKlant);
        }
    }

    private void beheerLeveringen() {
        leveringTimer++;
        // De wachttijd voor een nieuwe levering is verkort van 1000 naar 250 (4x sneller).
        if (!leveringOnderweg && leveringTimer > 3000) {
            leveringTimer = 0;
            leveringOnderweg = true;
            manager.roepVoorLevering(this);
        }
    }

    public void verwerkLevering() {
        magazijn.ontvangLevering(175);
        leveringOnderweg = false;
    }

    private void werkPersonenBij() {
        for (KassaRij rij : kassaRijen) {
            Klant wachtendeKlant = rij.getKlantVooraan();
            if (wachtendeKlant != null && wachtendeKlant.wachtOpManager()) {
                manager.roepNaarKassa(this, rij.getManagerPlek());
                break;
            }
        }

        for (Persoon p : new ArrayList<>(personen)) {
            p.update(this);
        }
    }

    private void verwijderKlantenDieKlaarZijn() {
        personen.removeIf(p -> p instanceof Klant && ((Klant) p).isKlaar());
    }

    private void werkKaartBij() {
        for (Schap schap : schappen) {
            Point p = schap.getLocatie();
            kaartData[p.y][p.x] = schap.isLeeg() ? 7 : schap.getOrigineleTegelId();
        }
    }

    public void handelKassaAf(Klant klant) {
        vindRijVanKlant(klant).ifPresent(rij -> {
            klant.magAfrekenen(this);
            rij.verwijderKlantEnSchuifDoor(klant);
        });
    }

    public KassaRij vindBesteKassaRij() {
        return kassaRijen.stream()
                .filter(r -> !r.isVol())
                .min(Comparator.comparingInt(KassaRij::getAantalKlanten))
                .orElse(null);
    }

    public Optional<KassaRij> vindRijVanKlant(Klant klant) {
        return kassaRijen.stream().filter(r -> r.bevatKlant(klant)).findFirst();
    }

    public boolean isBeloopbaar(Point p) {
        if (p == null || p.y < 0 || p.y >= kaartData.length || p.x < 0 || p.x >= kaartData[0].length) {
            return false;
        }
        int tegelId = kaartData[p.y][p.x];
        return tegelId == 0 || tegelId == 2;
    }

    public Point vindBeloopbareBuur(Point doel) {
        for (int i = 1; i < 5; i++) {
            for (int dx = -i; dx <= i; dx++) {
                for (int dy = -i; dy <= i; dy++) {
                    if (Math.abs(dx) != i && Math.abs(dy) != i) continue;
                    Point buur = new Point(doel.x + dx, doel.y + dy);
                    if (isBeloopbaar(buur)) return buur;
                }
            }
        }
        return getWillekeurigeBeloopbareTegel();
    }

    public Point getWillekeurigeBeloopbareTegel() {
        List<Point> begaanbaar = new ArrayList<>();
        for (int r = 0; r < kaartData.length; r++) {
            for (int c = 0; c < kaartData[r].length; c++) {
                if (kaartData[r][c] == 0) begaanbaar.add(new Point(c, r));
            }
        }
        return begaanbaar.get(random.nextInt(begaanbaar.size()));
    }

    public Magazijn getMagazijn() { return magazijn; }
    public int getMagazijnVoorraad() { return magazijn.getVoorraad(); }
    public int getVakkenvullerHandVoorraad() { return vakkenvuller != null ? vakkenvuller.getHandVoorraad() : 0; }
    public Schap getWillekeurigSchap() { return schappen.get(random.nextInt(schappen.size())); }
    public Schap vindLeegSchap() { return schappen.stream().filter(Schap::isLeeg).findFirst().orElse(null); }
    public int[][] getKaartData() { return kaartData; }
    public List<Persoon> getPersonen() { return personen; }
    public Point getUitgangLocatie() { return UITGANG_LOCATIE; }
    public Point getMagazijnDeur() { return MAGAZIJN_DEUR; }
    public Point getKantoorLocatie() { return KANTOOR_LOCATIE; }
    public Point getManagerLeveringPlek() { return MANAGER_LEVERING_PLEK; }
    public Map<String, Integer> getVoorraadStatistieken() {
        return schappen.stream().collect(Collectors.groupingBy(s -> s.getProduct().getNaam(), Collectors.summingInt(Schap::getVoorraad)));
    }

    private void laadKaart() {
        try (InputStream is = getClass().getResourceAsStream("/tilemap.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            List<int[]> rijen = new ArrayList<>();
            String lijn;
            while ((lijn = reader.readLine()) != null) {
                if (lijn.trim().isEmpty()) continue;
                int[] rij = Arrays.stream(lijn.trim().split("\\s+"))
                        .mapToInt(Integer::parseInt)
                        .toArray();
                rijen.add(rij);
            }
            kaartData = rijen.toArray(new int[0][]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}