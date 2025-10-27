module com.example.supermarktsimulator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.supermarktsimulator to javafx.fxml;
    exports com.example.supermarktsimulator;
    exports com.example.supermarktsimulator.model;
    opens com.example.supermarktsimulator.model to javafx.fxml;
    exports com.example.supermarktsimulator.game;
    opens com.example.supermarktsimulator.game to javafx.fxml;
    exports com.example.supermarktsimulator.graphics;
    opens com.example.supermarktsimulator.graphics to javafx.fxml;
    exports com.example.supermarktsimulator.util;
    opens com.example.supermarktsimulator.util to javafx.fxml;
    exports com.example.supermarktsimulator.oldcode;
    opens com.example.supermarktsimulator.oldcode to javafx.fxml;
}