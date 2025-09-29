module com.example.supermarktsimulator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.supermarktsimulator to javafx.fxml;
    exports com.example.supermarktsimulator;
}