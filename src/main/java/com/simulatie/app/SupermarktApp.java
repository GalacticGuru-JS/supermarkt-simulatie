package com.simulatie.app;

import com.simulatie.controller.SupermarktController;
import com.simulatie.model.Supermarkt;
import com.simulatie.view.SupermarktView;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Map;

/**
 * HET DOEL:
 * Dit is het startpunt van de hele applicatie. Deze klasse is verantwoordelijk voor het opzetten van
 * het venster, het aanmaken van de UI-elementen (knoppen, labels), en het starten van de 'game loop'
 * die de simulatie draaiende houdt. Het volgt de Model-View-Controller (MVC) structuur.
 */
public class SupermarktApp extends Application {

    /**
     * De start-methode is de hoofd-methode voor een JavaFX-applicatie.
     * Het wordt automatisch aangeroepen wanneer de applicatie wordt gestart.
     *
     * @param primaryStage Het hoofdvenster van de applicatie, geleverd door JavaFX.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Supermarkt Simulatie");

        // --- Model-View-Controller (MVC) Initialisatie ---
        // DE KEUZE: We scheiden de logica (Model), de weergave (View) en de besturing (Controller).
        // Dit maakt de code georganiseerd en makkelijker te onderhouden.
        // 1. Het Model (`Supermarkt`) bevat alle data en regels van de simulatie.
        Supermarkt model = new Supermarkt();
        // 2. De Controller (`SupermarktController`) is een dunne laag die input (zoals knopklikken) doorgeeft aan het Model.
        SupermarktController controller = new SupermarktController(model);

        // --- UI Opbouw ---
        // We gebruiken een BorderPane als hoofdlayout. Hiermee kunnen we makkelijk elementen
        // in het midden, rechts, boven, etc. plaatsen.
        BorderPane root = new BorderPane();

        // Het 'Canvas' is het tekengebied waar de supermarkt en de personen op getekend worden.
        int canvasWidth = Supermarkt.KAART_BREEDTE_IN_TEGELS * SupermarktView.TEGEL_GROOTTE;
        int canvasHeight = Supermarkt.KAART_HOOGTE_IN_TEGELS * SupermarktView.TEGEL_GROOTTE;
        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        root.setCenter(canvas); // Plaats het tekengebied in het midden van het venster.

        // 3. De View (`SupermarktView`) is verantwoordelijk voor het daadwerkelijk tekenen op het canvas.
        // We geven het de 'GraphicsContext' van het canvas, wat je kunt zien als de "pen" om mee te tekenen.
        SupermarktView view = new SupermarktView(canvas.getGraphicsContext2D());

        // Maak het rechterpaneel voor statistieken en knoppen.
        VBox statsContainer = createStatsPanel(controller);
        root.setRight(statsContainer);

        // --- De Game Loop ---
        // DE KEUZE: Een AnimationTimer is de standaard manier in JavaFX om een "game loop" te maken.
        // De 'handle'-methode wordt ongeveer 60 keer per seconde aangeroepen.
        // Dit zorgt voor vloeiende animaties.
        AnimationTimer gameLoop = new AnimationTimer() {
            public void handle(long now) {
                // 1. Update de logica van de simulatie (laat iedereen bewegen, beslissingen nemen, etc.).
                controller.updateSimulatie();
                // 2. Teken de nieuwe situatie op het scherm.
                view.render(model);
                // 3. Werk de tekst in het statistiekenpaneel bij.
                updateStatsPanel(statsContainer, model);
            }
        };
        gameLoop.start(); // Start de loop.

        // Toon het venster aan de gebruiker.
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false); // Maak het venster niet schaalbaar.
        primaryStage.show();
    }

    /**
     * Hulpmethode om het rechterpaneel met statistieken en knoppen te maken.
     * DE KEUZE: Door dit in een aparte methode te doen, houden we de 'start'-methode overzichtelijk.
     * @param controller De controller om de knop-acties aan te koppelen.
     * @return Een VBox (verticale layout) die het volledige rechterpaneel bevat.
     */
    private VBox createStatsPanel(SupermarktController controller) {
        VBox statsContainer = new VBox();
        statsContainer.setPrefWidth(256);
        statsContainer.setStyle("-fx-background-color: #2B2B2B;");
        statsContainer.setPadding(new Insets(10));

        BorderPane statsLayout = new BorderPane();
        statsContainer.getChildren().add(statsLayout);
        VBox.setVgrow(statsLayout, Priority.ALWAYS);

        // Box voor de labels (tekst)
        VBox labelsBox = new VBox(10);
        Label titelLabel = new Label("Statistieken");
        titelLabel.setFont(new Font("Arial Bold", 18));
        titelLabel.setStyle("-fx-text-fill: white;");

        // Maak de labels aan en geef ze een ID. Met dit ID kunnen we ze later makkelijk terugvinden om hun tekst aan te passen.
        Label personenLabel = new Label();
        personenLabel.setId("personenLabel");
        personenLabel.setStyle("-fx-text-fill: white;");

        Label magazijnLabel = new Label();
        magazijnLabel.setId("magazijnLabel");
        magazijnLabel.setStyle("-fx-text-fill: white;");

        Label vakkenvullerLabel = new Label();
        vakkenvullerLabel.setId("vakkenvullerLabel");
        vakkenvullerLabel.setStyle("-fx-text-fill: white;");

        VBox voorraadBox = new VBox(5);
        voorraadBox.setId("voorraadBox");

        // Voeg alle labels en scheidingslijnen toe aan de box.
        labelsBox.getChildren().addAll(titelLabel, personenLabel, new Separator(), magazijnLabel, vakkenvullerLabel, new Separator(), voorraadBox);
        statsLayout.setTop(labelsBox);

        // Box voor de knoppen
        HBox knoppenBox = new HBox(10);
        knoppenBox.setAlignment(Pos.CENTER);
        Button vertraagKnop = new Button("<<");
        Button versnelKnop = new Button(">>");
        knoppenBox.getChildren().addAll(vertraagKnop, versnelKnop);
        statsLayout.setBottom(knoppenBox);

        // Koppel de acties van de knoppen aan de methodes in de controller.
        versnelKnop.setOnAction(event -> controller.versnelSimulatie());
        vertraagKnop.setOnAction(event -> controller.vertraagSimulatie());

        return statsContainer;
    }

    /**
     * Werkt de tekst van de labels in het statistiekenpaneel bij met de laatste data uit het model.
     * @param statsContainer Het paneel dat de labels bevat.
     * @param model Het supermarktmodel waar de data vandaan komt.
     */
    private void updateStatsPanel(VBox statsContainer, Supermarkt model) {
        // Zoek de labels op basis van hun ID. Dit is een robuuste manier om UI-elementen te benaderen.
        Label personenLabel = (Label) statsContainer.lookup("#personenLabel");
        Label magazijnLabel = (Label) statsContainer.lookup("#magazijnLabel");
        Label vakkenvullerLabel = (Label) statsContainer.lookup("#vakkenvullerLabel");
        VBox voorraadBox = (VBox) statsContainer.lookup("#voorraadBox");

        // Update de tekst van elk label met de meest recente informatie.
        if (personenLabel != null) {
            personenLabel.setText("Aantal Personen: " + model.getPersonen().size());
        }

        if (magazijnLabel != null) {
            magazijnLabel.setText("Magazijnvoorraad: " + model.getMagazijnVoorraad());
        }

        if (vakkenvullerLabel != null) {
            vakkenvullerLabel.setText("Handvoorraad Vuller: " + model.getVakkenvullerHandVoorraad());
        }

        if (voorraadBox != null) {
            voorraadBox.getChildren().clear(); // Maak de oude lijst met voorraad leeg.
            Label voorraadTitel = new Label("Voorraad in schappen:");
            voorraadTitel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            voorraadBox.getChildren().add(voorraadTitel);

            // Haal de voorraadstatistieken op en maak voor elk product een nieuw label.
            Map<String, Integer> voorraadStats = model.getVoorraadStatistieken();
            for (Map.Entry<String, Integer> entry : voorraadStats.entrySet()) {
                Label itemLabel = new Label("- " + entry.getKey() + ": " + entry.getValue());
                itemLabel.setStyle("-fx-text-fill: lightgray;");
                voorraadBox.getChildren().add(itemLabel);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}