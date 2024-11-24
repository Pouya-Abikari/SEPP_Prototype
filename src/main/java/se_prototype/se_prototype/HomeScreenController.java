package se_prototype.se_prototype;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeScreenController {

    @FXML
    private Button messageButton;

    @FXML
    private Button notificationButton;

    @FXML
    private ImageView tutorialImage;

    @FXML
    private ImageView  christmasImage;

    @FXML
    private Button homeButton;
    @FXML
    private Button menuButton;
    @FXML
    private Button cartButton;
    @FXML
    private Button settingsButton;



    @FXML
    public void initialize() {

        setupImages();

        // Set up button actions
        menuButton.setOnAction(event -> switchToPage("menu.fxml", "Menu"));
        cartButton.setOnAction(event -> switchToPage("cart.fxml", "Cart"));
        settingsButton.setOnAction(event -> switchToPage("settings.fxml", "Settings"));
    }


    private void setupImages() {
        try {

            // Load and set the message button image
            Image messageImg = new Image(getClass().getResourceAsStream("/messageButton.png"));
            ImageView messageImgView = new ImageView(messageImg);
            messageImgView.setFitWidth(33);
            messageImgView.setFitHeight(33);
            messageButton.setGraphic(messageImgView);

            // Load and set the notification button image
            Image notificationImg = new Image(getClass().getResourceAsStream("/notificationButton.png"));
            ImageView notificationImgView = new ImageView(notificationImg);
            notificationImgView.setFitWidth(30);
            notificationImgView.setFitHeight(30);
            notificationButton.setGraphic(notificationImgView);

            // Load the tutorial image
            Image tutorialImg = new Image(this.getClass().getResourceAsStream("/videoscreen.png"));
            tutorialImage.setImage(tutorialImg);
            tutorialImage.setFitWidth(300.0);
            tutorialImage.setPreserveRatio(true);

            // Load the Halloween promo image
            Image halloweenImg = new Image(this.getClass().getResourceAsStream("/merryxmas.png"));
            christmasImage.setImage(halloweenImg);
            christmasImage.setFitWidth(300.0);
            christmasImage.setPreserveRatio(true);

            // home page button
            Image homeImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/homePageButtonClicked.png"));
            ImageView homeImgView = new ImageView(homeImg);
            homeImgView.setFitWidth(30);
            homeImgView.setFitHeight(30);
            homeButton.setGraphic(homeImgView);

            // Menu Button
            Image menuImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/menuPageButton.png"));
            ImageView menuImgView = new ImageView(menuImg);
            menuImgView.setFitWidth(35);
            menuImgView.setFitHeight(35);
            menuButton.setGraphic(menuImgView);

            // Cart Button
            Image cartImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/cartPageButton.png"));
            ImageView cartImgView = new ImageView(cartImg);
            cartImgView.setFitWidth(20);
            cartImgView.setFitHeight(20);
            cartButton.setGraphic(cartImgView);

            // Settings Button
            Image settingsImg = new Image(getClass().getResourceAsStream("/bottomPartSymbols/settingPageButton.png"));
            ImageView settingsImgView = new ImageView(settingsImg);
            settingsImgView.setFitWidth(35);
            settingsImgView.setFitHeight(35);
            settingsButton.setGraphic(settingsImgView);



        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void switchToPage(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) homeButton.getScene().getWindow(); // Get the current stage
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
