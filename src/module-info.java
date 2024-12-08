module com.misnotasapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics; 
    requires java.sql;

    opens com.misnotasapp to javafx.fxml;
    exports com.misnotasapp;
}
