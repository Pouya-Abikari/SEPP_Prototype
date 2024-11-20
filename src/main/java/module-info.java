module se_prototype.se_prototype {
    requires javafx.controls;
    requires javafx.fxml;


    opens se_prototype.se_prototype to javafx.fxml;
    exports se_prototype.se_prototype;
}