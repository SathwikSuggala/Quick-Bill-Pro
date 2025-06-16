module QuickBillPro {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens app to javafx.graphics, javafx.fxml;
    opens controllers to javafx.fxml;
    opens models to javafx.base;

    exports app;
}
