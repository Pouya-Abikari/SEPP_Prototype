package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class SignupSuccessfulController {
    @FXML
    private Button backToLoginButton;

    @FXML
    private ImageView successImage;

    @FXML
    public void initialize() {

        setupImage();
    }

    @FXML
    private void backToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se_prototype/se_prototype/login.fxml"));
            Scene loginScene = new Scene(loader.load(), 400, 711);
            Stage stage = (Stage) backToLoginButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupImage (){
        Image tickImg = new Image(this.getClass().getResourceAsStream("/loginSuccess.png"));
        successImage.setImage(tickImg);
        successImage.setFitWidth(100.0);
        successImage.setPreserveRatio(true);
    }
}