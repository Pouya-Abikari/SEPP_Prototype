module se_prototype.se_prototype {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens se_prototype.se_prototype to javafx.fxml;
    exports se_prototype.se_prototype;
}