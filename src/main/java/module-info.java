module se_prototype.se_prototype {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.management;

    opens se_prototype.se_prototype to javafx.fxml;
    exports se_prototype.se_prototype;
}