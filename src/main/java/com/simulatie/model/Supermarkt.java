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

/**
 * HET DOEL:
 * Dit is de centrale Model-klasse. Het is het brein van de simulatie.
 * Deze klasse bevat alle data (lijsten van personen, schappen, etc.) en de hoofdregels
 * van de simulatie (zoals het spawnen van klanten en het aansturen van de manager).
 */
public class Supermarkt {

    // --- Configuratie Constanten ---
    // DE KEUZE: Alle "magische getallen" (zoals kaartgrootte, locaties) worden bovenaan
    // als 'static final' constanten gedefinieerd. Dit maakt de code leesbaarder en
    // makkelijker aan te passen. Als de ingang verplaatst moet worden, hoeft dat maar op één plek.
    public static final int KAART_BREEDTE_IN_TEGELS = 32;
    public static final int KAART_HOOGTE_IN_TEGELS = 21;
    private static final Point INGANG_LOCATIE = new Point(4, 20);
    private static final Point UITGANG_LOCATIE = new Point(3, 20);
    private static final Point KANTOOR_LOCATIE = new Point(2, 2);
    private static final Point MAGAZIJN_DEUR = new Point(25, 12);
    private static final Point MANAGER_LEVERING_PLEK = new Point(30, 12);
    private static final int MAX_AANTAL_KLANTEN = 25;

    // --- Simulatie Staat ---
    // Dit zijn de variabelen die de huidige toestand van de supermarkt beschrijven.
    private int[][] kaartData; // De layout van de winkel.
    private final List<Persoon> personen = new ArrayList<>(); // Alle "levende" objecten.
    private final List<Schap> schappen = new ArrayList<>();   // Alle winkelschappen.
    private final List<KassaRij> kassaRijen = new ArrayList<>(); // De kassarijen.
    private SupermarktManager manager; // Een directe referentie naar de manager.
    private Vakkenvuller vakkenvuller; // Een directe referentie naar de vakkenvuller.
    private final Magazijn magazijn = new Magazijn(); // Het magazijn.
    private double snelheidsMultiplier = 1.0; // Factor om de simulatie te versnellen/vertragen.

    // --- Timers & Logica ---
    private int klantSpawnTimer = 0;
    private int leveringTimer = 0;
    private boolean leveringOnderweg = false; // Een 'vlag' om te voorkomen dat er meerdere leveringen tegelijk worden besteld.
    private final Random random = new Random(); // Voor willekeurige keuzes.

    public Supermarkt() {
        laadKaart();
        initialiseerWinkel();
    }

    /**
     * De hoofd-updatemethode. Wordt elke 'tick' van de game loop aangeroepen.
     * De volgorde hier is belangrijk voor de logica.
     */
    public void update() {
        beheerLeveringen();         // Check of er een nieuwe levering nodig is.
        werkPersonenBij();          // Update elke persoon (bewegen, beslissen).
        spawnNieuweKlant();         // Check of er een nieuwe klant mag binnenkomen.
        werkKaartBij();             // Update de tegels (bv. een leeg schap).
        verwijderKlantenDieKlaarZijn(); // Ruim klanten op die de winkel hebben verlaten.
    }

    /**
     * Zet de supermarkt op bij de start: leest de kaart en creëert schappen en personeel.
     */
    private void initialiseerWinkel() {
        Product vlees = new Product("Biefstuk", "Vleeswaren");
        Product frisdrank = new Product("Cola", "Frisdrank");
        Product groente = new Product("Broccoli", "Groente");
        Map<Integer, Product> productPerTegel = Map.of(3, vlees, 4, frisdrank, 5, groente);

        // Loop door de kaartdata om objecten te maken op basis van de tegel-ID's.
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

        // Maak het personeel aan en voeg ze toe aan de lijst van personen.
        manager = new SupermarktManager(KANTOOR_LOCATIE);
        vakkenvuller = new Vakkenvuller(MAGAZIJN_DEUR);
        personen.add(manager);
        personen.add(vakkenvuller);
    }

    /**
     * Beheert het spawnen van nieuwe klanten op basis van een timer en een maximumaantal.
     */
    private void spawnNieuweKlant() {
        klantSpawnTimer++;
        long aantalKlanten = personen.stream().filter(Klant.class::isInstance).count();
        // De timer wordt beïnvloed door de snelheidsmultiplier.
        if (klantSpawnTimer > (100 / snelheidsMultiplier) && aantalKlanten < MAX_AANTAL_KLANTEN) {
            klantSpawnTimer = 0; // Reset de timer.
            Klant nieuweKlant = new Klant(INGANG_LOCATIE);
            nieuweKlant.setSnelheidMultiplier(snelheidsMultiplier);
            personen.add(nieuweKlant);
        }
    }

    /**
     * Beheert het aanvragen van nieuwe leveringen voor het magazijn.
     */
    private void beheerLeveringen() {
        leveringTimer++;
        if (!leveringOnderweg && leveringTimer > 1000) {
            leveringTimer = 0;
            leveringOnderweg = true; // Zet de vlag om dubbele bestellingen te voorkomen.
            manager.roepVoorLevering(this);
        }
    }

    /**
     * Wordt aangeroepen door de Manager wanneer hij een levering heeft verwerkt.
     */
    public void verwerkLevering() {
        magazijn.ontvangLevering(50);
        leveringOnderweg = false; // Zet de vlag terug zodat een nieuwe levering kan worden aangevraagd.
    }

    /**
     * Stuurt de manager aan en update elke persoon in de simulatie.
     */
    private void werkPersonenBij() {
        // Loop door de kassarijen om te zien of een manager nodig is.
        for (KassaRij rij : kassaRijen) {
            Klant wachtendeKlant = rij.getKlantVooraan();
            if (wachtendeKlant != null && wachtendeKlant.wachtOpManager()) {
                manager.roepNaarKassa(this, rij.getManagerPlek());
                break; // De manager kan maar één klant tegelijk helpen.
            }
        }

        // Roep de 'update'-methode van elke persoon aan.
        // DE KEUZE: We maken een kopie (`new ArrayList<>(...)`) om over te loopen.
        // Dit voorkomt een `ConcurrentModificationException` als een persoon zichzelf
        // uit de lijst zou verwijderen tijdens de loop (gebeurt hier niet, maar is een goede gewoonte).
        for (Persoon p : new ArrayList<>(personen)) {
            p.update(this);
        }
    }

    /**
     * Verwijdert klanten uit de simulatie die klaar zijn met winkelen.
     */
    private void verwijderKlantenDieKlaarZijn() {
        // 'removeIf' is een handige manier om elementen uit een lijst te verwijderen die aan een voorwaarde voldoen.
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

    public void pasSnelheidAan(double factor) {
        this.snelheidsMultiplier = Math.max(0.25, Math.min(8.0, this.snelheidsMultiplier * factor));
        for (Persoon p : personen) {
            p.setSnelheidMultiplier(snelheidsMultiplier);
        }
    }

    // --- Getters: methodes om informatie op te vragen ---
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