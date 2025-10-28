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

public class SupermarktApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Supermarkt Simulatie");

        Supermarkt model = new Supermarkt();
        SupermarktController controller = new SupermarktController(model);

        BorderPane root = new BorderPane();

        int canvasWidth = Supermarkt.KAART_BREEDTE_IN_TEGELS * SupermarktView.TEGEL_GROOTTE;
        int canvasHeight = Supermarkt.KAART_HOOGTE_IN_TEGELS * SupermarktView.TEGEL_GROOTTE;
        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        root.setCenter(canvas);
        SupermarktView view = new SupermarktView(canvas.getGraphicsContext2D());

        VBox statsContainer = createStatsPanel(controller);
        root.setRight(statsContainer);

        AnimationTimer gameLoop = new AnimationTimer() {
            public void handle(long now) {
                controller.updateSimulatie();
                view.render(model);
                updateStatsPanel(statsContainer, model);
            }
        };
        gameLoop.start();

        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private VBox createStatsPanel(SupermarktController controller) {
        VBox statsContainer = new VBox();
        statsContainer.setPrefWidth(256);
        statsContainer.setStyle("-fx-background-color: #2B2B2B;");
        statsContainer.setPadding(new Insets(10));

        BorderPane statsLayout = new BorderPane();
        statsContainer.getChildren().add(statsLayout);
        VBox.setVgrow(statsLayout, Priority.ALWAYS);

        VBox labelsBox = new VBox(10);
        Label titelLabel = new Label("Statistieken");
        titelLabel.setFont(new Font("Arial Bold", 18));
        titelLabel.setStyle("-fx-text-fill: white;");

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

        labelsBox.getChildren().addAll(titelLabel, personenLabel, new Separator(), magazijnLabel, vakkenvullerLabel, new Separator(), voorraadBox);
        statsLayout.setTop(labelsBox);

        HBox knoppenBox = new HBox(10);
        knoppenBox.setAlignment(Pos.CENTER);
        Button vertraagKnop = new Button("<<");
        Button versnelKnop = new Button(">>");
        knoppenBox.getChildren().addAll(vertraagKnop, versnelKnop);
        statsLayout.setBottom(knoppenBox);

        versnelKnop.setOnAction(event -> controller.versnelSimulatie());
        vertraagKnop.setOnAction(event -> controller.vertraagSimulatie());

        return statsContainer;
    }

    private void updateStatsPanel(VBox statsContainer, Supermarkt model) {
        Label personenLabel = (Label) statsContainer.lookup("#personenLabel");
        Label magazijnLabel = (Label) statsContainer.lookup("#magazijnLabel");
        Label vakkenvullerLabel = (Label) statsContainer.lookup("#vakkenvullerLabel");
        VBox voorraadBox = (VBox) statsContainer.lookup("#voorraadBox");

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
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}