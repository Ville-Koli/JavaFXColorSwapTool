module com.quickcolorswap {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.swing;
    requires java.desktop;

    opens com.quickcolorswap to javafx.fxml;
    exports com.quickcolorswap;
}
