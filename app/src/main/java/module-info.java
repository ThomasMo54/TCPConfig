module com.motompro.tcpconfig.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens com.motompro.tcpconfig.app to javafx.fxml;
    exports com.motompro.tcpconfig.app;
}