package com.simulatie.app;

import com.simulatie.model.Supermarkt;
import com.simulatie.model.personen.Klant;
import com.simulatie.model.personen.Persoon;
import com.simulatie.model.winkel.Product;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SupermarktApp extends Application {

    private boolean isGepauzeerd = false;
    private Klant geselecteerdeKlant = null;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Supermarkt Simulatie");

        Supermarkt model = new Supermarkt();
        BorderPane root = new BorderPane();

        int canvasWidth = Supermarkt.KAART_BREEDTE_IN_TEGELS * SupermarktView.TEGEL_GROOTTE;
        int canvasHeight = Supermarkt.KAART_HOOGTE_IN_TEGELS * SupermarktView.TEGEL_GROOTTE;
        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        root.setCenter(canvas);

        SupermarktView view = new SupermarktView(canvas.getGraphicsContext2D());
        VBox statsContainer = createStatsPanel();
        root.setRight(statsContainer);

        // DE GROTE WIJZIGING zit hieronder in de AnimationTimer.
        AnimationTimer gameLoop = new AnimationTimer() {
            public void handle(long now) {
                // Stap 1: Update de simulatie logica ALLEEN als het spel NIET gepauzeerd is.
                if (!isGepauzeerd) {
                    model.update();
                }

                // Stap 2: Teken het scherm en de statistieken ALTIJD.
                // Hierdoor blijft het scherm reageren, zelfs tijdens pauze.
                view.render(model);
                updateStatsPanel(statsContainer, model);
            }
        };
        gameLoop.start(); // We starten de loop één keer en stoppen hem nooit meer.

        canvas.setOnMouseMoved(event -> {
            // De muis-detectie werkt alleen als het spel gepauzeerd is.
            if (isGepauzeerd) {
                geselecteerdeKlant = vindKlantOpPositie(model, event.getX(), event.getY());
            }
        });

        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private VBox createStatsPanel() {
        VBox statsContainer = new VBox();
        statsContainer.setPrefWidth(256);
        statsContainer.setStyle("-fx-background-color: #2B2B2B;");
        statsContainer.setPadding(new Insets(10));

        BorderPane statsLayout = new BorderPane();
        statsContainer.getChildren().add(statsLayout);
        VBox.setVgrow(statsLayout, Priority.ALWAYS);

        // --- Bovenste gedeelte met labels ---
        VBox labelsBox = new VBox(10);
        Label titelLabel = new Label("Statistieken");
        titelLabel.setFont(new Font("Arial Bold", 18));
        titelLabel.setStyle("-fx-text-fill: white;");

        // Alle labels voor de statistieken...
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

        VBox winkelmandjeBox = new VBox(5);
        winkelmandjeBox.setId("winkelmandjeBox");

        labelsBox.getChildren().addAll(titelLabel, personenLabel, new Separator(), magazijnLabel, vakkenvullerLabel, new Separator(), voorraadBox, new Separator(), winkelmandjeBox);
        statsLayout.setTop(labelsBox);

        // --- Onderste gedeelte met knoppen ---
        VBox knoppenContainer = new VBox(10);
        knoppenContainer.setAlignment(Pos.CENTER);

        Button pauzeerKnop = new Button("Pauzeer");
        pauzeerKnop.setMaxWidth(Double.MAX_VALUE);

        // De logica van de knop is nu veel simpeler.
        pauzeerKnop.setOnAction(event -> {
            // Zet de pauze-status om (van false naar true, of van true naar false).
            isGepauzeerd = !isGepauzeerd;

            if (isGepauzeerd) {
                pauzeerKnop.setText("Hervat");
            } else {
                pauzeerKnop.setText("Pauzeer");
                // Maak de selectie van de klant leeg als we het spel hervatten.
                geselecteerdeKlant = null;
            }
        });

        knoppenContainer.getChildren().add(pauzeerKnop);
        statsLayout.setBottom(knoppenContainer);

        return statsContainer;
    }

    private void updateStatsPanel(VBox statsContainer, Supermarkt model) {
        // Dit deel blijft hetzelfde en werkt nu perfect omdat deze methode altijd wordt aangeroepen.
        ((Label) statsContainer.lookup("#personenLabel")).setText("Aantal Personen: " + model.getPersonen().size());
        ((Label) statsContainer.lookup("#magazijnLabel")).setText("Magazijnvoorraad: " + model.getMagazijnVoorraad());
        ((Label) statsContainer.lookup("#vakkenvullerLabel")).setText("Handvoorraad Vuller: " + model.getVakkenvullerHandVoorraad());

        VBox voorraadBox = (VBox) statsContainer.lookup("#voorraadBox");
        voorraadBox.getChildren().clear();
        Label voorraadTitel = new Label("Voorraad in schappen:");
        voorraadTitel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        voorraadBox.getChildren().add(voorraadTitel);
        Map<String, Integer> voorraadStats = model.getVoorraadStatistieken();
        for (Map.Entry<String, Integer> entry : voorraadStats.entrySet()) {
            Label itemLabel = new Label("- " + entry.getKey() + ": " + entry.getValue());
            itemLabel.setStyle("-fx-text-fill: lightgray;");
            voorraadBox.getChildren().add(itemLabel);
        }

        VBox winkelmandjeBox = (VBox) statsContainer.lookup("#winkelmandjeBox");
        winkelmandjeBox.getChildren().clear();
        if (isGepauzeerd && geselecteerdeKlant != null) {
            Label mandjeTitel = new Label("Winkelmandje Klant:");
            mandjeTitel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            winkelmandjeBox.getChildren().add(mandjeTitel);

            List<Product> mandje = geselecteerdeKlant.getWinkelmandje();
            if (mandje.isEmpty()) {
                Label leegLabel = new Label("- (leeg)");
                leegLabel.setStyle("-fx-text-fill: lightgray;");
                winkelmandjeBox.getChildren().add(leegLabel);
            } else {
                Map<String, Long> productCounts = mandje.stream()
                        .collect(Collectors.groupingBy(Product::getNaam, Collectors.counting()));
                for (Map.Entry<String, Long> entry : productCounts.entrySet()) {
                    Label itemLabel = new Label("- " + entry.getKey() + ": " + entry.getValue());
                    itemLabel.setStyle("-fx-text-fill: lightgray;");
                    winkelmandjeBox.getChildren().add(itemLabel);
                }
            }
        }
    }

    private Klant vindKlantOpPositie(Supermarkt model, double muisX, double muisY) {
        for (Persoon persoon : model.getPersonen()) {
            if (persoon instanceof Klant) {
                double klantX = persoon.getX();
                double klantY = persoon.getY();
                double grootte = SupermarktView.TEGEL_GROOTTE;

                if (muisX >= klantX && muisX <= klantX + grootte &&
                        muisY >= klantY && muisY <= klantY + grootte) {
                    return (Klant) persoon;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}